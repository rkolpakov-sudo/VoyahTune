package ru.big.town.anative;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CanBusEventRouterTest {
    @Test
    public void routesOnlyRequestedKindsAndVehicleIds() {
        CanBusEventRouter router = new CanBusEventRouter();
        ManualExecutor executor = new ManualExecutor();
        List<CanBusEvent> delivered = new ArrayList<>();
        router.subscribe(CanBusEventRouter.INTEREST_DOOR
                        | CanBusEventRouter.INTEREST_VEHICLE_STATE,
                new int[]{545}, executor, delivered::add);

        router.dispatch(door(1, 1));
        router.dispatch(gear(2, 3));
        router.dispatch(vehicle(3, 957, 2));
        router.dispatch(vehicle(4, 545, 5));

        assertEquals(1, executor.size());
        executor.runAll();
        assertEquals(2, delivered.size());
        assertEquals(CanBusEvent.Kind.DOOR, delivered.get(0).kind);
        assertEquals(545, delivered.get(1).first);
    }

    @Test
    public void orderedTransitionsArePreservedWithOnePendingTask() {
        CanBusEventRouter router = new CanBusEventRouter();
        ManualExecutor executor = new ManualExecutor();
        List<Integer> gears = new ArrayList<>();
        router.subscribe(CanBusEventRouter.INTEREST_GEAR, null, executor,
                event -> gears.add(event.first));

        router.dispatch(gear(1, 0));
        router.dispatch(gear(2, 3));
        router.dispatch(gear(3, 0));
        router.dispatch(gear(4, 0));

        assertEquals(1, executor.size());
        executor.runAll();
        assertEquals(Arrays.asList(0, 3, 0), gears);
    }

    @Test
    public void connectionIsDeliveredAgainForANewEpoch() {
        CanBusEventRouter router = new CanBusEventRouter();
        ManualExecutor executor = new ManualExecutor();
        List<Long> epochs = new ArrayList<>();
        router.subscribe(CanBusEventRouter.INTEREST_CONNECTION, null, executor,
                event -> epochs.add(event.connectionEpoch));

        router.dispatch(CanBusEvent.connection(1, 1, 1));
        router.dispatch(CanBusEvent.connection(1, 2, 2));
        executor.runAll();
        router.dispatch(CanBusEvent.connection(2, 3, 3));
        executor.runAll();
        assertEquals(Arrays.asList(1L, 2L), epochs);
    }

    @Test
    public void connectionLostIsAnOrderedEpochBarrier() {
        CanBusEventRouter router = new CanBusEventRouter();
        ManualExecutor executor = new ManualExecutor();
        List<String> delivered = new ArrayList<>();
        router.subscribe(CanBusEventRouter.INTEREST_CONNECTION
                        | CanBusEventRouter.INTEREST_VEHICLE_STATE,
                new int[]{PowerHoldPolicy.POWER_HOLD_MODE_SWITCH_ID}, executor,
                event -> delivered.add(event.connectionEpoch + ":" + event.kind));

        router.dispatch(CanBusEvent.connection(1, 1, 1));
        router.dispatch(vehicle(2, PowerHoldPolicy.POWER_HOLD_MODE_SWITCH_ID, 1));
        router.invalidateThrough(1);
        router.dispatch(CanBusEvent.connectionLost(2, 3, 3, 1));
        router.dispatch(vehicle(4, PowerHoldPolicy.POWER_HOLD_MODE_SWITCH_ID, 0));
        executor.runAll();

        assertEquals(Arrays.asList("2:CONNECTION_LOST"), delivered);
    }

    @Test
    public void newEpochClearsQueuedEventsAndRejectsLateOldEpoch() {
        CanBusEventRouter router = new CanBusEventRouter();
        ManualExecutor executor = new ManualExecutor();
        List<String> delivered = new ArrayList<>();
        router.subscribe(CanBusEventRouter.INTEREST_CONNECTION
                        | CanBusEventRouter.INTEREST_DOOR
                        | CanBusEventRouter.INTEREST_GEAR,
                null, executor,
                event -> delivered.add(event.connectionEpoch + ":" + event.kind + ":"
                        + event.first));

        router.dispatch(CanBusEvent.door(CanBusEvent.Origin.LIVE, 1, 1, 1, 1));
        router.dispatch(CanBusEvent.gear(CanBusEvent.Origin.LIVE, 1, 2, 2, 3));
        router.dispatch(CanBusEvent.connection(2, 3, 3));
        router.dispatch(CanBusEvent.gear(CanBusEvent.Origin.LIVE, 1, 4, 4, 0));
        router.dispatch(CanBusEvent.gear(CanBusEvent.Origin.LIVE, 2, 5, 5, 0));

        executor.runAll();
        assertEquals(Arrays.asList("2:CONNECTION:0", "2:GEAR:0"), delivered);
    }

    @Test
    public void disconnectInvalidatesQueuedAndLateEventsBeforeReconnect() {
        CanBusEventRouter router = new CanBusEventRouter();
        ManualExecutor executor = new ManualExecutor();
        List<String> delivered = new ArrayList<>();
        router.subscribe(CanBusEventRouter.INTEREST_CONNECTION
                        | CanBusEventRouter.INTEREST_DOOR,
                null, executor,
                event -> delivered.add(event.connectionEpoch + ":" + event.kind));

        router.dispatch(CanBusEvent.connection(1, 1, 1));
        router.dispatch(CanBusEvent.door(
                CanBusEvent.Origin.LIVE, 1, 2, 2, 1));
        router.invalidateThrough(1);
        router.dispatch(CanBusEvent.door(
                CanBusEvent.Origin.LIVE, 1, 3, 3, 0));
        executor.runAll();
        assertTrue(delivered.isEmpty());

        router.dispatch(CanBusEvent.connection(2, 4, 4));
        executor.runAll();
        assertEquals(Arrays.asList("2:CONNECTION"), delivered);
    }

    @Test
    public void levelSignalsKeepLatestValuePerKey() {
        CanBusEventRouter router = new CanBusEventRouter();
        ManualExecutor executor = new ManualExecutor();
        List<CanBusEvent> delivered = new ArrayList<>();
        router.subscribe(CanBusEventRouter.INTEREST_VEHICLE_STATE,
                new int[]{545, 957}, executor, delivered::add);

        router.dispatch(vehicle(1, 545, 1));
        router.dispatch(vehicle(2, 957, 2));
        router.dispatch(vehicle(3, 545, 5));

        assertEquals(1, executor.size());
        executor.runAll();
        assertEquals(2, delivered.size());
        assertEquals(957, delivered.get(0).first);
        assertEquals(545, delivered.get(1).first);
        assertEquals(5, delivered.get(1).second);
    }

    @Test
    public void closeDropsQueuedAndFutureEvents() {
        CanBusEventRouter router = new CanBusEventRouter();
        ManualExecutor executor = new ManualExecutor();
        List<CanBusEvent> delivered = new ArrayList<>();
        CanBusEventRouter.Subscription subscription = router.subscribe(
                CanBusEventRouter.INTEREST_DOOR, null, executor, delivered::add);

        router.dispatch(door(1, 1));
        subscription.close();
        subscription.close();
        router.dispatch(door(2, 0));
        executor.runAll();

        assertTrue(delivered.isEmpty());
        assertEquals(0, router.subscriberCount());
    }

    @Test
    public void rejectedExecutorCanBeScheduledAgain() {
        CanBusEventRouter router = new CanBusEventRouter();
        RejectOnceExecutor executor = new RejectOnceExecutor();
        List<CanBusEvent> delivered = new ArrayList<>();
        router.subscribe(CanBusEventRouter.INTEREST_DOOR, null, executor, delivered::add);

        router.dispatch(door(1, 1));
        assertTrue(delivered.isEmpty());
        router.dispatch(door(2, 1)); // duplicate retries scheduling of the queued edge
        executor.runAll();

        assertEquals(1, delivered.size());
        assertEquals(1, delivered.get(0).first);
    }

    @Test
    public void boundedMailboxDropsOldestTransition() {
        CanBusEventRouter router = new CanBusEventRouter();
        ManualExecutor executor = new ManualExecutor();
        List<Integer> doors = new ArrayList<>();
        router.subscribe(CanBusEventRouter.INTEREST_DOOR, null, executor,
                event -> doors.add(event.first), 2);

        router.dispatch(door(1, 0));
        router.dispatch(door(2, 1));
        router.dispatch(door(3, 0));
        executor.runAll();

        assertEquals(Arrays.asList(1, 0), doors);
    }

    @Test
    public void callbackBurstCannotEvictConnectionBarrier() {
        CanBusEventRouter router = new CanBusEventRouter();
        ManualExecutor executor = new ManualExecutor();
        List<CanBusEvent.Kind> delivered = new ArrayList<>();
        router.subscribe(CanBusEventRouter.INTEREST_CONNECTION
                        | CanBusEventRouter.INTEREST_GEAR,
                null, executor, event -> delivered.add(event.kind), 2);

        router.dispatch(CanBusEvent.connection(1, 1, 1));
        router.dispatch(gear(2, 3));
        router.dispatch(gear(3, 0));
        executor.runAll();

        assertEquals(Arrays.asList(CanBusEvent.Kind.CONNECTION, CanBusEvent.Kind.GEAR),
                delivered);
    }

    @Test
    public void overflowDropsCoalescibleLevelBeforeTransition() {
        CanBusEventRouter router = new CanBusEventRouter();
        ManualExecutor executor = new ManualExecutor();
        List<CanBusEvent.Kind> delivered = new ArrayList<>();
        router.subscribe(CanBusEventRouter.INTEREST_CONNECTION
                        | CanBusEventRouter.INTEREST_DOOR
                        | CanBusEventRouter.INTEREST_GEAR
                        | CanBusEventRouter.INTEREST_VEHICLE_STATE,
                new int[]{545}, executor, event -> delivered.add(event.kind), 3);

        router.dispatch(CanBusEvent.connection(1, 1, 1));
        router.dispatch(door(2, 1));
        router.dispatch(vehicle(3, 545, 5));
        router.dispatch(gear(4, 3));
        executor.runAll();

        assertEquals(Arrays.asList(CanBusEvent.Kind.CONNECTION,
                CanBusEvent.Kind.DOOR, CanBusEvent.Kind.GEAR), delivered);
    }

    @Test
    public void transitionDroppedByOverflowCanBeAcceptedAgain() {
        CanBusEventRouter router = new CanBusEventRouter();
        ManualExecutor executor = new ManualExecutor();
        List<CanBusEvent.Kind> delivered = new ArrayList<>();
        router.subscribe(CanBusEventRouter.INTEREST_CONNECTION
                        | CanBusEventRouter.INTEREST_DOOR
                        | CanBusEventRouter.INTEREST_GEAR,
                null, executor, event -> delivered.add(event.kind), 2);

        router.dispatch(CanBusEvent.connection(1, 1, 1));
        router.dispatch(door(2, 1));
        router.dispatch(gear(3, 3)); // evicts the not-yet-delivered door transition
        router.dispatch(door(4, 1)); // must not be suppressed by dedupe memory
        executor.runAll();

        assertEquals(Arrays.asList(CanBusEvent.Kind.CONNECTION, CanBusEvent.Kind.DOOR),
                delivered);
    }

    @Test
    public void listenerFailureDoesNotStopMailbox() {
        CanBusEventRouter router = new CanBusEventRouter();
        ManualExecutor executor = new ManualExecutor();
        List<Integer> delivered = new ArrayList<>();
        router.subscribe(CanBusEventRouter.INTEREST_GEAR, null, executor, event -> {
            if (event.first == 0) throw new IllegalStateException("boom");
            delivered.add(event.first);
        });

        router.dispatch(gear(1, 0));
        router.dispatch(gear(2, 3));
        executor.runAll();
        assertEquals(Arrays.asList(3), delivered);
    }

    // --- IMP-09 (L51): приоритетные полосы, rate-limit, starvation, backpressure ---

    @Test
    public void safetyEventOvertakesComfortQueue() {
        CanBusEventRouter router = new CanBusEventRouter();
        ManualExecutor executor = new ManualExecutor();
        List<CanBusEvent.Kind> delivered = new ArrayList<>();
        router.subscribe(CanBusEventRouter.INTEREST_DOOR | CanBusEventRouter.INTEREST_VEHICLE_STATE,
                new int[]{545}, executor, event -> delivered.add(event.kind));

        router.dispatch(vehicle(1, 545, 5)); // L2 comfort
        router.dispatch(door(2, 1));         // L0 safety

        executor.runAll();
        assertEquals(Arrays.asList(CanBusEvent.Kind.DOOR, CanBusEvent.Kind.VEHICLE_STATE), delivered);
    }

    @Test
    public void connectionBarrierOvertakesQueuedStateEvents() {
        CanBusEventRouter router = new CanBusEventRouter();
        ManualExecutor executor = new ManualExecutor();
        List<CanBusEvent.Kind> delivered = new ArrayList<>();
        router.subscribe(CanBusEventRouter.INTEREST_CONNECTION
                        | CanBusEventRouter.INTEREST_GEAR
                        | CanBusEventRouter.INTEREST_VEHICLE_STATE,
                new int[]{545}, executor, event -> delivered.add(event.kind));

        router.dispatch(vehicle(1, 545, 5));              // L2
        router.dispatch(gear(2, 3));                      // L1
        router.dispatch(CanBusEvent.connection(1, 7, 7)); // L0 (тот же epoch — без очистки)

        executor.runAll();
        assertEquals(Arrays.asList(CanBusEvent.Kind.CONNECTION,
                CanBusEvent.Kind.GEAR, CanBusEvent.Kind.VEHICLE_STATE), delivered);
    }

    @Test
    public void modesOvertakeComfortWithinSameEpoch() {
        CanBusEventRouter router = new CanBusEventRouter();
        ManualExecutor executor = new ManualExecutor();
        List<CanBusEvent.Kind> delivered = new ArrayList<>();
        router.subscribe(CanBusEventRouter.INTEREST_GEAR | CanBusEventRouter.INTEREST_VEHICLE_STATE,
                new int[]{545}, executor, event -> delivered.add(event.kind));

        router.dispatch(vehicle(1, 545, 5)); // L2
        router.dispatch(gear(2, 3));         // L1

        executor.runAll();
        assertEquals(Arrays.asList(CanBusEvent.Kind.GEAR, CanBusEvent.Kind.VEHICLE_STATE), delivered);
    }

    @Test
    public void perLaneCapacityDropsOldestInFullLane() {
        CanBusEventRouter router = new CanBusEventRouter();
        ManualExecutor executor = new ManualExecutor();
        List<Integer> gears = new ArrayList<>();
        router.subscribe(CanBusEventRouter.INTEREST_GEAR, null, executor,
                event -> gears.add(event.first), 32);

        // L1 capacity = 32/2 = 16; 17-е событие вытесняет самое старое
        for (int i = 1; i <= 17; i++) {
            router.dispatch(gear(i, i));
        }
        executor.runAll();

        assertEquals(16, gears.size());
        assertEquals(Integer.valueOf(2), gears.get(0));
        assertEquals(Integer.valueOf(17), gears.get(15));
    }

    @Test
    public void starvationGuardDeliversComfortEvent() {
        CanBusEventRouter router = new CanBusEventRouter();
        ManualExecutor executor = new ManualExecutor();
        List<CanBusEvent.Kind> delivered = new ArrayList<>();
        router.subscribe(CanBusEventRouter.INTEREST_DOOR | CanBusEventRouter.INTEREST_VEHICLE_STATE,
                new int[]{545}, executor, event -> delivered.add(event.kind), 64);

        router.dispatch(vehicle(1, 545, 5)); // L2, ждёт в очереди
        for (int i = 2; i <= 13; i++) {
            router.dispatch(door(i, i % 2)); // L0, 12 событий подряд (L0 cap = 64/4 = 16)
        }
        executor.runAll();

        // STARVE_LIMIT=10: девять дверей, затем форс-drain L2, затем остальные двери
        assertEquals(13, delivered.size());
        assertEquals(CanBusEvent.Kind.VEHICLE_STATE, delivered.get(9));
        assertEquals(CanBusEvent.Kind.DOOR, delivered.get(0));
        assertEquals(CanBusEvent.Kind.DOOR, delivered.get(12));
    }

    @Test
    public void rateLimitBlocksDrainWithinWindow() throws Exception {
        int[] original = CanBusEventRouter.RATE_PER_LANE;
        CanBusEventRouter.RATE_PER_LANE = new int[]{50, 0, 0}; // L0: окно 20ms
        try {
            CanBusEventRouter router = new CanBusEventRouter();
            ManualExecutor executor = new ManualExecutor();
            List<Integer> doors = new ArrayList<>();
            router.subscribe(CanBusEventRouter.INTEREST_DOOR, null, executor,
                    event -> doors.add(event.first));

            router.dispatch(door(1, 0));
            executor.runAll();
            assertEquals(Arrays.asList(0), doors); // первый drain вне окна

            router.dispatch(door(2, 1));
            executor.runAll();
            assertEquals(Arrays.asList(0), doors); // в окне → drain пропущен

            Thread.sleep(60); // окно 20ms истекло
            router.dispatch(door(3, 0));
            executor.runAll();
            assertEquals(Arrays.asList(0, 1), doors); // после окна доставлена
        } finally {
            CanBusEventRouter.RATE_PER_LANE = original;
        }
    }

    @Test
    public void rateLimitZeroMeansUnlimited() {
        assertEquals(0, CanBusEventRouter.RATE_PER_LANE[0]);
        CanBusEventRouter router = new CanBusEventRouter();
        ManualExecutor executor = new ManualExecutor();
        List<Integer> doors = new ArrayList<>();
        router.subscribe(CanBusEventRouter.INTEREST_DOOR, null, executor,
                event -> doors.add(event.first));

        router.dispatch(door(1, 1));
        router.dispatch(door(2, 0));
        router.dispatch(door(3, 1));
        executor.runAll();

        assertEquals(Arrays.asList(1, 0, 1), doors);
    }

    @Test
    public void diagnosticsExposeBackpressureCounters() {
        CanBusEventRouter router = new CanBusEventRouter();
        ManualExecutor executor = new ManualExecutor();
        router.subscribe(CanBusEventRouter.INTEREST_CONNECTION | CanBusEventRouter.INTEREST_VEHICLE_STATE,
                new int[]{545, 957, 546, 547}, executor, event -> { }, 4);

        router.dispatch(CanBusEvent.connection(1, 1, 1)); // L0
        router.dispatch(vehicle(1, 545, 5));               // L2
        router.dispatch(vehicle(1, 957, 6));               // L2
        router.dispatch(vehicle(1, 546, 7));               // L2
        router.dispatch(vehicle(1, 547, 8));               // total=4 → вытесняет 545 (level)
        executor.runAll();

        String diag = router.diagnostics();
        assertTrue(diag.contains("subscribers=1"));
        assertTrue(diag.contains("dropped=[0, 0, 1]"));
        assertTrue(diag.contains("accepted=[1, 0, 4]"));
        assertTrue(diag.contains("peak=[1, 0, 3]"));
    }

    @Test
    public void transitionBurstYieldsToAnotherConsumer() {
        CanBusEventRouter router = new CanBusEventRouter();
        ManualExecutor executor = new ManualExecutor();
        List<String> delivered = new ArrayList<>();
        router.subscribe(CanBusEventRouter.INTEREST_GEAR, null, executor,
                event -> delivered.add("gear:" + event.first));
        router.subscribe(CanBusEventRouter.INTEREST_DOOR, null, executor,
                event -> delivered.add("door:" + event.first));

        router.dispatch(gear(1, 0));
        router.dispatch(gear(2, 3));
        router.dispatch(gear(3, 0));
        router.dispatch(door(4, 1));

        executor.runNext();
        executor.runNext();
        assertEquals(Arrays.asList("gear:0", "door:1"), delivered);
        executor.runAll();
        assertEquals(Arrays.asList("gear:0", "door:1", "gear:3", "gear:0"),
                delivered);
    }

    private static CanBusEvent door(long sequence, int value) {
        return CanBusEvent.door(CanBusEvent.Origin.LIVE, 1, sequence, sequence, value);
    }

    private static CanBusEvent gear(long sequence, int value) {
        return CanBusEvent.gear(CanBusEvent.Origin.LIVE, 1, sequence, sequence, value);
    }

    private static CanBusEvent vehicle(long sequence, int id, int value) {
        return CanBusEvent.vehicleState(
                CanBusEvent.Origin.LIVE, 1, sequence, sequence, id, value);
    }

    private static class ManualExecutor implements Executor {
        final ArrayDeque<Runnable> tasks = new ArrayDeque<>();

        @Override
        public void execute(Runnable command) {
            tasks.addLast(command);
        }

        int size() {
            return tasks.size();
        }

        void runAll() {
            while (!tasks.isEmpty()) tasks.removeFirst().run();
        }

        void runNext() {
            tasks.removeFirst().run();
        }
    }

    private static final class RejectOnceExecutor extends ManualExecutor {
        private boolean rejected;

        @Override
        public void execute(Runnable command) {
            if (!rejected) {
                rejected = true;
                throw new IllegalStateException("rejected");
            }
            super.execute(command);
        }
    }
}
