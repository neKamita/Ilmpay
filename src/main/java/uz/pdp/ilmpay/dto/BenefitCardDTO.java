package uz.pdp.ilmpay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * 🎁 BenefitCardDTO: The data carrier for our benefits!
 * Like a gift box, but with Java fields instead of surprises.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenefitCardDTO {
    private Long id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @Min(value = 1, message = "Display order must be at least 1")
    @Max(value = 4, message = "Display order cannot be more than 4")
    private int displayOrder;
    
    private boolean active;
}