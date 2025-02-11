package uz.pdp.ilmpay.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;

/**
 * 🎨 Support Logo DTO
 * Carrying our partner logos in style!
 * 
 * @author Your Logo Handler
 * @version 1.0 (The "Brand Carrier" Edition)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportLogoDTO {
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    // Image can be provided either as URL or file
    private String imageUrl;
    @JsonIgnore 
    private MultipartFile imageFile;
    
    @URL(message = "Website URL must be valid")
    private String websiteUrl;
    
    private boolean active;
    
    private Integer displayOrder;
    
    // Constructor for URL-based creation
    public SupportLogoDTO(Long id, String name, String imageUrl, String websiteUrl, boolean active, Integer displayOrder) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.websiteUrl = websiteUrl;
        this.active = active;
        this.displayOrder = displayOrder;
    }

    public SupportLogoDTO(Long id, String name, String imageUrl, String websiteUrl, boolean active) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.websiteUrl = websiteUrl;
        this.active = active;
    }
}