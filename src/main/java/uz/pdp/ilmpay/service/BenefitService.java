package uz.pdp.ilmpay.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.i18n.LocaleContextHolder;
import uz.pdp.ilmpay.dto.BenefitCardDTO;
import uz.pdp.ilmpay.dto.ReorderItemDTO;
import uz.pdp.ilmpay.model.Benefit;
import uz.pdp.ilmpay.repository.BenefitRepository;
import uz.pdp.ilmpay.service.TranslationService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 🎁 BenefitService: The magical place where benefits come to life!
 * Like a benefit factory, but with more code and less conveyor belts.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BenefitService {

    private final BenefitRepository benefitRepository;
     private final TranslationService translationService;

    /**
     * 📚 Get all active benefits - Collecting all the goodies in one place
     */
    @Cacheable(value = "benefits", key = "'allActive'")
    public List<BenefitCardDTO> findAllActive() {
        return benefitRepository.findByActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 🎯 Get benefit by ID - The benefit treasure hunt
     */
    @Cacheable(value = "benefits", key = "#id")
    public BenefitCardDTO findById(Long id) {
        return benefitRepository.findById(id)
            .map(this::toDTO)
            .orElseThrow(() -> new EntityNotFoundException(
                "🔍 Benefit not found! It seems this benefit is playing hide and seek too well!"
            ));
    }

    /**
     * ✨ Create new benefit - The benefit birth center
     */
    @CacheEvict(value = "benefits", allEntries = true)
    public BenefitCardDTO create(BenefitCardDTO dto) {
        validateBenefitCard(dto);
        
        Benefit benefit = new Benefit();
        benefit.setTitle(dto.getTitle());
        benefit.setDescription(dto.getDescription());
        benefit.setDisplayOrder(dto.getDisplayOrder());
        benefit.setActive(true);
        
        log.info("Creating new benefit: {}", dto.getTitle());
        return toDTO(benefitRepository.save(benefit));
    }

    /**
     * 🔄 Update benefit - The benefit makeover station
     */
    @CacheEvict(value = "benefits", allEntries = true)
    public BenefitCardDTO update(Long id, BenefitCardDTO dto) {
        validateBenefitCard(dto);
        
        Benefit existingBenefit = benefitRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Benefit not found with id: " + id));
        
        existingBenefit.setTitle(dto.getTitle());
        existingBenefit.setDescription(dto.getDescription());
        existingBenefit.setDisplayOrder(dto.getDisplayOrder());
        existingBenefit.setActive(dto.isActive());
        
        log.info("Updating benefit with id: {}", id);
        return toDTO(benefitRepository.save(existingBenefit));
    }

    /**
     * 🗑️ Delete benefit - The benefit retirement home
     */
    @CacheEvict(value = "benefits", allEntries = true)
    public void delete(Long id) {
        Benefit benefit = benefitRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Benefit not found with id: " + id));
            
        benefit.setActive(false);
        log.info("Soft deleting benefit with id: {}", id);
        benefitRepository.save(benefit);
    }

    /**
     * 🔄 Reorders benefits based on the provided order list
     * Like playing musical chairs, but with benefits! 🎵
     *
     * @param reorderItems List of items with their new display order
     * @return List of reordered benefits
     */
    @Transactional
    @CacheEvict(value = "benefits", allEntries = true)
    public List<BenefitCardDTO> reorder(List<ReorderItemDTO> reorderItems) {
        log.info("🔄 Starting reorder of {} benefits", reorderItems.size());

        try {
            // Create a map of id to order for quick lookup
            Map<Long, Integer> orderMap = reorderItems.stream()
                    .collect(Collectors.toMap(
                            ReorderItemDTO::getId,
                            ReorderItemDTO::getDisplayOrder
                    ));

            // Get all active benefits that need to be updated
            List<Benefit> benefits = benefitRepository.findActiveByIds(orderMap.keySet());
            
            if (benefits.isEmpty()) {
                log.warn("❌ No active benefits found to reorder!");
                throw new EntityNotFoundException("No active benefits found to reorder");
            }

            // Validate that we found all requested benefits
            if (benefits.size() != orderMap.size()) {
                log.warn("❌ Some benefits were not found or are inactive! Found {}, Expected {}", 
                        benefits.size(), orderMap.size());
                throw new EntityNotFoundException("Some benefits were not found or are inactive");
            }

            // Update orders
            benefits.forEach(benefit -> {
                Integer newOrder = orderMap.get(benefit.getId());
                if (newOrder != null) {
                    benefit.setDisplayOrder(newOrder);
                    log.debug("📋 Updated order for benefit {}: {} -> {}", 
                            benefit.getTitle(), benefit.getDisplayOrder(), newOrder);
                }
            });

            // Save all updated benefits
            List<Benefit> savedBenefits = benefitRepository.saveAll(benefits);
            log.info("✨ Successfully reordered {} benefits", savedBenefits.size());

            // Convert to DTOs and return
            return savedBenefits.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("❌ Failed to reorder benefits: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to reorder benefits: " + e.getMessage(), e);
        }
    }

    /**
     * 🎯 Validate benefit - Making sure our benefits stay in line
     */
    private void validateBenefitCard(BenefitCardDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }
        if (dto.getDisplayOrder() < 1 || dto.getDisplayOrder() > 4) {
            throw new IllegalArgumentException("Display order must be between 1 and 4");
        }
    }

    /**
     * 🔄 Convert entity to DTO - The benefit transformer
     */
private BenefitCardDTO toDTO(Benefit benefit) {
     String currentLanguage = LocaleContextHolder.getLocale().getLanguage();
     return BenefitCardDTO.builder()
             .id(benefit.getId())
             .title(currentLanguage.equals("en") ? benefit.getTitle() : translationService.translate(benefit.getTitle(),"en",currentLanguage))
             .description(currentLanguage.equals("en") ? benefit.getDescription() : translationService.translate(benefit.getDescription(),"en",currentLanguage))
             .displayOrder(benefit.getDisplayOrder())
             .active(benefit.isActive())
             .build();
 }
}
