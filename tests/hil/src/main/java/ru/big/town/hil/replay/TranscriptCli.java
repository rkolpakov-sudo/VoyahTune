package ru.big.town.hil.replay;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import ru.big.town.hil.CanEmulatorCore;

/**
 * Генерация/сверка канонического транскрипта HIL-replay из logcat-трассы (SPEC L112).
 *
 * <p>Режимы:</p>
 * <ul>
 *   <li>{@code --trace F} — печатает транскрипт в stdout (просмотр);</li>
 *   <li>{@code --trace F --out R} — записывает эталон R (генерация REF из
 *       трассы ОРИГИНАЛА);</li>
 *   <li>{@code --trace F --ref R} — сверяет с эталоном R (трасса ФОРКА);
 *       дрейф → код 1.</li>
 * </ul>
 *
 * <p>Коды выхода: 0 — ок/паритет; 1 — дрейф транскрипта; 2 — ошибка
 * аргументов/файлов. Запуск: {@code gradlew :tests:hil:replay -Ptrace=...}.</p>
 */
public final class TranscriptCli {

    private static final String USAGE =
            "usage: TranscriptCli --trace <logcat> [--out <ref> | --ref <ref>]\n"
                    + "  --trace  logcat/nativelog-трасса (формат -v threadtime)\n"
                    + "  --out    записать сюда сгенерированный эталон (REF)\n"
                    + "  --ref    сверить транскрипт с эталоном (1 при дрейфе)\n"
                    + "  --help   эта справка";

    private TranscriptCli() {
    }

    public static void main(String[] args) {
        System.exit(run(args));
    }

    /** Точка входа для тестов: возвращает код выхода, не завершая процесс. */
    static int run(String[] args) {
        String trace = null;
        String ref = null;
        String out = null;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--trace":
                    if (++i >= args.length) {
                        return usage("--trace требует значение");
                    }
                    trace = args[i];
                    break;
                case "--ref":
                    if (++i >= args.length) {
                        return usage("--ref требует значение");
                    }
                    ref = args[i];
                    break;
                case "--out":
                    if (++i >= args.length) {
                        return usage("--out требует значение");
                    }
                    out = args[i];
                    break;
                case "-h":
                case "--help":
                    System.out.println(USAGE);
                    return 0;
                default:
                    return usage("неизвестный аргумент: " + args[i]);
            }
        }
        if (trace == null) {
            return usage("--trace обязателен");
        }
        if (ref != null && out != null) {
            return usage("--ref и --out одновременно нельзя");
        }

        String text;
        try {
            text = Files.readString(Paths.get(trace), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("replay-cli: не удалось прочитать " + trace
                    + ": " + e.getMessage());
            return 2;
        }
        List<String> transcript = TraceReplay.run(text, new CanEmulatorCore());

        if (out != null) {
            try {
                // канонический REF: '\n', завершающий перевод обязателен (как в git)
                Files.writeString(Paths.get(out),
                        String.join("\n", transcript) + "\n", StandardCharsets.UTF_8);
            } catch (IOException e) {
                System.err.println("replay-cli: не удалось записать " + out
                        + ": " + e.getMessage());
                return 2;
            }
            System.out.println("replay-cli: записано " + transcript.size()
                    + " строк -> " + out);
            return 0;
        }

        if (ref != null) {
            List<String> expected;
            try {
                expected = Files.readAllLines(Paths.get(ref), StandardCharsets.UTF_8);
            } catch (IOException e) {
                System.err.println("replay-cli: не удалось прочитать эталон " + ref
                        + ": " + e.getMessage());
                return 2;
            }
            List<String> drift = TraceReplay.compare(expected, transcript);
            if (drift.isEmpty()) {
                System.out.println("replay-cli: ПАРИТЕТ: " + transcript.size()
                        + " строк совпали с " + ref);
                return 0;
            }
            for (String line : drift) {
                System.err.println(line);
            }
            System.err.println("replay-cli: ДРЕЙФ: " + drift.size()
                    + " строк(и) против " + ref);
            return 1;
        }

        for (String line : transcript) {
            System.out.println(line);
        }
        return 0;
    }

    private static int usage(String msg) {
        System.err.println("replay-cli: " + msg);
        System.err.println(USAGE);
        return 2;
    }
}
