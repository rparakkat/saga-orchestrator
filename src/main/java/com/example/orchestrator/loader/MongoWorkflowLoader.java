package com.example.orchestrator.loader;

import com.example.orchestrator.models.db.WorkflowDocument;
import com.example.orchestrator.models.dto.StepDefinition;
import com.example.orchestrator.models.dto.TaskDefinition;
import com.example.orchestrator.services.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MongoWorkflowLoader {
    
    @Autowired
    private WorkflowService workflowService;
    
    /**
     * Load workflow by workflow ID
     */
    public WorkflowDocument loadWorkflowById(String workflowId) {
        return workflowService.findByWorkflowId(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow not found with ID: " + workflowId));
    }
    
    /**
     * Load workflow by name and version
     */
    public WorkflowDocument loadWorkflowByNameAndVersion(String name, String version) {
        return workflowService.findByNameAndVersion(name, version)
                .orElseThrow(() -> new RuntimeException("Workflow not found with name: " + name + " and version: " + version));
    }
    
    /**
     * Load tasks from workflow
     */
    public Map<String, TaskDefinition> loadTasks(String workflowId) {
        WorkflowDocument workflow = loadWorkflowById(workflowId);
        return workflow.getTasks();
    }
    
    /**
     * Load steps from workflow
     */
    public List<StepDefinition> loadSteps(String workflowId) {
        WorkflowDocument workflow = loadWorkflowById(workflowId);
        return workflow.getSteps();
    }
    
    /**
     * Load default workflow (first available)
     */
    public WorkflowDocument loadDefaultWorkflow() {
        List<WorkflowDocument> workflows = workflowService.findAllWorkflows();
        if (workflows.isEmpty()) {
            throw new RuntimeException("No workflows found in database");
        }
        return workflows.get(0);
    }
    
    /**
     * Load failure workflow
     */
    public WorkflowDocument loadFailureWorkflow() {
        return workflowService.findWorkflowsByName("Failure Workflow")
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Failure workflow not found"));
    }
} 