package com.example.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main Spring Boot application class for Task Orchestration System
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.example.orchestrator")
public class TaskOrchestrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskOrchestrationApplication.class, args);
    }
}