package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class MediaKeyPairDelivery {

    interface EventSender {
        boolean send(boolean z) throws Exception;
    }

    enum Outcome {
        NOT_SENT,
        DOWN_ONLY,
        COMPLETE
    }

    private MediaKeyPairDelivery() {
    }

    static Outcome dispatch(EventSender eventSender) {
        if (eventSender == null) {
            return Outcome.NOT_SENT;
        }
        try {
            if (!eventSender.send(true)) {
                return Outcome.NOT_SENT;
            }
            try {
                return eventSender.send(false) ? Outcome.COMPLETE : Outcome.DOWN_ONLY;
            } catch (Throwable unused) {
                return Outcome.DOWN_ONLY;
            }
        } catch (Throwable unused2) {
            return Outcome.NOT_SENT;
        }
    }
}
