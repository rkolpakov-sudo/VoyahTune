package android.car.storagemonitoring;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class IoStats implements Parcelable {
    public static final Parcelable.Creator<IoStats> CREATOR = new Parcelable.Creator<IoStats>() { // from class: android.car.storagemonitoring.IoStats.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public IoStats createFromParcel(Parcel parcel) {
            return new IoStats(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public IoStats[] newArray(int i) {
            return new IoStats[i];
        }
    };
    private final List<IoStatsEntry> mStats;
    private final long mUptimeTimestamp;

    public IoStats(Parcel parcel) {
        this.mStats = parcel.createTypedArrayList(IoStatsEntry.CREATOR);
        this.mUptimeTimestamp = parcel.readLong();
    }

    public IoStats(List<IoStatsEntry> list, long j) {
        this.mStats = list;
        this.mUptimeTimestamp = j;
    }

    public IoStats(JSONObject jSONObject) throws JSONException {
        this.mUptimeTimestamp = jSONObject.getInt("uptime");
        JSONArray jSONArray = jSONObject.getJSONArray("stats");
        this.mStats = new ArrayList();
        for (int i = 0; i < jSONArray.length(); i++) {
            this.mStats.add(new IoStatsEntry(jSONArray.getJSONObject(i)));
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj instanceof IoStats) {
            IoStats ioStats = (IoStats) obj;
            if (ioStats.getTimestamp() == getTimestamp() && ioStats.getStats().equals(getStats())) {
                return true;
            }
        }
        return false;
    }

    public IoStatsEntry.Metrics getBackgroundTotals() {
        long j = 0;
        long j2 = 0;
        long j3 = 0;
        long j4 = 0;
        long j5 = 0;
        for (IoStatsEntry ioStatsEntry : getStats()) {
            j += ioStatsEntry.background.bytesRead;
            j2 += ioStatsEntry.background.bytesWritten;
            j3 += ioStatsEntry.background.bytesReadFromStorage;
            j4 += ioStatsEntry.background.bytesWrittenToStorage;
            j5 += ioStatsEntry.background.fsyncCalls;
        }
        return new IoStatsEntry.Metrics(j, j2, j3, j4, j5);
    }

    public IoStatsEntry.Metrics getForegroundTotals() {
        long j = 0;
        long j2 = 0;
        long j3 = 0;
        long j4 = 0;
        long j5 = 0;
        for (IoStatsEntry ioStatsEntry : getStats()) {
            j += ioStatsEntry.foreground.bytesRead;
            j2 += ioStatsEntry.foreground.bytesWritten;
            j3 += ioStatsEntry.foreground.bytesReadFromStorage;
            j4 += ioStatsEntry.foreground.bytesWrittenToStorage;
            j5 += ioStatsEntry.foreground.fsyncCalls;
        }
        return new IoStatsEntry.Metrics(j, j2, j3, j4, j5);
    }

    public List<IoStatsEntry> getStats() {
        return this.mStats;
    }

    public long getTimestamp() {
        return this.mUptimeTimestamp;
    }

    public IoStatsEntry.Metrics getTotals() {
        IoStatsEntry.Metrics foregroundTotals = getForegroundTotals();
        IoStatsEntry.Metrics backgroundTotals = getBackgroundTotals();
        return new IoStatsEntry.Metrics(foregroundTotals.bytesRead + backgroundTotals.bytesRead, foregroundTotals.bytesWritten + backgroundTotals.bytesWritten, foregroundTotals.bytesReadFromStorage + backgroundTotals.bytesReadFromStorage, foregroundTotals.bytesWrittenToStorage + backgroundTotals.bytesWrittenToStorage, foregroundTotals.fsyncCalls + backgroundTotals.fsyncCalls);
    }

    public IoStatsEntry getUserIdStats(int i) {
        for (IoStatsEntry ioStatsEntry : getStats()) {
            if (ioStatsEntry.uid == i) {
                return ioStatsEntry;
            }
        }
        return null;
    }

    public int hashCode() {
        return Objects.hash(this.mStats, Long.valueOf(this.mUptimeTimestamp));
    }

    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(", ");
        Iterator<IoStatsEntry> it = getStats().iterator();
        while (it.hasNext()) {
            stringJoiner.add(it.next().toString());
        }
        return "timestamp = " + getTimestamp() + ", stats = " + stringJoiner.toString();
    }

    public void writeToJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("uptime").value(this.mUptimeTimestamp);
        jsonWriter.name("stats").beginArray();
        Iterator<IoStatsEntry> it = this.mStats.iterator();
        while (it.hasNext()) {
            it.next().writeToJson(jsonWriter);
        }
        jsonWriter.endArray();
        jsonWriter.endObject();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(this.mStats);
        parcel.writeLong(this.mUptimeTimestamp);
    }
}
