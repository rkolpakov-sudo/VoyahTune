package ru.big.town.restoremode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.SharedPreferences;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class HookGenerationRegistryTest {
    private static final long T0 = 1_760_000_000_000L;
    private static final String OK_PAYLOAD = "v=1;loader=running;pid=321"
            + ";vd-bypass=active:100"
            + ";steering-wheel=active:101"
            + ";launcher-dock=active:103"
            + ";multi-display=active:102"
            + ";apollo-tech=active:105"
            + ";keyboard-en=active:106"
            + ";keyboard-ru=active:106";

    private SharedPreferences prefs(Map<String, Object> values) {
        final Object[] editor = new Object[1];
        editor[0] = Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{SharedPreferences.Editor.class},
                (p, m, a) -> {
                    if (m.getName().startsWith("put")) {
                        values.put((String) a[0], a[1]);
                        return editor[0];
                    }
                    if (m.getName().equals("commit")) {
                        return true;
                    }
                    return null;
                });
        return (SharedPreferences) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{SharedPreferences.class},
                (p, m, a) -> {
                    if (m.getName().equals("edit")) {
                        return editor[0];
                    }
                    if (m.getName().startsWith("get") && a != null && a.length == 2) {
                        return values.getOrDefault((String) a[0], a[1]);
                    }
                    return null;
                });
    }

    @Test
    public void allActiveReconcilesToOkWithManifestAndGeneration() {
        Map<String, Object> data = new HashMap<>();
        HookGenerationRegistry.Report report =
                HookGenerationRegistry.reconcile(prefs(data), OK_PAYLOAD, 5, T0);
        assertEquals(HookGenerationRegistry.OVERALL_OK, report.overall);
        assertEquals("boot=5 pid=321", report.generation);
        assertEquals(T0, report.firstSeenAt);
        String described = report.describe();
        assertTrue(described, described.contains("Сверка: OK"));
        assertTrue(described, described.contains("манифест 7 агентов v3.22.0"));
        assertTrue(described, described.contains("Поколение: boot=5 pid=321"));
    }

    @Test
    public void waitingHookIsPendingWithinGraceThenDegrades() {
        Map<String, Object> data = new HashMap<>();
        String waiting = OK_PAYLOAD.replace("steering-wheel=active:101",
                "steering-wheel=waiting:0");
        HookGenerationRegistry.Report first =
                HookGenerationRegistry.reconcile(prefs(data), waiting, 5, T0);
        assertEquals(HookGenerationRegistry.OVERALL_PENDING, first.overall);
        assertTrue(first.describe(), first.describe().contains("ожидание — Кнопки руля"));

        HookGenerationRegistry.Report later = HookGenerationRegistry.reconcile(
                prefs(data), waiting, 5, T0 + HookGenerationRegistry.GRACE_MS + 1);
        assertEquals(HookGenerationRegistry.OVERALL_DEGRADED, later.overall);
        assertTrue(later.describe(), later.describe().contains("деградация — Кнопки руля"));
        assertTrue(later.firstSeenAt <= T0 + 1);
    }

    @Test
    public void failedHookDegradesImmediately() {
        Map<String, Object> data = new HashMap<>();
        String failed = OK_PAYLOAD.replace("launcher-dock=active:103",
                "launcher-dock=failed:0");
        HookGenerationRegistry.Report report =
                HookGenerationRegistry.reconcile(prefs(data), failed, 5, T0);
        assertEquals(HookGenerationRegistry.OVERALL_DEGRADED, report.overall);
        assertTrue(report.describe(), report.describe().contains("Док лаунчера"));
    }

    @Test
    public void stoppedLoaderIsLoaderDown() {
        Map<String, Object> data = new HashMap<>();
        String stopped = "v=1;loader=stopped;pid=0"
                + ";vd-bypass=disabled:0"
                + ";steering-wheel=disabled:0"
                + ";launcher-dock=disabled:0"
                + ";multi-display=disabled:0"
                + ";apollo-tech=disabled:0"
                + ";keyboard-en=disabled:0"
                + ";keyboard-ru=disabled:0";
        HookGenerationRegistry.Report report =
                HookGenerationRegistry.reconcile(prefs(data), stopped, 5, T0);
        assertEquals(HookGenerationRegistry.OVERALL_LOADER_DOWN, report.overall);
        assertTrue(report.describe(), report.describe().contains("loader остановлен"));
    }

    @Test
    public void missingPayloadIsNoData() {
        Map<String, Object> data = new HashMap<>();
        HookGenerationRegistry.Report report =
                HookGenerationRegistry.reconcile(prefs(data), null, 5, T0);
        assertEquals(HookGenerationRegistry.OVERALL_NO_DATA, report.overall);
        assertTrue(report.describe(), report.describe().contains("данных ещё нет"));
    }

    @Test
    public void generationChangeResetsFirstSeenAndJournals() {
        Map<String, Object> data = new HashMap<>();
        SharedPreferences prefs = prefs(data);
        HookGenerationRegistry.reconcile(prefs, OK_PAYLOAD, 5, T0);
        String reloaded = OK_PAYLOAD.replace("pid=321", "pid=777");
        HookGenerationRegistry.Report report = HookGenerationRegistry.reconcile(prefs, reloaded, 5, T0 + 10_000);
        assertEquals(T0 + 10_000, report.firstSeenAt);
        assertTrue(HookGenerationRegistry.journalText(prefs).contains("|gen"));
    }

    @Test
    public void bootCountChangeStartsNewGeneration() {
        Map<String, Object> data = new HashMap<>();
        SharedPreferences prefs = prefs(data);
        HookGenerationRegistry.reconcile(prefs, OK_PAYLOAD, 5, T0);
        HookGenerationRegistry.Report report =
                HookGenerationRegistry.reconcile(prefs, OK_PAYLOAD, 6, T0 + 10_000);
        assertEquals("boot=6 pid=321", report.generation);
        assertEquals(T0 + 10_000, report.firstSeenAt);
        assertTrue(HookGenerationRegistry.journalText(prefs).contains("gen "));
    }

    @Test
    public void repeatedReconcileIsIdempotent() {
        Map<String, Object> data = new HashMap<>();
        SharedPreferences prefs = prefs(data);
        HookGenerationRegistry.reconcile(prefs, OK_PAYLOAD, 5, T0);
        String journalAfterFirst = HookGenerationRegistry.journalText(prefs);
        HookGenerationRegistry.reconcile(prefs, OK_PAYLOAD, 5, T0 + 5_000);
        HookGenerationRegistry.reconcile(prefs, OK_PAYLOAD, 5, T0 + 9_000);
        assertEquals(journalAfterFirst, HookGenerationRegistry.journalText(prefs));
        assertEquals(T0, data.get(HookGenerationRegistry.KEY_FIRST_SEEN));
    }

    @Test
    public void journalRingIsBounded() {
        Map<String, Object> data = new HashMap<>();
        SharedPreferences prefs = prefs(data);
        String failed = OK_PAYLOAD.replace("steering-wheel=active:101",
                "steering-wheel=failed:0");
        for (int i = 0; i < 40; i++) {
            String payload = (i % 2 == 0) ? failed : OK_PAYLOAD;
            HookGenerationRegistry.reconcile(prefs, payload, 5, T0 + i * 1_000L);
        }
        String journal = HookGenerationRegistry.journalText(prefs);
        String[] lines = journal.split("\n");
        assertTrue("journal lines: " + lines.length,
                lines.length <= HookGenerationRegistry.JOURNAL_LIMIT);
        assertTrue(lines.length >= HookGenerationRegistry.JOURNAL_LIMIT - 1);
    }

    @Test
    public void disabledHooksAreNotDegradation() {
        Map<String, Object> data = new HashMap<>();
        String disabled = OK_PAYLOAD
                .replace("vd-bypass=active:100", "vd-bypass=disabled:0")
                .replace("steering-wheel=active:101", "steering-wheel=disabled:0")
                .replace("launcher-dock=active:103", "launcher-dock=disabled:0")
                .replace("multi-display=active:102", "multi-display=disabled:0")
                .replace("apollo-tech=active:105", "apollo-tech=disabled:0")
                .replace("keyboard-en=active:106", "keyboard-en=disabled:0")
                .replace("keyboard-ru=active:106", "keyboard-ru=disabled:0");
        HookGenerationRegistry.Report report =
                HookGenerationRegistry.reconcile(prefs(data), disabled, 5, T0);
        assertEquals(HookGenerationRegistry.OVERALL_OK, report.overall);
    }

    @Test
    public void journalEntriesCarryOverallAndDetails() {
        Map<String, Object> data = new HashMap<>();
        SharedPreferences prefs = prefs(data);
        String failed = OK_PAYLOAD.replace("steering-wheel=active:101",
                "steering-wheel=failed:0");
        HookGenerationRegistry.reconcile(prefs, failed, 5, T0);
        String journal = HookGenerationRegistry.journalText(prefs);
        assertTrue(journal, journal.contains("|DEGRADED|"));
        assertTrue(journal, journal.contains("Кнопки руля"));
        HookGenerationRegistry.reconcile(prefs, OK_PAYLOAD, 5, T0 + 1_000);
        journal = HookGenerationRegistry.journalText(prefs);
        assertTrue(journal, journal.contains("|OK|"));
    }
}
