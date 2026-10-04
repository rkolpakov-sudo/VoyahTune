package ru.big.town.anative;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.nio.file.Files;
import java.nio.file.Path;

import ru.big.town.hil.CanEmulatorCore;
import ru.big.town.hil.scenario.Scenario;
import ru.big.town.hil.scenario.ScenarioLoader;
import ru.big.town.hil.scenario.ScenarioResult;
import ru.big.town.hil.scenario.ScenarioRunner;

/**
 * IMP-10a bridge: runs the SPEC L104 avas-wake YAML scenario through the real
 * native stack - OemVehicleStateTransport (schema resolve over PathClassLoader,
 * bindService, TX58) into FakeCanBusBinder/CanEmulatorCore, with the CB36 echo
 * delivered back through the real CanBusEventHub router.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class NativeHilScenarioTest {

    @Test
    public void avasWakeThroughRealNativeStack() {
        Context context = RuntimeEnvironment.getApplication();
        Scenario scenario = ScenarioLoader.load(locateScenarios().resolve("avas-wake.yaml"));
        CanEmulatorCore core = new CanEmulatorCore(ScenarioRunner.buildConfig(scenario.config));
        RobolectricCanBridge bridge = RobolectricCanBridge.create(context, core);
        ScenarioResult result = new ScenarioRunner().run(scenario, bridge, core);

        StringBuilder details = new StringBuilder();
        for (ScenarioResult.StepResult step : result.steps) {
            details.append('\n').append(step.ok ? "ok   " : "FAIL ")
                    .append(step.description)
                    .append(step.message == null ? "" : " -> " + step.message);
        }
        assertTrue("scenario " + result.name + " failed:" + details, result.passed);

        bridge.pump();
        assertEquals("real hub must route CB36 echo to app subscriber",
                Integer.valueOf(665), bridge.appState("routed_id"));
        assertEquals("routed echo carries the applied value",
                Integer.valueOf(1), bridge.appState("routed_value"));
    }

    static Path locateScenarios() {
        String prop = System.getProperty("hil.scenarios.dir");
        if (prop != null && !prop.isEmpty()) {
            return Path.of(prop);
        }
        Path dir = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        while (dir != null) {
            Path nested = dir.resolve("tests").resolve("hil").resolve("scenarios");
            if (Files.isDirectory(nested)) {
                return nested;
            }
            Path plain = dir.resolve("scenarios");
            if (Files.isDirectory(plain)) {
                return plain;
            }
            dir = dir.getParent();
        }
        throw new IllegalStateException(
                "cannot locate tests/hil/scenarios from " + System.getProperty("user.dir"));
    }
}
