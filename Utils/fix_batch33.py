import io, sys

p = r"RestoreMode\app\src\main\java\ru\big\town\restoremode\AdvanceActivity.java"
t = io.open(p, encoding="utf-8").read()
ok = True

def rep(old, new, count=1):
    global t, ok
    n = t.count(old)
    if n != count:
        print(f"  !! x{n} (expected {count}): {old[:90]!r}")
        ok = False
        return
    t = t.replace(old, new)

# 1) onCreate: i not assigned when intent == null
rep("""        initApolloTech();
        if (intent != null) {
            i = intent.getIntExtra(EXTRA_SECTION, 0) != 7 ? 0 : 7;
        }
        setSection(i);""",
    """        initApolloTech();
        i = 0;
        if (intent != null) {
            i = intent.getIntExtra(EXTRA_SECTION, 0) != 7 ? 0 : 7;
        }
        setSection(i);""")

# 2) readSystemMetrics: broken nested try/catch from jadx -> single try/catch
rep("""    private SystemMetricsSnapshot readSystemMetrics(long j) {
        long jMax;
        long j2;
        long jMax2;
        try {
            ActivityManager activityManager = (ActivityManager) getSystemService("activity");
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            if (activityManager != null) {
                activityManager.getMemoryInfo(memoryInfo);
                jMax = Math.max(0L, memoryInfo.totalMem);
                try {
                    jMax2 = Math.max(0L, Math.min(jMax, memoryInfo.availMem));
                } catch (RuntimeException e) {
                    e = e;
                    Log.w("SystemMetrics", "RAM read failed: " + e.getMessage());
                    j2 = 0;
                }
            } else {
                jMax = 0;
                jMax2 = 0;
            }
            j2 = jMax2;
        } catch (RuntimeException e2) {
            RuntimeException e = e2;
            jMax = 0;
        }
        long j3 = jMax;""",
    """    private SystemMetricsSnapshot readSystemMetrics(long j) {
        long jMax;
        long j2;
        try {
            ActivityManager activityManager = (ActivityManager) getSystemService("activity");
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            if (activityManager != null) {
                activityManager.getMemoryInfo(memoryInfo);
                jMax = Math.max(0L, memoryInfo.totalMem);
                j2 = Math.max(0L, Math.min(jMax, memoryInfo.availMem));
            } else {
                jMax = 0;
                j2 = 0;
            }
        } catch (RuntimeException e) {
            Log.w("SystemMetrics", "RAM read failed: " + e.getMessage());
            jMax = 0;
            j2 = 0;
        }
        long j3 = jMax;""")

io.open(p, "w", encoding="utf-8", newline="").write(t)
print("AdvanceActivity fixed" if ok else "PATTERNS FAILED")
sys.exit(0 if ok else 1)
