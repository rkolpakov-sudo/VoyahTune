package ru.big.town.hil.scenario;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

public final class ScenarioArtifacts {
    private ScenarioArtifacts() {
    }

    public static void write(Path dir, String suiteName, List<ScenarioResult> results) throws IOException {
        Files.createDirectories(dir);
        int tests = results.size();
        int failures = 0;
        double totalTime = 0.0;
        for (ScenarioResult r : results) {
            if (!r.passed) {
                failures++;
            }
            totalTime += r.wallSeconds();
        }
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append(String.format(Locale.ROOT,
                "<testsuite name=\"%s\" tests=\"%d\" failures=\"%d\" errors=\"0\" skipped=\"0\" time=\"%.3f\" timestamp=\"%s\">\n",
                escape(suiteName), tests, failures, totalTime, escape(Instant.now().toString())));
        for (ScenarioResult r : results) {
            xml.append(String.format(Locale.ROOT,
                    "  <testcase classname=\"hil.scenarios\" name=\"%s\" time=\"%.3f\"",
                    escape(r.name), r.wallSeconds()));
            if (r.passed) {
                xml.append("/>\n");
            } else {
                xml.append(">\n");
                ScenarioResult.StepResult failed = r.firstFailure();
                String message = failed == null ? "scenario failed" : "step " + failed.index + ": " + failed.description;
                String detail = failureDetail(r);
                xml.append(String.format(Locale.ROOT, "    <failure message=\"%s\" type=\"ScenarioFailure\">%s</failure>\n",
                        escape(message), escape(detail)));
                xml.append("  </testcase>\n");
            }
        }
        xml.append("  <system-out>\n");
        for (ScenarioResult r : results) {
            xml.append(escape("=== " + r.name + " transactions ===")).append('\n');
            for (String line : r.logLines) {
                xml.append(escape(line)).append('\n');
            }
        }
        xml.append("  </system-out>\n");
        xml.append("</testsuite>\n");
        Files.write(dir.resolve(suiteName + ".xml"), xml.toString().getBytes(StandardCharsets.UTF_8));
        for (ScenarioResult r : results) {
            StringBuilder log = new StringBuilder();
            log.append("# scenario: ").append(r.name).append('\n');
            if (!r.description.isEmpty()) {
                log.append("# ").append(r.description).append('\n');
            }
            for (String line : r.logLines) {
                log.append(line).append('\n');
            }
            Files.write(dir.resolve(r.name + ".transactions.log"),
                    log.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    private static String failureDetail(ScenarioResult result) {
        StringBuilder sb = new StringBuilder();
        if (!result.description.isEmpty()) {
            sb.append(result.description).append('\n');
        }
        for (ScenarioResult.StepResult s : result.steps) {
            sb.append('[').append(s.ok ? "PASS" : "FAIL").append("] step ").append(s.index).append(": ")
                    .append(s.description);
            if (s.message != null) {
                sb.append(" -> ").append(s.message);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    private static String escape(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                default:
                    if (c == '\n' || c == '\t' || c == '\r' || c >= 0x20) {
                        sb.append(c);
                    }
                    break;
            }
        }
        return sb.toString();
    }
}
