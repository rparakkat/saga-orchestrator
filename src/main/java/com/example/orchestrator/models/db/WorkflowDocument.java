package com.example.orchestrator.models.db;

import com.example.orchestrator.models.dto.StepDefinition;
import com.example.orchestrator.models.dto.TaskDefinition;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Document(collection = "workflows")
public class WorkflowDocument {
    
    @Id
    private String id;
    
    @Field("workflow_id")
    private String workflowId;
    
    @Field("name")
    private String name;
    
    @Field("description")
    private String description;
    
    @Field("version")
    private String version;
    
    @Field("tasks")
    private Map<String, TaskDefinition> tasks;
    
    @Field("steps")
    private List<StepDefinition> steps;
    
    @Field("metadata")
    private Map<String, Object> metadata;
    
    @Field("created_at")
    private java.time.LocalDateTime createdAt;
    
    @Field("updated_at")
    private java.time.LocalDateTime updatedAt;
    
    // Constructors
    public WorkflowDocument() {}
    
    public WorkflowDocument(String workflowId, String name, String description, String version,
                          Map<String, TaskDefinition> tasks, List<StepDefinition> steps) {
        this.workflowId = workflowId;
        this.name = name;
        this.description = description;
        this.version = version;
        this.tasks = tasks;
        this.steps = steps;
        this.createdAt = java.time.LocalDateTime.now();
        this.updatedAt = java.time.LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getWorkflowId() {
        return workflowId;
    }
    
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public Map<String, TaskDefinition> getTasks() {
        return tasks;
    }
    
    public void setTasks(Map<String, TaskDefinition> tasks) {
        this.tasks = tasks;
    }
    
    public List<StepDefinition> getSteps() {
        return steps;
    }
    
    public void setSteps(List<StepDefinition> steps) {
        this.steps = steps;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public java.time.LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 