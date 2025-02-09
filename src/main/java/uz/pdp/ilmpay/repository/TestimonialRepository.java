package uz.pdp.ilmpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.jsonwebtoken.security.Jwks.OP;
import uz.pdp.ilmpay.dto.TestimonialDTO;
import uz.pdp.ilmpay.model.Testimonial;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TestimonialRepository extends JpaRepository<Testimonial, Long> {
    List<Testimonial> findByIsActiveTrueOrderByCreatedAtDesc();

    Optional<Testimonial> findByIdAndIsActiveTrue(Long id);

}