package ru.big.town.anative;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.*;

public class PendingApplySeriesTest {

    private static final class RecordingRunner implements PendingApplySeries.Runner {
        int cycles;
        long gate;
        long generation;
        long epoch;
        boolean manual;
        final List<String> ran = new ArrayList();

        @Override // ru.big.town.anative.PendingApplySeries.Runner
        public void run(Runnable completion, long gate, long generation, long epoch, boolean manual) {
            this.cycles++;
            this.gate = gate;
            this.generation = generation;
            this.epoch = epoch;
            this.manual = manual;
            completion.run();
        }
    }

    private static Runnable payload(final String str, final List<String> list) {
        return new Runnable() {
            @Override // java.lang.Runnable
            public final void run() {
                list.add(str);
            }
        };
    }

    @Test public void burstCoalescesIntoSingleCycleRunningAllPayloadsInOrder() {
        RecordingRunner runner = new RecordingRunner();
        PendingApplySeries series = new PendingApplySeries(runner);
        List<String> ran = new ArrayList();
        PendingApplySeries.Offer first = series.offer(10L, 1L, 5L, true, payload("a", ran));
        PendingApplySeries.Offer second = series.offer(11L, 1L, 6L, true, payload("b", ran));
        PendingApplySeries.Offer third = series.offer(12L, 1L, 7L, true, payload("c", ran));
        assertNull(first.superseded);
        assertNotNull(second.superseded);
        assertNotNull(third.superseded);
        first.toPost.run();
        second.toPost.run();
        third.toPost.run();
        assertEquals(1, runner.cycles);
        assertEquals(12L, runner.gate);
        assertEquals(7L, runner.epoch);
        assertTrue(runner.manual);
        assertEquals(3, ran.size());
        assertEquals("a", ran.get(0));
        assertEquals("b", ran.get(1));
        assertEquals("c", ran.get(2));
    }

    @Test public void supersededTaskRunBeforeOfferDoesNothing() {
        RecordingRunner runner = new RecordingRunner();
        PendingApplySeries series = new PendingApplySeries(runner);
        List<String> ran = new ArrayList();
        PendingApplySeries.Offer first = series.offer(10L, 1L, 5L, true, payload("a", ran));
        PendingApplySeries.Offer second = series.offer(11L, 1L, 6L, true, payload("b", ran));
        assertNotNull(second.superseded);
        second.superseded.run();
        assertEquals(0, runner.cycles);
        assertTrue(ran.isEmpty());
        second.toPost.run();
        assertEquals(1, runner.cycles);
        assertEquals(2, ran.size());
    }

    @Test public void offerAfterRunStartsNewCycle() {
        RecordingRunner runner = new RecordingRunner();
        PendingApplySeries series = new PendingApplySeries(runner);
        List<String> ran = new ArrayList();
        PendingApplySeries.Offer first = series.offer(10L, 1L, 5L, true, payload("a", ran));
        first.toPost.run();
        PendingApplySeries.Offer second = series.offer(11L, 1L, 6L, true, payload("b", ran));
        assertNull(second.superseded);
        second.toPost.run();
        assertEquals(2, runner.cycles);
        assertEquals(2, ran.size());
        assertEquals("a", ran.get(0));
        assertEquals("b", ran.get(1));
    }

    @Test public void nullPayloadStillCarriesEarlierPayloads() {
        RecordingRunner runner = new RecordingRunner();
        PendingApplySeries series = new PendingApplySeries(runner);
        List<String> ran = new ArrayList();
        PendingApplySeries.Offer first = series.offer(10L, 1L, 5L, true, payload("a", ran));
        PendingApplySeries.Offer second = series.offer(11L, 1L, 6L, true, null);
        assertNotNull(second.superseded);
        first.toPost.run();
        second.toPost.run();
        assertEquals(1, runner.cycles);
        assertEquals(1, ran.size());
        assertEquals("a", ran.get(0));
    }

    @Test public void nonManualCycleKeepsItsFlagThroughCoalescing() {
        RecordingRunner runner = new RecordingRunner();
        PendingApplySeries series = new PendingApplySeries(runner);
        PendingApplySeries.Offer first = series.offer(10L, 1L, 5L, false, null);
        PendingApplySeries.Offer second = series.offer(11L, 1L, 6L, true, null);
        assertNotNull(second.superseded);
        first.toPost.run();
        second.toPost.run();
        assertEquals(1, runner.cycles);
        assertTrue(runner.manual);
    }
}
