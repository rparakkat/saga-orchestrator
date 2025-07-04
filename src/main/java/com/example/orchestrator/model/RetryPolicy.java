package com.example.orchestrator.model;

/**
 * Retry policy configuration for step execution
 */
public class RetryPolicy {
    private int maxRetries;
    private long retryDelayMs;

    public RetryPolicy() {
        this.maxRetries = 0;
        this.retryDelayMs = 1000; // Default 1 second delay
    }

    public RetryPolicy(int maxRetries) {
        this.maxRetries = maxRetries;
        this.retryDelayMs = 1000;
    }

    public RetryPolicy(int maxRetries, long retryDelayMs) {
        this.maxRetries = maxRetries;
        this.retryDelayMs = retryDelayMs;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public long getRetryDelayMs() {
        return retryDelayMs;
    }

    public void setRetryDelayMs(long retryDelayMs) {
        this.retryDelayMs = retryDelayMs;
    }

    @Override
    public String toString() {
        return "RetryPolicy{" +
                "maxRetries=" + maxRetries +
                ", retryDelayMs=" + retryDelayMs +
                '}';
    }
}