package uk.gov.hmcts.reform.dev.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.CreateTaskDTO;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskDTO;
import uk.gov.hmcts.reform.dev.models.UpdateTaskDTO;
import uk.gov.hmcts.reform.dev.repository.TaskRepository;

import java.util.List;

@Service
public class TaskManagementService {

    private final TaskRepository taskRepository;

    public TaskManagementService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findTaskById(id);
        if (task != null) {
            return task.toDTO();
        } else {
            throw new TaskNotFoundException(id);
        }
    }

    public List<TaskDTO> getAllTasks() {
        List<TaskDTO> results = taskRepository.findAll()
            .stream()
            .map(Task::toDTO)
            .toList();

        if (results.isEmpty()) {
            throw new TaskNotFoundException("No tasks exist.");
        }

        return results;
    }

    public TaskDTO createTask(CreateTaskDTO createTaskDTO) {
        Task newTask = new Task();
        newTask.setTitle(createTaskDTO.getTitle());
        newTask.setDescription(createTaskDTO.getDescription());
        newTask.setStatus(createTaskDTO.getStatus());
        newTask.setDueDatetime(createTaskDTO.getDueDatetime());

        return taskRepository.save(newTask).toDTO();
    }

    public TaskDTO deleteTask(Long id) {
        Task task = taskRepository.findTaskById(id);
        if (task != null) {
            taskRepository.delete(task);
            return task.toDTO();
        } else {
            throw new TaskNotFoundException(id);
        }
    }

    @Transactional
    public TaskDTO updateTask(Long id, UpdateTaskDTO taskUpdateDTO) {
        Task currentTask = taskRepository.findTaskById(id);

        if (currentTask != null) {
            currentTask.setStatus(taskUpdateDTO.getStatus());
            taskRepository.save(currentTask);
            return currentTask.toDTO();
        } else {
            throw new TaskNotFoundException(id);
        }
    }
}
