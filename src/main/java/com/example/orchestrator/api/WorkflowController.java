package com.example.orchestrator.api;

import com.example.orchestrator.models.db.WorkflowDocument;
import com.example.orchestrator.models.dto.StepDefinition;
import com.example.orchestrator.models.dto.TaskDefinition;
import com.example.orchestrator.services.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {
    
    @Autowired
    private WorkflowService workflowService;
    
    @PostMapping
    public ResponseEntity<WorkflowDocument> createWorkflow(@RequestBody WorkflowCreateRequest request) {
        WorkflowDocument workflow = workflowService.createWorkflow(
            request.getName(),
            request.getDescription(),
            request.getVersion(),
            request.getTasks(),
            request.getSteps()
        );
        return ResponseEntity.ok(workflow);
    }
    
    @GetMapping
    public ResponseEntity<List<WorkflowDocument>> getAllWorkflows() {
        List<WorkflowDocument> workflows = workflowService.findAllWorkflows();
        return ResponseEntity.ok(workflows);
    }
    
    @GetMapping("/{workflowId}")
    public ResponseEntity<WorkflowDocument> getWorkflow(@PathVariable String workflowId) {
        return workflowService.findByWorkflowId(workflowId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<List<WorkflowDocument>> getWorkflowsByName(@PathVariable String name) {
        List<WorkflowDocument> workflows = workflowService.findWorkflowsByName(name);
        return ResponseEntity.ok(workflows);
    }
    
    @GetMapping("/version/{version}")
    public ResponseEntity<List<WorkflowDocument>> getWorkflowsByVersion(@PathVariable String version) {
        List<WorkflowDocument> workflows = workflowService.findWorkflowsByVersion(version);
        return ResponseEntity.ok(workflows);
    }
    
    @PutMapping("/{workflowId}")
    public ResponseEntity<WorkflowDocument> updateWorkflow(@PathVariable String workflowId,
                                                         @RequestBody WorkflowDocument updatedWorkflow) {
        try {
            WorkflowDocument workflow = workflowService.updateWorkflow(workflowId, updatedWorkflow);
            return ResponseEntity.ok(workflow);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{workflowId}")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable String workflowId) {
        if (workflowService.workflowExists(workflowId)) {
            workflowService.deleteWorkflow(workflowId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    // Request DTO for creating workflows
    public static class WorkflowCreateRequest {
        private String name;
        private String description;
        private String version;
        private Map<String, TaskDefinition> tasks;
        private List<StepDefinition> steps;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public Map<String, TaskDefinition> getTasks() { return tasks; }
        public void setTasks(Map<String, TaskDefinition> tasks) { this.tasks = tasks; }
        
        public List<StepDefinition> getSteps() { return steps; }
        public void setSteps(List<StepDefinition> steps) { this.steps = steps; }
    }
} 