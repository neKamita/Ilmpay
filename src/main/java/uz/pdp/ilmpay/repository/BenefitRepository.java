package uz.pdp.ilmpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.pdp.ilmpay.model.Benefit;

import java.util.List;
import java.util.Set;

/**
 * 🎁 BenefitRepository: Where benefits are safely stored!
 * Like a treasure chest, but for awesome features.
 */
public interface BenefitRepository extends JpaRepository<Benefit, Long> {
    
    List<Benefit> findByActiveTrueOrderByDisplayOrderAsc();
    
    @Query("SELECT b FROM Benefit b WHERE b.id IN :ids AND b.active = true")
    List<Benefit> findActiveByIds(@Param("ids") Set<Long> ids);
}
