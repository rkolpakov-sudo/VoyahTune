package ru.big.town.anative;

/* JADX INFO: compiled from: D8$$SyntheticClass */
/* JADX INFO: loaded from: classes2.dex */
public final /* synthetic */ class HeadlightCanTransport$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ HeadlightCanTransport f$0;

    public /* synthetic */ HeadlightCanTransport$$ExternalSyntheticLambda1(HeadlightCanTransport headlightCanTransport) {
        this.f$0 = headlightCanTransport;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.f$0.bindCanBus();
    }
}
