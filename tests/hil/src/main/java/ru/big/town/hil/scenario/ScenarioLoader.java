package ru.big.town.hil.scenario;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import com.qinggan.canbus.VehicleState;

import ru.big.town.hil.WriteMode;

public final class ScenarioLoader {
    private static final long DEFAULT_WITHIN_MS = 2000L;
    private static final long DEFAULT_NO_PUSH_MS = 500L;

    public static final class ScenarioLoadException extends RuntimeException {
        public ScenarioLoadException(String message) {
            super(message);
        }
    }

    private ScenarioLoader() {
    }

    public static Scenario load(Path file) {
        try {
            return load(Files.readString(file), file.toString());
        } catch (IOException e) {
            throw new ScenarioLoadException("cannot read " + file + ": " + e);
        }
    }

    public static Scenario load(String yamlText, String source) {
        Object root;
        try {
            root = new Yaml(new SafeConstructor(new LoaderOptions())).load(new StringReader(yamlText));
        } catch (RuntimeException e) {
            throw new ScenarioLoadException(source + ": YAML parse error: " + e.getMessage());
        }
        if (!(root instanceof Map)) {
            throw new ScenarioLoadException(source + ": scenario root must be a mapping");
        }
        Map<?, ?> map = (Map<?, ?>) root;
        String name = requireString(map, "name", source);
        String description = optionalString(map, "description", source);
        Map<String, Object> config = parseConfig(map.get("config"), source);
        Object stepsRaw = map.get("steps");
        if (!(stepsRaw instanceof List) || ((List<?>) stepsRaw).isEmpty()) {
            throw new ScenarioLoadException(source + ": 'steps' must be a non-empty list");
        }
        List<?> rawSteps = (List<?>) stepsRaw;
        List<Step> steps = new ArrayList<>();
        List<String> stepIds = new ArrayList<>();
        for (int i = 0; i < rawSteps.size(); i++) {
            Step step = parseStep(rawSteps.get(i), source, i);
            steps.add(step);
            stepIds.add(stepIdOf(step));
        }
        for (int i = 0; i < steps.size(); i++) {
            String after = afterOf(steps.get(i));
            if (after != null && indexOfId(stepIds, after, i) < 0) {
                throw new ScenarioLoadException(source + ": steps[" + i + "]: 'after: " + after
                        + "' does not match an earlier step id");
            }
        }
        return new Scenario(name, description, steps, config);
    }

    private static Map<String, Object> parseConfig(Object raw, String source) {
        Map<String, Object> out = new LinkedHashMap<>();
        if (raw == null) {
            return out;
        }
        if (!(raw instanceof Map)) {
            throw new ScenarioLoadException(source + ": 'config' must be a mapping");
        }
        for (Map.Entry<?, ?> e : ((Map<?, ?>) raw).entrySet()) {
            String key = String.valueOf(e.getKey());
            Object value = e.getValue();
            switch (key) {
                case "write_mode":
                    parseWriteMode(value, source + ": config.write_mode");
                    break;
                case "fuel_liters":
                    requireNumber(value, source + ": config.fuel_liters");
                    break;
                case "late_ms":
                case "missing_value":
                case "conflict_value":
                case "door":
                case "gear_ordinal":
                case "gear_value":
                case "speed":
                case "fuel_percent":
                    requireIntValue(value, source + ": config." + key);
                    break;
                default:
                    throw new ScenarioLoadException(source + ": unknown config key '" + key + "'");
            }
            out.put(key, value);
        }
        return out;
    }

