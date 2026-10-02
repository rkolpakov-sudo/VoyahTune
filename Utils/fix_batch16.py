import pathlib

p = pathlib.Path("Native/app/src/main/java/ru/big/town/anative/MediaRefreshDelivery.java")
ls = p.read_text(encoding="utf-8", errors="replace").splitlines()
assert ls[106] == "    public void drain() {", repr(ls[106])
assert ls[162] == "    }", repr(ls[162])
assert ls[163] == "}", repr(ls[163])

new_drain = """    public void drain() {
        final Work work;
        final String reason;
        synchronized (this) {
            if (this.closed || this.pendingWork == null) {
                this.scheduled = false;
                return;
            }
            work = this.pendingWork;
            reason = this.pendingReason;
            this.pendingWork = null;
            this.pendingReason = "";
        }
        try {
            this.listener.accept(work, reason);
        } catch (Throwable ignored) {
        } finally {
            long j = 0;
            synchronized (this) {
                if (this.closed) {
                    this.scheduled = false;
                    this.pendingWork = null;
                    this.pendingReason = "";
                } else if (this.pendingWork == null) {
                    this.scheduled = false;
                } else {
                    j = this.revision;
                }
            }
            if (j != 0) {
                schedule(j);
            }
        }
    }""".split("\n")

ls[106:163] = new_drain
p.write_text("\n".join(ls) + "\n", encoding="utf-8")
print("drain reconstructed; lines:", len(ls))
