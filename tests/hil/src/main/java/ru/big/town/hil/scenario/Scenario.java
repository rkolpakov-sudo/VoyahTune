package ru.big.town.hil.scenario;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class Scenario {
    public final String name;
    public final String description;
    public final List<Step> steps;
    public final Map<String, Object> config;

    public Scenario(String name, String description, List<Step> steps, Map<String, Object> config) {
        this.name = name;
        this.description = description == null ? "" : description;
        this.steps = Step.unmodifiable(steps);
        this.config = Collections.unmodifiableMap(config);
    }
}
