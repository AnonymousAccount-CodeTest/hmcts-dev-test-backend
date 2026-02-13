import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.CreateTaskDTO;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskDTO;
import uk.gov.hmcts.reform.dev.models.UpdateTaskDTO;
import uk.gov.hmcts.reform.dev.repository.TaskRepository;
import uk.gov.hmcts.reform.dev.service.TaskManagementService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TaskManagementServiceTest {

    @Mock
    private TaskRepository mockedTaskRepository;

    @InjectMocks
    private TaskManagementService taskManagementService;

    Task getTestTask() {
        return new Task(
            1L,
            "Test Title",
            "Test Description",
            "Test Status",
            LocalDateTime.of(2026, 2, 10, 0, 0, 0)
        );
    }

    @Test
    void getTaskByIdTest() {
        Task task = getTestTask();
        Mockito.when(mockedTaskRepository.findTaskById(1L)).thenReturn(task);

        TaskDTO returnedTask = taskManagementService.getTaskById(1L);

        assertSame(returnedTask.getId(), task.getId());
        assertSame(returnedTask.getTitle(), task.getTitle());
        assertSame(returnedTask.getDescription(), task.getDescription());
        assertSame(returnedTask.getStatus(), task.getStatus());
        assertSame(returnedTask.getDueDatetime(), task.getDueDatetime());
    }

    @Test
    void getTaskByIdNotFoundTest() {
        TaskNotFoundException expectedException = assertThrows(
            TaskNotFoundException.class,
            () -> taskManagementService.getTaskById(1L)
        );

        assertEquals("Task not found with id: 1", expectedException.getMessage());
    }

    @Test
    void getTaskAllTasksTest() {
        Task task = getTestTask();
        Mockito.when(mockedTaskRepository.findAll()).thenReturn(List.of(task, task));

        List<TaskDTO> allTasks = taskManagementService.getAllTasks();

        assertEquals(2, allTasks.size());
    }

    @Test
    void getTaskAllTasksNoneExistTest() {
        TaskNotFoundException expectedException = assertThrows(
            TaskNotFoundException.class,
            () -> taskManagementService.getAllTasks()
        );

        assertEquals("No tasks exist.", expectedException.getMessage());
    }

    @Test
    void createTaskTest() {
        CreateTaskDTO createTaskDTO = new CreateTaskDTO(
            "Test Title",
            "Test Description",
            "Test Status",
            LocalDateTime.of(2026, 2, 10, 0, 0, 0)
        );
        Mockito.when(mockedTaskRepository.save(any())).thenReturn(getTestTask());

        TaskDTO result = taskManagementService.createTask(createTaskDTO);
        assertSame(1L, result.getId());
        assertSame(createTaskDTO.getTitle(), result.getTitle());
        assertSame(createTaskDTO.getDescription(), result.getDescription());
        assertSame(createTaskDTO.getStatus(), result.getStatus());
        assertEquals(createTaskDTO.getDueDatetime(), result.getDueDatetime());
    }

    @Test
    void deleteTaskTest() {
        long id = 1L;
        Task expectedTask = getTestTask();
        Mockito.when(mockedTaskRepository.findTaskById(id)).thenReturn(expectedTask);

        TaskDTO taskDTO = taskManagementService.deleteTask(id);
        assertSame(expectedTask.getId(), taskDTO.getId());
        assertSame(expectedTask.getTitle(), taskDTO.getTitle());
        assertSame(expectedTask.getDescription(), taskDTO.getDescription());
        assertSame(expectedTask.getStatus(), taskDTO.getStatus());
        assertEquals(expectedTask.getDueDatetime(), taskDTO.getDueDatetime());
    }

    @Test
    void deleteTaskDoesNotExistTest() {
        long id = 1L;
        Mockito.when(mockedTaskRepository.findTaskById(id)).thenReturn(null);

        TaskNotFoundException expectedException = assertThrows(
            TaskNotFoundException.class,
            () -> taskManagementService.deleteTask(id)
        );

        assertEquals("Task not found with id: 1", expectedException.getMessage());
    }

    @Test
    void updateTaskTest() {
        long id = 1L;
        UpdateTaskDTO updateTaskDTO = new UpdateTaskDTO("new Status");
        Task taskToUpdate = getTestTask();
        taskToUpdate.setStatus(updateTaskDTO.getStatus());

        Mockito.when(mockedTaskRepository.findTaskById(id)).thenReturn(taskToUpdate);
        Mockito.when(mockedTaskRepository.save(any())).thenReturn(taskToUpdate);

        TaskDTO taskDTO = taskManagementService.updateTask(id, updateTaskDTO);
        assertSame(taskToUpdate.getId(), taskDTO.getId());
        assertSame(taskToUpdate.getTitle(), taskDTO.getTitle());
        assertSame(taskToUpdate.getDescription(), taskDTO.getDescription());
        assertSame(taskToUpdate.getStatus(), taskDTO.getStatus());
        assertEquals(taskToUpdate.getDueDatetime(), taskDTO.getDueDatetime());
    }

    @Test
    void updateTaskDoesNotExistTest() {
        long id = 1L;
        UpdateTaskDTO updateTaskDTO = new UpdateTaskDTO("New Status");

        Mockito.when(mockedTaskRepository.findTaskById(id)).thenReturn(null);

        TaskNotFoundException expectedException = assertThrows(
            TaskNotFoundException.class,
            () -> taskManagementService.updateTask(id, updateTaskDTO)
        );

        assertEquals("Task not found with id: 1", expectedException.getMessage());
    }


}
