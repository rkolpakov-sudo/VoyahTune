package ru.big.town.restoremode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class SplitRatioSaveReceiver extends BroadcastReceiver {
    public static final String ACTION = "ru.big.town.restoremode.SPLIT_RATIO_SAVE";
    private static final String TAG = "$$$ SplitRatioSave $$$";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (ACTION.equals(intent.getAction())) {
            float floatExtra = intent.getFloatExtra(TileOrderStore.Tile.TYPE_SPLIT, 0.0f);
            if (floatExtra <= 0.05f || floatExtra >= 0.95f) {
                return;
            }
            String stringExtra = intent.getStringExtra("presetId");
            int i = -1;
            int intExtra = intent.getIntExtra("presetIdx", -1);
            SharedPreferences sharedPreferences = context.getSharedPreferences("DrivePreferences", 0);
            List<SplitStore.Preset> listLoad = SplitStore.load(sharedPreferences);
            if (stringExtra != null && !stringExtra.isEmpty()) {
                for (int i2 = 0; i2 < listLoad.size(); i2++) {
                    if (stringExtra.equals(listLoad.get(i2).id)) {
                        i = i2;
                        break;
                    }
                }
            }
            if (i >= 0 || intExtra < 0 || intExtra >= listLoad.size()) {
                intExtra = i;
            }
            if (intExtra < 0 || !listLoad.get(intExtra).resizable) {
                return;
            }
            listLoad.get(intExtra).split = floatExtra;
            SplitStore.save(sharedPreferences, listLoad);
            SplitConfigSync.pushAll(context, sharedPreferences);
            Log.i(TAG, "пропорция пресета " + listLoad.get(intExtra).id + " сохранена: " + floatExtra);
        }
    }
}
