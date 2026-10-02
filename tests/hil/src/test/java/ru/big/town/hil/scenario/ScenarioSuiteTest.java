package ru.big.town.hil.scenario;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

public class ScenarioSuiteTest {

    @Test
    public void allScenariosPass() throws Exception {
        Path dir = locateScenarios();
        List<Path> files;
        try (Stream<Path> stream = Files.list(dir)) {
            files = stream
                    .filter(p -> {
                        String n = p.getFileName().toString();
                        return n.endsWith(".yaml") || n.endsWith(".yml");
                    })
                    .sorted()
                    .collect(Collectors.toList());
        }
        assertTrue("expected >= 10 scenarios in " + dir + ", found " + files.size(), files.size() >= 10);

        List<ScenarioResult> results = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        for (Path file : files) {
            try {
                Scenario scenario = ScenarioLoader.load(file);
                results.add(new ScenarioRunner().run(scenario, new JvmHarness()));
            } catch (RuntimeException e) {
                errors.add(file.getFileName() + ": " + e.getMessage());
            }
        }

        Path resultsDir = locateResults();
        if (!results.isEmpty()) {
            ScenarioArtifacts.write(resultsDir, "hil-scenarios", results);
        }

        StringBuilder report = new StringBuilder();
        for (String error : errors) {
            report.append("LOAD ERROR ").append(error).append('\n');
        }
        for (ScenarioResult result : results) {
            if (!result.passed) {
                ScenarioResult.StepResult failed = result.firstFailure();
                report.append("FAILED ").append(result.name).append(": step ")
                        .append(failed == null ? -1 : failed.index).append(' ')
                        .append(failed == null ? "?" : failed.description)
                        .append(" -> ").append(failed == null ? "?" : failed.message).append('\n');
            }
        }
        if (report.length() > 0) {
            report.append("artifacts: ").append(resultsDir.toAbsolutePath()).append('\n');
            for (ScenarioResult result : results) {
                if (!result.passed) {
                    report.append("--- ").append(result.name).append(" transaction log ---\n");
                    for (String line : result.logLines) {
                        report.append(line).append('\n');
                    }
                }
            }
            fail(report.toString());
        }
    }

    static Path locateScenarios() {
        String prop = System.getProperty("hil.scenarios.dir");
        if (prop != null && !prop.isEmpty()) {
            return Path.of(prop);
        }
        Path dir = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        for (int i = 0; i < 8 && dir != null; i++) {
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
        throw new IllegalStateException("cannot locate tests/hil/scenarios from " + System.getProperty("user.dir"));
    }

    static Path locateResults() {
        String prop = System.getProperty("hil.results.dir");
        if (prop != null && !prop.isEmpty()) {
            return Path.of(prop);
        }
        return Path.of(System.getProperty("user.dir")).resolve("build").resolve("hil-results");
    }
}