    private static Step parseStep(Object raw, String source, int index) {
        String where = source + ": steps[" + index + "]";
        if (!(raw instanceof Map) || ((Map<?, ?>) raw).size() != 1) {
            throw new ScenarioLoadException(where + ": step must be a single-key mapping");
        }
        Map.Entry<?, ?> entry = ((Map<?, ?>) raw).entrySet().iterator().next();
        String type = String.valueOf(entry.getKey());
        Object body = entry.getValue();
        switch (type) {
            case "sleep": {
                long millis = requireIntValue(body, where + ": sleep");
                if (millis < 0) {
                    throw new ScenarioLoadException(where + ": sleep must be >= 0");
                }
                return new Step.SleepStep(millis);
            }
            case "set_mode":
                return new Step.SetModeStep(parseWriteMode(body, where + ": set_mode"));
            case "action":
                return parseAction(body, where);
            case "inject":
                return parseInject(body, where);
            case "expect_txn":
                return parseExpectTxn(body, where);
            case "expect_echo":
                return parseExpectPush(body, where, 36);
            case "expect_push":
                return parseExpectPush(body, where, -1);
            case "assert_no_echo":
                return parseAssertNoPush(body, where, 36);
            case "assert_no_push":
                return parseAssertNoPush(body, where, -1);
            case "assert_cache":
                return parseAssertCache(body, where);
            case "assert_cache_absent":
                return parseAssertCacheAbsent(body, where);
            case "assert_app":
                return parseAssertApp(body, where);
            default:
                throw new ScenarioLoadException(where + ": unknown step type '" + type + "'");
        }
    }

    private static Step parseAction(Object body, String where) {
        if (body instanceof String) {
            return Step.ActionStep.harness((String) body);
        }
        if (!(body instanceof Map)) {
            throw new ScenarioLoadException(where + ": action must be a string or a mapping");
        }
        Map<?, ?> map = (Map<?, ?>) body;
        String name = requireString(map, "name", where);
        List<String> forms = new ArrayList<>();
        for (String key : new String[] {"send", "read", "txn_code", "bundle"}) {
            if (map.containsKey(key)) {
                forms.add(key);
            }
        }
        if (forms.isEmpty()) {
            for (Object k : map.keySet()) {
                if (!"name".equals(String.valueOf(k))) {
                    throw new ScenarioLoadException(where + ": unexpected action key '" + k + "'");
                }
            }
            return Step.ActionStep.harness(name);
        }
        if (forms.size() != 1) {
            throw new ScenarioLoadException(where + ": action must have exactly one of send/read/txn_code/bundle");
        }
        switch (forms.get(0)) {
            case "send": {
                Map<?, ?> send = requireMap(map.get("send"), where + ": action.send");
                String stateName = null;
                Integer stateId = null;
                if (send.containsKey("state_name")) {
                    stateName = String.valueOf(send.get("state_name"));
                    stateId = resolveStateId(stateName, where);
                } else if (send.containsKey("state_id")) {
                    stateId = requireIntValue(send.get("state_id"), where + ": send.state_id");
                } else {
                    throw new ScenarioLoadException(where + ": action.send requires state_name or state_id");
                }
                if (!send.containsKey("value")) {
                    throw new ScenarioLoadException(where + ": action.send requires value");
                }
                int value = requireIntValue(send.get("value"), where + ": send.value");
                String label = stateName != null ? stateName : String.valueOf(stateId);
                return Step.ActionStep.send(name, stateId, value, label);
            }
            case "read": {
                Map<?, ?> read = requireMap(map.get("read"), where + ": action.read");
                String stateName = null;
                Integer stateId = null;
                if (read.containsKey("state_name")) {
                    stateName = String.valueOf(read.get("state_name"));
                    stateId = resolveStateId(stateName, where);
                } else if (read.containsKey("state_id")) {
                    stateId = requireIntValue(read.get("state_id"), where + ": read.state_id");
                } else {
                    throw new ScenarioLoadException(where + ": action.read requires state_name or state_id");
                }
                String label = stateName != null ? stateName : String.valueOf(stateId);
                return Step.ActionStep.read(name, stateId, label);
            }
            case "txn_code": {
                int code = requireIntValue(map.get("txn_code"), where + ": txn_code");
                return Step.ActionStep.txn(code, name);
            }
            case "bundle": {
                Map<?, ?> bundleRaw = requireMap(map.get("bundle"), where + ": action.bundle");
                if (bundleRaw.isEmpty()) {
                    throw new ScenarioLoadException(where + ": action.bundle must not be empty");
                }
                Map<String, Integer> bundle = new LinkedHashMap<>();
                for (Map.Entry<?, ?> e : bundleRaw.entrySet()) {
                    String stateName = String.valueOf(e.getKey());
                    int id = resolveStateId(stateName, where);
                    bundle.put(stateName, requireIntValue(e.getValue(), where + ": bundle." + stateName));
                    if (id < 0) {
                        throw new ScenarioLoadException(where + ": unresolved state " + stateName);
                    }
                }
                return Step.ActionStep.bundle(name, bundle);
            }
            default:
                throw new ScenarioLoadException(where + ": unsupported action form");
        }
    }

