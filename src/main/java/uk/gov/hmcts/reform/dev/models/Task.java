package uk.gov.hmcts.reform.dev.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Task {
    private @Id @GeneratedValue Long id;
    @NotBlank(message = "Title is a mandatory field.")
    private String title;
    private String description;
    @NotBlank(message = "Status is a mandatory field.")
    private String status;
    @NotNull(message = "Due Date is a mandatory field.")
    private LocalDateTime dueDatetime;

    public TaskDTO toDTO() {
        return new TaskDTO(id, title, description, status, dueDatetime);
    }
}
