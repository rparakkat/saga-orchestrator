package com.example.orchestrator.api;

import com.example.orchestrator.AsyncTaskOrchestrator;
import com.example.orchestrator.TaskOrchestrator;
import com.example.orchestrator.loader.YamlLoader;
import com.example.orchestrator.model.TaskDefinition;
import com.example.orchestrator.store.StateStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API controller for task orchestration
 */
@RestController
@RequestMapping("/api")
public class OrchestrationController {
    private static final Logger logger = LoggerFactory.getLogger(OrchestrationController.class);
    
    @Autowired
    private TaskOrchestrator taskOrchestrator;
    
    @Autowired
    private AsyncTaskOrchestrator asyncTaskOrchestrator;
    
    @Autowired
    private YamlLoader yamlLoader;
    
    @Autowired
    private StateStore stateStore;

    /**
     * Synchronous orchestration endpoint
     */
    @PostMapping("/orchestrate")
    public ResponseEntity<Map<String, Object>> orchestrate(
            @RequestParam(defaultValue = "workflow.yml") String yamlFile) {
        
        logger.info("Received orchestration request for file: {}", yamlFile);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Clear previous states
            stateStore.clearAllStates();
            
            // Load tasks from YAML
            List<TaskDefinition> tasks = yamlLoader.loadTasks(yamlFile);
            
            // Execute tasks
            boolean success = taskOrchestrator.executeTasks(tasks);
            
            response.put("success", success);
            response.put("message", success ? "All tasks completed successfully" : "Orchestration failed");
            response.put("yamlFile", yamlFile);
            response.put("tasksCount", tasks.size());
            response.put("taskStates", stateStore.getAllStates());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Orchestration failed", e);
            response.put("success", false);
            response.put("message", "Orchestration failed: " + e.getMessage());
            response.put("yamlFile", yamlFile);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Asynchronous orchestration endpoint
     */
    @PostMapping("/orchestrate-async")
    public ResponseEntity<Map<String, Object>> orchestrateAsync(
            @RequestParam(defaultValue = "workflow.yml") String yamlFile) {
        
        logger.info("Received async orchestration request for file: {}", yamlFile);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Clear previous states
            stateStore.clearAllStates();
            
            // Load tasks from YAML
            List<TaskDefinition> tasks = yamlLoader.loadTasks(yamlFile);
            
            // Execute tasks asynchronously
            boolean success = asyncTaskOrchestrator.executeTasksAsync(tasks);
            
            response.put("success", success);
            response.put("message", success ? "All tasks completed successfully (async)" : "Async orchestration failed");
            response.put("yamlFile", yamlFile);
            response.put("tasksCount", tasks.size());
            response.put("taskStates", stateStore.getAllStates());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Async orchestration failed", e);
            response.put("success", false);
            response.put("message", "Async orchestration failed: " + e.getMessage());
            response.put("yamlFile", yamlFile);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get current task states
     */
    @GetMapping("/states")
    public ResponseEntity<Map<String, Object>> getStates() {
        Map<String, Object> response = new HashMap<>();
        response.put("taskStates", stateStore.getAllStates());
        return ResponseEntity.ok(response);
    }

    /**
     * Clear all task states
     */
    @DeleteMapping("/states")
    public ResponseEntity<Map<String, Object>> clearStates() {
        stateStore.clearAllStates();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "All task states cleared");
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Task Orchestration System");
        return ResponseEntity.ok(response);
    }
}