package com.example.orchestrator.loader;

import com.example.orchestrator.models.dto.RetryPolicy;
import com.example.orchestrator.models.dto.StepDefinition;
import com.example.orchestrator.models.dto.TaskDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * YAML loader utility for loading workflow definitions
 */
@Component
public class YamlLoader {
    private static final Logger logger = LoggerFactory.getLogger(YamlLoader.class);

    /**
     * Load tasks from YAML file
     */
    public List<TaskDefinition> loadTasks(String yamlFileName) {
        logger.info("Loading tasks from YAML file: {}", yamlFileName);
        
        try {
            ClassPathResource resource = new ClassPathResource(yamlFileName);
            try (InputStream inputStream = resource.getInputStream()) {
                Yaml yaml = new Yaml();
                Map<String, Object> data = yaml.load(inputStream);
                
                return parseTaskDefinitions(data);
            }
        } catch (Exception e) {
            logger.error("Failed to load YAML file: {}", yamlFileName, e);
            throw new RuntimeException("Failed to load YAML file: " + yamlFileName, e);
        }
    }

    /**
     * Parse task definitions from YAML data
     */
    @SuppressWarnings("unchecked")
    private List<TaskDefinition> parseTaskDefinitions(Map<String, Object> data) {
        List<TaskDefinition> tasks = new ArrayList<>();
        
        Object tasksData = data.get("tasks");
        if (!(tasksData instanceof List)) {
            throw new IllegalArgumentException("Invalid YAML format: 'tasks' should be a list");
        }
        
        List<Map<String, Object>> tasksList = (List<Map<String, Object>>) tasksData;
        
        for (Map<String, Object> taskData : tasksList) {
            TaskDefinition task = parseTaskDefinition(taskData);
            tasks.add(task);
        }
        
        logger.info("Loaded {} tasks from YAML", tasks.size());
        return tasks;
    }

    /**
     * Parse a single task definition
     */
    @SuppressWarnings("unchecked")
    private TaskDefinition parseTaskDefinition(Map<String, Object> taskData) {
        String taskName = (String) taskData.get("name");
        if (taskName == null) {
            throw new IllegalArgumentException("Task name is required");
        }
        
        TaskDefinition task = new TaskDefinition(taskName);
        
        Object stepsData = taskData.get("steps");
        if (stepsData instanceof List) {
            List<Map<String, Object>> stepsList = (List<Map<String, Object>>) stepsData;
            
            for (Map<String, Object> stepData : stepsList) {
                StepDefinition step = parseStepDefinition(stepData);
                task.addStep(step);
            }
        }
        
        logger.debug("Parsed task '{}' with {} steps", taskName, task.getSteps().size());
        return task;
    }

    /**
     * Parse a single step definition
     */
    @SuppressWarnings("unchecked")
    private StepDefinition parseStepDefinition(Map<String, Object> stepData) {
        String stepName = (String) stepData.get("name");
        String command = (String) stepData.get("command");
        String input = (String) stepData.get("input");
        String compensateCommand = (String) stepData.get("compensateCommand");
        
        if (stepName == null || command == null) {
            throw new IllegalArgumentException("Step name and command are required");
        }
        
        StepDefinition step = new StepDefinition(stepName, command, input);
        step.setCompensateCommand(compensateCommand);
        
        // Parse retry policy
        Object retryPolicyData = stepData.get("retryPolicy");
        if (retryPolicyData instanceof Map) {
            Map<String, Object> retryData = (Map<String, Object>) retryPolicyData;
            RetryPolicy retryPolicy = parseRetryPolicy(retryData);
            step.setRetryPolicy(retryPolicy);
        }
        
        logger.debug("Parsed step '{}'", stepName);
        return step;
    }

    /**
     * Parse retry policy
     */
    private RetryPolicy parseRetryPolicy(Map<String, Object> retryData) {
        Integer maxRetries = (Integer) retryData.get("maxRetries");
        Integer retryDelayMs = (Integer) retryData.get("retryDelayMs");
        
        if (maxRetries == null) {
            maxRetries = 0;
        }
        
        if (retryDelayMs == null) {
            retryDelayMs = 1000;
        }
        
        return new RetryPolicy(maxRetries, retryDelayMs.longValue());
    }
}