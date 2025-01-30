package uz.pdp.ilmpay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestimonialDTO {
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String role;
    
    @NotBlank(message = "Comment is required")
    private String comment;
    
    private String avatarUrl;
    
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    private int rating;
    
    private boolean active;
} 