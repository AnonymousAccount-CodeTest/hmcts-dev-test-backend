package uk.gov.hmcts.reform.dev;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.dev.models.CreateTaskDTO;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskDTO;
import uk.gov.hmcts.reform.dev.models.UpdateTaskDTO;
import uk.gov.hmcts.reform.dev.repository.TaskRepository;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureTestDatabase
class TaskControllerTest {

    @Autowired
    private TaskRepository taskRepository;

    @Value("${TEST_URL:http://localhost:4000}")
    private String testUrl;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
        // Make sure that there is no data in the database before the test runs
        taskRepository.deleteAll();
    }

    @Test
    void getTaskByIdTest() {
        CreateTaskDTO createTaskDTO = new CreateTaskDTO(
            "Test Task", "desc", "status",
            LocalDateTime.of(2026, 2, 10, 0, 0, 0)
        );

        Task task = new Task();
        task.setTitle(createTaskDTO.getTitle());
        task.setDescription(createTaskDTO.getDescription());
        task.setStatus(createTaskDTO.getStatus());
        task.setDueDatetime(createTaskDTO.getDueDatetime());

        Task savedTask = taskRepository.save(task);

        Response response = given().queryParam("id", savedTask.getId())
            .contentType(ContentType.JSON)
            .when()
            .get("/getTask")
            .then()
            .extract().response();

        Assertions.assertEquals(200, response.statusCode());
        TaskDTO getTaskResponseObject = response.as(TaskDTO.class);
        Assertions.assertEquals(savedTask.getId(), getTaskResponseObject.getId());
        Assertions.assertEquals(createTaskDTO.getTitle(), getTaskResponseObject.getTitle());
        Assertions.assertEquals(createTaskDTO.getDescription(), getTaskResponseObject.getDescription());
        Assertions.assertEquals(createTaskDTO.getStatus(), getTaskResponseObject.getStatus());
        Assertions.assertEquals(createTaskDTO.getDueDatetime(), getTaskResponseObject.getDueDatetime());
    }

    @Test
    void getTaskByIdNotFoundTest() {
        Response response = given().queryParam("id", 1L)
            .contentType(ContentType.JSON)
            .when()
            .get("/getTask")
            .then()
            .extract().response();

        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertEquals(
            "Task not found with id: 1",
            response.jsonPath().getMap("$").get("message")
        );
    }

    @Test
    void createTaskTest() {
        CreateTaskDTO createTaskDTO = new CreateTaskDTO(
            "Test Task", "desc", "status",
            LocalDateTime.of(2026, 2, 10, 0, 0, 0)
        );

        Response response = given().body(createTaskDTO)
            .contentType(ContentType.JSON)
            .when()
            .post("/createTask")
            .then()
            .extract().response();

        Assertions.assertEquals(200, response.statusCode());
        TaskDTO getTaskResponseObject = response.as(TaskDTO.class);

        Assertions.assertEquals(createTaskDTO.getTitle(), getTaskResponseObject.getTitle());
        Assertions.assertEquals(createTaskDTO.getDescription(), getTaskResponseObject.getDescription());
        Assertions.assertEquals(createTaskDTO.getStatus(), getTaskResponseObject.getStatus());
        Assertions.assertEquals(createTaskDTO.getDueDatetime(), getTaskResponseObject.getDueDatetime());
    }

    @Test
    void createTaskParamValidationTitleTest() {
        CreateTaskDTO createTaskDTO = new CreateTaskDTO(
            null, "desc", "status",
            LocalDateTime.of(2026, 2, 10, 0, 0, 0)
        );

        Response response = given().body(createTaskDTO)
            .contentType(ContentType.JSON)
            .when()
            .post("/createTask")
            .then()
            .extract().response();

        Assertions.assertEquals(400, response.statusCode());

        Assertions.assertEquals(
            "Validation errors with the request body. "
                + "Please ensure that all mandatory fields are included.",
            response.jsonPath().getMap("$").get("message")
        );

        Assertions.assertEquals(
            "Title is a mandatory field.",
            response.jsonPath().getMap("validationErrors").get("title")
        );
    }

    @Test
    void updateTaskStatusTest() {
        CreateTaskDTO createTaskDTO = new CreateTaskDTO(
            "Test Task", "desc", "status",
            LocalDateTime.of(2026, 2, 10, 0, 0, 0)
        );

        Task task = new Task();
        task.setTitle(createTaskDTO.getTitle());
        task.setDescription(createTaskDTO.getDescription());
        task.setStatus(createTaskDTO.getStatus());
        task.setDueDatetime(createTaskDTO.getDueDatetime());

        Task savedTask = taskRepository.save(task);

        UpdateTaskDTO updateTaskDTO = new UpdateTaskDTO("Updated Status");

        Response response = given()
            .queryParam("id", savedTask.getId())
            .body(updateTaskDTO)
            .contentType(ContentType.JSON)
            .when()
            .put("/updateTaskStatus")
            .then()
            .extract().response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(updateTaskDTO.getStatus(), response.as(TaskDTO.class).getStatus());
    }

    @Test
    void updateTaskStatusTaskDoesNotExistTest() {
        UpdateTaskDTO updateTaskDTO = new UpdateTaskDTO("Updated Status");

        Response response = given()
            .queryParam("id", 1L)
            .body(updateTaskDTO)
            .contentType(ContentType.JSON)
            .when()
            .put("/updateTaskStatus")
            .then()
            .extract().response();

        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertEquals(
            "Task not found with id: 1",
            response.jsonPath().getMap("$").get("message")
        );
    }

    @Test
    void deleteTaskTest() {
        CreateTaskDTO createTaskDTO = new CreateTaskDTO(
            "Test Task", "desc", "status",
            LocalDateTime.of(2026, 2, 10, 0, 0, 0)
        );

        Task task = new Task();
        task.setTitle(createTaskDTO.getTitle());
        task.setDescription(createTaskDTO.getDescription());
        task.setStatus(createTaskDTO.getStatus());
        task.setDueDatetime(createTaskDTO.getDueDatetime());

        Task savedTask = taskRepository.save(task);

        Response response = given()
            .queryParam("id", savedTask.getId())
            .contentType(ContentType.JSON)
            .when()
            .delete("/deleteTask")
            .then()
            .extract().response();

        Assertions.assertEquals(204, response.statusCode());

        Task deletedTask = taskRepository.findTaskById(savedTask.getId());
        Assertions.assertNull(deletedTask);
    }

    @Test
    void deleteTaskTaskDoesNotExistTest() {
        Response response = given()
            .queryParam("id", 1L)
            .contentType(ContentType.JSON)
            .when()
            .delete("/deleteTask")
            .then()
            .extract().response();

        Assertions.assertEquals(404, response.statusCode());
        Assertions.assertEquals(
            "Task not found with id: 1",
            response.jsonPath().getMap("$").get("message")
        );
    }
}
