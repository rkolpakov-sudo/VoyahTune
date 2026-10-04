package ru.big.town.anative;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: loaded from: classes2.dex */
final class CanBusEventRouter {
    private static final int DEFAULT_MAILBOX_CAPACITY = 32;
    private static final int DRAIN_SLICE = 1;
    static final int INTEREST_AMBIENT_TEMPERATURE = 32;
    static final int INTEREST_CONNECTION = 1;
    static final int INTEREST_DOOR = 2;
    static final int INTEREST_GEAR = 4;
    static final int INTEREST_LIGHT_STATUS = 8;
    static final int INTEREST_VEHICLE_STATE = 16;
    private final CopyOnWriteArrayList<Mailbox> mailboxes = new CopyOnWriteArrayList<>();

    interface Listener {
        void onCanBusEvent(CanBusEvent canBusEvent);
    }

    CanBusEventRouter() {
    }

    Subscription subscribe(int i, int[] iArr, Executor executor, Listener listener) {
        return subscribe(i, iArr, executor, listener, 32);
    }

    Subscription subscribe(int i, int[] iArr, Executor executor, Listener listener, int i2) {
        if (executor == null || listener == null) {
            throw new IllegalArgumentException("executor/listener required");
        }
        Mailbox mailbox = new Mailbox(i, iArr, executor, listener, i2);
        this.mailboxes.add(mailbox);
        return new Subscription(this, mailbox);
    }

    void dispatch(CanBusEvent canBusEvent) {
        if (canBusEvent == null) {
            return;
        }
        Iterator<Mailbox> it = this.mailboxes.iterator();
        while (it.hasNext()) {
            it.next().offer(canBusEvent);
        }
    }

    boolean hasInterest(int i) {
        Iterator<Mailbox> it = this.mailboxes.iterator();
        while (it.hasNext()) {
            if (it.next().hasInterest(i)) {
                return true;
            }
        }
        return false;
    }

    boolean hasVehicleStateInterest(int i) {
        Iterator<Mailbox> it = this.mailboxes.iterator();
        while (it.hasNext()) {
            if (it.next().acceptsVehicleState(i)) {
                return true;
            }
        }
        return false;
    }

    int subscriberCount() {
        return this.mailboxes.size();
    }

