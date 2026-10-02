package ru.big.town.anative;

import android.content.Context;
import android.os.Environment;
import android.os.Process;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Iterator;

/* JADX INFO: loaded from: classes2.dex */
final class NativeLog {
    static final String FILE_NAME = "voyahtune_native_log.txt";
    private static final int FLUSH_EVERY = 25;
    private static final NativeLog INSTANCE = new NativeLog();
    private static final int RING_MAX = 600;
    private static final String TAG = "$$$ NativeLog $$$";
    private File file;
    private Process proc;
    private final ArrayDeque<String> ring = new ArrayDeque<>();
    private volatile boolean running = false;
    private Thread thread;
    private FileWriter writer;

    static NativeLog get() {
        return INSTANCE;
    }

    private NativeLog() {
    }

    synchronized boolean isRunning() {
        return this.running;
    }

    synchronized File logFile(Context context) {
        if (this.file == null) {
            this.file = resolveFile(context);
        }
        return this.file;
    }

    synchronized String snapshot() {
        StringBuilder sb;
        sb = new StringBuilder();
        Iterator<String> it = this.ring.iterator();
        while (it.hasNext()) {
            sb.append(it.next()).append('\n');
        }
        return sb.toString();
    }

    synchronized void start(Context context) {
        if (this.running) {
            return;
        }
        File fileResolveFile = resolveFile(context);
        this.file = fileResolveFile;
        try {
            File parentFile = fileResolveFile.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                parentFile.mkdirs();
            }
            this.writer = new FileWriter(this.file, false);
            String str = "==== Native log start (pid=" + Process.myPid() + ", file=" + this.file.getAbsolutePath() + ") ====";
            this.writer.write(str + "\n");
            this.writer.flush();
            addRing(str);
        } catch (Exception e) {
            Log.e(TAG, "start: не удалось открыть файл " + e.getMessage());
            this.writer = null;
        }
        final int iMyPid = Process.myPid();
        this.thread = new Thread(new Runnable() { // from class: ru.big.town.anative.NativeLog$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.m1987lambda$start$0$rubigtownanativeNativeLog(iMyPid);
            }
        }, "native-log-pump");
        this.running = true;
        this.thread.start();
        Log.i(TAG, "logging STARTED → " + this.file.getAbsolutePath());
    }

    synchronized void stop() {
        if (this.running) {
            this.running = false;
            try {
                Process process = this.proc;
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception unused) {
            }
            this.proc = null;
            Thread thread = this.thread;
            if (thread != null) {
                thread.interrupt();
            }
            this.thread = null;
            try {
                FileWriter fileWriter = this.writer;
                if (fileWriter != null) {
                    fileWriter.flush();
                    this.writer.close();
                }
            } catch (Exception unused2) {
            }
            this.writer = null;
        }
        Log.i(TAG, "logging STOPPED");
    }

    synchronized void stopAndDelete(Context context) {
        stop();
        File fileResolveFile = this.file;
        if (fileResolveFile == null) {
            fileResolveFile = resolveFile(context);
        }
        if (fileResolveFile != null) {
            try {
                if (fileResolveFile.exists() && fileResolveFile.delete()) {
                    Log.i(TAG, "log file deleted: " + fileResolveFile.getAbsolutePath());
                }
            } catch (Exception e) {
                Log.w(TAG, "stopAndDelete: " + e.getMessage());
            }
        }
        this.ring.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: pump, reason: merged with bridge method [inline-methods] */
    public void m1987lambda$start$0$rubigtownanativeNativeLog(int i) throws Throwable {
        String line;
        BufferedReader bufferedReader = null;
        try {
            try {
                Process processStart = new ProcessBuilder("logcat", "--pid=" + i, "-v", "time").redirectErrorStream(true).start();
                synchronized (this) {
                    this.proc = processStart;
                }
                BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(processStart.getInputStream()));
                int i2 = 0;
                while (this.running && (line = bufferedReader2.readLine()) != null) {
                    try {
                        synchronized (this) {
                            try {
                                addRing(line);
                                FileWriter fileWriter = this.writer;
                                if (fileWriter != null) {
                                    try {
                                        fileWriter.write(line);
                                        this.writer.write(10);
                                        i2++;
                                        if (i2 >= 25) {
                                            this.writer.flush();
                                            i2 = 0;
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "pump write: " + e.getMessage());
                                    }
                                }
                            } catch (Throwable th) {
                                throw th;
                            }
                        }
                    } catch (Exception e2) {
                        e = e2;
                        bufferedReader = bufferedReader2;
                        Log.e(TAG, "pump: " + e.getMessage());
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (Exception unused) {
                            }
                        }
                        synchronized (this) {
                            try {
                                FileWriter fileWriter2 = this.writer;
                                if (fileWriter2 != null) {
                                    fileWriter2.flush();
                                }
                            } catch (Exception unused2) {
                            }
                            return;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        bufferedReader = bufferedReader2;
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (Exception unused3) {
                            }
                        }
                        synchronized (this) {
                            try {
                                FileWriter fileWriter3 = this.writer;
                                if (fileWriter3 != null) {
                                    fileWriter3.flush();
                                }
                            } catch (Exception unused4) {
                            }
                            throw th;
                        }
                    }
                }
                try {
                    bufferedReader2.close();
                } catch (Exception unused5) {
                }
                synchronized (this) {
                    try {
                        FileWriter fileWriter4 = this.writer;
                        if (fileWriter4 != null) {
                            fileWriter4.flush();
                        }
                    } catch (Exception unused6) {
                    }
                }
            } catch (Exception e3) {
                e = e3;
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    private void addRing(String str) {
        this.ring.addLast(str);
        while (this.ring.size() > 600) {
            this.ring.pollFirst();
        }
    }

    private File resolveFile(Context context) {
        File file = new File(Environment.getExternalStorageDirectory(), "tmp");
        if (file.exists() || file.mkdirs()) {
            return new File(file, FILE_NAME);
        }
        return new File(context.getExternalFilesDir(null), FILE_NAME);
    }
}
