# Task-manager
This component represents task manager. It allows users to add, list and kill processes. Application is modelling the processes and does not execute underlying management like creating, starting/stopping real processes, etc. The design allows easy implementation of low level OS calls.

## Implementation
Task manager is build on the top of the popular light-weight and powerful Micronaut framework(https://micronaut.io).
It is Spring and VertX alternative intended to quickly put together REST microservices.

### Logging
Application is logging main operations.

### Building blocks
#### Priority
The component is supporting LOW, MEDIUM, HIGH priority.

#### OSProcess
Each process contains three fields - unique Id(UUID in contrast to Linux based Ids), priority and creation time.

#### ProcessFactory
Factory which is used to create new processes with predefined priority.

#### ProcessService
Singleton service is responsible for keeping track and managing processes.

#### TaskManagerApi
Endpoint controller which serves the incoming requests.

### Multithreading
All entry endpoint methods of ProcessService are synchronized. Private methods are not because could not be called directly.
There are numerous improvements available including using concurrent structures like ConcurrentHashMap, implement 
more granular locking with ReentrantLock(https://winterbe.com/posts/2015/04/30/java8-concurrency-tutorial-synchronized-locks-examples/),
combine read-shared and write-only locks, etc. 

### Reactive processing 
Micronaut supports reactive processing out of the box with RXJava. This application was build on top of it.
This will improve underlying resource usage(threads) and simultaneous processing.

## Building and running

### How to build the application
```shell script
mvn package
```

Running the package lifecycle will validate, compile and create a Fat JAR of the application using the `maven-shade-plugin` in the top-level target directory.

### Running locally
To run the application locally, it's just a matter of executing the fat jar. From the top-level directory of this project, simply do:

```shell script
java -jar target/task-manager-0.5.jar
```

### Deploy to docker/cloud
Dockerfile could be used to create an image.

## Testing
Here are some examples of typical use cases

### Create process
Create new process and return its ID. Method will return error if no more free resources.

```shell script
curl -v -d '{"priority":"LOW"}' -H "Content-Type: application/json" -X POST http://localhost:8080/tasks/process
```

### Create process FIFO
Create new process and return its ID. If no free resources are available method will remove the oldest process.

```shell script
curl -v -d '{"priority":"LOW"}' -H "Content-Type: application/json" -X POST http://localhost:8080/tasks/process-fifo
```

### Create process with higher priority
This method will create new process. If process pool is full one will try to remove the oldest process with lower priority.

```shell script
curl -v -d '{"priority":"HIGH"}' -H "Content-Type: application/json" -X POST http://localhost:8080/tasks/process-priority
```

### List all processes
List all processes sorted by priority and creation date.

```shell script
curl -v http://localhost:8080/tasks
```

### Kill process
Kill process selected by uuid. Method will return error if no such process is available.

```shell script
curl -v -X DELETE http://localhost:8080/tasks/process/6b147254-977d-450e-850b-378063fea716
```

### Kill all processes with specified priority

```shell script
curl -v -H "Content-Type: application/json" -X DELETE http://localhost:8080/tasks/processes-priority/LOW
```

### Kill all processes

```shell script
curl -v -H "Content-Type: application/json" -X DELETE http://localhost:8080/tasks/processes-all
```