    void invalidateThrough(long j) {
        Iterator<Mailbox> it = this.mailboxes.iterator();
        while (it.hasNext()) {
            it.next().invalidateThrough(j);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void remove(Mailbox mailbox) {
        this.mailboxes.remove(mailbox);
        mailbox.close();
    }

    static final class Subscription implements AutoCloseable {
        private final AtomicBoolean closed = new AtomicBoolean();
        private final Mailbox mailbox;
        private final CanBusEventRouter owner;

        Subscription(CanBusEventRouter canBusEventRouter, Mailbox mailbox) {
            this.owner = canBusEventRouter;
            this.mailbox = mailbox;
        }

        void offer(CanBusEvent canBusEvent) {
            if (this.closed.get()) {
                return;
            }
            this.mailbox.offer(canBusEvent);
        }

        void forgetSignal(CanBusEvent.Kind kind) {
            if (this.closed.get()) {
                return;
            }
            this.mailbox.forgetSignal(kind);
        }

        @Override // java.lang.AutoCloseable
        public void close() {
            if (this.closed.compareAndSet(false, true)) {
                this.owner.remove(this.mailbox);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class Mailbox {
        private final int capacity;
        private volatile boolean closed;
        private boolean drainScheduled;
        private long dropped;
        private final Executor executor;
        private final int interestMask;
        private final Listener listener;
        private final Set<Integer> vehicleStateIds = new HashSet();
        private final ArrayDeque<CanBusEvent> queue = new ArrayDeque<>();
        private final Map<Integer, CanBusEvent> lastAccepted = new HashMap();
        private final Runnable drainRunnable = new Runnable() { // from class: ru.big.town.anative.CanBusEventRouter$Mailbox$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                CanBusEventRouter.Mailbox.this.drain();
            }
        };
        private long acceptedEpoch = Long.MIN_VALUE;
        private long invalidatedThroughEpoch = Long.MIN_VALUE;

        Mailbox(int i, int[] iArr, Executor executor, Listener listener, int i2) {
            if (i2 < 2) {
                throw new IllegalArgumentException("capacity must be >= 2");
            }
            this.interestMask = i;
            if (iArr != null) {
                for (int i3 : iArr) {
                    this.vehicleStateIds.add(Integer.valueOf(i3));
                }
            }
            this.executor = executor;
            this.listener = listener;
            this.capacity = i2;
        }

        boolean hasInterest(int i) {
            return (this.closed || (this.interestMask & i) == 0) ? false : true;
        }

        synchronized void forgetSignal(CanBusEvent.Kind kind) {
            if (kind != null) {
                this.lastAccepted.remove(Integer.valueOf(kind.ordinal() << 24));
            }
        }

        boolean acceptsVehicleState(int i) {
            return hasInterest(16) && this.vehicleStateIds.contains(Integer.valueOf(i));
        }

        /* JADX WARN: Code duplicated, block: B:40:0x00af  */
        void offer(CanBusEvent canBusEvent) {
            if (accepts(canBusEvent)) {
                synchronized (this) {
                    if (this.closed) {
                        return;
                    }
                    if (canBusEvent.connectionEpoch <= this.invalidatedThroughEpoch) {
                        return;
                    }
                    if (canBusEvent.connectionEpoch < this.acceptedEpoch) {
                        return;
                    }
                    if (canBusEvent.connectionEpoch > this.acceptedEpoch) {
                        this.queue.clear();
                        this.lastAccepted.clear();
                        this.acceptedEpoch = canBusEvent.connectionEpoch;
                    }
                    int iSignalKey = canBusEvent.signalKey();
                    boolean z = true;
                    if (canBusEvent.samePayload(this.lastAccepted.get(Integer.valueOf(iSignalKey)))) {
                        if (this.drainScheduled || this.queue.isEmpty()) {
                            z = false;
                        } else {
                            this.drainScheduled = true;
                        }
                    } else {
                        this.lastAccepted.put(Integer.valueOf(iSignalKey), canBusEvent);
                        if (!canBusEvent.isOrderedTransition()) {
                            removeQueuedLevel(iSignalKey);
                        }
                        if (this.queue.size() == this.capacity) {
                            CanBusEvent canBusEventDropForCapacity = dropForCapacity();
                            if (this.lastAccepted.get(Integer.valueOf(canBusEventDropForCapacity.signalKey())) == canBusEventDropForCapacity) {
                                this.lastAccepted.remove(Integer.valueOf(canBusEventDropForCapacity.signalKey()));
                            }
                            this.dropped++;
                        }
                        this.queue.addLast(canBusEvent);
                        if (this.drainScheduled) {
                            z = false;
                        } else {
                            this.drainScheduled = true;
                        }
                    }
                    if (z) {
                        scheduleDrain();
                    }
                }
            }
        }

        private boolean accepts(CanBusEvent canBusEvent) {
            switch (AnonymousClass1.$SwitchMap$ru$big$town$anative$CanBusEvent$Kind[canBusEvent.kind.ordinal()]) {
                case 1:
                    return hasInterest(1);
                case 2:
                    return hasInterest(1);
                case 3:
                    return hasInterest(2);
                case 4:
                    return hasInterest(4);
                case 5:
                    return hasInterest(8);
                case 6:
                    return acceptsVehicleState(canBusEvent.first);
                case 7:
                    return hasInterest(32);
                default:
                    return false;
            }
        }

        private void removeQueuedLevel(int i) {
            if (this.queue.isEmpty()) {
                return;
            }
            ArrayList arrayList = new ArrayList(this.queue.size());
            while (!this.queue.isEmpty()) {
                CanBusEvent canBusEventRemoveFirst = this.queue.removeFirst();
                if (canBusEventRemoveFirst.signalKey() != i) {
                    arrayList.add(canBusEventRemoveFirst);
                }
            }
            this.queue.addAll(arrayList);
        }

        private CanBusEvent dropForCapacity() {
            Iterator<CanBusEvent> it = this.queue.iterator();
            while (it.hasNext()) {
                CanBusEvent next = it.next();
                if (next.kind != CanBusEvent.Kind.CONNECTION && next.kind != CanBusEvent.Kind.CONNECTION_LOST && !next.isOrderedTransition()) {
                    it.remove();
                    return next;
                }
            }
            Iterator<CanBusEvent> it2 = this.queue.iterator();
            while (it2.hasNext()) {
                CanBusEvent next2 = it2.next();
                if (next2.kind != CanBusEvent.Kind.CONNECTION && next2.kind != CanBusEvent.Kind.CONNECTION_LOST) {
                    it2.remove();
                    return next2;
                }
            }
            return this.queue.removeFirst();
        }

        private void scheduleDrain() {
            try {
                this.executor.execute(this.drainRunnable);
            } catch (RuntimeException unused) {
                synchronized (this) {
                    this.drainScheduled = false;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void drain() {
            synchronized (this) {
                if (this.closed) {
                    return;
                }
                CanBusEvent canBusEventPollFirst = this.queue.pollFirst();
                if (canBusEventPollFirst == null) {
                    this.drainScheduled = false;
                    return;
                }
                try {
                    this.listener.onCanBusEvent(canBusEventPollFirst);
                } catch (RuntimeException unused) {
                }
                synchronized (this) {
                    if (this.closed) {
                        return;
                    }
                    boolean zIsEmpty = this.queue.isEmpty();
                    if (zIsEmpty) {
                        this.drainScheduled = false;
                    }
                    if (zIsEmpty) {
                        return;
                    }
                    scheduleDrain();
                }
            }
        }

        synchronized void close() {
            this.closed = true;
            this.queue.clear();
            this.drainScheduled = false;
        }

        synchronized void invalidateThrough(long j) {
            if (!this.closed && j > this.invalidatedThroughEpoch) {
                this.invalidatedThroughEpoch = j;
                this.queue.clear();
                this.lastAccepted.clear();
            }
        }
    }

    /* JADX INFO: renamed from: ru.big.town.anative.CanBusEventRouter$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$ru$big$town$anative$CanBusEvent$Kind;

        static {
            int[] iArr = new int[CanBusEvent.Kind.values().length];
            $SwitchMap$ru$big$town$anative$CanBusEvent$Kind = iArr;
            try {
                iArr[CanBusEvent.Kind.CONNECTION.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$ru$big$town$anative$CanBusEvent$Kind[CanBusEvent.Kind.CONNECTION_LOST.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$ru$big$town$anative$CanBusEvent$Kind[CanBusEvent.Kind.DOOR.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$ru$big$town$anative$CanBusEvent$Kind[CanBusEvent.Kind.GEAR.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$ru$big$town$anative$CanBusEvent$Kind[CanBusEvent.Kind.LIGHT_STATUS.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$ru$big$town$anative$CanBusEvent$Kind[CanBusEvent.Kind.VEHICLE_STATE.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$ru$big$town$anative$CanBusEvent$Kind[CanBusEvent.Kind.AMBIENT_TEMPERATURE.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
        }
    }
}
