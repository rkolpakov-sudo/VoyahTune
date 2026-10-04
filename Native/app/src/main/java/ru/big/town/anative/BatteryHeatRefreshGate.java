package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class BatteryHeatRefreshGate {
    private boolean closed;
    private Request pending;
    private Request running;

    BatteryHeatRefreshGate() {
    }

    static final class Request {
        final long epoch;
        final boolean evaluateAuto;
        final String reason;

        private Request(long j, boolean z, String str) {
            this.epoch = j;
            this.evaluateAuto = z;
            this.reason = str == null ? "" : str;
        }
    }

    static final class Completion {
        final Request next;
        final boolean publish;

        private Completion(boolean z, Request request) {
            this.publish = z;
            this.next = request;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static Completion empty() {
            return new Completion(false, null);
        }
    }

    synchronized Request offer(long j, boolean z, String str) {
        if (this.closed) {
            return null;
        }
        Request request = new Request(j, z, str);
        if (this.running == null) {
            Request requestMerge = merge(this.pending, request);
            this.running = requestMerge;
            this.pending = null;
            return requestMerge;
        }
        this.pending = merge(this.pending, request);
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    synchronized Completion finish(Request request) {
        Request request2;
        if (!this.closed && request != null && (request2 = this.running) == request) {
            Request request3 = this.pending;
            Request request4 = null;
            if (request3 == null) {
                this.running = null;
                return new Completion(true, request4);
            }
            Request requestMerge = merge(request2, request3);
            this.running = requestMerge;
            this.pending = null;
            return new Completion(false, requestMerge);
        }
        return Completion.empty();
    }

    synchronized void reject(Request request) {
        Request request2;
        if (!this.closed && request != null && (request2 = this.running) == request) {
            this.pending = merge(request2, this.pending);
            this.running = null;
        }
    }

    synchronized void close() {
        this.closed = true;
        this.running = null;
        this.pending = null;
    }

    private static Request merge(Request request, Request request2) {
        String str;
        if (request != null) {
            if (request2 == null) {
                return request;
            }
            if (request.epoch == request2.epoch) {
                boolean z = request.evaluateAuto || request2.evaluateAuto;
                if (request2.evaluateAuto || !request.evaluateAuto) {
                    str = request2.reason;
                } else {
                    str = request.reason;
                }
                return new Request(request2.epoch, z, str);
            }
        }
        return request2;
    }
}
