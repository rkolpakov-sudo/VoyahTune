package ru.big.town.restoremode;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AdvanceActivityStarButton extends AppCompatActivity {
    static final int MSG_APPLY_DRIVE_MODES_STAR_BUTTON = 2;
    private EditText canCommandEditorStarButton1, canCommandEditorStarButton2;
    private Button buttonSaveStarButton, buttonApplyStarButton1, buttonApplyStarButton2, buttonBackStarButton;
    private NumberPicker pickerCustomCommandCountStarButton;
    private Messenger serviceMessenger;
    private boolean isBound;
    static final int MSG_RESULT = 4;

    private String customCommandStarButton1 = "";
    private String customCommandStarButton2 = "";


    public void onButtonClickCleanStarButton1(View v) {
        canCommandEditorStarButton1.setText("");
    }

    public void onButtonClickCleanStarButton2(View v) {
        canCommandEditorStarButton2.setText("");
    }

    public void onButtonClickSaveStarButton(View v) {
        if (buttonSaveStarButton != null && !buttonSaveStarButton.isEnabled()) {
            Log.w("$$$ StarButton save $$$", "Команды не сохранены: неверный формат");
            return;
        }
        GlobalVars.editor.putString("customCommandStarButton1", canCommandEditorStarButton1.getText().toString());
        GlobalVars.editor.putString("customCommandStarButton2", canCommandEditorStarButton2.getText().toString());
        GlobalVars.editor.apply();
    }

    public void onButtonClickApplyStarButton1(View v) {
        if (GlobalVars.isBound) {
            try {
                Message msg = Message.obtain(null, MSG_APPLY_DRIVE_MODES_STAR_BUTTON);
                msg.replyTo = GlobalVars.clientMessenger;
                msg.arg1=1;
                GlobalVars.serviceMessenger.send(msg);
            } catch (RemoteException e) {
                Log.e("$$$ StarButton1 $$$", "MSG_APPLY_DRIVE_MODES_STAR_BUTTON send failed", e);
            }
        }
    }

    public void onButtonClickApplyStarButton2(View v) {
        if (GlobalVars.isBound) {
            try {
                Message msg = Message.obtain(null, MSG_APPLY_DRIVE_MODES_STAR_BUTTON);
                msg.replyTo = GlobalVars.clientMessenger;
                msg.arg1=2;
                GlobalVars.serviceMessenger.send(msg);
            } catch (RemoteException e) {
                Log.e("$$$ StarButton2 $$$", "MSG_APPLY_DRIVE_MODES_STAR_BUTTON send failed", e);
            }
        }
    }

    public void onButtonClickBackStarButton(View v) {
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_advance_start_button);

        canCommandEditorStarButton1 = findViewById(R.id.rawCanCodesStarButton1);
        buttonApplyStarButton1 = findViewById(R.id.buttonApplyStarButton1);
        canCommandEditorStarButton2 = findViewById(R.id.rawCanCodesStarButton2);
        buttonApplyStarButton2 = findViewById(R.id.buttonApplyStarButton2);

        buttonSaveStarButton = findViewById(R.id.buttonSaveStarButton);
        buttonBackStarButton = findViewById(R.id.buttonBackStarButton);

        customCommandStarButton1 = GlobalVars.sharedPreferences.getString("customCommandStarButton1", "");
        canCommandEditorStarButton1.setText(customCommandStarButton1);
        customCommandStarButton2 = GlobalVars.sharedPreferences.getString("customCommandStarButton2", "");
        canCommandEditorStarButton2.setText(customCommandStarButton2);

        // Начальная валидация: при пустых/валидных значениях кнопки не должны остаться disabled
        // от предыдущего состояния; невалидное уже сохранённое — сразу помечаем.
        validateStarButtons();

        canCommandEditorStarButton1.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("$$$ beforeTextChanged $$$", s.toString() + String.format("int start, int count, int after: %d, %d %d ", start, count, after));
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("$$$ afterTextChanged $$$", s.toString());

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("$$$ onTextChanged $$$", s.toString() + String.format("int start, int before, int count: %d, %d %d ", start, before, count));
                if (isFormatting) return;
                isFormatting = true;

                String input = s.toString().toLowerCase();
                String filtered = input.replaceAll("[^0-9a-f,\n]", "");
                String[] q;
                q = filtered.split("\n");
                StringBuilder formatted = new StringBuilder();
                for (String i : q) {
                    Log.i("LENGTH i", String.format("%s %d", i, i.length()));
                    for (int j = 0; j < i.length(); j++) {
                        if (j % 2 == 0) {
                            formatted.append(" ");
                        }
                        formatted.append(i.charAt(j));
                        if (j >= 19) {
                            formatted.append("\n");
                        }
                    }
                }
                Log.i("$$$ LENGTH formatted.length $$$ ", String.format("%d", formatted.length()));

                canCommandEditorStarButton1.removeTextChangedListener(this);
                canCommandEditorStarButton1.setText(formatted.toString());
                canCommandEditorStarButton1.setSelection(formatted.length());
                canCommandEditorStarButton1.addTextChangedListener(this);
                isFormatting = false;
                validateStarButtons();
            }
        });


        canCommandEditorStarButton2.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("$$$ beforeTextChanged $$$", s.toString() + String.format("int start, int count, int after: %d, %d %d ", start, count, after));
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("$$$ afterTextChanged $$$", s.toString());

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("$$$ onTextChanged $$$", s.toString() + String.format("int start, int before, int count: %d, %d %d ", start, before, count));
                if (isFormatting) return;
                isFormatting = true;

                String input = s.toString().toLowerCase();
                String filtered = input.replaceAll("[^0-9a-f,\n]", "");
                String[] q;
                q = filtered.split("\n");
                StringBuilder formatted = new StringBuilder();
                for (String i : q) {
                    Log.i("LENGTH i", String.format("%s %d", i, i.length()));
                    for (int j = 0; j < i.length(); j++) {
                        if (j % 2 == 0) {
                            formatted.append(" ");
                        }
                        formatted.append(i.charAt(j));
                        if (j >= 19) {
                            formatted.append("\n");
                        }
                    }
                }
                Log.i("$$$ LENGTH formatted.length $$$ ", String.format("%d", formatted.length()));

                canCommandEditorStarButton2.removeTextChangedListener(this);
                canCommandEditorStarButton2.setText(formatted.toString());
                canCommandEditorStarButton2.setSelection(formatted.length());
                canCommandEditorStarButton2.addTextChangedListener(this);
                isFormatting = false;
                validateStarButtons();
            }
        });
    }

    /**
     * Единая валидация обеих команд: формат AdvanceActivity — только hex-пары, каждая строка
     * ровно 20 hex-символов (10 байт), пустой ввод тоже валиден. Save/Apply блокируются
     * только при невалидном непустом вводе; Back не блокируется (нет back-ловушки).
     */
    private void validateStarButtons() {
        boolean v1 = isValidCanCommand(canCommandEditorStarButton1);
        boolean v2 = isValidCanCommand(canCommandEditorStarButton2);
        boolean ok = v1 && v2;

        if (canCommandEditorStarButton1 != null) {
            canCommandEditorStarButton1.setBackgroundColor(v1 ? Color.WHITE : 0xffffafaf);
        }
        if (canCommandEditorStarButton2 != null) {
            canCommandEditorStarButton2.setBackgroundColor(v2 ? Color.WHITE : 0xffffafaf);
        }
        if (buttonSaveStarButton != null) {
            buttonSaveStarButton.setEnabled(ok);
            buttonSaveStarButton.setTextColor(ok ? Color.WHITE : Color.GRAY);
        }
        if (buttonApplyStarButton1 != null) {
            buttonApplyStarButton1.setEnabled(v1);
            buttonApplyStarButton1.setTextColor(v1 ? Color.WHITE : Color.GRAY);
        }
        if (buttonApplyStarButton2 != null) {
            buttonApplyStarButton2.setEnabled(v2);
            buttonApplyStarButton2.setTextColor(v2 ? Color.WHITE : Color.GRAY);
        }
        // Back всегда доступен — пользователь не должен быть заперт в экране.
        if (buttonBackStarButton != null) {
            buttonBackStarButton.setEnabled(true);
            buttonBackStarButton.setTextColor(Color.WHITE);
        }
    }

    /** Пусто — OK; иначе каждая непустая строка — ровно 10 байт hex (20 hex-символов после пробелов). */
    private static boolean isValidCanCommand(EditText editor) {
        if (editor == null) return false;
        String text = editor.getText() == null ? "" : editor.getText().toString().trim();
        if (text.isEmpty()) return true;
        for (String line : text.split("\n")) {
            String hex = line.replaceAll("[^0-9a-fA-F]", "");
            if (hex.length() != 20) return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}