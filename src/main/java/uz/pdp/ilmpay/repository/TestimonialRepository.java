package uz.pdp.ilmpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uz.pdp.ilmpay.model.Testimonial;

import java.util.List;
import java.util.Optional;

/**
 * 🗄️ Testimonial Repository
 * Where we store all those amazing student stories! ⭐
 */
@Repository
public interface TestimonialRepository extends JpaRepository<Testimonial, Long> {
    List<Testimonial> findByIsActiveTrueOrderByOrderAsc();

    Optional<Testimonial> findByIdAndIsActiveTrue(Long id);
}