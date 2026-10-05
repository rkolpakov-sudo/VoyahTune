package ru.big.town.restoremode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * IMP-08 (SPEC L50): сборка локального отчёта стабильности («Сохранить отчёт»).
 *
 * Содержимое zip: hook_status.txt (payload + рендер + реконсиляция + журнал поколений +
 * boot count + версия) и crashes/** — зеркало crash-артефактов лоадера из files/crashes.
 * Только локальные файлы, никакой сети.
 */
final class HookReportExporter {
    static final String STATUS_ENTRY = "hook_status.txt";
    static final String CRASH_ENTRY_PREFIX = "crashes/";

    private HookReportExporter() {
    }

    /** Текстовая часть отчёта: чистая функция (покрывается JVM-тестом). */
    static String composeStatus(String payload, String rendered, String reconciliation,
            String journal, int bootCount, String appVersion, long nowMs) {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("voyahtune stability report\n");
        sb.append("generated=").append(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
                .format(new Date(nowMs))).append('\n');
        sb.append("app_version=").append(appVersion).append('\n');
        sb.append("boot_count=").append(bootCount).append('\n');
        sb.append('\n').append("--- renderForUi ---\n").append(rendered == null ? "" : rendered).append('\n');
        sb.append('\n').append("--- reconciliation ---\n").append(reconciliation == null ? "" : reconciliation).append('\n');
        sb.append('\n').append("--- generation journal ---\n").append(journal == null || journal.isEmpty() ? "(пусто)" : journal).append('\n');
        sb.append('\n').append("--- raw payload ---\n").append(payload == null ? "(нет данных)" : payload).append('\n');
        return sb.toString();
    }

    /**
     * Пишет zip в cacheDir/reports и возвращает файл. Отсутствующее зеркало crashes —
     * не ошибка: отчёт содержит только текстовую часть.
     */
    static File buildReport(File filesDir, File cacheDir, String statusText) throws IOException {
        File reports = new File(cacheDir, "reports");
        if (!reports.isDirectory() && !reports.mkdirs()) {
            throw new IOException("cannot create report dir: " + reports);
        }
        File out = new File(reports, "voyahtune-report-" + System.currentTimeMillis() + ".zip");
        ZipOutputStream zip = null;
        try {
            zip = new ZipOutputStream(new FileOutputStream(out));
            putText(zip, STATUS_ENTRY, statusText);
            File crashes = new File(filesDir, "crashes");
            if (crashes.isDirectory()) {
                putDir(zip, crashes, "");
            }
            zip.finish();
            return out;
        } catch (IOException e) {
            out.delete();
            throw e;
        } finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private static void putText(ZipOutputStream zip, String name, String text) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(text.getBytes("UTF-8"));
        zip.closeEntry();
    }

    private static void putDir(ZipOutputStream zip, File dir, String prefix) throws IOException {
        File[] children = dir.listFiles();
        if (children == null) {
            return;
        }
        for (File child : children) {
            String name = prefix + child.getName();
            if (child.isDirectory()) {
                putDir(zip, child, name + "/");
            } else {
                zip.putNextEntry(new ZipEntry(CRASH_ENTRY_PREFIX + name));
                copy(child, zip);
                zip.closeEntry();
            }
        }
    }

    private static void copy(File file, OutputStream out) throws IOException {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
