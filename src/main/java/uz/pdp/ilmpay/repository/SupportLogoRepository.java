package uz.pdp.ilmpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import uz.pdp.ilmpay.model.SupportLogo;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportLogoRepository extends JpaRepository<SupportLogo, Long> {
    // Find all active logos ordered by display order (null values last)
    @Query("SELECT s FROM SupportLogo s WHERE s.active = true ORDER BY s.order ASC NULLS LAST")
    List<SupportLogo> findByActiveTrueOrderByOrder();
    
    // Find specific logo by ID that is active
    Optional<SupportLogo> findByIdAndActiveTrue(Long id);

    List<SupportLogo> findByActiveTrue();
}