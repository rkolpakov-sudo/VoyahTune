"""fix_batch9: final double-assign в BatteryHeatService."""
import pathlib

p = pathlib.Path("Native/app/src/main/java/ru/big/town/anative/BatteryHeatService.java")
t = p.read_text(encoding="utf-8", errors="replace")
log = []


def rep(old, new, label):
    global t
    if old in t:
        t = t.replace(old, new, 1)
        log.append(f"OK {label}")
    else:
        log.append(f"MISS {label}")


rep("""    private void submitSettingsRefresh(BatteryHeatRefreshGate.Request request) {
        final BatteryHeatRefreshGate.Request request2;""",
    """    private void submitSettingsRefresh(BatteryHeatRefreshGate.Request request) {
        final BatteryHeatRefreshGate.Request request2 = request;""",
    "request2 decl")

rep("""        try {
            request2 = request;
            try {
                SETTINGS_EXECUTOR.execute(new Runnable() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda16""",
    """        try {
            try {
                SETTINGS_EXECUTOR.execute(new Runnable() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda16""",
    "request2 try-assign removed")

rep("""        } catch (RejectedExecutionException unused2) {
            request2 = request;
        }""",
    """        } catch (RejectedExecutionException unused2) {
        }""",
    "request2 catch-assign removed")

rep("""    public void initializeMonitoring() {
        final BatteryHeatService batteryHeatService;""",
    """    public void initializeMonitoring() {
        final BatteryHeatService batteryHeatService = this;""",
    "bhs decl")

rep("""        try {
            batteryHeatService = this;
            try {
                ContextCompat.registerReceiver(batteryHeatService, this.uiReceiver, intentFilter, BIND_PERMISSION, this.handler, 2);""",
    """        try {
            try {
                ContextCompat.registerReceiver(batteryHeatService, this.uiReceiver, intentFilter, BIND_PERMISSION, this.handler, 2);""",
    "bhs try-assign removed")

rep("""        } catch (Exception e2) {
            Exception e = e2;
            batteryHeatService = this;
        }""",
    """        } catch (Exception e2) {
            Exception e = e2;
        }""",
    "bhs catch-assign removed")

p.write_text(t, encoding="utf-8")
pathlib.Path("logs/fix_batch9.log").write_text("\n".join(log), encoding="utf-8")
print("\n".join(log))
