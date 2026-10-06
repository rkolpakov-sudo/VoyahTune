package ru.big.town.anative;

import android.os.Handler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: loaded from: classes2.dex */
final class DriverDoorStateController {
    private final Handler serialHandler;
    private final List<Registration> registrations = new ArrayList();
    private volatile int currentFrontLeft = -1;

    interface Listener {
        void onDriverDoorChanged(State state);
    }

    enum Source {
        LIVE,
        SNAPSHOT,
        REPLAY
    }

    static final class State {
        final int frontLeft;
        final Source source;

        State(int i, Source source) {
            this.frontLeft = i;
            this.source = source;
        }

        boolean isLive() {
            return this.source == Source.LIVE;
        }
    }

    DriverDoorStateController(Handler handler) {
        this.serialHandler = handler;
    }

    Subscription subscribe(Handler deliveryHandler, Listener listener) {
        if (deliveryHandler == null || listener == null) {
            throw new IllegalArgumentException("deliveryHandler/listener required");
        }
        final Registration registration = new Registration(deliveryHandler, listener);
        this.serialHandler.post(new Runnable() { // from class: ru.big.town.anative.DriverDoorStateController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DriverDoorStateController.this.m1870lambda$subscribe$0$rubigtownanativeDriverDoorStateController(registration);
            }
        });
        return new Subscription(this, registration);
    }

    /* JADX INFO: renamed from: lambda$subscribe$0$ru-big-town-anative-DriverDoorStateController, reason: not valid java name */
    /* synthetic */ void m1870lambda$subscribe$0$rubigtownanativeDriverDoorStateController(Registration registration) {
        if (registration.active.get()) {
            this.registrations.add(registration);
            int i = this.currentFrontLeft;
            if (i >= 0) {
                registration.deliver(new State(i, Source.REPLAY));
            }
        }
    }

    int currentFrontLeft() {
        return this.currentFrontLeft;
    }

    void reset() {
        this.currentFrontLeft = -1;
    }

    void accept(int i, Source source) {
        if (i < 0) {
            return;
        }
        this.currentFrontLeft = i;
        State state = new State(i, source);
        Iterator<Registration> it = this.registrations.iterator();
        while (it.hasNext()) {
            it.next().deliver(state);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void remove(final Registration registration) {
        if (registration.active.compareAndSet(true, false)) {
            this.serialHandler.post(new Runnable() { // from class: ru.big.town.anative.DriverDoorStateController$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DriverDoorStateController.this.m1869lambda$remove$1$rubigtownanativeDriverDoorStateController(registration);
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$remove$1$ru-big-town-anative-DriverDoorStateController, reason: not valid java name */
    /* synthetic */ void m1869lambda$remove$1$rubigtownanativeDriverDoorStateController(Registration registration) {
        this.registrations.remove(registration);
    }

    static final class Subscription implements AutoCloseable {
        private final DriverDoorStateController owner;
        private final Registration registration;

        Subscription(DriverDoorStateController driverDoorStateController, Registration registration) {
            this.owner = driverDoorStateController;
            this.registration = registration;
        }

        @Override // java.lang.AutoCloseable
        public void close() {
            this.owner.remove(this.registration);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class Registration {
        final AtomicBoolean active = new AtomicBoolean(true);
        final Handler deliveryHandler;
        final Listener listener;

        Registration(Handler handler, Listener listener) {
            this.deliveryHandler = handler;
            this.listener = listener;
        }

        void deliver(final State state) {
            this.deliveryHandler.post(new Runnable() { // from class: ru.big.town.anative.DriverDoorStateController$Registration$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DriverDoorStateController.Registration.this.m1871xb77d6d8f(state);
                }
            });
        }

        /* JADX INFO: renamed from: lambda$deliver$0$ru-big-town-anative-DriverDoorStateController$Registration, reason: not valid java name */
        /* synthetic */ void m1871xb77d6d8f(State state) {
            if (this.active.get()) {
                this.listener.onDriverDoorChanged(state);
            }
        }
    }
}
