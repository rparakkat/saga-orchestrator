# saga-orchestrator# Task Orchestration System

A comprehensive Java-based task orchestration system with retry mechanisms, compensation logic, and async execution capabilities.

## Features

- **Sequential Task Execution**: Execute tasks in order with step-by-step processing
- **Retry Logic**: Configurable retry policies for failed steps
- **Compensation**: Automatic rollback of completed steps/tasks on failure
- **Async Execution**: Parallel execution of steps within tasks using CompletableFuture
- **YAML Configuration**: Define workflows using simple YAML files
- **State Management**: In-memory state tracking for all tasks
- **REST API**: HTTP endpoints for triggering orchestration
- **Spring Boot Integration**: Production-ready Spring Boot application

## Project Structure

```
src/
├── main/
│   ├── java/com/example/orchestrator/
│   │   ├── api/
│   │   │   └── OrchestrationController.java
│   │   ├── executor/
│   │   │   └── TaskExecutor.java
│   │   ├── loader/
│   │   │   └── YamlLoader.java
│   │   ├── model/
│   │   │   ├── RetryPolicy.java
│   │   │   ├── StepDefinition.java
│   │   │   ├── TaskDefinition.java
│   │   │   └── TaskState.java
│   │   ├── store/
│   │   │   └── StateStore.java
│   │   ├── AsyncTaskOrchestrator.java
│   │   ├── TaskOrchestrator.java
│   │   └── TaskOrchestrationApplication.java
│   └── resources/
│       ├── workflow.yml
│       ├── failure-workflow.yml
│       └── application.properties
└── pom.xml
```

## Quick Start

### 1. Build the Project

```bash
mvn clean install
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 3. Test the API

**Synchronous Execution:**
```bash
curl -X POST "http://localhost:8080/api/orchestrate?yamlFile=workflow.yml"
```

**Asynchronous Execution:**
```bash
curl -X POST "http://localhost:8080/api/orchestrate-async?yamlFile=workflow.yml"
```

**Test Failure Scenario:**
```bash
curl -X POST "http://localhost:8080/api/orchestrate?yamlFile=failure-workflow.yml"
```

**Check Task States:**
```bash
curl -X GET "http://localhost:8080/api/states"
```

## YAML Configuration

### Basic Structure

```yaml
tasks:
  - name: TaskName
    steps:
      - name: StepName
        command: POST /endpoint
        input: '{"key":"value"}'
        compensateCommand: DELETE /endpoint
        retryPolicy:
          maxRetries: 3
          retryDelayMs: 1000
```

### Configuration Options

- **name**: Task or step identifier
- **command**: Simulated command to execute
- **input**: JSON payload for the command
- **compensateCommand**: Rollback command (optional)
- **retryPolicy**: Retry configuration
  - **maxRetries**: Maximum retry attempts
  - **retryDelayMs**: Delay between retries in milliseconds

## API Endpoints

### Orchestration Endpoints

- **POST /api/orchestrate**: Execute workflow synchronously
- **POST /api/orchestrate-async**: Execute workflow asynchronously
- **GET /api/states**: Get current task states
- **DELETE /api/states**: Clear all task states
- **GET /api/health**: Health check

### Parameters

- **yamlFile**: YAML configuration file name (default: workflow.yml)

## Failure Simulation

Use the command `FAIL` in your YAML configuration to simulate step failures:

```yaml
- name: FailingStep
  command: FAIL
  input: '{"test":"failure"}'
  compensateCommand: POST /cleanup
  retryPolicy:
    maxRetries: 2
    retryDelayMs: 1000
```

## Compensation Logic

When a step fails:
1. All previously completed steps in the current task are compensated in reverse order
2. If task-level failure occurs, all completed tasks are compensated in reverse order
3. Compensation commands are executed without retry logic

## State Management

Tasks can be in the following states:
- **NOT_STARTED**: Initial state
- **RUNNING**: Currently executing
- **COMPLETED**: Successfully finished
- **FAILED**: Failed execution
- **COMPENSATING**: Running compensation
- **COMPENSATED**: Compensation completed

## Async Execution

The async orchestrator:
- Executes steps within each task in parallel
- Waits for all steps in a task to complete before proceeding
- Maintains task-level sequential execution
- Provides better performance for I/O intensive workflows

## Logging

The system provides comprehensive logging:
- Step execution progress
- Retry attempts and failures
- Compensation activities
- State transitions
- Error details

## Dependencies

- Spring Boot 3.2.0
- SnakeYAML
- SLF4J for logging
- Java 17+

## Extension Points

The system is designed for easy extension:
- **Custom Command Executors**: Replace simulated execution with real HTTP calls
- **Persistent State Store**: Replace in-memory store with database
- **Custom Retry Policies**: Add exponential backoff, circuit breakers
- **Monitoring**: Add metrics and health checks
- **Security**: Add authentication and authorization

## Example Workflows

### Successful Workflow
```yaml
tasks:
  - name: DataProcessing
    steps:
      - name: ValidateData
        command: POST /validate
        input: '{"data":"sample"}'
        retryPolicy:
          maxRetries: 2
```

### Failure with Compensation
```yaml
tasks:
  - name: PaymentFlow
    steps:
      - name: ReserveAmount
        command: POST /reserve
        input: '{"amount":100}'
        compensateCommand: DELETE /reserve
        retryPolicy:
          maxRetries: 3
      - name: ProcessPayment
        command: FAIL
        input: '{"amount":100}'
        compensateCommand: POST /refund
        retryPolicy:
          maxRetries: 2
```

## Testing

The system includes sample YAML files:
- `workflow.yml`: Successful execution example
- `failure-workflow.yml`: Failure scenario with compensation

Run both scenarios to see the complete orchestration behavior including retry logic and compensation mechanisms.

