package ru.big.town.restoremode;

import android.os.Bundle;
import android.os.Handler;
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

/* JADX INFO: loaded from: classes2.dex */
public class AdvanceActivityStarButton extends AppCompatActivity {
    static final int MSG_APPLY_DRIVE_MODES_STAR_BUTTON = 2;
    static final int MSG_RESULT = 4;
    private Button buttonApplyStarButton1;
    private Button buttonApplyStarButton2;
    private Button buttonBackStarButton;
    private Button buttonSaveStarButton;
    private EditText canCommandEditorStarButton1;
    private EditText canCommandEditorStarButton2;
    private String customCommandStarButton1 = "";
    private String customCommandStarButton2 = "";
    private boolean isBound;
    private NumberPicker pickerCustomCommandCountStarButton;
    private Messenger serviceMessenger;

    public void onButtonClickCleanStarButton1(View view) {
        this.canCommandEditorStarButton1.setText("");
    }

    public void onButtonClickCleanStarButton2(View view) {
        this.canCommandEditorStarButton2.setText("");
    }

    public void onButtonClickSaveStarButton(View view) {
        GlobalVars.editor.putString("customCommandStarButton1", this.canCommandEditorStarButton1.getText().toString());
        GlobalVars.editor.putString("customCommandStarButton2", this.canCommandEditorStarButton2.getText().toString());
        GlobalVars.editor.apply();
    }

