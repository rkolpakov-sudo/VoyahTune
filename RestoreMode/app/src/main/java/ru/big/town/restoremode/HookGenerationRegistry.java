package ru.big.town.restoremode;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * IMP-08 (SPEC L50): реестр поколений хуков и boot-reconciliation «ожидание vs факт».
 *
 * Манифест агентов — HookStatusContract.HOOK_IDS (7 агентов) + версия payload.
 * Поколение — Settings.Global.BOOT_COUNT | pid лоадера из payload; таймстарт — момент
 * первого наблюдения поколения (firstSeenAt). Вердикт на хука сравнивает манифест (ожидание)
 * с payload (факт): grace-окно GRACE_MS после таймстарта, затем waiting/injecting = деградация.
 *
 * Побочные эффекты только при изменении (смена поколения или overall) — повторный вызов с
 * теми же входами ничего не пишет: реконсиляция идемпотентна и переживает restart процесса.
 */
final class HookGenerationRegistry {
    static final String PREFERENCES_NAME = "HookGeneration";
    static final String KEY_GENERATION = "generation";
    static final String KEY_FIRST_SEEN = "firstSeenAt";
    static final String KEY_LAST_OVERALL = "lastOverall";
    static final String KEY_JOURNAL = "journal";
    static final long GRACE_MS = 60_000L;
    static final int JOURNAL_LIMIT = 30;
    static final String MANIFEST_VERSION = "3.22.0";

    static final String OVERALL_NO_DATA = "NO_DATA";
    static final String OVERALL_LOADER_DOWN = "LOADER_DOWN";
    static final String OVERALL_PENDING = "PENDING";
    static final String OVERALL_DEGRADED = "DEGRADED";
    static final String OVERALL_OK = "OK";

    private HookGenerationRegistry() {
    }

    static final class Report {
        final String overall;
        final String generation;
        final long firstSeenAt;
        final List<String> degraded;
        final List<String> pending;

        Report(String overall, String generation, long firstSeenAt,
                List<String> degraded, List<String> pending) {
            this.overall = overall;
            this.generation = generation;
            this.firstSeenAt = firstSeenAt;
            this.degraded = degraded;
            this.pending = pending;
        }

        String describe() {
            StringBuilder sb = new StringBuilder(192);
            switch (this.overall) {
                case OVERALL_NO_DATA:
                    sb.append("Сверка: данных ещё нет (loader не публиковал статус)");
                    break;
                case OVERALL_LOADER_DOWN:
                    sb.append("Сверка: loader остановлен");
                    break;
                case OVERALL_DEGRADED:
                    sb.append("Сверка: деградация — ").append(join(this.degraded));
                    break;
                case OVERALL_PENDING:
                    sb.append("Сверка: ожидание — ").append(join(this.pending));
                    break;
                default:
                    sb.append("Сверка: OK");
                    break;
            }
            sb.append(" · манифест ").append(HookStatusContract.HOOK_IDS.length)
                    .append(" агентов v").append(MANIFEST_VERSION);
            if (this.generation != null) {
                sb.append('\n').append("Поколение: ").append(this.generation)
                        .append(" с ").append(new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                        .format(new Date(this.firstSeenAt)));
            }
            return sb.toString();
        }

        private static String join(List<String> labels) {
            if (labels.isEmpty()) {
                return "-";
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < labels.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(labels.get(i));
            }
            return sb.toString();
        }
    }

    /** Точка входа для Android-вызовов (провайдер публикации, тик диагностики). */
    static Report reconcile(Context context, String payload) {
        return reconcile(context.getSharedPreferences(PREFERENCES_NAME, 0), payload,
                bootCount(context), System.currentTimeMillis());
    }

    static int bootCount(Context context) {
        try {
            return Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.BOOT_COUNT, 0);
        } catch (RuntimeException e) {
            return 0;
        }
    }

    static synchronized Report reconcile(SharedPreferences prefs, String payload,
            int bootCount, long nowMs) {
        HookStatusContract.Snapshot snapshot = HookStatusContract.parse(payload);
        if (snapshot == null) {
            Report report = new Report(OVERALL_NO_DATA, null, 0L,
                    new ArrayList<String>(), new ArrayList<String>());
            record(prefs, report, false, nowMs);
            return report;
        }
        String genKey = bootCount + "|" + snapshot.loaderPid;
        String generation = "boot=" + bootCount + " pid=" + snapshot.loaderPid;
        boolean generationChanged = !genKey.equals(prefs.getString(KEY_GENERATION, ""));
        long firstSeen = nowMs;
        if (generationChanged) {
            prefs.edit().putString(KEY_GENERATION, genKey).putLong(KEY_FIRST_SEEN, nowMs).commit();
        } else {
            firstSeen = prefs.getLong(KEY_FIRST_SEEN, nowMs);
        }
        List<String> degraded = new ArrayList<>();
        List<String> pending = new ArrayList<>();
        String overall;
        if ("stopped".equals(snapshot.loaderState)) {
            overall = OVERALL_LOADER_DOWN;
        } else {
            boolean withinGrace = nowMs - firstSeen <= GRACE_MS;
            for (int i = 0; i < HookStatusContract.HOOK_IDS.length; i++) {
                HookStatusContract.Entry entry =
                        snapshot.hooks.get(HookStatusContract.HOOK_IDS[i]);
                String label = HookStatusContract.HOOK_LABELS[i];
                if (entry == null) {
                    degraded.add(label);
                    continue;
                }
                switch (entry.state) {
                    case "active":
                    case "disabled":
                        break;
                    case "failed":
                        degraded.add(label);
                        break;
                    default:
                        if (withinGrace) {
                            pending.add(label);
                        } else {
                            degraded.add(label);
                        }
                        break;
                }
            }
            if (!degraded.isEmpty()) {
                overall = OVERALL_DEGRADED;
            } else if (!pending.isEmpty()) {
                overall = OVERALL_PENDING;
            } else {
                overall = OVERALL_OK;
            }
        }
        Report report = new Report(overall, generation, firstSeen, degraded, pending);
        record(prefs, report, generationChanged, nowMs);
        return report;
    }

    /** Журнал реконсиляций (кольцевой, хранится построчно, без дублей подряд). */
    static String journalText(SharedPreferences prefs) {
        return prefs.getString(KEY_JOURNAL, "");
    }

    private static void record(SharedPreferences prefs, Report report,
            boolean generationChanged, long nowMs) {
        String last = prefs.getString(KEY_LAST_OVERALL, null);
        boolean changed = generationChanged || last == null || !report.overall.equals(last);
        if (!changed) {
            return;
        }
        List<String> details = report.degraded.isEmpty() ? report.pending : report.degraded;
        StringBuilder entry = new StringBuilder(64);
        entry.append(nowMs).append('|').append(report.overall).append('|');
        if (generationChanged) {
            entry.append("gen ");
        }
        for (int i = 0; i < details.size(); i++) {
            if (i > 0) {
                entry.append(',');
            }
            entry.append(details.get(i));
        }
        List<String> lines = new ArrayList<>();
        for (String line : prefs.getString(KEY_JOURNAL, "").split("\n")) {
            if (!line.isEmpty()) {
                lines.add(line);
            }
        }
        lines.add(entry.toString());
        while (lines.size() > JOURNAL_LIMIT) {
            lines.remove(0);
        }
        StringBuilder journal = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                journal.append('\n');
            }
            journal.append(lines.get(i));
        }
        prefs.edit().putString(KEY_JOURNAL, journal.toString())
                .putString(KEY_LAST_OVERALL, report.overall).commit();
    }
}
