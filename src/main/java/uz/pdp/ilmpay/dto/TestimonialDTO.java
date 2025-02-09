package uz.pdp.ilmpay.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * 🎭 Testimonial DTO
 * Carrying student testimonials with style!
 * 
 * @author Your Testimonial Handler
 * @version 1.0 (The "Student Voice" Edition)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestimonialDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Comment is required")
    private String comment;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be more than 5")
    private int rating;

    // For file upload
    private MultipartFile imageFile;

    // For displaying stored S3 URL
    private String avatarUrl;

    private boolean isActive = true;
}