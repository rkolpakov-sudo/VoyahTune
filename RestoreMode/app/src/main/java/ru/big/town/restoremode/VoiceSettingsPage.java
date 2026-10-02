package ru.big.town.restoremode;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Function;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceSettingsPage {
    private static final int MICROPHONE_REQUEST = 41;
    private static final int TEST_MICROPHONE_REQUEST = 42;
    private final AppCompatActivity activity;
    private final Runnable changed;
    private final LinearLayout commands;
    private final LinearLayout deepFilterStrength;
    private final TextView deepFilterStrengthLabel;
    private final SeekBar deepFilterStrengthSlider;
    private final Switch enabled;
    private final SharedPreferences prefs;
    private final Switch shortcut;
    private final MaterialButton steeringPress;
    private final Button tryVoice;
    private boolean updating;

    VoiceSettingsPage(final AppCompatActivity appCompatActivity, LinearLayout linearLayout, final SharedPreferences sharedPreferences, final Runnable runnable) {
        this.activity = appCompatActivity;
        this.prefs = sharedPreferences;
        this.changed = runnable;
        Switch r5 = setting(linearLayout, "Включить голосового помощника");
        this.enabled = r5;
        linearLayout.addView(text("Выберите короткое или долгое нажатие кнопки голосового помощника на руле. Повторный вызов начинает новую сессию. Прежние действия выбранного нажатия сохранятся и вернутся после смены нажатия или отключения помощника. Помощника также можно вызвать кнопкой «Попробовать голосовую команду» или ярлыком на главном экране.", 20, -5592406));
        MaterialButton materialButton = new MaterialButton(appCompatActivity);
        this.steeringPress = materialButton;
        materialButton.setAllCaps(false);
        materialButton.setTextSize(0, 24.0f);
        materialButton.setMinHeight(dp(64));
        linearLayout.addView(materialButton, new LinearLayout.LayoutParams(-1, -2));
        updateSteeringPress();
        materialButton.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.VoiceSettingsPage$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VoiceSettingsPage.this.m1988lambda$new$1$rubigtownrestoremodeVoiceSettingsPage(appCompatActivity, sharedPreferences, runnable, view);
            }
        });
        linearLayout.addView(text("Распознавание работает без интернета. Произносите одну команду за раз. Не обязательно произносить фразу целиком: достаточно ключевых слов, например «спорт» или «фары авто». Слова «выключи» и «переключи» определяют действие — их пропускать нельзя. Можно менять порядок слов и добавлять «пожалуйста». Помощник учитывает окончания и небольшие ошибки в названиях автомобильных команд. Неизвестная или неоднозначная фраза не выполняется.", 20, -5592406));
        linearLayout.addView(text("Массаж, подогрев и вентиляция: без указания места команда относится к водителю. Для переднего пассажира добавьте «пассажира», например «массаж пассажира волны» или «подогрев сиденья пассажира три». Уровни — от 1 до 3. Выбор уровня или типа массажа может одновременно включить функцию. Доступность зависит от комплектации и прошивки автомобиля.", 20, -5592406));
        linearLayout.addView(text("Окна: «открой» или «опусти» — открыть, «закрой» или «подними» — закрыть. Можно указать водителя, переднего пассажира, заднее левое/правое окно или группу: передние, задние, левые, правые. «Открой окна» относится ко всем четырём; для одного окна укажите место. «Проветривание» приоткрывает все четыре окна, «проветривание люка» — только люк. Величину открытия задаёт автомобиль. Шторка управляется отдельно: «открой шторку» / «закрой шторку». После отправки помощник не проверяет фактическое положение.", 20, -5592406));
        LinearLayout linearLayout2 = new LinearLayout(appCompatActivity);
        this.deepFilterStrength = linearLayout2;
        linearLayout2.setOrientation(1);
        linearLayout2.setBackgroundResource(R.drawable.layout_category_bg);
        linearLayout2.setPadding(dp(16), dp(10), dp(16), dp(10));
        linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(-1, -2));
        TextView textViewText = text("", 26, -1);
        this.deepFilterStrengthLabel = textViewText;
        linearLayout2.addView(textViewText);
        AppCompatSeekBar appCompatSeekBar = new AppCompatSeekBar(appCompatActivity);
        this.deepFilterStrengthSlider = appCompatSeekBar;
        appCompatSeekBar.setMax(30);
        appCompatSeekBar.setContentDescription("Сила подавления шума");
        linearLayout2.addView(appCompatSeekBar, new LinearLayout.LayoutParams(-1, dp(48)));
        linearLayout2.addView(text("0 дБ — без обработки · 6 дБ — мягче · 30 дБ — сильнее.\nМеньше значение — больше исходного звука и меньше искажений голоса. Настройка действует со следующей записи.", 20, -5592406));
        appCompatSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: ru.big.town.restoremode.VoiceSettingsPage.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                VoiceSettingsPage.this.deepFilterStrengthLabel.setText("Сила подавления шума: до " + i + " дБ");
                if (z) {
                    sharedPreferences.edit().putInt("voiceDeepFilterAttenuationDb", i).apply();
                }
            }
        });
        Switch r4 = setting(linearLayout, "Ярлык «Голосовая команда» на главном экране");
        this.shortcut = r4;
        MaterialButton materialButton2 = new MaterialButton(appCompatActivity);
        materialButton2.setCornerRadius(dp(20));
        this.tryVoice = materialButton2;
        materialButton2.setText("Попробовать голосовую команду");
        materialButton2.setAllCaps(false);
        materialButton2.setTextSize(0, 26.0f);
        materialButton2.setTextColor(-1);
        materialButton2.setBackgroundTintList(ColorStateList.valueOf(-13156534));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, dp(64));
        layoutParams.setMargins(0, dp(12), 0, dp(20));
        linearLayout.addView(materialButton2, layoutParams);
        materialButton2.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.VoiceSettingsPage$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AppCompatActivity appCompatActivity2 = appCompatActivity;
                appCompatActivity2.startActivity(new Intent(appCompatActivity2, (Class<?>) VoiceActivity.class));
            }
        });
        MaterialButton materialButton3 = new MaterialButton(appCompatActivity);
        materialButton3.setText("Проверить распознавание без выполнения");
        materialButton3.setAllCaps(false);
        materialButton3.setTextSize(0, 24.0f);
        linearLayout.addView(materialButton3, new LinearLayout.LayoutParams(-1, dp(64)));
        materialButton3.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.VoiceSettingsPage$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VoiceSettingsPage.lambda$new$3(appCompatActivity, view);
            }
        });
        LinearLayout linearLayout3 = new LinearLayout(appCompatActivity);
        this.commands = linearLayout3;
        linearLayout3.setOrientation(1);
        linearLayout.addView(linearLayout3, new LinearLayout.LayoutParams(-1, -2));
        r5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.VoiceSettingsPage$$ExternalSyntheticLambda8
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                VoiceSettingsPage.this.m1989lambda$new$4$rubigtownrestoremodeVoiceSettingsPage(appCompatActivity, compoundButton, z);
            }
        });
        r4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.VoiceSettingsPage$$ExternalSyntheticLambda9
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                VoiceSettingsPage.this.m1990lambda$new$5$rubigtownrestoremodeVoiceSettingsPage(sharedPreferences, compoundButton, z);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$new$1$ru-big-town-restoremode-VoiceSettingsPage, reason: not valid java name */
    /* synthetic */ void m1988lambda$new$1$rubigtownrestoremodeVoiceSettingsPage(final AppCompatActivity appCompatActivity, final SharedPreferences sharedPreferences, final Runnable runnable, View view) {
        new MaterialAlertDialogBuilder(appCompatActivity, R.style.DarkDialog).setTitle((CharSequence) "Вызов кнопкой на руле").setSingleChoiceItems((CharSequence[]) new String[]{"Короткое нажатие", "Долгое нажатие"}, !"short".equals(selectedPress()) ? 1 : 0, new DialogInterface.OnClickListener() { // from class: ru.big.town.restoremode.VoiceSettingsPage$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                VoiceSettingsPage.this.m1987lambda$new$0$rubigtownrestoremodeVoiceSettingsPage(sharedPreferences, appCompatActivity, runnable, dialogInterface, i);
            }
        }).setNegativeButton((CharSequence) "Отмена", (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: renamed from: lambda$new$0$ru-big-town-restoremode-VoiceSettingsPage, reason: not valid java name */
    /* synthetic */ void m1987lambda$new$0$rubigtownrestoremodeVoiceSettingsPage(SharedPreferences sharedPreferences, AppCompatActivity appCompatActivity, Runnable runnable, DialogInterface dialogInterface, int i) {
        sharedPreferences.edit().putString("voiceAssistantSteeringPress", i == 0 ? "short" : "long").apply();
        updateSteeringPress();
        SplitConfigSync.pushSteering(appCompatActivity, sharedPreferences);
        runnable.run();
        dialogInterface.dismiss();
    }

    static /* synthetic */ void lambda$new$3(AppCompatActivity appCompatActivity, View view) {
        if (appCompatActivity.checkSelfPermission("android.permission.RECORD_AUDIO") != 0) {
            appCompatActivity.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 42);
        } else {
            appCompatActivity.startActivity(new Intent(appCompatActivity, (Class<?>) VoiceActivity.class).putExtra("voiceTestOnly", true));
        }
    }

    /* JADX INFO: renamed from: lambda$new$4$ru-big-town-restoremode-VoiceSettingsPage, reason: not valid java name */
    /* synthetic */ void m1989lambda$new$4$rubigtownrestoremodeVoiceSettingsPage(AppCompatActivity appCompatActivity, CompoundButton compoundButton, boolean z) {
        if (this.updating) {
            return;
        }
        if (z && appCompatActivity.checkSelfPermission("android.permission.RECORD_AUDIO") != 0) {
            this.updating = true;
            this.enabled.setChecked(false);
            this.updating = false;
            appCompatActivity.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 41);
            return;
        }
        saveEnabled(z);
    }

    /* JADX INFO: renamed from: lambda$new$5$ru-big-town-restoremode-VoiceSettingsPage, reason: not valid java name */
    /* synthetic */ void m1990lambda$new$5$rubigtownrestoremodeVoiceSettingsPage(SharedPreferences sharedPreferences, CompoundButton compoundButton, boolean z) {
        if (this.updating) {
            return;
        }
        sharedPreferences.edit().putBoolean("showVoiceCommand", z).apply();
    }

    void refresh() {
        this.updating = true;
        this.enabled.setChecked(this.prefs.getBoolean("voiceAssistantEnabled", false));
        this.shortcut.setChecked(this.prefs.getBoolean("showVoiceCommand", false));
        updateStrength();
        updateSteeringPress();
        this.updating = false;
        this.tryVoice.setEnabled(this.enabled.isChecked());
        this.tryVoice.setAlpha(this.enabled.isChecked() ? 1.0f : 0.45f);
        this.commands.removeAllViews();
        LinkedHashMap<String, VoiceCommandCatalog.Command> linkedHashMap = new LinkedHashMap<>();
        LinkedHashMap<String, LinkedHashSet<String>> linkedHashMap2 = new LinkedHashMap<>();
        for (VoiceCommandCatalog.Command command : VoiceCommands.load(this.activity)) {
            String str = "fuel_charge:";
            if (!command.action.startsWith("fuel_charge:")) {
                str = command.action;
            }
            linkedHashMap.putIfAbsent(str, command);
            linkedHashMap2.computeIfAbsent(str, new Function() { // from class: ru.big.town.restoremode.VoiceSettingsPage$$ExternalSyntheticLambda4
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return VoiceSettingsPage.lambda$refresh$6((String) obj);
                }
            }).addAll(command.phrases);
        }
        for (VoiceCommandGroups.Group group : VoiceCommandGroups.Group.values()) {
            LinkedHashMap<String, VoiceCommandCatalog.Command> linkedHashMap3 = new LinkedHashMap<>();
            for (Map.Entry entry : linkedHashMap.entrySet()) {
                if (VoiceCommandGroups.forAction(((VoiceCommandCatalog.Command) entry.getValue()).action).contains(group)) {
                    linkedHashMap3.put((String) entry.getKey(), (VoiceCommandCatalog.Command) entry.getValue());
                }
            }
            commandGroup(group, linkedHashMap3, linkedHashMap2);
        }
    }

    static /* synthetic */ LinkedHashSet lambda$refresh$6(String str) {
        return new LinkedHashSet();
    }

    private void commandGroup(final VoiceCommandGroups.Group group, final Map<String, VoiceCommandCatalog.Command> map, final Map<String, LinkedHashSet<String>> map2) {
        final MaterialButton materialButton = new MaterialButton(this.activity);
        materialButton.setAllCaps(false);
        materialButton.setCornerRadius(dp(12));
        materialButton.setTextSize(0, 26.0f);
        materialButton.setTextColor(-1);
        materialButton.setBackgroundTintList(ColorStateList.valueOf(-13156534));
        materialButton.setGravity(8388627);
        materialButton.setPadding(dp(16), dp(10), dp(16), dp(10));
        materialButton.setMinHeight(dp(64));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.topMargin = dp(12);
        this.commands.addView(materialButton, layoutParams);
        final LinearLayout linearLayout = new LinearLayout(this.activity);
        linearLayout.setOrientation(1);
        this.commands.addView(linearLayout, new LinearLayout.LayoutParams(-1, -2));
        final String str = "voiceCommandGroupExpanded_" + group.name();
        final Runnable runnable = new Runnable() { // from class: ru.big.town.restoremode.VoiceSettingsPage$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                VoiceSettingsPage.this.m1985lambda$commandGroup$7$rubigtownrestoremodeVoiceSettingsPage(str, materialButton, group, map, linearLayout, map2);
            }
        };
        materialButton.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.VoiceSettingsPage$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VoiceSettingsPage.this.m1986lambda$commandGroup$8$rubigtownrestoremodeVoiceSettingsPage(str, runnable, view);
            }
        });
        runnable.run();
    }

    /* JADX INFO: renamed from: lambda$commandGroup$7$ru-big-town-restoremode-VoiceSettingsPage, reason: not valid java name */
    /* synthetic */ void m1985lambda$commandGroup$7$rubigtownrestoremodeVoiceSettingsPage(String str, MaterialButton materialButton, VoiceCommandGroups.Group group, Map<String, VoiceCommandCatalog.Command> map, LinearLayout linearLayout, Map<String, LinkedHashSet<String>> map2) {
        boolean z = this.prefs.getBoolean(str, false);
        materialButton.setText((z ? "▾  " : "▸  ") + group.title + " (" + map.size() + ")");
        materialButton.setContentDescription(group.title + ", " + (z ? "свернуть группу" : "раскрыть группу"));
        linearLayout.setVisibility(z ? 0 : 8);
        if (z && linearLayout.getChildCount() == 0) {
            row(linearLayout, "Команда", "Фразы", true);
            for (Map.Entry entry : map.entrySet()) {
                VoiceCommandCatalog.Command command = (VoiceCommandCatalog.Command) entry.getValue();
                boolean zEquals = ((String) entry.getKey()).equals("fuel_charge:");
                String str2 = "";
                String str3 = zEquals ? "Топливо: поддержание заряда (SREV)" : command.title + (command.confirm ? "\nС подтверждением" : "");
                StringBuilder sb = new StringBuilder();
                if (zEquals) {
                    str2 = "Топливо <число>: словами или цифрами. Любое число округляется до ближайших 5% в пределах 25–80%. Например: топливо семьдесят три → 75%.\n";
                }
                row(linearLayout, str3, sb.append(str2).append(String.join("; ", (Iterable<? extends CharSequence>) map2.get(entry.getKey()))).toString(), false);
            }
        }
    }

    /* JADX INFO: renamed from: lambda$commandGroup$8$ru-big-town-restoremode-VoiceSettingsPage, reason: not valid java name */
    /* synthetic */ void m1986lambda$commandGroup$8$rubigtownrestoremodeVoiceSettingsPage(String str, Runnable runnable, View view) {
        this.prefs.edit().putBoolean(str, !this.prefs.getBoolean(str, false)).apply();
        runnable.run();
    }

    private String selectedPress() {
        return VoiceSteeringPolicy.normalize(this.prefs.getString("voiceAssistantSteeringPress", "long"));
    }

    private void updateSteeringPress() {
        this.steeringPress.setText("Кнопка на руле: ".concat("short".equals(selectedPress()) ? "короткое нажатие" : "долгое нажатие"));
    }

    private void updateStrength() {
        VoiceAudioConfig voiceAudioConfig = VoiceAudioConfig.read(this.prefs);
        this.deepFilterStrengthSlider.setProgress(voiceAudioConfig.deepFilterDb);
        this.deepFilterStrengthLabel.setText("Сила подавления шума: до " + voiceAudioConfig.deepFilterDb + " дБ");
    }

    private Switch setting(LinearLayout linearLayout, String str) {
        LinearLayout linearLayout2 = new LinearLayout(this.activity);
        linearLayout2.setGravity(16);
        linearLayout2.setBackgroundResource(R.drawable.layout_category_bg);
        linearLayout2.setPadding(dp(16), dp(10), dp(16), dp(10));
        linearLayout2.addView(text(str, 26, -1), new LinearLayout.LayoutParams(0, -2, 1.0f));
        final Switch r1 = new Switch(this.activity);
        r1.setContentDescription(str);
        r1.setThumbResource(R.drawable.switch_thumb);
        r1.setTrackResource(R.drawable.switch_track);
        linearLayout2.addView(r1);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.bottomMargin = dp(10);
        linearLayout.addView(linearLayout2, layoutParams);
        linearLayout2.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.VoiceSettingsPage$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                Switch r0 = r1;
                r0.setChecked(!r0.isChecked());
            }
        });
        return r1;
    }

    private void row(LinearLayout linearLayout, String str, String str2, boolean z) {
        LinearLayout linearLayout2 = new LinearLayout(this.activity);
        linearLayout2.setGravity(48);
        linearLayout2.setPadding(dp(16), dp(10), dp(16), dp(10));
        TextView textViewText = text(str, z ? 24 : 22, -1);
        TextView textViewText2 = text(str2, z ? 24 : 20, z ? -1 : -3355444);
        textViewText.setPadding(0, dp(4), dp(24), dp(4));
        textViewText2.setPadding(dp(24), dp(4), 0, dp(4));
        if (z) {
            textViewText.setTypeface(null, 1);
            textViewText2.setTypeface(null, 1);
        }
        linearLayout2.addView(textViewText, new LinearLayout.LayoutParams(0, -2, 1.0f));
        linearLayout2.addView(textViewText2, new LinearLayout.LayoutParams(0, -2, 3.0f));
        linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(-1, -2));
        View view = new View(this.activity);
        view.setBackgroundColor(-13156534);
        linearLayout.addView(view, new LinearLayout.LayoutParams(-1, dp(1)));
    }

    private TextView text(String str, int i, int i2) {
        TextView textView = new TextView(this.activity);
        textView.setText(str);
        textView.setTextColor(i2);
        textView.setTextSize(0, i);
        textView.setPadding(0, dp(4), 0, dp(12));
        return textView;
    }

    private int dp(int i) {
        return Math.round(i * this.activity.getResources().getDisplayMetrics().density);
    }

    private void saveEnabled(boolean z) {
        this.prefs.edit().putBoolean("voiceAssistantEnabled", z).apply();
        VoiceWarmupService.sync(this.activity);
        this.updating = true;
        this.enabled.setChecked(z);
        this.updating = false;
        this.tryVoice.setEnabled(z);
        this.tryVoice.setAlpha(z ? 1.0f : 0.45f);
        SplitConfigSync.pushSteering(this.activity, this.prefs);
        this.changed.run();
    }

    void onPermissionResult(int i, int[] iArr) {
        boolean z = false;
        if (i == 42) {
            if (iArr.length > 0 && iArr[0] == 0) {
                this.activity.startActivity(new Intent(this.activity, (Class<?>) VoiceActivity.class).putExtra("voiceTestOnly", true));
                return;
            } else {
                Toast.makeText(this.activity, "Для проверки разрешите доступ к микрофону", 1).show();
                return;
            }
        }
        if (i != 41) {
            return;
        }
        if (iArr.length > 0 && iArr[0] == 0) {
            z = true;
        }
        saveEnabled(z);
        if (z) {
            return;
        }
        Toast.makeText(this.activity, "Для голосового управления разрешите микрофон в настройках Android", 1).show();
    }
}
