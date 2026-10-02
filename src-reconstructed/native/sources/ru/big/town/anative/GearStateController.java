package ru.big.town.anative;

import android.os.Handler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: loaded from: classes2.dex */
final class GearStateController {
    private final Handler serialHandler;
    private final List<Registration> registrations = new ArrayList();
    private volatile int currentGear = -1;

    interface Listener {
        void onGearChanged(int i);
    }

    GearStateController(Handler handler) {
        this.serialHandler = handler;
    }

    Subscription subscribe(Handler handler, Listener listener) {
        if (handler == null || listener == null) {
            throw new IllegalArgumentException("deliveryHandler/listener required");
        }
        final Registration registration = new Registration(handler, listener);
        this.serialHandler.post(new Runnable() { // from class: ru.big.town.anative.GearStateController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1874lambda$subscribe$0$rubigtownanativeGearStateController(registration);
            }
        });
        return new Subscription(this, registration);
    }

    /* JADX INFO: renamed from: lambda$subscribe$0$ru-big-town-anative-GearStateController, reason: not valid java name */
    /* synthetic */ void m1874lambda$subscribe$0$rubigtownanativeGearStateController(Registration registration) {
        if (registration.active.get()) {
            this.registrations.add(registration);
            int i = this.currentGear;
            if (i >= 0) {
                registration.deliver(i);
            }
        }
    }

    int currentGear() {
        return this.currentGear;
    }

    void reset() {
        this.currentGear = -1;
    }

    void accept(int i) {
        if (i < 0 || i == this.currentGear) {
            return;
        }
        this.currentGear = i;
        Iterator<Registration> it = this.registrations.iterator();
        while (it.hasNext()) {
            it.next().deliver(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void remove(final Registration registration) {
        if (registration.active.compareAndSet(true, false)) {
            this.serialHandler.post(new Runnable() { // from class: ru.big.town.anative.GearStateController$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1873lambda$remove$1$rubigtownanativeGearStateController(registration);
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$remove$1$ru-big-town-anative-GearStateController, reason: not valid java name */
    /* synthetic */ void m1873lambda$remove$1$rubigtownanativeGearStateController(Registration registration) {
        this.registrations.remove(registration);
    }

    static final class Subscription implements AutoCloseable {
        private final GearStateController owner;
        private final Registration registration;

        Subscription(GearStateController gearStateController, Registration registration) {
            this.owner = gearStateController;
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

        void deliver(final int i) {
            this.deliveryHandler.post(new Runnable() { // from class: ru.big.town.anative.GearStateController$Registration$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1875x6a1c7e68(i);
                }
            });
        }

        /* JADX INFO: renamed from: lambda$deliver$0$ru-big-town-anative-GearStateController$Registration, reason: not valid java name */
        /* synthetic */ void m1875x6a1c7e68(int i) {
            if (this.active.get()) {
                this.listener.onGearChanged(i);
            }
        }
    }
}
