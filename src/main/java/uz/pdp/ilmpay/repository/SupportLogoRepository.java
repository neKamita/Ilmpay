package uz.pdp.ilmpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uz.pdp.ilmpay.model.SupportLogo;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportLogoRepository extends JpaRepository<SupportLogo, Long> {
    List<SupportLogo> findByActiveTrue();
    Optional<SupportLogo> findByIdAndActiveTrue(Long id);
} 