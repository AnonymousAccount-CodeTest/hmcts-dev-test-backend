package uk.gov.hmcts.reform.dev.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class CreateTaskDTO {
    @NotBlank(message = "Title is a mandatory field.")
    private String title;
    private String description;
    @NotBlank(message = "Status is a mandatory field.")
    private String status;
    @NotNull(message = "Due Date is a mandatory field.")
    private LocalDateTime dueDatetime;
}
