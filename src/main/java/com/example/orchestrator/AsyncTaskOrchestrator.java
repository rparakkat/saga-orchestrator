package com.example.orchestrator;

import com.example.orchestrator.executor.TaskExecutor;
import com.example.orchestrator.model.StepDefinition;
import com.example.orchestrator.model.TaskDefinition;
import com.example.orchestrator.model.TaskState;
import com.example.orchestrator.store.StateStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Async orchestrator for executing steps within tasks in parallel
 */
@Component
public class AsyncTaskOrchestrator {
    private static final Logger logger = LoggerFactory.getLogger(AsyncTaskOrchestrator.class);
    
    @Autowired
    private TaskExecutor taskExecutor;
    
    @Autowired
    private StateStore stateStore;
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * Execute a list of tasks with async step execution
     */
    public boolean executeTasksAsync(List<TaskDefinition> tasks) {
        logger.info("Starting async orchestration of {} tasks", tasks.size());
        
        List<TaskDefinition> completedTasks = new ArrayList<>();
        
        for (TaskDefinition task : tasks) {
            stateStore.updateTaskState(task.getName(), TaskState.RUNNING);
            
            boolean taskSuccess = executeTaskAsync(task);
            
            if (taskSuccess) {
                stateStore.updateTaskState(task.getName(), TaskState.COMPLETED);
                completedTasks.add(task);
                logger.info("Task '{}' completed successfully", task.getName());
            } else {
                stateStore.updateTaskState(task.getName(), TaskState.FAILED);
                logger.error("Task '{}' failed, starting compensation", task.getName());
                
                // Perform compensation on completed tasks in reverse order
                compensateCompletedTasks(completedTasks);
                
                return false;
            }
        }
        
        logger.info("All tasks completed successfully");
        return true;
    }

    /**
     * Execute a single task with parallel step execution
     */
    private boolean executeTaskAsync(TaskDefinition task) {
        logger.info("Executing task async: {}", task.getName());
        
        // Create CompletableFuture for each step
        List<CompletableFuture<StepResult>> stepFutures = new ArrayList<>();
        
        for (StepDefinition step : task.getSteps()) {
            CompletableFuture<StepResult> stepFuture = CompletableFuture.supplyAsync(() -> {
                logger.info("Starting async execution of step: {}", step.getName());
                boolean success = taskExecutor.executeStep(step);
                logger.info("Async step '{}' completed with result: {}", step.getName(), success);
                return new StepResult(step, success);
            }, executorService);
            
            stepFutures.add(stepFuture);
        }
        
        // Wait for all steps to complete
        CompletableFuture<Void> allSteps = CompletableFuture.allOf(
            stepFutures.toArray(new CompletableFuture[0])
        );
        
        try {
            allSteps.join();
            
            // Check if all steps succeeded
            List<StepDefinition> completedSteps = new ArrayList<>();
            boolean allSucceeded = true;
            
            for (CompletableFuture<StepResult> future : stepFutures) {
                StepResult result = future.get();
                if (result.isSuccess()) {
                    completedSteps.add(result.getStep());
                } else {
                    allSucceeded = false;
                    logger.error("Step '{}' in task '{}' failed", result.getStep().getName(), task.getName());
                }
            }
            
            if (!allSucceeded) {
                // Compensate completed steps
                compensateSteps(completedSteps);
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("Error during async task execution", e);
            return false;
        }
    }

    /**
     * Compensate completed tasks in reverse order
     */
    private void compensateCompletedTasks(List<TaskDefinition> completedTasks) {
        logger.info("Starting compensation for {} completed tasks", completedTasks.size());
        
        // Reverse the order for compensation
        Collections.reverse(completedTasks);
        
        for (TaskDefinition task : completedTasks) {
            stateStore.updateTaskState(task.getName(), TaskState.COMPENSATING);
            
            // Compensate all steps in the task (in reverse order)
            List<StepDefinition> steps = new ArrayList<>(task.getSteps());
            Collections.reverse(steps);
            
            boolean compensationSuccess = true;
            for (StepDefinition step : steps) {
                if (!taskExecutor.executeCompensation(step)) {
                    compensationSuccess = false;
                    logger.error("Compensation failed for step '{}' in task '{}'", step.getName(), task.getName());
                }
            }
            
            if (compensationSuccess) {
                stateStore.updateTaskState(task.getName(), TaskState.COMPENSATED);
                logger.info("Task '{}' compensated successfully", task.getName());
            } else {
                stateStore.updateTaskState(task.getName(), TaskState.FAILED);
                logger.error("Compensation failed for task '{}'", task.getName());
            }
        }
    }

    /**
     * Compensate steps in reverse order
     */
    private void compensateSteps(List<StepDefinition> completedSteps) {
        logger.info("Starting compensation for {} completed steps", completedSteps.size());
        
        // Reverse the order for compensation
        Collections.reverse(completedSteps);
        
        for (StepDefinition step : completedSteps) {
            taskExecutor.executeCompensation(step);
        }
    }

    /**
     * Clean up resources
     */
    public void shutdown() {
        executorService.shutdown();
    }

    /**
     * Inner class to hold step execution results
     */
    private static class StepResult {
        private final StepDefinition step;
        private final boolean success;

        public StepResult(StepDefinition step, boolean success) {
            this.step = step;
            this.success = success;
        }

        public StepDefinition getStep() {
            return step;
        }

        public boolean isSuccess() {
            return success;
        }
    }
}