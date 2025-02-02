package uz.pdp.ilmpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.ilmpay.model.Benefit;

import java.util.List;

/**
 * 🏪 BenefitRepository: The benefit storage facility!
 * Like a magical database where benefits go to rest between requests.
 */
public interface BenefitRepository extends JpaRepository<Benefit, Long> {
    // Spring Data JPA magic happens here! 🪄
    List<Benefit> findByActiveTrueOrderByDisplayOrderAsc();
}
