package uz.pdp.ilmpay.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "benefits")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Benefit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Description is required")
    private String description;
    
    @Min(value = 1, message = "Display order must be at least 1")
    @Max(value = 4, message = "Display order cannot be more than 4")
    private Integer displayOrder;  // To control the display order of benefits
    
    @Column(nullable = false)
    private boolean active = true;  // Default to active when created
    
    // Funny comment: "Keeping it simple, just like my coffee - no fancy toppings! ☕"
}
