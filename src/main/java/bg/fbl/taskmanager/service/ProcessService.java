package bg.fbl.taskmanager.service;

import bg.fbl.taskmanager.factory.ProcessFactory;
import bg.fbl.taskmanager.model.OSProcess;
import bg.fbl.taskmanager.model.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.time.Instant;
import java.util.*;

@Singleton
public class ProcessService {

    private static final Logger logger = LoggerFactory.getLogger(ProcessService.class);

    static final private int MAX_PROCESSES = 3;

    final private ProcessFactory processFactory;

    HashMap<UUID, OSProcess> processPool = new HashMap<>();

    // This could be organized as nested structures in order to support more than 3 priorities.
    // For example map of ArrayDeques
    ArrayDeque<UUID> lowPriorityProcesses = new ArrayDeque<>();
    ArrayDeque<UUID> mediumPriorityProcesses = new ArrayDeque<>();
    ArrayDeque<UUID> highPriorityProcesses = new ArrayDeque<>();

    public ProcessService(ProcessFactory processFactory) {
        this.processFactory = processFactory;
    }

    private UUID create(Priority priority) {

        OSProcess process = processFactory.createProcess(priority);
        processPool.put(process.getId(), process);
        switch (priority) {
            case LOW:
                lowPriorityProcesses.addLast(process.getId());
                break;
            case MEDIUM:
                mediumPriorityProcesses.addLast(process.getId());
                break;
            case HIGH:
                highPriorityProcesses.addLast(process.getId());
                break;
        }

        logger.info("process CREATED with id: " + process.getId().toString() + " and priority: "
            + process.getPriority().toString());

        // start the process
        process.run();
        logger.info("process STARTED with id: " + process.getId().toString());

        return process.getId();
    }

    private Instant getInstantOrDefault(UUID uuid) {
        if (uuid != null) {
            return processPool.get(uuid).getCreationTime();
        }

        return Instant.MAX;
    }

    private void remove(UUID uuid) {

        OSProcess process = processPool.remove(uuid);
        logger.info("process REMOVED from pool with id: " + process.getId().toString());
        // we are not making validation if process will stop gracefully
        // we could force kill if process is still running after certain timeout
        process.kill();
        logger.info("process KILLED with id: " + process.getId().toString());
    }

    private void deleteOldest() {

        // We are sure that least one ArrayDeque is not empty(therefore peekFirst != null),
        // because processPool.size() == MAX_PROCESSES
        // Therefore will always find real oldest value.
        Instant lowItem = getInstantOrDefault(lowPriorityProcesses.peekFirst());
        Instant mediumItem = getInstantOrDefault(mediumPriorityProcesses.peekFirst());
        Instant highItem = getInstantOrDefault(highPriorityProcesses.peekFirst());

        UUID removedId;
        if (lowItem.compareTo(mediumItem) < 0) {
            // lowItem is smaller
            if (lowItem.compareTo(highItem) < 0) {
                // lowItem is the smallest
                removedId = lowPriorityProcesses.pollFirst();
            } else {
                // highItem is the smallest
                removedId = highPriorityProcesses.pollFirst();
            }
        } else {
            // mediumItem is smaller
            if (mediumItem.compareTo(highItem) < 0) {
                // mediumItem is the smallest
                removedId = mediumPriorityProcesses.pollFirst();
            } else {
                // highItem is the smallest
                removedId = highPriorityProcesses.pollFirst();
            }
        }

        remove(removedId);
    }

    synchronized public UUID addProcess(Priority priority) {

        if (processPool.size() < MAX_PROCESSES) {
            // there is free capacity for new process
            return create(priority);
        }

        logger.error("can't create more processes. limit of " + MAX_PROCESSES + " reached.");
        return null;
    }

    synchronized public UUID addProcessFIFO(Priority priority) {

        if (processPool.size() == MAX_PROCESSES) {
            deleteOldest();
        }

        return create(priority);
    }

    synchronized public UUID addProcessPriority(Priority priority) {

        if (processPool.size() < MAX_PROCESSES) {
            return create(priority);
        }

        // We can't add process based on the requirements and skip it
        if (priority == Priority.LOW) {
            logger.error("can't create more processes. limit of " + MAX_PROCESSES + " reached.");
            return null;
        }

        if (lowPriorityProcesses.size() > 0) {
            UUID removedId = lowPriorityProcesses.pollFirst();
            remove(removedId);
            return create(priority);
        } else if (priority == Priority.HIGH && mediumPriorityProcesses.size() > 0) {
            UUID removedId = mediumPriorityProcesses.pollFirst();
            remove(removedId);
            return create(priority);
        }

        logger.error("can't create more processes. limit of " + MAX_PROCESSES + " reached.");
        return null;
    }

    synchronized public List<OSProcess> listProcesses() {

        List<OSProcess> processList = new ArrayList<>();

        // return all processes - order by priority and timestamp - ascending
        Iterator iteratorL = lowPriorityProcesses.iterator();
        while (iteratorL.hasNext()) {
            processList.add(processPool.get(iteratorL.next()));
        }

        Iterator iteratorM = mediumPriorityProcesses.iterator();
        while (iteratorM.hasNext()) {
            processList.add(processPool.get(iteratorM.next()));
        }

        Iterator iteratorH = highPriorityProcesses.iterator();
        while (iteratorH.hasNext()) {
            processList.add(processPool.get(iteratorH.next()));
        }

        return processList;
    }

    synchronized public boolean killProcess(UUID processId) {

        // first we search for the processId to validate is available in our pool
        OSProcess process = processPool.get(processId);
        if (process != null) {
            UUID uuid = process.getId();
            Priority priority = process.getPriority();
            boolean result;
            switch (priority) {
                case LOW:
                    result = lowPriorityProcesses.remove(uuid);
                    break;
                case MEDIUM:
                    result = mediumPriorityProcesses.remove(uuid);
                    break;
                case HIGH:
                    result = highPriorityProcesses.remove(uuid);
                    break;
                default:
                    result = false;
            }
            if (result) {
                remove(uuid);
                return true;
            }
        }

        // process Id not found
        logger.error("can't find process with id: " + processId.toString() + " in the pool.");
        return false;
    }

    synchronized public void killAllWithPriority(Priority priority) {

        ArrayDeque<UUID> targetDQ;
        switch (priority) {
            case LOW:
                targetDQ = lowPriorityProcesses;
                break;
            case MEDIUM:
                targetDQ = mediumPriorityProcesses;
                break;
            case HIGH:
                targetDQ = highPriorityProcesses;
                break;
            default:
                targetDQ = null;
        }

        if (targetDQ != null) {
            Iterator iterator = targetDQ.iterator();
            while (iterator.hasNext()) {
                UUID uuid = (UUID) iterator.next();
                remove(uuid);
            }

            logger.info("killed " + targetDQ.size() + " processes with priority: " + priority.toString());
            targetDQ.clear();
        }
    }

    synchronized public void killAll() {

        // kill processes based on the priority
        killAllWithPriority(Priority.LOW);
        killAllWithPriority(Priority.MEDIUM);
        killAllWithPriority(Priority.HIGH);
    }
}
