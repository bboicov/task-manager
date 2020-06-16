package bg.fbl.taskmanager.factory;

import bg.fbl.taskmanager.model.Priority;
import bg.fbl.taskmanager.model.OSProcess;
import bg.fbl.taskmanager.model.OSProcessImpl;

import javax.inject.Singleton;
import java.time.Instant;
import java.util.UUID;

@Singleton
public class ProcessFactory {

    public OSProcess createProcess(Priority priority) {

        // create and initialize new process
        OSProcess process = new OSProcessImpl(UUID.randomUUID(), priority, Instant.now());

        return process;
    }

}