    private static Step parseInject(Object body, String where) {
        if (!(body instanceof Map) || ((Map<?, ?>) body).size() != 1) {
            throw new ScenarioLoadException(where + ": inject must be a mapping with exactly one key");
        }
        Map.Entry<?, ?> entry = ((Map<?, ?>) body).entrySet().iterator().next();
        String key = String.valueOf(entry.getKey());
        Object value = entry.getValue();
        switch (key) {
            case "door":
                return Step.InjectStep.door(requireIntValue(value, where + ": inject.door"));
            case "gear":
                return Step.InjectStep.gear(requireIntValue(value, where + ": inject.gear"));
            case "speed":
                return Step.InjectStep.speed(requireIntValue(value, where + ": inject.speed"));
            case "state": {
                Map<?, ?> state = requireMap(value, where + ": inject.state");
                String stateName = null;
                Integer stateId = null;
                if (state.containsKey("name")) {
                    stateName = String.valueOf(state.get("name"));
                    stateId = resolveStateId(stateName, where);
                } else if (state.containsKey("id")) {
                    stateId = requireIntValue(state.get("id"), where + ": inject.state.id");
                } else {
                    throw new ScenarioLoadException(where + ": inject.state requires name or id");
                }
                if (!state.containsKey("value")) {
                    throw new ScenarioLoadException(where + ": inject.state requires value");
                }
                int v = requireIntValue(state.get("value"), where + ": inject.state.value");
                return Step.InjectStep.state(stateId, v, stateName);
            }
            default:
                throw new ScenarioLoadException(where + ": unknown inject key '" + key + "'");
        }
    }

    private static Step parseExpectTxn(Object body, String where) {
        Map<?, ?> map = requireMap(body, where + ": expect_txn");
        if (!map.containsKey("code")) {
            throw new ScenarioLoadException(where + ": expect_txn requires code");
        }
        int code = requireIntValue(map.get("code"), where + ": code");
        Integer stateId = resolveOptionalStateId(map, where);
        Integer value = null;
        if (map.containsKey("value")) {
            value = requireIntValue(map.get("value"), where + ": value");
        }
        if (stateId != null && code != 58 && code != 57) {
            throw new ScenarioLoadException(where + ": state_id/state_name is only supported for code 57/58");
        }
        if (value != null && code != 58) {
            throw new ScenarioLoadException(where + ": value is only supported for code 58");
        }
        long within = map.containsKey("within_ms")
                ? requireIntValue(map.get("within_ms"), where + ": within_ms")
                : DEFAULT_WITHIN_MS;
        String after = optionalAfter(map, where);
        return new Step.ExpectTxnStep(code, stateId, value, within, after);
    }

