package uz.pdp.ilmpay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Testimonial Entity
 * Stores student testimonials with their feedback and ratings
 * 
 * @author neKamita
 * @version 1.0
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "testimonials")
public class Testimonial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 1000)
    private String comment;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(nullable = false)
    private int rating;

    @Column(name = "display_order")
    private Integer order;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}