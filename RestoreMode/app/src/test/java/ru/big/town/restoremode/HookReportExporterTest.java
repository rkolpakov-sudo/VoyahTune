package ru.big.town.restoremode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.junit.Test;

public class HookReportExporterTest {
    private Map<String, String> readZip(File zip) throws IOException {
        Map<String, String> entries = new HashMap<>();
        try (ZipInputStream in = new ZipInputStream(Files.newInputStream(zip.toPath()))) {
            ZipEntry entry;
            byte[] buffer = new byte[8192];
            while ((entry = in.getNextEntry()) != null) {
                StringBuilder sb = new StringBuilder();
                int read;
                while ((read = in.read(buffer)) != -1) {
                    sb.append(new String(buffer, 0, read, StandardCharsets.UTF_8));
                }
                entries.put(entry.getName(), sb.toString());
            }
        }
        return entries;
    }

    @Test
    public void composeStatusContainsEveryReportSection() {
        String text = HookReportExporter.composeStatus(
                "v=1;loader=running;pid=321",
                "Loader: работает (PID 321)",
                "Сверка: OK · манифест 7 агентов v3.22.0",
                "1760000000000|OK|",
                5, "4.0.0", 1_760_000_000_000L);
        assertTrue(text, text.contains("boot_count=5"));
        assertTrue(text, text.contains("app_version=4.0.0"));
        assertTrue(text, text.contains("--- renderForUi ---"));
        assertTrue(text, text.contains("--- reconciliation ---"));
        assertTrue(text, text.contains("--- generation journal ---"));
        assertTrue(text, text.contains("--- raw payload ---"));
        assertTrue(text, text.contains("v=1;loader=running;pid=321"));
    }

    @Test
    public void composeStatusShowsEmptyJournalAndMissingPayload() {
        String text = HookReportExporter.composeStatus(null, "нет данных", null, "", 0, "4.0.0", 0L);
        assertTrue(text, text.contains("(пусто)"));
        assertTrue(text, text.contains("(нет данных)"));
    }

    @Test
    public void reportWithoutCrashMirrorContainsOnlyStatus() throws IOException {
        File filesDir = Files.createTempDirectory("vt-files").toFile();
        File cacheDir = Files.createTempDirectory("vt-cache").toFile();
        File zip = HookReportExporter.buildReport(filesDir, cacheDir,
                HookReportExporter.composeStatus("p", "r", "c", "", 1, "4.0.0", 0L));
        assertTrue(zip.isFile());
        assertTrue(zip.getName().startsWith("voyahtune-report-"));
        Map<String, String> entries = readZip(zip);
        assertEquals(entries.toString(), 1, entries.size());
        assertTrue(entries.containsKey(HookReportExporter.STATUS_ENTRY));
        assertTrue(entries.get(HookReportExporter.STATUS_ENTRY).contains("boot_count=1"));
        zip.delete();
    }

    @Test
    public void reportIncludesCrashMirrorArtifacts() throws IOException {
        File filesDir = Files.createTempDirectory("vt-files").toFile();
        File cacheDir = Files.createTempDirectory("vt-cache").toFile();
        File incident = new File(new File(filesDir, "crashes"), "1760000123");
        assertTrue(incident.mkdirs());
        Files.write(new File(incident, "tombstone_00.txt").toPath(),
                "SIGSEGV in system_server".getBytes(StandardCharsets.UTF_8));
        Files.write(new File(incident, "context.txt").toPath(),
                "boot_id=test".getBytes(StandardCharsets.UTF_8));

        File zip = HookReportExporter.buildReport(filesDir, cacheDir, "status");
        Map<String, String> entries = readZip(zip);
        assertEquals(entries.toString(), 3, entries.size());
        assertEquals("SIGSEGV in system_server",
                entries.get("crashes/1760000123/tombstone_00.txt"));
        assertEquals("boot_id=test", entries.get("crashes/1760000123/context.txt"));
        assertTrue(entries.containsKey(HookReportExporter.STATUS_ENTRY));
        assertFalse(entries.keySet().stream().anyMatch(n -> n.contains("\\")));
        zip.delete();
    }

    @Test
    public void reportGoesToCacheReportsDirectory() throws IOException {
        File filesDir = Files.createTempDirectory("vt-files").toFile();
        File cacheDir = Files.createTempDirectory("vt-cache").toFile();
        File zip = HookReportExporter.buildReport(filesDir, cacheDir, "s");
        assertEquals("reports", zip.getParentFile().getName());
        assertEquals(cacheDir.getCanonicalPath(), zip.getParentFile().getParentFile().getCanonicalPath());
        zip.delete();
    }
}