    private static Step parseExpectPush(Object body, String where, int defaultCb) {
        Map<?, ?> map = requireMap(body, where);
        int cbCode = defaultCb;
        if (map.containsKey("cb_code")) {
            cbCode = requireIntValue(map.get("cb_code"), where + ": cb_code");
        }
        validateCbCode(cbCode, where);
        Integer stateId = null;
        if (map.containsKey("state_id") || map.containsKey("state_name")) {
            if (cbCode != 36) {
                throw new ScenarioLoadException(where + ": state_id/state_name is only supported for cb_code 36");
            }
            stateId = resolveOptionalStateId(map, where);
        }
        Integer value = null;
        if (map.containsKey("value")) {
            value = requireIntValue(map.get("value"), where + ": value");
            if (cbCode != 36 && cbCode != 1 && cbCode != 12) {
                throw new ScenarioLoadException(where + ": value is not supported for cb_code " + cbCode);
            }
        }
        long within = map.containsKey("within_ms")
                ? requireIntValue(map.get("within_ms"), where + ": within_ms")
                : DEFAULT_WITHIN_MS;
        String after = optionalAfter(map, where);
        return new Step.ExpectPushStep(cbCode, stateId, value, within, after);
    }

    private static Step parseAssertNoPush(Object body, String where, int defaultCb) {
        Map<?, ?> map = requireMap(body, where);
        int cbCode = defaultCb;
        if (map.containsKey("cb_code")) {
            cbCode = requireIntValue(map.get("cb_code"), where + ": cb_code");
        }
        validateCbCode(cbCode, where);
        Integer stateId = null;
        if (map.containsKey("state_id") || map.containsKey("state_name")) {
            if (cbCode != 36) {
                throw new ScenarioLoadException(where + ": state_id/state_name is only supported for cb_code 36");
            }
            stateId = resolveOptionalStateId(map, where);
        }
        Integer value = null;
        if (map.containsKey("value")) {
            value = requireIntValue(map.get("value"), where + ": value");
            if (cbCode != 36 && cbCode != 1 && cbCode != 12) {
                throw new ScenarioLoadException(where + ": value is not supported for cb_code " + cbCode);
            }
        }
        long within = map.containsKey("within_ms")
                ? requireIntValue(map.get("within_ms"), where + ": within_ms")
                : DEFAULT_NO_PUSH_MS;
        String after = optionalAfter(map, where);
        return new Step.AssertNoPushStep(cbCode, stateId, value, within, after);
    }

    private static Step parseAssertCache(Object body, String where) {
        Map<?, ?> map = requireMap(body, where + ": assert_cache");
        Integer stateId = resolveRequiredStateId(map, where + ": assert_cache");
        String stateName = map.containsKey("state_name") ? String.valueOf(map.get("state_name")) : null;
        if (!map.containsKey("equals")) {
            throw new ScenarioLoadException(where + ": assert_cache requires equals");
        }
        long expected = requireIntValue(map.get("equals"), where + ": equals");
        return new Step.AssertCacheStep(stateId, stateName, expected);
    }

    private static Step parseAssertCacheAbsent(Object body, String where) {
        Map<?, ?> map = requireMap(body, where + ": assert_cache_absent");
        Integer stateId = resolveRequiredStateId(map, where + ": assert_cache_absent");
        String stateName = map.containsKey("state_name") ? String.valueOf(map.get("state_name")) : null;
        return new Step.AssertCacheAbsentStep(stateId, stateName);
    }

    private static Step parseAssertApp(Object body, String where) {
        Map<?, ?> map = requireMap(body, where + ": assert_app");
        if (!map.containsKey("name")) {
            throw new ScenarioLoadException(where + ": assert_app requires name");
        }
        if (!map.containsKey("equals")) {
            throw new ScenarioLoadException(where + ": assert_app requires equals");
        }
        return new Step.AssertAppStep(String.valueOf(map.get("name")), map.get("equals"));
    }

    private static void validateCbCode(int cbCode, String where) {
        if (cbCode != 1 && cbCode != 4 && cbCode != 10 && cbCode != 12 && cbCode != 36) {
            throw new ScenarioLoadException(where + ": unsupported cb_code " + cbCode);
        }
    }

    private static Integer resolveOptionalStateId(Map<?, ?> map, String where) {
        if (map.containsKey("state_id")) {
            return requireIntValue(map.get("state_id"), where + ": state_id");
        }
        if (map.containsKey("state_name")) {
            return resolveStateId(String.valueOf(map.get("state_name")), where);
        }
        return null;
    }

