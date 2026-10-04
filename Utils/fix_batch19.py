import pathlib

p = pathlib.Path("Native/app/src/main/java/ru/big/town/anative/PowerHoldStatusTracker.java")
t = p.read_text(encoding="utf-8", errors="replace")
log = []


def rep(old, new):
    global t
    if old in t:
        t = t.replace(old, new, 1)
        log.append("OK " + old.splitlines()[0].strip()[:80])
    else:
        log.append("MISS " + old.splitlines()[0].strip()[:80])


rep("    void finishActivation(final long j, final PowerHoldPolicy.Outcome outcome) {",
    "    void finishActivation(final long j, PowerHoldPolicy.Outcome outcome) {")

rep("""        if (outcome == null) {
            outcome = PowerHoldPolicy.Outcome.TRANSPORT_FAILURE;
        }
        this.scheduler.post(new Runnable() {""",
    """        if (outcome == null) {
            outcome = PowerHoldPolicy.Outcome.TRANSPORT_FAILURE;
        }
        final PowerHoldPolicy.Outcome outcomeTerminal = outcome;
        this.scheduler.post(new Runnable() {""")

rep("                PowerHoldStatusTracker.this.m2021xa28cc0e8(j, outcome);",
    "                PowerHoldStatusTracker.this.m2021xa28cc0e8(j, outcomeTerminal);")

p.write_text(t, encoding="utf-8")
print("\n".join(log))
