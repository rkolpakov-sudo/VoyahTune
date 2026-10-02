package ru.big.town.hil.scenario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ScenarioResult {
    public static final class StepResult {
        public final int index;
        public final String description;
        public final boolean ok;
        public final String message;
        public final long elapsedNanos;

        public StepResult(int index, String description, boolean ok, String message, long elapsedNanos) {
            this.index = index;
            this.description = description;
            this.ok = ok;
            this.message = message;
            this.elapsedNanos = elapsedNanos;
        }
    }

    public final String name;
    public final String description;
    public final boolean passed;
    public final List<StepResult> steps;
    public final List<String> logLines;
    public final long wallNanos;

    public ScenarioResult(String name, String description, boolean passed, List<StepResult> steps,
            List<String> logLines, long wallNanos) {
        this.name = name;
        this.description = description;
        this.passed = passed;
        this.steps = Collections.unmodifiableList(new ArrayList<>(steps));
        this.logLines = Collections.unmodifiableList(new ArrayList<>(logLines));
        this.wallNanos = wallNanos;
    }

    public StepResult firstFailure() {
        for (StepResult s : this.steps) {
            if (!s.ok) {
                return s;
            }
        }
        return null;
    }

    public double wallSeconds() {
        return this.wallNanos / 1.0e9;
    }
}
