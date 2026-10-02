package android.car.storagemonitoring;

import android.annotation.SystemApi;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class UidIoRecord {
    public final long background_fsync;
    public final long background_rchar;
    public final long background_read_bytes;
    public final long background_wchar;
    public final long background_write_bytes;
    public final long foreground_fsync;
    public final long foreground_rchar;
    public final long foreground_read_bytes;
    public final long foreground_wchar;
    public final long foreground_write_bytes;
    public final int uid;

    public UidIoRecord(int i, long j, long j2, long j3, long j4, long j5, long j6, long j7, long j8, long j9, long j10) {
        this.uid = i;
        this.foreground_rchar = j;
        this.foreground_wchar = j2;
        this.foreground_read_bytes = j3;
        this.foreground_write_bytes = j4;
        this.foreground_fsync = j5;
        this.background_rchar = j6;
        this.background_wchar = j7;
        this.background_read_bytes = j8;
        this.background_write_bytes = j9;
        this.background_fsync = j10;
    }

    public UidIoRecord delta(IoStatsEntry ioStatsEntry) {
        if (this.uid == ioStatsEntry.uid) {
            return new UidIoRecord(this.uid, this.foreground_rchar - ioStatsEntry.foreground.bytesRead, this.foreground_wchar - ioStatsEntry.foreground.bytesWritten, this.foreground_read_bytes - ioStatsEntry.foreground.bytesReadFromStorage, this.foreground_write_bytes - ioStatsEntry.foreground.bytesWrittenToStorage, this.foreground_fsync - ioStatsEntry.foreground.fsyncCalls, this.background_rchar - ioStatsEntry.background.bytesRead, this.background_wchar - ioStatsEntry.background.bytesWritten, this.background_read_bytes - ioStatsEntry.background.bytesReadFromStorage, this.background_write_bytes - ioStatsEntry.background.bytesWrittenToStorage, this.background_fsync - ioStatsEntry.background.fsyncCalls);
        }
        throw new IllegalArgumentException("cannot calculate delta between different user IDs");
    }

    public UidIoRecord delta(UidIoRecord uidIoRecord) {
        int i = this.uid;
        if (i == uidIoRecord.uid) {
            return new UidIoRecord(i, this.foreground_rchar - uidIoRecord.foreground_rchar, this.foreground_wchar - uidIoRecord.foreground_wchar, this.foreground_read_bytes - uidIoRecord.foreground_read_bytes, this.foreground_write_bytes - uidIoRecord.foreground_write_bytes, this.foreground_fsync - uidIoRecord.foreground_fsync, this.background_rchar - uidIoRecord.background_rchar, this.background_wchar - uidIoRecord.background_wchar, this.background_read_bytes - uidIoRecord.background_read_bytes, this.background_write_bytes - uidIoRecord.background_write_bytes, this.background_fsync - uidIoRecord.background_fsync);
        }
        throw new IllegalArgumentException("cannot calculate delta between different user IDs");
    }
}
