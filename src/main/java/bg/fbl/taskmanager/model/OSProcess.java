package bg.fbl.taskmanager.model;

import java.time.Instant;
import java.util.UUID;

public interface Process {

    public boolean run();
    public boolean suspend();
    public boolean kill();
    public void killForced();

    public UUID getId();
    public Priority getPriority();
    public Instant getCreationTime();
}
