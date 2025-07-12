package com.example.orchestrator.services;

import com.example.orchestrator.models.db.WorkflowDocument;
import com.example.orchestrator.models.dto.StepDefinition;
import com.example.orchestrator.models.dto.TaskDefinition;
import com.example.orchestrator.repositories.WorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class WorkflowService {
    
    @Autowired
    private WorkflowRepository workflowRepository;
    
    /**
     * Create a new workflow
     */
    public WorkflowDocument createWorkflow(String name, String description, String version,
                                         Map<String, TaskDefinition> tasks, List<StepDefinition> steps) {
        String workflowId = generateWorkflowId();
        WorkflowDocument workflow = new WorkflowDocument(workflowId, name, description, version, tasks, steps);
        return workflowRepository.save(workflow);
    }
    
    /**
     * Save or update a workflow
     */
    public WorkflowDocument saveWorkflow(WorkflowDocument workflow) {
        workflow.setUpdatedAt(java.time.LocalDateTime.now());
        return workflowRepository.save(workflow);
    }
    
    /**
     * Find workflow by ID
     */
    public Optional<WorkflowDocument> findById(String id) {
        return workflowRepository.findById(id);
    }
    
    /**
     * Find workflow by workflow ID
     */
    public Optional<WorkflowDocument> findByWorkflowId(String workflowId) {
        return workflowRepository.findByWorkflowId(workflowId);
    }
    
    /**
     * Find workflow by name and version
     */
    public Optional<WorkflowDocument> findByNameAndVersion(String name, String version) {
        return workflowRepository.findByNameAndVersion(name, version);
    }
    
    /**
     * Find all workflows
     */
    public List<WorkflowDocument> findAllWorkflows() {
        return workflowRepository.findAll();
    }
    
    /**
     * Find workflows by name
     */
    public List<WorkflowDocument> findWorkflowsByName(String name) {
        return workflowRepository.findByName(name);
    }
    
    /**
     * Find workflows by version
     */
    public List<WorkflowDocument> findWorkflowsByVersion(String version) {
        return workflowRepository.findByVersion(version);
    }
    
    /**
     * Update workflow
     */
    public WorkflowDocument updateWorkflow(String workflowId, WorkflowDocument updatedWorkflow) {
        Optional<WorkflowDocument> existing = workflowRepository.findByWorkflowId(workflowId);
        if (existing.isPresent()) {
            WorkflowDocument current = existing.get();
            current.setName(updatedWorkflow.getName());
            current.setDescription(updatedWorkflow.getDescription());
            current.setVersion(updatedWorkflow.getVersion());
            current.setTasks(updatedWorkflow.getTasks());
            current.setSteps(updatedWorkflow.getSteps());
            current.setMetadata(updatedWorkflow.getMetadata());
            current.setUpdatedAt(java.time.LocalDateTime.now());
            return workflowRepository.save(current);
        }
        throw new RuntimeException("Workflow not found with ID: " + workflowId);
    }
    
    /**
     * Delete workflow by workflow ID
     */
    public void deleteWorkflow(String workflowId) {
        workflowRepository.deleteByWorkflowId(workflowId);
    }
    
    /**
     * Check if workflow exists
     */
    public boolean workflowExists(String workflowId) {
        return workflowRepository.existsByWorkflowId(workflowId);
    }
    
    /**
     * Generate unique workflow ID
     */
    private String generateWorkflowId() {
        return "wf-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
} 