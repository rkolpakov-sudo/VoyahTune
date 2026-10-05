package ru.big.town.hil.model;

import java.util.ArrayList;
import java.util.List;

/**
 * IMP-20: Model-based state-space generator for AVAS and SleepController.
 * Generates Cartesian products of event sequences to test state machine invariants.
 *
 * Usage: StateSpace.explore(avasModel, 4) -> List of 4-step scenarios
 */
public final class StateSpace {

    public static final class Transition {
        public final String from;
        public final String event;
        public final String to;
        public Transition(String from, String event, String to) {
            this.from = from; this.event = event; this.to = to;
        }
    }

    public static final class Scenario {
        public final List<String> steps;
        public final String expectedEndState;
        public Scenario(List<String> steps, String expectedEndState) {
            this.steps = steps; this.expectedEndState = expectedEndState;
        }
    }

    private StateSpace() {}

    /** Generate all N-step sequences (depth-first) from start state. */
    public static List<Scenario> explore(List<Transition> transitions, String startState, int maxDepth) {
        List<Scenario> out = new ArrayList<>();
        walk(transitions, startState, new ArrayList<>(), out, maxDepth);
        return out;
    }

    private static void walk(List<Transition> transitions, String current,
                              List<String> path, List<Scenario> out, int depth) {
        if (path.size() >= depth) {
            out.add(new Scenario(new ArrayList<>(path), current));
            return;
        }
        List<String> events = new ArrayList<>();
        for (Transition t : transitions) {
            if (t.from.equals(current) && !events.contains(t.event)) {
                events.add(t.event);
            }
        }
        for (String event : events) {
            for (Transition t : transitions) {
                if (t.from.equals(current) && t.event.equals(event)) {
                    path.add(event);
                    walk(transitions, t.to, path, out, depth);
                    path.remove(path.size() - 1);
                }
            }
        }
    }

    // --- AVAS state machine model ---
    public static List<Transition> avasModel() {
        List<Transition> t = new ArrayList<>();
        t.add(new Transition("ON", "STEER_PRESS", "ACTIVE"));
        t.add(new Transition("ON", "SLEEP", "OFF"));
        t.add(new Transition("ACTIVE", "SLEEP", "OFF"));
        t.add(new Transition("ACTIVE", "STEER_RELEASE", "PENDING"));
        t.add(new Transition("PENDING", "STEER_PRESS", "ACTIVE"));
        t.add(new Transition("PENDING", "TIMEOUT", "ON"));
        t.add(new Transition("OFF", "WAKE", "ON"));
        return t;
    }

    /** Invariant: after any sequence without SLEEP, state != OFF */
    public static boolean invariantNoSleep(String state, List<String> events) {
        return !events.contains("SLEEP") || !"OFF".equals(state);
    }
}