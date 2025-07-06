package com.example.orchestrator.executor;

import com.example.orchestrator.models.dto.StepDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Executor for individual steps with retry and compensation logic
 */
@Component
public class TaskExecutor {
    private static final Logger logger = LoggerFactory.getLogger(TaskExecutor.class);

    /**
     * Execute a step with retry logic
     */
    public boolean executeStep(StepDefinition step) {
        logger.info("Starting execution of step: {}", step.getName());
        
        int maxRetries = step.getRetryPolicy().getMaxRetries();
        int attempt = 0;
        
        while (attempt <= maxRetries) {
            try {
                if (attempt > 0) {
                    logger.info("Retrying step '{}' - attempt {}/{}", step.getName(), attempt, maxRetries);
                    Thread.sleep(step.getRetryPolicy().getRetryDelayMs());
                }
                
                boolean success = simulateCommandExecution(step.getCommand(), step.getInput());
                
                if (success) {
                    logger.info("Step '{}' executed successfully", step.getName());
                    return true;
                } else {
                    logger.warn("Step '{}' failed on attempt {}", step.getName(), attempt + 1);
                }
                
            } catch (InterruptedException e) {
                logger.error("Step '{}' execution interrupted", step.getName(), e);
                Thread.currentThread().interrupt();
                return false;
            } catch (Exception e) {
                logger.error("Step '{}' execution failed with exception on attempt {}", step.getName(), attempt + 1, e);
            }
            
            attempt++;
        }
        
        logger.error("Step '{}' failed after {} attempts", step.getName(), maxRetries + 1);
        return false;
    }

    /**
     * Execute compensation for a step
     */
    public boolean executeCompensation(StepDefinition step) {
        if (!step.hasCompensation()) {
            logger.info("No compensation command defined for step: {}", step.getName());
            return true;
        }
        
        logger.info("Executing compensation for step: {}", step.getName());
        
        try {
            boolean success = simulateCommandExecution(step.getCompensateCommand(), step.getInput());
            
            if (success) {
                logger.info("Compensation for step '{}' executed successfully", step.getName());
                return true;
            } else {
                logger.error("Compensation for step '{}' failed", step.getName());
                return false;
            }
            
        } catch (Exception e) {
            logger.error("Compensation for step '{}' failed with exception", step.getName(), e);
            return false;
        }
    }

    /**
     * Simulate command execution
     * Returns false if command equals "FAIL" to simulate failure
     */
    private boolean simulateCommandExecution(String command, String input) {
        logger.info("Executing command: {} with input: {}", command, input);
        
        // Simulate processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        
        // Simulate failure if command is "FAIL"
        if ("FAIL".equals(command)) {
            logger.warn("Command execution failed (simulated failure)");
            return false;
        }
        
        // Simulate success for all other commands
        logger.info("Command executed successfully");
        return true;
    }
}