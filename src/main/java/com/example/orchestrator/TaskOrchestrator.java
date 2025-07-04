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

/**
 * Main orchestrator for executing tasks sequentially with compensation
 */
@Component
public class TaskOrchestrator {
    private static final Logger logger = LoggerFactory.getLogger(TaskOrchestrator.class);
    
    @Autowired
    private TaskExecutor taskExecutor;
    
    @Autowired
    private StateStore stateStore;

    /**
     * Execute a list of tasks sequentially
     */
    public boolean executeTasks(List<TaskDefinition> tasks) {
        logger.info("Starting orchestration of {} tasks", tasks.size());
        
        List<TaskDefinition> completedTasks = new ArrayList<>();
        
        for (TaskDefinition task : tasks) {
            stateStore.updateTaskState(task.getName(), TaskState.RUNNING);
            
            boolean taskSuccess = executeTask(task);
            
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
     * Execute a single task (all its steps)
     */
    private boolean executeTask(TaskDefinition task) {
        logger.info("Executing task: {}", task.getName());
        
        List<StepDefinition> completedSteps = new ArrayList<>();
        
        for (StepDefinition step : task.getSteps()) {
            boolean stepSuccess = taskExecutor.executeStep(step);
            
            if (stepSuccess) {
                completedSteps.add(step);
            } else {
                logger.error("Step '{}' in task '{}' failed", step.getName(), task.getName());
                
                // Compensate completed steps in reverse order
                compensateSteps(completedSteps);
                
                return false;
            }
        }
        
        return true;
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
}