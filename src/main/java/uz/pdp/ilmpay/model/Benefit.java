package uz.pdp.ilmpay.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Benefit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String iconUrl;  // Store the URL or path to the benefit icon
    
    private Integer displayOrder;  // To control the display order of benefits
    
    private boolean active = true;  // To enable/disable benefits without deleting them
    
    // Funny comment: "Because who doesn't love a good benefit? 🎁"
}
