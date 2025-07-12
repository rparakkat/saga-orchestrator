package com.example.orchestrator.services;

import com.example.orchestrator.loader.YamlLoader;
import com.example.orchestrator.models.db.WorkflowDocument;
import com.example.orchestrator.models.dto.StepDefinition;
import com.example.orchestrator.models.dto.TaskDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WorkflowMigrationService implements CommandLineRunner {
    
    @Autowired
    private WorkflowService workflowService;
    
    @Autowired
    private YamlLoader yamlLoader;
    
    @Override
    public void run(String... args) throws Exception {
        migrateYamlWorkflowsToMongoDB();
    }
    
    /**
     * Migrate YAML workflows to MongoDB
     */
    public void migrateYamlWorkflowsToMongoDB() {
        try {
            // Load workflows from YAML files
            Map<String, TaskDefinition> workflowTasks = yamlLoader.loadWorkflow();
            List<StepDefinition> workflowSteps = yamlLoader.loadSteps();
            
            // Create workflow document
            WorkflowDocument workflow = workflowService.createWorkflow(
                "Main Workflow",
                "Main workflow migrated from YAML",
                "1.0.0",
                workflowTasks,
                workflowSteps
            );
            
            System.out.println("Successfully migrated workflow to MongoDB with ID: " + workflow.getWorkflowId());
            
            // Migrate failure workflow if it exists
            try {
                Map<String, TaskDefinition> failureTasks = yamlLoader.loadFailureWorkflow();
                List<StepDefinition> failureSteps = yamlLoader.loadFailureSteps();
                
                WorkflowDocument failureWorkflow = workflowService.createWorkflow(
                    "Failure Workflow",
                    "Failure workflow migrated from YAML",
                    "1.0.0",
                    failureTasks,
                    failureSteps
                );
                
                System.out.println("Successfully migrated failure workflow to MongoDB with ID: " + failureWorkflow.getWorkflowId());
            } catch (Exception e) {
                System.out.println("No failure workflow found to migrate: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("Error migrating workflows to MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 