    private static Integer resolveRequiredStateId(Map<?, ?> map, String where) {
        Integer id = resolveOptionalStateId(map, where);
        if (id == null) {
            throw new ScenarioLoadException(where + " requires state_id or state_name");
        }
        return id;
    }

    private static int resolveStateId(String stateName, String where) {
        try {
            return VehicleState.idOf(stateName);
        } catch (IllegalArgumentException e) {
            throw new ScenarioLoadException(where + ": unknown state name '" + stateName + "'");
        }
    }

    private static String optionalAfter(Map<?, ?> map, String where) {
        if (!map.containsKey("after")) {
            return null;
        }
        String after = String.valueOf(map.get("after"));
        if (after.isEmpty()) {
            throw new ScenarioLoadException(where + ": 'after' must not be empty");
        }
        return after;
    }

    private static WriteMode parseWriteMode(Object value, String where) {
        if (!(value instanceof String)) {
            throw new ScenarioLoadException(where + ": write mode must be a string");
        }
        try {
            return WriteMode.valueOf(((String) value).toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ScenarioLoadException(where + ": unknown write mode '" + value + "'");
        }
    }

    private static String stepIdOf(Step step) {
        if (step instanceof Step.ActionStep) {
            return ((Step.ActionStep) step).name;
        }
        if (step instanceof Step.InjectStep) {
            Step.InjectStep inject = (Step.InjectStep) step;
            if (inject.kind == Step.InjectStep.Kind.STATE) {
                return "inject:state:" + (inject.stateName != null ? inject.stateName : inject.stateId);
            }
            return "inject:" + inject.kind.name().toLowerCase(Locale.ROOT);
        }
        if (step instanceof Step.SetModeStep) {
            return "set_mode:" + ((Step.SetModeStep) step).mode.name();
        }
        if (step instanceof Step.SleepStep) {
            return "sleep";
        }
        return null;
    }

    private static String afterOf(Step step) {
        if (step instanceof Step.ExpectTxnStep) {
            return ((Step.ExpectTxnStep) step).after;
        }
        if (step instanceof Step.ExpectPushStep) {
            return ((Step.ExpectPushStep) step).after;
        }
        if (step instanceof Step.AssertNoPushStep) {
            return ((Step.AssertNoPushStep) step).after;
        }
        return null;
    }

    private static int indexOfId(List<String> ids, String id, int beforeIndex) {
        for (int i = beforeIndex - 1; i >= 0; i--) {
            if (id.equals(ids.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private static Map<?, ?> requireMap(Object raw, String where) {
        if (!(raw instanceof Map)) {
            throw new ScenarioLoadException(where + " must be a mapping");
        }
        return (Map<?, ?>) raw;
    }

    private static String requireString(Map<?, ?> map, String key, String source) {
        Object v = map.get(key);
        if (!(v instanceof String) || ((String) v).trim().isEmpty()) {
            throw new ScenarioLoadException(source + ": '" + key + "' must be a non-empty string");
        }
        return (String) v;
    }

    private static String optionalString(Map<?, ?> map, String key, String source) {
        Object v = map.get(key);
        if (v == null) {
            return null;
        }
        if (!(v instanceof String)) {
            throw new ScenarioLoadException(source + ": '" + key + "' must be a string");
        }
        return (String) v;
    }

    private static int requireIntValue(Object value, String where) {
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Long) {
            long l = (Long) value;
            if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
                throw new ScenarioLoadException(where + ": number out of int range: " + l);
            }
            return (int) l;
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                throw new ScenarioLoadException(where + ": expected an integer, got '" + value + "'");
            }
        }
        throw new ScenarioLoadException(where + ": expected an integer, got "
                + (value == null ? "null" : value.getClass().getSimpleName()));
    }

    private static void requireNumber(Object value, String where) {
        if (!(value instanceof Number)) {
            throw new ScenarioLoadException(where + ": expected a number, got "
                    + (value == null ? "null" : value.getClass().getSimpleName()));
        }
    }
}
