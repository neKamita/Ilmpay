package uz.pdp.ilmpay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "benefit_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BenefitCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String description;

    private String iconUrl;
    private int displayOrder;
    private boolean active;
    private LocalDateTime createdAt;
}
