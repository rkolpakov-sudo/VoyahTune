package ru.big.town.hil.replay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ru.big.town.hil.CanEmulatorCore;

public class TraceReplayTest {

    private static List<String> refLines() throws IOException {
        String ref = LogcatTxParserTest.resource("/replay/sample.ref");
        return Arrays.asList(ref.split("\n"));
    }

    private static String sampleLogcat() throws IOException {
        return LogcatTxParserTest.resource("/replay/sample.logcat");
    }

    @Test
    public void replayMatchesCommittedReference() throws IOException {
        List<String> actual = TraceReplay.run(sampleLogcat(), new CanEmulatorCore());
        List<String> expected = refLines();
        List<String> drift = TraceReplay.compare(expected, actual);
        assertTrue("transcript drifted from reference: " + drift, drift.isEmpty());
    }

    @Test
    public void changedWriteValueIsDetected() throws IOException {
        String mutated = sampleLogcat().replace(
                "TX58 accepted-unconfirmed [door-wake] LOW_BEAM=1",
                "TX58 accepted-unconfirmed [door-wake] LOW_BEAM=7");
        List<String> actual = TraceReplay.run(mutated, new CanEmulatorCore());
        List<String> drift = TraceReplay.compare(refLines(), actual);
        assertFalse("value change went unnoticed", drift.isEmpty());
        assertTrue("drift must point at the write line: " + drift,
                drift.get(0).contains("line 2"));
        assertTrue("drift must show both values: " + drift,
                drift.get(0).contains("LOW_BEAM=1") && drift.get(0).contains("LOW_BEAM=7"));
    }

    @Test
    public void droppedLineIsDetected() throws IOException {
        List<String> actual = TraceReplay.run(sampleLogcat(), new CanEmulatorCore());
        List<String> withoutLast = actual.subList(0, actual.size() - 1);
        List<String> drift = TraceReplay.compare(refLines(), withoutLast);
        assertFalse("missing line went unnoticed", drift.isEmpty());
    }

    @Test
    public void readSeesValueWrittenEarlierInTrace() {
        String logcat = "10-04 09:15:01.640  2841  2863 I $$$ OemVehicleState $$$: "
                + "TX58 accepted-unconfirmed [t] LOW_BEAM=1\n"
                + "10-04 09:15:01.840  2841  2863 I $$$ OemVehicleState $$$: "
                + "TX57 getVehicleState LOW_BEAM=1\n";
        List<String> transcript = TraceReplay.run(logcat, new CanEmulatorCore());
        assertEquals(List.of(
                "TX58 LOW_BEAM=1 accepted emu=1",
                "TX57 LOW_BEAM trace=1 emu=1"), transcript);
    }

    @Test
    public void unsupportedFormatStaysInTranscript() {
        String logcat = "10-04 09:15:01.640  2841  2863 I $$$ OemVehicleState $$$: "
                + "TX58 totally-new-line\n";
        List<String> transcript = TraceReplay.run(logcat, new CanEmulatorCore());
        assertEquals(List.of("UNSUPPORTED TX58 totally-new-line"), transcript);
    }

    @Test
    public void compareReportsLineNumbers() {
        List<String> drift = TraceReplay.compare(
                List.of("a", "b", "c"),
                List.of("a", "X", "c"));
        assertEquals(1, drift.size());
        assertTrue(drift.get(0).contains("line 2"));
        assertTrue(drift.get(0).contains("expected <b>"));
        assertTrue(drift.get(0).contains("actual <X>"));
    }
}
