package uk.gov.hmcts.reform.dev.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.dev.models.CreateTaskDTO;
import uk.gov.hmcts.reform.dev.models.TaskDTO;
import uk.gov.hmcts.reform.dev.models.UpdateTaskDTO;
import uk.gov.hmcts.reform.dev.service.TaskManagementService;

import java.util.List;

@RestController
@Tag(name = "Tasks", description = "Contains REST endpoints that support viewing tasks, creating new tasks, "
    + "updating the status of a task and deleting tasks.")
public class TaskController {

    private final TaskManagementService taskManagementService;

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    public TaskController(TaskManagementService taskManagementService) {
        this.taskManagementService = taskManagementService;
    }

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<String> root() {
        return ResponseEntity.ok("Api is online and available.");
    }

    @Operation(
        summary = "Retrieve a task by ID",
        description = "Returns a single task object. Returns 404 if the task does not exist."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    @GetMapping(value = "/getTask", produces = "application/json")
    public ResponseEntity<TaskDTO> getTaskById(@RequestParam Long id) {
        return ResponseEntity.ok(taskManagementService.getTaskById(id));
    }

    @Operation(
        summary = "Retrieve all tasks.",
        description = "Returns a list of task objects. Returns 404 if the task does not exist."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
        @ApiResponse(responseCode = "404", description = "No Tasks not found", content = @Content)
    })
    @GetMapping(value = "/getTasks", produces = "application/json")
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        return ResponseEntity.ok(taskManagementService.getAllTasks());
    }

    @Operation(
        summary = "Update the status of a task given the ID and a request body containing the new status.",
        description = "Returns a single task object. This returned task will be the complete new object."
            + " Returns 404 if the task does not exist."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    @PutMapping(value = "/updateTaskStatus", produces = "application/json")
    public ResponseEntity<TaskDTO> updateTaskStatus(@RequestParam Long id,
                                                    @RequestBody UpdateTaskDTO updateTaskDTO) {
        return ResponseEntity.ok(taskManagementService.updateTask(id, updateTaskDTO));
    }

    @Operation(
        summary = "Delete a task given the ID.",
        description = "Returns a 204 no content confirming that the task was successfully deleted."
            + " Returns 404 if the task does not exist."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Successfully retrieved"),
        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    @DeleteMapping(value = "/deleteTask", produces = "application/json")
    public ResponseEntity<TaskDTO> deleteTask(@RequestParam Long id) {
        taskManagementService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Create a new task with the content supplied in the request body.",
        description = "Returns the newly created Task. Returns 404 if the task does not exist."
            + " Returns 400 if a validation failure has occurred (With the details of why it failed)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
        @ApiResponse(responseCode = "400", description = "Successfully retrieved"),
        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    @PostMapping(value = "/createTask", produces = "application/json")
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody CreateTaskDTO createTaskDTO) {
        logger.info("Request to create new task: " + createTaskDTO);
        return ResponseEntity.ok(taskManagementService.createTask(createTaskDTO));
    }
}
