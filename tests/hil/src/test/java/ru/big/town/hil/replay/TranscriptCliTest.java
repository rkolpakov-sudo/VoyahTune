package ru.big.town.hil.replay;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/** Тесты TranscriptCli: генерация REF, сверка, коды выхода 0/1/2 (SPEC L112). */
public class TranscriptCliTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private Path writeResource(String resource, String fileName) throws IOException {
        Path path = tmp.getRoot().toPath().resolve(fileName);
        Files.writeString(path, LogcatTxParserTest.resource(resource),
                StandardCharsets.UTF_8);
        return path;
    }

    @Test
    public void generateWritesCanonicalReference() throws IOException {
        Path trace = writeResource("/replay/sample.logcat", "gen.logcat");
        Path refOut = tmp.getRoot().toPath().resolve("gen.ref");
        int code = TranscriptCli.run(new String[] {
                "--trace", trace.toString(), "--out", refOut.toString()
        });
        assertEquals(0, code);
        List<String> written = Files.readAllLines(refOut, StandardCharsets.UTF_8);
        List<String> expected = List.of(
                LogcatTxParserTest.resource("/replay/sample.ref").split("\n"));
        assertEquals(expected, written);
    }

    @Test
    public void checkPassesAgainstMatchingReference() throws IOException {
        Path trace = writeResource("/replay/sample.logcat", "ok.logcat");
        Path ref = writeResource("/replay/sample.ref", "ok.ref");
        int code = TranscriptCli.run(new String[] {
                "--trace", trace.toString(), "--ref", ref.toString()
        });
        assertEquals(0, code);
    }

    @Test
    public void checkFailsOnDriftedReference() throws IOException {
        Path trace = writeResource("/replay/sample.logcat", "drift.logcat");
        Path ref = writeResource("/replay/sample.ref", "drift.ref");
        String drifted = LogcatTxParserTest.resource("/replay/sample.ref")
                .replace("TX58 LOW_BEAM=1 accepted", "TX58 LOW_BEAM=7 accepted");
        Files.writeString(ref, drifted, StandardCharsets.UTF_8);
        int code = TranscriptCli.run(new String[] {
                "--trace", trace.toString(), "--ref", ref.toString()
        });
        assertEquals(1, code);
    }

    @Test
    public void missingTraceIsUsageError() {
        assertEquals(2, TranscriptCli.run(new String[] {"--ref", "x.ref"}));
        assertEquals(2, TranscriptCli.run(new String[] {"--trace"}));
        assertEquals(2, TranscriptCli.run(new String[] {"--bogus"}));
    }

    @Test
    public void refAndOutTogetherRejected() throws IOException {
        Path trace = writeResource("/replay/sample.logcat", "both.logcat");
        assertEquals(2, TranscriptCli.run(new String[] {
                "--trace", trace.toString(), "--ref", "a.ref", "--out", "b.ref"
        }));
    }

    @Test
    public void unreadableTraceIsUsageError() {
        int code = TranscriptCli.run(new String[] {
                "--trace", tmp.getRoot().toPath().resolve("missing.logcat").toString()
        });
        assertEquals(2, code);
    }

    @Test
    public void helpIsOk() {
        assertEquals(0, TranscriptCli.run(new String[] {"--help"}));
    }
}
