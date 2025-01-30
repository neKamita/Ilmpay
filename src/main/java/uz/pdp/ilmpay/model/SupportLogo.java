package uz.pdp.ilmpay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_logos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportLogo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String imageUrl;
    private String websiteUrl;
    private boolean active;
    private LocalDateTime createdAt;
} 