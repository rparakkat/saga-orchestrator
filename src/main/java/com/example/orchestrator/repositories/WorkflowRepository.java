package com.example.orchestrator.repositories;

import com.example.orchestrator.models.db.WorkflowDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowRepository extends MongoRepository<WorkflowDocument, String> {
    
    /**
     * Find workflow by workflow ID
     */
    Optional<WorkflowDocument> findByWorkflowId(String workflowId);
    
    /**
     * Find workflows by name
     */
    List<WorkflowDocument> findByName(String name);
    
    /**
     * Find workflows by version
     */
    List<WorkflowDocument> findByVersion(String version);
    
    /**
     * Find workflows by name and version
     */
    Optional<WorkflowDocument> findByNameAndVersion(String name, String version);
    
    /**
     * Find all workflows with a specific task type
     */
    @Query("{'tasks.?0.type': ?1}")
    List<WorkflowDocument> findByTaskType(String taskName, String taskType);
    
    /**
     * Find workflows created after a specific date
     */
    List<WorkflowDocument> findByCreatedAtAfter(java.time.LocalDateTime date);
    
    /**
     * Find workflows by metadata field
     */
    @Query("{'metadata.?0': ?1}")
    List<WorkflowDocument> findByMetadataField(String field, Object value);
    
    /**
     * Check if workflow exists by workflow ID
     */
    boolean existsByWorkflowId(String workflowId);
    
    /**
     * Delete workflow by workflow ID
     */
    void deleteByWorkflowId(String workflowId);
} 