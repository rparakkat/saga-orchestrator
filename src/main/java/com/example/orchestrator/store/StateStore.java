package com.example.orchestrator.store;

import com.example.orchestrator.model.TaskState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory state store for tracking task states
 */
@Component
public class StateStore {
    private static final Logger logger = LoggerFactory.getLogger(StateStore.class);
    
    private final ConcurrentMap<String, TaskState> taskStates = new ConcurrentHashMap<>();

    /**
     * Update the state of a task
     */
    public void updateTaskState(String taskName, TaskState state) {
        TaskState previousState = taskStates.put(taskName, state);
        logger.info("Task '{}' state transition: {} -> {}", taskName, previousState, state);
    }

    /**
     * Get the current state of a task
     */
    public TaskState getTaskState(String taskName) {
        return taskStates.getOrDefault(taskName, TaskState.NOT_STARTED);
    }

    /**
     * Check if a task has completed successfully
     */
    public boolean isTaskCompleted(String taskName) {
        return TaskState.COMPLETED.equals(taskStates.get(taskName));
    }

    /**
     * Check if a task has failed
     */
    public boolean isTaskFailed(String taskName) {
        TaskState state = taskStates.get(taskName);
        return TaskState.FAILED.equals(state);
    }

    /**
     * Clear all task states
     */
    public void clearAllStates() {
        taskStates.clear();
        logger.info("All task states cleared");
    }

    /**
     * Get all task states
     */
    public ConcurrentMap<String, TaskState> getAllStates() {
        return new ConcurrentHashMap<>(taskStates);
    }
}