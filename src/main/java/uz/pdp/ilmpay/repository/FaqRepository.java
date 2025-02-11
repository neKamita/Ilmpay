package uz.pdp.ilmpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uz.pdp.ilmpay.model.Faq;

import java.util.List;
import java.util.Optional;

/**
 * 🤔 FAQ Repository
 * Where FAQs come to rest! 📚
 */
@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {
    /**
     * 📚 Find all active FAQs ordered by display order
     */
    List<Faq> findByActiveTrueOrderByDisplayOrderAsc();

    /**
     * 🎯 Find the FAQ with the highest display order
     */
    Optional<Faq> findFirstByOrderByDisplayOrderDesc();
}