    public void onButtonClickApplyStarButton1(View view) {
        if (GlobalVars.isBound) {
            try {
                Message messageObtain = Message.obtain((Handler) null, 2);
                messageObtain.replyTo = GlobalVars.clientMessenger;
                messageObtain.arg1 = 1;
                GlobalVars.serviceMessenger.send(messageObtain);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void onButtonClickApplyStarButton2(View view) {
        if (GlobalVars.isBound) {
            try {
                Message messageObtain = Message.obtain((Handler) null, 2);
                messageObtain.replyTo = GlobalVars.clientMessenger;
                messageObtain.arg1 = 2;
                GlobalVars.serviceMessenger.send(messageObtain);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void onButtonClickBackStarButton(View view) {
        finish();
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_advance_start_button);
        this.canCommandEditorStarButton1 = (EditText) findViewById(R.id.rawCanCodesStarButton1);
        this.buttonApplyStarButton1 = (Button) findViewById(R.id.buttonApplyStarButton1);
        this.canCommandEditorStarButton2 = (EditText) findViewById(R.id.rawCanCodesStarButton2);
        this.buttonApplyStarButton2 = (Button) findViewById(R.id.buttonApplyStarButton2);
        this.buttonSaveStarButton = (Button) findViewById(R.id.buttonSaveStarButton);
        this.buttonBackStarButton = (Button) findViewById(R.id.buttonBackStarButton);
        String string = GlobalVars.sharedPreferences.getString("customCommandStarButton1", "");
        this.customCommandStarButton1 = string;
        this.canCommandEditorStarButton1.setText(string);
        String string2 = GlobalVars.sharedPreferences.getString("customCommandStarButton2", "");
        this.customCommandStarButton2 = string2;
        this.canCommandEditorStarButton2.setText(string2);
        this.canCommandEditorStarButton1.addTextChangedListener(new TextWatcher() { // from class: ru.big.town.restoremode.AdvanceActivityStarButton.1
            private boolean isFormatting = false;

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.i("$$$ beforeTextChanged $$$", charSequence.toString() + String.format("int start, int count, int after: %d, %d %d ", Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3)));
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                Log.i("$$$ afterTextChanged $$$", editable.toString());
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.i("$$$ onTextChanged $$$", charSequence.toString() + String.format("int start, int before, int count: %d, %d %d ", Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3)));
                if (this.isFormatting) {
                    return;
                }
                this.isFormatting = true;
                String[] strArrSplit = charSequence.toString().toLowerCase().replaceAll("[^0-9a-f,\n]", "").split("\n");
                StringBuilder sb = new StringBuilder();
                for (String str : strArrSplit) {
                    Log.i("LENGTH i", String.format("%s %d", str, Integer.valueOf(str.length())));
                    for (int i4 = 0; i4 < str.length(); i4++) {
                        if (i4 % 2 == 0) {
                            sb.append(" ");
                        }
                        sb.append(str.charAt(i4));
                        if (i4 >= 19) {
                            sb.append("\n");
                        }
                    }
                }
                Log.i("$$$ LENGTH formatted.length $$$ ", String.format("%d", Integer.valueOf(sb.length())));
                if (sb.length() % 31 == 0) {
                    AdvanceActivityStarButton.this.canCommandEditorStarButton1.setBackgroundColor(-1);
                    AdvanceActivityStarButton.this.buttonSaveStarButton.setEnabled(true);
                    AdvanceActivityStarButton.this.buttonSaveStarButton.setTextColor(-1);
                    AdvanceActivityStarButton.this.buttonApplyStarButton1.setEnabled(true);
                    AdvanceActivityStarButton.this.buttonApplyStarButton1.setTextColor(-1);
                    AdvanceActivityStarButton.this.buttonBackStarButton.setEnabled(true);
                    AdvanceActivityStarButton.this.buttonBackStarButton.setTextColor(-1);
                } else {
                    AdvanceActivityStarButton.this.canCommandEditorStarButton1.setBackgroundColor(-20561);
                    AdvanceActivityStarButton.this.buttonSaveStarButton.setEnabled(false);
                    AdvanceActivityStarButton.this.buttonSaveStarButton.setTextColor(-7829368);
                    AdvanceActivityStarButton.this.buttonApplyStarButton1.setEnabled(false);
                    AdvanceActivityStarButton.this.buttonApplyStarButton1.setTextColor(-7829368);
                    AdvanceActivityStarButton.this.buttonBackStarButton.setEnabled(false);
                    AdvanceActivityStarButton.this.buttonBackStarButton.setTextColor(-7829368);
                }
                AdvanceActivityStarButton.this.canCommandEditorStarButton1.removeTextChangedListener(this);
                AdvanceActivityStarButton.this.canCommandEditorStarButton1.setText(sb.toString());
                AdvanceActivityStarButton.this.canCommandEditorStarButton1.setSelection(sb.length());
                AdvanceActivityStarButton.this.canCommandEditorStarButton1.addTextChangedListener(this);
                this.isFormatting = false;
            }
        });
        this.canCommandEditorStarButton2.addTextChangedListener(new TextWatcher() { // from class: ru.big.town.restoremode.AdvanceActivityStarButton.2
            private boolean isFormatting = false;

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.i("$$$ beforeTextChanged $$$", charSequence.toString() + String.format("int start, int count, int after: %d, %d %d ", Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3)));
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                Log.i("$$$ afterTextChanged $$$", editable.toString());
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.i("$$$ onTextChanged $$$", charSequence.toString() + String.format("int start, int before, int count: %d, %d %d ", Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3)));
                if (this.isFormatting) {
                    return;
                }
                this.isFormatting = true;
                String[] strArrSplit = charSequence.toString().toLowerCase().replaceAll("[^0-9a-f,\n]", "").split("\n");
                StringBuilder sb = new StringBuilder();
                for (String str : strArrSplit) {
                    Log.i("LENGTH i", String.format("%s %d", str, Integer.valueOf(str.length())));
                    for (int i4 = 0; i4 < str.length(); i4++) {
                        if (i4 % 2 == 0) {
                            sb.append(" ");
                        }
                        sb.append(str.charAt(i4));
                        if (i4 >= 19) {
                            sb.append("\n");
                        }
                    }
                }
                Log.i("$$$ LENGTH formatted.length $$$ ", String.format("%d", Integer.valueOf(sb.length())));
                if (sb.length() % 31 == 0) {
                    AdvanceActivityStarButton.this.canCommandEditorStarButton2.setBackgroundColor(-1);
                    AdvanceActivityStarButton.this.buttonSaveStarButton.setEnabled(true);
                    AdvanceActivityStarButton.this.buttonSaveStarButton.setTextColor(-1);
                    AdvanceActivityStarButton.this.buttonApplyStarButton2.setEnabled(true);
                    AdvanceActivityStarButton.this.buttonApplyStarButton2.setTextColor(-1);
                    AdvanceActivityStarButton.this.buttonBackStarButton.setEnabled(true);
                    AdvanceActivityStarButton.this.buttonBackStarButton.setTextColor(-1);
                } else {
                    AdvanceActivityStarButton.this.canCommandEditorStarButton2.setBackgroundColor(-20561);
                    AdvanceActivityStarButton.this.buttonSaveStarButton.setEnabled(false);
                    AdvanceActivityStarButton.this.buttonSaveStarButton.setTextColor(-7829368);
                    AdvanceActivityStarButton.this.buttonApplyStarButton2.setEnabled(false);
                    AdvanceActivityStarButton.this.buttonApplyStarButton2.setTextColor(-7829368);
                    AdvanceActivityStarButton.this.buttonBackStarButton.setEnabled(false);
                    AdvanceActivityStarButton.this.buttonBackStarButton.setTextColor(-7829368);
                }
                AdvanceActivityStarButton.this.canCommandEditorStarButton2.removeTextChangedListener(this);
                AdvanceActivityStarButton.this.canCommandEditorStarButton2.setText(sb.toString());
                AdvanceActivityStarButton.this.canCommandEditorStarButton2.setSelection(sb.length());
                AdvanceActivityStarButton.this.canCommandEditorStarButton2.addTextChangedListener(this);
                this.isFormatting = false;
            }
        });
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
    }
}
