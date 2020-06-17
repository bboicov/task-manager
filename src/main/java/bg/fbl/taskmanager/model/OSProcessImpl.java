package bg.fbl.taskmanager.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Implementation of the process abstraction.
 *
 * @author  Boyko Boykov
 * @version 0.5
 * @since   2020-06-15
 */
public class OSProcessImpl implements OSProcess {

    private final UUID processId;
    private final Priority priority;
    private final Instant creationTime;

    public OSProcessImpl(UUID processId, Priority priority, Instant creationTime) {
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
        OSProcessImpl process = (OSProcessImpl) o;
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
