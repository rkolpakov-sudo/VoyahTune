package ru.big.town.restoremode;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.ResultReceiver;
import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceCommandMessage {
    VoiceCommandMessage() {
    }

    static Message create(String str, String str2, String str3, ResultReceiver resultReceiver) {
        return create(str, str2, str3, resultReceiver, 0, 1);
    }

    static Message create(String str, String str2, String str3, ResultReceiver resultReceiver, int i, int i2) {
        Bundle bundle = new Bundle();
        bundle.putString("session", str);
        bundle.putString("op", str2);
        bundle.putString("action", str3);
        bundle.putParcelable("reply", remoteReceiver(resultReceiver));
        bundle.putInt("index", i);
        bundle.putInt("count", i2);
        bundle.putInt("resultDisplayMs", PathInterpolatorCompat.MAX_NUM_POINTS);
        Message messageObtain = Message.obtain((Handler) null, 36);
        messageObtain.setData(bundle);
        return messageObtain;
    }

    private static ResultReceiver remoteReceiver(ResultReceiver resultReceiver) {
        if (resultReceiver == null) {
            return null;
        }
        Parcel parcelObtain = Parcel.obtain();
        try {
            resultReceiver.writeToParcel(parcelObtain, 0);
            parcelObtain.setDataPosition(0);
            return (ResultReceiver) ResultReceiver.CREATOR.createFromParcel(parcelObtain);
        } finally {
            parcelObtain.recycle();
        }
    }
}
