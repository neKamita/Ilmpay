package uz.pdp.ilmpay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportLogoDTO {
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Image URL is required")
    private String imageUrl;
    
    @URL(message = "Website URL must be valid")
    private String websiteUrl;
    
    private boolean active;
} 