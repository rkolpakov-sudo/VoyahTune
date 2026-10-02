package ru.big.town.hil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class CanEmulatorCoreTest {

    private static final int AVAS_ID = 665;

    private static List<Object> ints(int... values) {
        List<Object> list = new ArrayList<>();
        for (int v : values) {
            list.add(v);
        }
        return list;
    }

    private static final class Recorder implements CallbackSink {
        final List<Integer> codes = new ArrayList<>();
        final List<List<Object>> args = new ArrayList<>();

        @Override
        public void deliver(int cbCode, List<Object> args) {
            this.codes.add(cbCode);
            this.args.add(new ArrayList<>(args));
        }
    }

    private static CanEmulatorCore ackedCore(Recorder rec) {
        CanEmulatorCore core = new CanEmulatorCore();
        core.setSink(rec);
        return core;
    }

    @Test
    public void ackWriteRepliesOkEchoesAndCaches() {
        Recorder rec = new Recorder();
        CanEmulatorCore core = ackedCore(rec);

        TxResult result = core.transact(TxCode.SET_STATE, ints(1, 0, AVAS_ID, 0));

        assertTrue(result.send);
        assertTrue(result.replyArgs.isEmpty());
        assertEquals(Integer.valueOf(0), core.cacheValue(AVAS_ID));
        assertEquals(1, rec.codes.size());
        assertEquals(Integer.valueOf(TxCode.CB_VEHICLE_STATE), rec.codes.get(0));
        assertEquals(ints(1, 0, AVAS_ID, 0), rec.args.get(0));
        assertEquals(1, core.log().count(TxLogEntry.Dir.CLIENT_TO_SVC, TxCode.SET_STATE));
        assertEquals(1, core.log().count(TxLogEntry.Dir.SVC_TO_CLIENT, TxCode.SET_STATE));
        assertEquals(1, core.log().count(TxLogEntry.Dir.SVC_TO_APP, TxCode.CB_VEHICLE_STATE));
    }

    @Test
    public void silentWriteRepliesOkWithoutApplying() {
        Recorder rec = new Recorder();
        CanEmulatorCore core = ackedCore(rec);
        core.setWriteMode(WriteMode.SILENT);

        TxResult result = core.transact(TxCode.SET_STATE, ints(1, 0, AVAS_ID, 0));

        assertTrue(result.send);
        assertTrue(result.replyArgs.isEmpty());
        assertNull(core.cacheValue(AVAS_ID));
        assertTrue(rec.codes.isEmpty());
    }

    @Test
    public void lateWriteDelaysReplyAndThenApplies() {
        Recorder rec = new Recorder();
        CanEmulatorCore core = ackedCore(rec);
        core.setWriteMode(WriteMode.LATE);
        core.config().lateMillis = 250L;

        long t0 = System.nanoTime();
        TxResult result = core.transact(TxCode.SET_STATE, ints(1, 0, AVAS_ID, 0));
        long elapsedMs = (System.nanoTime() - t0) / 1_000_000L;

        assertTrue(result.send);
        assertTrue("expected late reply, took " + elapsedMs + "ms", elapsedMs >= 200L);
        assertEquals(Integer.valueOf(0), core.cacheValue(AVAS_ID));
        assertEquals(1, rec.codes.size());
        assertEquals(ints(1, 0, AVAS_ID, 0), rec.args.get(0));
    }

    @Test
    public void conflictingWriteRepliesOkButReportsDifferentValue() {
        Recorder rec = new Recorder();
        CanEmulatorCore core = ackedCore(rec);
        core.setWriteMode(WriteMode.CONFLICTING);
        core.config().conflictValue = 1;

        TxResult result = core.transact(TxCode.SET_STATE, ints(1, 0, AVAS_ID, 0));

        assertTrue(result.send);
        assertTrue(result.replyArgs.isEmpty());
        assertEquals(Integer.valueOf(1), core.cacheValue(AVAS_ID));
        assertEquals(1, rec.codes.size());
        assertEquals(ints(1, 0, AVAS_ID, 1), rec.args.get(0));
    }

    @Test
    public void readReturnsCacheAndConfiguredMissingValue() {
        Recorder rec = new Recorder();
        CanEmulatorCore core = ackedCore(rec);
        core.transact(TxCode.SET_STATE, ints(1, 0, AVAS_ID, 0));

        TxResult hit = core.transact(TxCode.GET_STATE, ints(1, 0, AVAS_ID));
        assertEquals(List.of(0), hit.replyArgs);

        EmuConfig cfg = new EmuConfig();
        cfg.missingValue = 42;
        CanEmulatorCore other = new CanEmulatorCore(cfg);
        TxResult miss = other.transact(TxCode.GET_STATE, ints(1, 0, 999));
        assertEquals(List.of(42), miss.replyArgs);
    }

    @Test
    public void snapshotQueryRepliesEmptyAndPushesEveryCachedState() {
        Recorder rec = new Recorder();
        CanEmulatorCore core = ackedCore(rec);
        core.transact(TxCode.SET_STATE, ints(1, 0, AVAS_ID, 0));
        core.transact(TxCode.SET_STATE, ints(1, 0, 711, 2));
        rec.codes.clear();
        rec.args.clear();

        TxResult result = core.transact(TxCode.SNAPSHOT_QUERY, new ArrayList<>());

        assertTrue(result.send);
        assertTrue(result.replyArgs.isEmpty());
        assertEquals(2, rec.codes.size());
        Set<Integer> pushedIds = new HashSet<>();
        for (List<Object> args : rec.args) {
            assertEquals(ints(1, 0, (Integer) args.get(2), (Integer) args.get(3)), args);
            pushedIds.add((Integer) args.get(2));
        }
        assertTrue(pushedIds.contains(AVAS_ID));
        assertTrue(pushedIds.contains(711));
    }

    @Test
    public void bundleWriteAppliesAllEntriesAndEchoes() {
        Recorder rec = new Recorder();
        CanEmulatorCore core = ackedCore(rec);
        Map<String, Integer> bundle = new HashMap<>();
        bundle.put("HUM_VSP_FUNCTION_SW", 0);
        bundle.put("ASC_MAINTAIN_SWITCH", 2);

        TxResult result = core.transact(TxCode.SET_BUNDLE, withBundle(bundle));

        assertTrue(result.send);
        assertEquals(List.of(0), result.replyArgs);
        assertEquals(Integer.valueOf(0), core.cacheValue(AVAS_ID));
        assertEquals(Integer.valueOf(2), core.cacheValue(711));
        assertEquals(2, rec.codes.size());
    }

    @Test
    public void bundleWithUnknownNameFailsWithoutPartialApply() {
        Recorder rec = new Recorder();
        CanEmulatorCore core = ackedCore(rec);
        Map<String, Integer> bundle = new HashMap<>();
        bundle.put("HUM_VSP_FUNCTION_SW", 0);
        bundle.put("NOT_A_REAL_STATE", 5);

        TxResult result = core.transact(TxCode.SET_BUNDLE, withBundle(bundle));

        assertTrue(result.send);
        assertEquals(List.of(1), result.replyArgs);
        assertNull(core.cacheValue(AVAS_ID));
        assertTrue(rec.codes.isEmpty());
    }

    @Test
    public void doorSeedAndEvent() {
        Recorder rec = new Recorder();
        CanEmulatorCore core = ackedCore(rec);

        TxResult seed = core.transact(TxCode.DOOR_STATUS, new ArrayList<>());
        assertEquals(ints(1, 0, 0), seed.replyArgs);

        core.setDoor(1);
        assertEquals(1, core.doorValue());
        assertEquals(1, rec.codes.size());
        assertEquals(Integer.valueOf(TxCode.CB_DOOR), rec.codes.get(0));
        assertEquals(ints(1, 0, 1), rec.args.get(0));

        TxResult again = core.transact(TxCode.DOOR_STATUS, new ArrayList<>());
        assertEquals(ints(1, 0, 1), again.replyArgs);
    }

    @Test
    public void gearFuelAndSpeedQueries() {
        Recorder rec = new Recorder();
        CanEmulatorCore core = ackedCore(rec);

        TxResult gear0 = core.transact(TxCode.GEAR_STATUS, new ArrayList<>());
        assertEquals(ints(1, 0, 0), gear0.replyArgs);
        core.setGear(1, 4);
        TxResult gear1 = core.transact(TxCode.GEAR_STATUS, new ArrayList<>());
        assertEquals(ints(1, 1, 4), gear1.replyArgs);
        assertEquals(1, rec.codes.size());
        assertEquals(Integer.valueOf(TxCode.CB_GEAR), rec.codes.get(0));

        TxResult fuel = core.transact(TxCode.FUEL_LEVEL, new ArrayList<>());
        assertEquals(8, fuel.replyArgs.size());
        assertEquals(1, fuel.replyArgs.get(0));

        core.setSpeed(88);
        TxResult speed = core.transact(TxCode.VEHICLE_SPEED, new ArrayList<>());
        assertEquals(List.of(88), speed.replyArgs);
    }

    @Test
    public void malformedWriteIsRejected() {
        Recorder rec = new Recorder();
        CanEmulatorCore core = ackedCore(rec);

        TxResult shortArgs = core.transact(TxCode.SET_STATE, ints(1, 0));
        assertTrue(!shortArgs.send);
        TxResult badType = core.transact(TxCode.SET_STATE, ints(1, 0, AVAS_ID));
        assertTrue(!badType.send);
        List<Object> notAnInt = new ArrayList<>();
        notAnInt.add(1);
        notAnInt.add(0);
        notAnInt.add(AVAS_ID);
        notAnInt.add("zero");
        TxResult badValue = core.transact(TxCode.SET_STATE, notAnInt);
        assertTrue(!badValue.send);
        assertNull(core.cacheValue(AVAS_ID));
        assertTrue(rec.codes.isEmpty());
    }

    @Test
    public void callbackRegistrationTracksIdentity() {
        Recorder rec = new Recorder();
        CanEmulatorCore core = ackedCore(rec);
        Object binder = new Object();

        TxResult add = core.transact(TxCode.ADD_CALLBACK, List.of(binder));
        assertTrue(add.send);
        assertEquals(List.of(1), add.replyArgs);
        assertEquals(1, core.registeredCallbacks());

        TxResult remove = core.transact(TxCode.REMOVE_CALLBACK, List.of(binder));
        assertTrue(remove.send);
        assertEquals(List.of(1), remove.replyArgs);
        assertEquals(0, core.registeredCallbacks());
    }

    @Test
    public void txLogRecordsTimestampedFlow() throws Exception {
        Recorder rec = new Recorder();
        CanEmulatorCore core = ackedCore(rec);
        core.transact(TxCode.SET_STATE, ints(1, 0, AVAS_ID, 0));
        Thread.sleep(30);
        TxResult read = core.transact(TxCode.GET_STATE, ints(1, 0, AVAS_ID));
        assertEquals(List.of(0), read.replyArgs);

        List<TxLogEntry> entries = core.log().entries();
        assertEquals(5, entries.size());
        assertTrue(entries.get(3).tNanos > entries.get(0).tNanos);
        assertTrue(core.log().ageMillis(entries.get(3)) >= 30L);
        assertNotNull(core.log().firstClientWrite(AVAS_ID, 0));
        assertNotNull(core.log().firstCallbackState(AVAS_ID, 0, core.log().startNanos()));
        assertTrue(core.log().lines().get(0).contains("TX58"));
    }

    private static List<Object> withBundle(Map<String, Integer> bundle) {
        List<Object> args = ints(0, 1);
        args.add(bundle);
        return args;
    }
}
