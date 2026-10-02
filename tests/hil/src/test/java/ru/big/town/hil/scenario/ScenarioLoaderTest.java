package ru.big.town.hil.scenario;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import ru.big.town.hil.scenario.ScenarioLoader.ScenarioLoadException;

public class ScenarioLoaderTest {

    private static Scenario load(String yaml) {
        return ScenarioLoader.load(yaml, "test.yaml");
    }

    private static String expectFailure(String yaml) {
        try {
            load(yaml);
        } catch (ScenarioLoadException e) {
            return e.getMessage();
        }
        fail("expected ScenarioLoadException");
        return null;
    }

    @Test
    public void parsesFullSchema() {
        Scenario scenario = load(""
                + "name: full\n"
                + "description: d\n"
                + "config:\n"
                + "  write_mode: late\n"
                + "  late_ms: 100\n"
                + "steps:\n"
                + "  - sleep: 10\n"
                + "  - set_mode: ACK\n"
                + "  - action: {name: send_x, send: {state_name: HUM_VSP_FUNCTION_SW, value: 1}}\n"
                + "  - expect_txn: {code: 58, state_name: HUM_VSP_FUNCTION_SW, value: 1, after: send_x}\n"
                + "  - expect_echo: {state_name: HUM_VSP_FUNCTION_SW, value: 1, after: send_x}\n"
                + "  - assert_no_echo: {state_name: HUM_VSP_FUNCTION_SW, after: send_x}\n"
                + "  - assert_cache: {state_name: HUM_VSP_FUNCTION_SW, equals: 1}\n"
                + "  - assert_cache_absent: {state_name: ASC_MAINTAIN_SWITCH}\n"
                + "  - assert_app: {name: ui_state, equals: CONFIRMED}\n");
        assertEquals("full", scenario.name);
        assertEquals(9, scenario.steps.size());
        assertEquals("late", String.valueOf(scenario.config.get("write_mode")).toLowerCase());
        assertTrue(scenario.steps.get(2) instanceof Step.ActionStep);
        assertEquals(Step.ActionStep.Kind.SEND, ((Step.ActionStep) scenario.steps.get(2)).kind);
        assertTrue(scenario.steps.get(6) instanceof Step.AssertCacheStep);
    }

    @Test
    public void rejectsUnknownStepType() {
        String msg = expectFailure("name: x\nsteps:\n  - teleport: 1\n");
        assertTrue(msg, msg.contains("unknown step type 'teleport'"));
    }

    @Test
    public void rejectsUnknownStateName() {
        String msg = expectFailure(""
                + "name: x\n"
                + "steps:\n"
                + "  - action: {name: a, send: {state_name: NOPE_STATE, value: 1}}\n");
        assertTrue(msg, msg.contains("unknown state name 'NOPE_STATE'"));
    }

    @Test
    public void rejectsDanglingAfterReference() {
        String msg = expectFailure(""
                + "name: x\n"
                + "steps:\n"
                + "  - expect_txn: {code: 58, after: ghost}\n");
        assertTrue(msg, msg.contains("does not match an earlier step id"));
    }

    @Test
    public void rejectsUnknownConfigKey() {
        String msg = expectFailure("name: x\nconfig: {speeed: 1}\nsteps:\n  - sleep: 1\n");
        assertTrue(msg, msg.contains("unknown config key 'speeed'"));
    }

    @Test
    public void rejectsValueExpectationForNonWriteCode() {
        String msg = expectFailure("name: x\nsteps:\n  - expect_txn: {code: 2, value: 1}\n");
        assertTrue(msg, msg.contains("value is only supported for code 58"));
    }

    @Test
    public void rejectsUnsupportedWriteMode() {
        String msg = expectFailure("name: x\nconfig: {write_mode: YOLO}\nsteps:\n  - sleep: 1\n");
        assertTrue(msg, msg.contains("unknown write mode"));
    }
}
