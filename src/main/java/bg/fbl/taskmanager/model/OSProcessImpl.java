package bg.fbl.taskmanager.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ProcessImpl implements Process {

    private final UUID processId;
    private final Priority priority;
    private final Instant creationTime;

    public ProcessImpl(UUID processId, Priority priority, Instant creationTime) {
        this.processId = processId;
        this.priority = priority;
        this.creationTime = creationTime;
    }

    @Override
    public boolean run() {
        // validate if process can be run and return false if error occurred
        // if ( error ) return false;
        return true;
    }

    @Override
    public boolean suspend() {
        // validate if process can be suspended and return false if error occurred
        // if ( error ) return false;
        return true;
    }

    @Override
    public boolean kill() {
        // validate if process can be killed and return false if error occurred
        // if ( error ) return false;
        return true;
    }

    @Override
    public void killForced() {
        // kill the process in any case
    }

    @Override
    public UUID getId() {
        return processId;
    }

    @Override
    public Priority getPriority() {
        return priority;
    }

    @Override
    public Instant getCreationTime() {
        return creationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessImpl process = (ProcessImpl) o;
        return processId.equals(process.processId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processId);
    }

    @Override
    public String toString() {
        return "Process{" +
            "processId=" + processId +
            ", priority=" + priority +
            ", creationTime=" + creationTime +
            '}';
    }
}
