package ru.big.town.restoremode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/* JADX INFO: loaded from: classes2.dex */
public class VoiceSettingsActivity extends Activity {
    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        startActivity(new Intent(this, (Class<?>) AdvanceActivity.class).putExtra("settingsSection", 7));
        finish();
    }
}
