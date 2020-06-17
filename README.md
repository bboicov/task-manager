# Task-manager
This component represents task manager. It allows users to add, list and kill processes.

## Implementation
Task manager is build on the top of popular light-weight and powerful Micronaut framework(https://micronaut.io).
It is Spring and VertX alternative intended to quickly put together REST microservices.

### Building blocks
#### Priority
The component is supporting LOW, MEDIUM, HIGH priority.

#### OSProcess
Each process contains three fields - unique Id(UUID in contrast to Linux based Ids), priority and creation time.

#### ProcessFactory
Factory which is used to create new processes with predefined priority.

#### ProcessService
Singleton service responsible for keeping track and managing processes.

#### TaskManagerApi
Endpoint controller which serve incoming requests.

### Multithreading
All entry endpoint methods of ProcessService are synchronized. Private methods are not because could not be called directly.
There are numerous improvements available including using concurrent structures like ConcurrentHashMap, implement 
more granular locking with ReentrantLock(https://winterbe.com/posts/2015/04/30/java8-concurrency-tutorial-synchronized-locks-examples/),
combine read-shared and write-only locks, etc. 

### Reactive processing 
Micronaut supports reactive processing out of the box with RXJava. We can easily migrate the controller to the one.
This will improve underlying resource usage(threads) and could improve simultaneous processing.

## How to build the application

## Running locally

## Deploy to cloud
