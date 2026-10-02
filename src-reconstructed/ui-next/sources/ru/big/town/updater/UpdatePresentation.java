package ru.big.town.updater;

/* JADX INFO: loaded from: classes.dex */
final class UpdatePresentation {
    boolean busy;
    boolean indeterminate;
    boolean meter;
    int nav;
    int percent;
    boolean secondary;
    boolean success;
    String eyebrow = "ОБНОВЛЕНИЯ";
    String title = "Обновления VoyahTune";
    String subtitle = "Проверьте наличие нового релиза.";
    String badge = "Установлено";
    String primary = "Проверить обновления";
    String command = "check";
    String progressLabel = "";
    String progressNote = "";

    UpdatePresentation() {
    }

    static String menuPhase(String str, boolean z) {
        return (!z || isBusy(str)) ? str : "idle";
    }

    void offerFinish(boolean z) {
        if (this.success || z) {
            this.primary = "Завершить";
            this.command = "finish";
            this.secondary = false;
        }
    }

    static UpdatePresentation from(String str, boolean z, String str2, long j, long j2, long j3, long j4) {
        UpdatePresentation updatePresentation = new UpdatePresentation();
        updatePresentation.busy = isBusy(str);
        str.hashCode();
        switch (str) {
            case "verified":
                updatePresentation.heading("ВСЁ ГОТОВО", "Можно устанавливать", "Релиз скачан и проверен. Установка начнётся после подтверждения.", "Архив проверен");
                updatePresentation.nav = 2;
                updatePresentation.primary = "Установить";
                updatePresentation.command = "apply";
                return updatePresentation;
            case "verifying":
                updatePresentation.heading("ПРОВЕРКА АРХИВА", "Проверяем скачанный релиз", "Проверяем целостность и распаковываем архив.", "Проверяем");
                updatePresentation.waiting(1, "Проверка целостности архива", str2);
                return updatePresentation;
            case "committed":
                updatePresentation.heading("ОБНОВЛЕНИЕ ЗАВЕРШЕНО", "VoyahTune готов к работе", "Новая версия установлена. Проверка запуска успешно завершена.", "Установлен");
                updatePresentation.nav = 3;
                updatePresentation.primary = "Завершить";
                updatePresentation.command = "finish";
                updatePresentation.success = true;
                return updatePresentation;
            case "downloading":
                updatePresentation.heading("СКАЧИВАНИЕ", "Скачиваем VoyahTune", "Можно вернуться в VoyahTune — скачивание продолжится в фоне.", "Скачивается");
                updatePresentation.waiting(1, "Загрузка архива", "Можно пользоваться VoyahTune");
                if (j2 > 0 && j >= 0) {
                    updatePresentation.indeterminate = false;
                    updatePresentation.percent = percentage(j, j2);
                    updatePresentation.progressNote = (j / 1048576) + " из " + (j2 / 1048576) + " МБ";
                }
                return updatePresentation;
            case "preparing":
                updatePresentation.heading("ПОДГОТОВКА", "Готовимся к установке", "Проверяем условия установки и доступ к системным файлам.", "Подготовка");
                updatePresentation.waiting(2, "Подготовка к установке", str2);
                return updatePresentation;
            case "validating":
                updatePresentation.heading("ПРОВЕРКА ЗАПУСКА", "Проверяем работу VoyahTune", "Дожидаемся стабильной работы приложений и служб.", "Проверка запуска");
                updatePresentation.waiting(2, "Проверка запуска VoyahTune", str2);
                return updatePresentation;
            case "repair-required":
                updatePresentation.heading("ОШИБКА УСТАНОВКИ", "Не удалось завершить обновление", "Установите релиз через USB с компьютера.", "Нужен USB");
                updatePresentation.nav = 2;
                updatePresentation.primary = "Завершить";
                updatePresentation.command = "finish";
                return updatePresentation;
            case "applying":
                updatePresentation.heading("УСТАНОВКА", "Устанавливаем обновление", "Обновляем файлы и приложения. Сохраняйте питание автомобиля.", "Установка");
                updatePresentation.waiting(2, str2, "Обновление выполняется автономно");
                if (j4 > 0 && j3 >= 0 && j3 <= j4) {
                    updatePresentation.indeterminate = false;
                    updatePresentation.percent = percentage(j3, j4);
                    updatePresentation.progressNote = "Завершено " + j3 + " из " + j4 + " шагов";
                    return updatePresentation;
                }
                return updatePresentation;
            case "checking":
                updatePresentation.heading("ПРОВЕРКА ОБНОВЛЕНИЙ", "Ищем новый релиз", "Проверяем каталог релизов.", "Проверяем");
                updatePresentation.waiting(0, "Проверка каталога", "Скачивание не начинается автоматически.");
                return updatePresentation;
            case "reboot-pending":
                updatePresentation.heading("ПЕРЕЗАГРУЗКА", "Перезапускаем систему", "Экран временно погаснет. Проверка продолжится после загрузки.", "Перезагрузка");
                updatePresentation.waiting(2, "Ожидаем перезагрузку", "Система перезапускается");
                return updatePresentation;
            default:
                if (z && !"failed".equals(str)) {
                    updatePresentation.heading("НОВАЯ ВЕРСИЯ", "Доступен релиз", "Скачайте релиз, затем запустите установку в удобное время.", "Готов к скачиванию");
                    updatePresentation.primary = "Скачать";
                    updatePresentation.command = "download";
                    updatePresentation.secondary = true;
                    return updatePresentation;
                }
                if ("failed".equals(str)) {
                    updatePresentation.heading("ОШИБКА ОБНОВЛЕНИЯ", "Не удалось подготовить релиз", "Подробная причина показана ниже.", "Ошибка");
                    updatePresentation.primary = "Завершить";
                    updatePresentation.command = "finish";
                    return updatePresentation;
                }
                if (!str2.isEmpty()) {
                    updatePresentation.subtitle = str2;
                    return updatePresentation;
                }
                return updatePresentation;
        }
    }

    private void heading(String str, String str2, String str3, String str4) {
        this.eyebrow = str;
        this.title = str2;
        this.subtitle = str3;
        this.badge = str4;
    }

    private void waiting(int i, String str, String str2) {
        this.nav = i;
        this.meter = true;
        this.indeterminate = true;
        this.progressLabel = str;
        this.progressNote = str2;
        this.primary = "Выполняется…";
        this.command = "";
    }

    static boolean isBusy(String str) {
        str.hashCode();
        switch (str) {
            case "verifying":
            case "downloading":
            case "preparing":
            case "validating":
            case "applying":
            case "checking":
            case "reboot-pending":
                return true;
            default:
                return false;
        }
    }

    private static int percentage(long j, long j2) {
        return (int) Math.min(100.0d, Math.max(0.0d, (j * 100.0d) / j2));
    }
}
