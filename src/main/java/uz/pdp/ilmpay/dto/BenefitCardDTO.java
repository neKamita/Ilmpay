package uz.pdp.ilmpay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BenefitCardDTO {
    private Long id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotBlank(message = "Icon URL is required")
    private String iconUrl;
    
    @Min(value = 0, message = "Display order must be non-negative")
    private int displayOrder;
    
    private boolean active;
} 