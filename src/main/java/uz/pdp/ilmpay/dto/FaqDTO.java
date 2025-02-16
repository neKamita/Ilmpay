package uz.pdp.ilmpay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 🤔 FAQ Data Transfer Object
 * Carrying knowledge from server to client! 📚
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaqDTO {
    private Long id;

    @NotBlank(message = "Question is required")
    @Size(max = 200, message = "Question cannot be longer than 200 characters")
    private String question;

    @NotBlank(message = "Answer is required")
    private String answer;

    private int displayOrder;
    private boolean active = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
