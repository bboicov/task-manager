package bg.fbl.taskmanager.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Represent the abstraction of the process
 *
 * @author  Boyko Boykov
 * @version 0.5
 * @since   2020-06-14
 */
public interface OSProcess {

    public boolean run();
    public boolean suspend();
    public boolean kill();
    public void killForced();

    public UUID getId();
    public Priority getPriority();
    public Instant getCreationTime();
}
