package bg.fbl.taskmanager.factory;

import bg.fbl.taskmanager.model.Priority;
import bg.fbl.taskmanager.model.OSProcess;
import bg.fbl.taskmanager.model.OSProcessImpl;

import javax.inject.Singleton;
import java.time.Instant;
import java.util.UUID;

/**
 * ProcessFactory utilizes "Factory" design pattern to create new processes.
 * It provides specific control over the entity creation.
 *
 * @author  Boyko Boykov
 * @version 0.5
 * @since   2020-06-14
 */
@Singleton
public class ProcessFactory {

    public OSProcess createProcess(Priority priority) {

        // create and initialize new process
        OSProcess process = new OSProcessImpl(UUID.randomUUID(), priority, Instant.now());

        return process;
    }

}
