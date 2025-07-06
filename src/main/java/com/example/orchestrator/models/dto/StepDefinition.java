package com.example.orchestrator.models.dto;

/**
 * Definition of a single step in a task workflow
 */
public class StepDefinition {
    private String name;
    private String command;
    private String input;
    private String compensateCommand;
    private RetryPolicy retryPolicy;

    public StepDefinition() {
        this.retryPolicy = new RetryPolicy();
    }

    public StepDefinition(String name, String command, String input) {
        this.name = name;
        this.command = command;
        this.input = input;
        this.retryPolicy = new RetryPolicy();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getCompensateCommand() {
        return compensateCommand;
    }

    public void setCompensateCommand(String compensateCommand) {
        this.compensateCommand = compensateCommand;
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy != null ? retryPolicy : new RetryPolicy();
    }

    public boolean hasCompensation() {
        return compensateCommand != null && !compensateCommand.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "StepDefinition{" +
                "name='" + name + '\'' +
                ", command='" + command + '\'' +
                ", input='" + input + '\'' +
                ", compensateCommand='" + compensateCommand + '\'' +
                ", retryPolicy=" + retryPolicy +
                '}';
    }
}