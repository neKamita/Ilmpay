package uz.pdp.ilmpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uz.pdp.ilmpay.model.BenefitCard;

import java.util.List;
import java.util.Optional;

@Repository
public interface BenefitCardRepository extends JpaRepository<BenefitCard, Long> {
    List<BenefitCard> findByActiveTrueOrderByDisplayOrderAsc();
    Optional<BenefitCard> findByIdAndActiveTrue(Long id);
    long countByActiveTrue();
} 