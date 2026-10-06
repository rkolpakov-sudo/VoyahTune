package ru.big.town.anative;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
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
    // IMP-09: priority lanes
    static final int LANE_0 = 0; // safety (CONNECTION bar, LIGHT_STATUS, DOOR)
    static final int LANE_1 = 1; // modes (GEAR, DRIVING_MODE signals)
    static final int LANE_2 = 2; // comfort (AMBIENT_TEMPERATURE, other VEHICLE_STATE)
    private static final int LANE_COUNT = 3;
    private static final int STARVE_LIMIT = 10;
    private static final int[] CAPACITY_PER_LANE = {8, 16, 32};
    // IMP-09 (L51): rate-limit per lane, events/sec; 0 = unlimited (default)
    static volatile int[] RATE_PER_LANE = {0, 0, 0};
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

    // IMP-09 (L51): backpressure-метрики в локальную диагностику
    String diagnostics() {
        StringBuilder sb = new StringBuilder("subscribers=").append(this.mailboxes.size());
        Iterator<Mailbox> it = this.mailboxes.iterator();
        int i = 0;
        while (it.hasNext()) {
            sb.append("\nm").append(i++).append(": ").append(it.next().diagnostics());
        }
        return sb.toString();
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

    static int tier(CanBusEvent canBusEvent) {
        // IMP-09: L0 safety (barriers + свет/двери), L1 modes, L2 comfort
        switch (canBusEvent.kind) {
            case CONNECTION: case CONNECTION_LOST: case LIGHT_STATUS: case DOOR:
                return LANE_0;
            case GEAR:
                return LANE_1;
            default:
                return LANE_2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class Mailbox {
        private final int[] capacityPerLane = {CAPACITY_PER_LANE[0], CAPACITY_PER_LANE[1], CAPACITY_PER_LANE[2]};
        private final int globalCapacity;
        private volatile boolean closed;
        private boolean drainScheduled;
        private long[] droppedPerLane = {0, 0, 0};
        private long[] acceptedPerLane = {0, 0, 0};
        private long[] peakQueueDepth = {0, 0, 0};
        private long[] lastDrainNs = {0, 0, 0};
        private int starveCount;
        private final Executor executor;
        private final int interestMask;
        private final Listener listener;
        private final Set<Integer> vehicleStateIds = new HashSet();
        private final ArrayDeque<CanBusEvent>[] queues = new ArrayDeque[] {
            new ArrayDeque<>(), new ArrayDeque<>(), new ArrayDeque<>()
        };
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
            this.globalCapacity = i2;
            if (iArr != null) {
                for (int i3 : iArr) {
                    this.vehicleStateIds.add(Integer.valueOf(i3));
                }
            }
            this.executor = executor;
            this.listener = listener;
            this.capacityPerLane[LANE_0] = Math.max(2, i2 / 4);
            this.capacityPerLane[LANE_1] = Math.max(2, i2 / 2);
            this.capacityPerLane[LANE_2] = Math.max(2, i2);
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
        void offer(CanBusEvent event) {
            if (accepts(event)) {
                synchronized (this) {
                    if (this.closed) {
                        return;
                    }
                    if (event.connectionEpoch <= this.invalidatedThroughEpoch) {
                        return;
                    }
                    if (event.connectionEpoch < this.acceptedEpoch) {
                        return;
                    }
                    if (event.connectionEpoch > this.acceptedEpoch) {
                        for (int lane = 0; lane < LANE_COUNT; lane++) {
                            this.queues[lane].clear();
                        }
                        this.lastAccepted.clear();
                        this.acceptedEpoch = event.connectionEpoch;
                    }
                    int lane = CanBusEventRouter.tier(event);
                    ArrayDeque<CanBusEvent> queue = this.queues[lane];
                    int key = event.signalKey();
                    CanBusEvent previous = this.lastAccepted.get(Integer.valueOf(key));
                    boolean z = true;
                    if (event.samePayload(previous)) {
                        if (this.drainScheduled || queue.isEmpty()) {
                            z = false;
                        } else {
                            this.drainScheduled = true;
                        }
                    } else {
                        this.lastAccepted.put(Integer.valueOf(key), event);
                        if (!event.isOrderedTransition()) removeQueuedLevel(key);
                        int capacity = this.capacityPerLane[lane];
                        if (queue.size() == capacity) {
                            // IMP-09: per-lane capacity
                            recordDrop(dropForCapacity(lane));
                        }
                        if (totalSize() >= this.globalCapacity) {
                            // Паритет с вендором: общая ёмкость mailbox = capacity подписки
                            recordDrop(dropGlobalCapacity());
                        }
                        queue.addLast(event);
                        this.acceptedPerLane[lane]++;
                        if (queue.size() > this.peakQueueDepth[lane]) {
                            this.peakQueueDepth[lane] = queue.size();
                        }
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

        private void removeQueuedLevel(int key) {
            for (int lane = 0; lane < LANE_COUNT; lane++) {
                ArrayDeque<CanBusEvent> queue = this.queues[lane];
                if (queue.isEmpty()) {
                    continue;
                }
                ArrayList arrayList = new ArrayList(queue.size());
                while (!queue.isEmpty()) {
                    CanBusEvent event = queue.removeFirst();
                    if (event.signalKey() != key) {
                        arrayList.add(event);
                    }
                }
                queue.addAll(arrayList);
            }
        }

        private CanBusEvent dropForCapacity(int lane) {
            ArrayDeque<CanBusEvent> queue = this.queues[lane];
            Iterator<CanBusEvent> it = queue.iterator();
            while (it.hasNext()) {
                CanBusEvent next = it.next();
                if (next.kind != CanBusEvent.Kind.CONNECTION && next.kind != CanBusEvent.Kind.CONNECTION_LOST && !next.isOrderedTransition()) {
                    it.remove();
                    return next;
                }
            }
            Iterator<CanBusEvent> it2 = queue.iterator();
            while (it2.hasNext()) {
                CanBusEvent next2 = it2.next();
                if (next2.kind != CanBusEvent.Kind.CONNECTION && next2.kind != CanBusEvent.Kind.CONNECTION_LOST) {
                    it2.remove();
                    return next2;
                }
            }
            return queue.removeFirst();
        }

        private int totalSize() {
            return this.queues[LANE_0].size() + this.queues[LANE_1].size() + this.queues[LANE_2].size();
        }

        // Паритет вендора: общая ёмкость mailbox. Дроп: сначала level (L0→L2),
        // затем ordered (L0→L2); барьеры CONNECTION/CONNECTION_LOST не дропаются
        // до крайнего fallback (как vendor dropForCapacity).
        private CanBusEvent dropGlobalCapacity() {
            for (int lane = 0; lane < LANE_COUNT; lane++) {
                Iterator<CanBusEvent> it = this.queues[lane].iterator();
                while (it.hasNext()) {
                    CanBusEvent next = it.next();
                    if (next.kind != CanBusEvent.Kind.CONNECTION && next.kind != CanBusEvent.Kind.CONNECTION_LOST && !next.isOrderedTransition()) {
                        it.remove();
                        return next;
                    }
                }
            }
            for (int lane = 0; lane < LANE_COUNT; lane++) {
                Iterator<CanBusEvent> it = this.queues[lane].iterator();
                while (it.hasNext()) {
                    CanBusEvent next = it.next();
                    if (next.kind != CanBusEvent.Kind.CONNECTION && next.kind != CanBusEvent.Kind.CONNECTION_LOST) {
                        it.remove();
                        return next;
                    }
                }
            }
            for (int lane = 0; lane < LANE_COUNT; lane++) {
                if (!this.queues[lane].isEmpty()) {
                    return this.queues[lane].removeFirst();
                }
            }
            return null;
        }

        private void recordDrop(CanBusEvent dropped) {
            if (dropped == null) {
                return;
            }
            int lane = CanBusEventRouter.tier(dropped);
            Integer key = Integer.valueOf(dropped.signalKey());
            if (this.lastAccepted.get(key) == dropped) {
                this.lastAccepted.remove(key);
            }
            this.droppedPerLane[lane]++;
        }

        // IMP-09 (L51): backpressure-метрики mailbox
        String diagnostics() {
            return "queues=[" + this.queues[LANE_0].size() + "," + this.queues[LANE_1].size() + "," + this.queues[LANE_2].size()
                + "] accepted=" + Arrays.toString(this.acceptedPerLane)
                + " dropped=" + Arrays.toString(this.droppedPerLane)
                + " peak=" + Arrays.toString(this.peakQueueDepth);
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
                // IMP-09: drain priority L0 > L1 > L2 с rate-limit per lane (L51)
                long now = System.nanoTime();
                CanBusEvent event = null;
                for (int lane = LANE_0; lane <= LANE_2; lane++) {
                    if (this.queues[lane].isEmpty()) {
                        continue;
                    }
                    int rate = RATE_PER_LANE[lane];
                    if (rate > 0 && this.lastDrainNs[lane] != 0 && now - this.lastDrainNs[lane] < 1000000000L / rate) {
                        continue; // lane в rate-окне → пропуск drain'а этой lane на один цикл
                    }
                    event = this.queues[lane].pollFirst();
                    this.lastDrainNs[lane] = now;
                    break;
                }
                if (event == null) {
                    // пусто ИЛИ все непустые lane в rate-окне (ждём следующий offer)
                    this.drainScheduled = false;
                    return;
                }
                // Starvation guard: после STARVE_LIMIT приоритетных drain'ов отдать L2
                int tier = CanBusEventRouter.tier(event);
                if (tier < LANE_2 && !this.queues[LANE_2].isEmpty()) {
                    this.starveCount++;
                    if (this.starveCount >= STARVE_LIMIT) {
                        this.queues[tier].addFirst(event);
                        event = this.queues[LANE_2].pollFirst();
                        this.lastDrainNs[LANE_2] = now;
                        this.starveCount = 0;
                    }
                } else if (tier == LANE_2 || this.queues[LANE_2].isEmpty()) {
                    this.starveCount = 0;
                }
                try {
                    this.listener.onCanBusEvent(event);
                } catch (RuntimeException unused) {
                }
                synchronized (this) {
                    if (this.closed) {
                        return;
                    }
                    boolean empty = true;
                    for (int lane = 0; lane < LANE_COUNT; lane++) {
                        if (!this.queues[lane].isEmpty()) {
                            empty = false;
                            break;
                        }
                    }
                    if (empty) {
                        this.drainScheduled = false;
                        return;
                    }
                    scheduleDrain();
                }
            }
        }

        synchronized void close() {
            this.closed = true;
            for (int lane = 0; lane < LANE_COUNT; lane++) {
                this.queues[lane].clear();
            }
            this.drainScheduled = false;
        }

        synchronized void invalidateThrough(long j) {
            if (!this.closed && j > this.invalidatedThroughEpoch) {
                this.invalidatedThroughEpoch = j;
                for (int lane = 0; lane < LANE_COUNT; lane++) {
                    this.queues[lane].clear();
                }
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
