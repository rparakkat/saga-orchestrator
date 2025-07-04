package com.example.orchestrator.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Definition of a task containing multiple steps
 */
public class TaskDefinition {
    private String name;
    private List<StepDefinition> steps;

    public TaskDefinition() {
        this.steps = new ArrayList<>();
    }

    public TaskDefinition(String name) {
        this.name = name;
        this.steps = new ArrayList<>();
    }

    public TaskDefinition(String name, List<StepDefinition> steps) {
        this.name = name;
        this.steps = steps != null ? steps : new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StepDefinition> getSteps() {
        return steps;
    }

    public void setSteps(List<StepDefinition> steps) {
        this.steps = steps != null ? steps : new ArrayList<>();
    }

    public void addStep(StepDefinition step) {
        if (step != null) {
            this.steps.add(step);
        }
    }

    public boolean hasSteps() {
        return steps != null && !steps.isEmpty();
    }

    @Override
    public String toString() {
        return "TaskDefinition{" +
                "name='" + name + '\'' +
                ", steps=" + steps +
                '}';
    }
}