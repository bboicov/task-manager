package bg.fbl.taskmanager.controller;

import bg.fbl.taskmanager.model.OSProcess;
import bg.fbl.taskmanager.model.Priority;
import bg.fbl.taskmanager.service.ProcessService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.reactivex.Single;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The TaskManagerApi is a controller which provides main application endpoints.
 *
 * @author  Boyko Boykov
 * @version 0.5
 * @since   2020-06-14
 */
@Controller("/tasks")
public class TaskManagerApi {

    final private ProcessService processService;

    public TaskManagerApi(ProcessService processService) {
        this.processService = processService;
    }

    private HttpResponse<Map> createReponse(UUID uuid) {
        Map<String, String> result = new HashMap<>();
        if (uuid != null) {
            result.put("id", uuid.toString());
            return HttpResponse.created(result);
        } else {
            result.put("error", "Can't add more processes. Limit was reached.");
            return HttpResponse.serverError(result);
        }
    }

    @Post(uri = "/process", produces = MediaType.APPLICATION_JSON)
    public Single<HttpResponse<Map>> addProcess(Priority priority) {

        return Single.create(emitter -> {
            UUID uuid = processService.addProcess(priority);
            emitter.onSuccess(createReponse(uuid));
        });
    }

    @Post(uri = "/process-fifo", produces = MediaType.APPLICATION_JSON)
    public Single<HttpResponse<Map>> addProcessFifo(Priority priority) {

        return Single.create(emitter -> {
            UUID uuid = processService.addProcessFIFO(priority);
            Map<String, String> result = new HashMap<>();
            result.put("id", uuid.toString());
            emitter.onSuccess(HttpResponse.created(result));
        });
    }

    @Post(uri = "/process-priority", produces = MediaType.APPLICATION_JSON)
    public Single<HttpResponse<Map>> addProcessPriority(Priority priority) {

        return Single.create(emitter -> {
            UUID uuid = processService.addProcessPriority(priority);
            emitter.onSuccess(createReponse(uuid));
        });
    }

    @Get(produces = MediaType.APPLICATION_JSON)
    public Single<HttpResponse<List>> listAll() {

        return Single.create(emitter -> {
            List<OSProcess> result = processService.listProcesses();
            emitter.onSuccess(HttpResponse.ok(result));
        });
    }

    @Delete(uri = "/process/{uuid}")
    public Single<HttpResponse<Map>> killProcess(@PathVariable UUID uuid) {

        return Single.create(emitter -> {
            Map<String, String> result = new HashMap<>();
            if (processService.killProcess(uuid)) {
                result.put("id", uuid.toString());
                emitter.onSuccess(HttpResponse.ok(result));
            } else {
                result.put("error", "Can't find process with id: " + uuid.toString());
                emitter.onSuccess(HttpResponse.serverError(result));
            }
        });
    }

    @Delete(uri = "/processes-priority/{priority}")
    public Single<HttpResponse<Map>> killProcessesWithPriority(@PathVariable Priority priority) {

        return Single.create(emitter -> {
            Map<String, String> result = new HashMap<>();
            processService.killAllWithPriority(priority);
            emitter.onSuccess(HttpResponse.noContent());
        });
    }

    @Delete(uri = "/processes-all")
    public Single<HttpResponse<Map>> killAllProcesses() {

        return Single.create(emitter -> {
            Map<String, String> result = new HashMap<>();
            processService.killAll();
            emitter.onSuccess(HttpResponse.noContent());
        });
    }
}
