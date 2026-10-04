package ru.big.town.hil.replay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Test;

public class LogcatTxParserTest {

    static String resource(String name) throws IOException {
        try (InputStream in = LogcatTxParserTest.class.getResourceAsStream(name)) {
            if (in == null) {
                throw new IOException("resource not found: " + name);
            }
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            byte[] chunk = new byte[8192];
            int n;
            while ((n = in.read(chunk)) != -1) {
                buf.write(chunk, 0, n);
            }
            return new String(buf.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    private static List<TxRecord> sample() throws IOException {
        String text = resource("/replay/sample.logcat");
        return LogcatTxParser.parse(List.of(text.split("\n", -1)));
    }

    @Test
    public void extractsAllTxLinesAndSkipsNoise() throws IOException {
        List<TxRecord> records = sample();
        assertEquals(15, records.size());
        for (TxRecord r : records) {
            if (r.op == TxRecord.Op.UNSUPPORTED) {
                assertEquals("TX58 new-format-something unexpected", r.raw);
            } else {
                assertTrue("unexpected empty raw: " + r.raw, r.raw.startsWith("TX"));
            }
        }
    }

    @Test
    public void ignoresNonTxMessagesAndForeignLines() throws IOException {
        for (TxRecord r : sample()) {
            assertTrue("noise leaked: " + r.raw,
                    !r.raw.contains("demand bindService")
                            && !r.raw.contains("routing frame"));
        }
    }

    @Test
    public void parsesAcceptedWrite() throws IOException {
        TxRecord r = sample().get(1);
        assertEquals(TxRecord.Op.TX58, r.op);
        assertEquals(TxRecord.Outcome.OK, r.outcome);
        assertEquals("door-wake", r.label);
        assertEquals("LOW_BEAM", r.key);
        assertEquals(Integer.valueOf(1), r.value);
    }

    @Test
    public void parsesBundleKeepingOrder() throws IOException {
        TxRecord r = sample().get(3);
        assertEquals(TxRecord.Op.TX77, r.op);
        assertEquals(TxRecord.Outcome.OK, r.outcome);
        assertEquals(2, r.states.size());
        assertEquals(Integer.valueOf(1), r.states.get("LOW_BEAM"));
        assertEquals(Integer.valueOf(1), r.states.get("AUTO_LAMP_SWITCH"));
        assertEquals("LOW_BEAM", r.states.keySet().iterator().next());
    }

    @Test
    public void parsesGearRead() throws IOException {
        TxRecord r = sample().get(7);
        assertEquals(TxRecord.Op.TX6, r.op);
        assertEquals(TxRecord.Outcome.OK, r.outcome);
        assertEquals(Integer.valueOf(4), r.gearOrdinal);
        assertEquals(Integer.valueOf(8), r.value);
    }

    @Test
    public void parsesHeadlightWriteWithoutLabel() throws IOException {
        TxRecord r = sample().get(5);
        assertEquals(TxRecord.Op.TX58, r.op);
        assertEquals(TxRecord.Outcome.OK, r.outcome);
        assertEquals(null, r.label);
        assertEquals("BCM_RSM_lightSWReason", r.key);
        assertEquals(Integer.valueOf(1), r.value);
    }

    @Test
    public void parsesReturnedCode() throws IOException {
        TxRecord r = sample().get(13);
        assertEquals(TxRecord.Op.TX77, r.op);
        assertEquals(TxRecord.Outcome.RETURNED, r.outcome);
        assertEquals(Integer.valueOf(2), r.returnCode);
    }

    @Test
    public void flagsUnknownTxFormatAsUnsupported() throws IOException {
        TxRecord r = sample().get(14);
        assertEquals(TxRecord.Op.UNSUPPORTED, r.op);
        assertEquals("TX58 new-format-something unexpected", r.raw);
    }

    @Test
    public void toleratesCrlfLineEndings() {
        List<TxRecord> records = LogcatTxParser.parse(List.of(
                "10-04 09:15:01.310  2841  2863 I $$$ OemVehicleState $$$: "
                        + "TX57 getVehicleState LOW_BEAM=0\r"));
        assertEquals(1, records.size());
        assertEquals("LOW_BEAM", records.get(0).key);
    }

    @Test
    public void skipsNonThreadtimeLines() {
        List<TxRecord> records = LogcatTxParser.parse(List.of(
                "not a logcat line TX57 getVehicleState LOW_BEAM=0"));
        assertEquals(0, records.size());
    }

    @Test
    public void ignoresTxLinesFromForeignTags() {
        // TX-подобный шум чужих приложений не должен попадать в транскрипт:
        // иначе эталон «шумит» и паритет ложно регрессирует.
        List<TxRecord> records = LogcatTxParser.parse(List.of(
                "10-04 09:15:01.310  2841  2863 I SomeOtherApp: "
                        + "TX57 getVehicleState LOW_BEAM=0",
                "10-04 09:15:01.400  2841  2863 D RandomTag: "
                        + "TX99 whatever-formatted here"));
        assertEquals(0, records.size());
    }

    @Test
    public void keepsUnknownTxNumberFromKnownTagAsUnsupported() {
        List<TxRecord> records = LogcatTxParser.parse(List.of(
                "10-04 09:15:01.310  2841  2863 I $$$ OemVehicleState $$$: "
                        + "TX99 whatever-formatted here"));
        assertEquals(1, records.size());
        assertEquals(TxRecord.Op.UNSUPPORTED, records.get(0).op);
        assertEquals("TX99 whatever-formatted here", records.get(0).raw);
    }

    @Test
    public void hugeNumbersBecomeUnsupportedInsteadOfCrashing() {
        // переполнение int не должно ронять разбор всей трассы
        List<TxRecord> records = LogcatTxParser.parse(List.of(
                "10-04 09:15:01.310  2841  2863 I $$$ OemVehicleState $$$: "
                        + "TX58 accepted-unconfirmed [t] LOW_BEAM=99999999999999999999",
                "10-04 09:15:01.400  2841  2863 I $$$ OemVehicleState $$$: "
                        + "TX99999999999999999999 getVehicleState LOW_BEAM=0",
                "10-04 09:15:01.500  2841  2863 I $$$ OemVehicleState $$$: "
                        + "TX57 getVehicleState LOW_BEAM=1"));
        assertEquals(3, records.size());
        assertEquals(TxRecord.Op.UNSUPPORTED, records.get(0).op);
        assertEquals(TxRecord.Op.UNSUPPORTED, records.get(1).op);
        assertEquals(TxRecord.Op.TX57, records.get(2).op);
    }

    @Test
    public void malformedBundleIsUnsupportedNotPartial() {
        // нечисловое значение бандла: реплей с частично разобранными
        // состояниями скрыл бы потерю данных — только unsupported
        List<TxRecord> records = LogcatTxParser.parse(List.of(
                "10-04 09:15:01.310  2841  2863 I $$$ OemVehicleState $$$: "
                        + "TX77 accepted-unconfirmed [t] states=Bundle[{LOW_BEAM=oops}]",
                "10-04 09:15:01.400  2841  2863 I $$$ OemVehicleState $$$: "
                        + "TX77 accepted-unconfirmed [t] states=Bundle[{LOW_BEAM=9}]"));
        assertEquals(2, records.size());
        assertEquals(TxRecord.Op.UNSUPPORTED, records.get(0).op);
        assertEquals(TxRecord.Op.TX77, records.get(1).op);
        assertEquals(1, records.get(1).states.size());
    }
}
