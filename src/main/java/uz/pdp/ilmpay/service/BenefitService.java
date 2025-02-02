package uz.pdp.ilmpay.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.ilmpay.dto.BenefitCardDTO;
import uz.pdp.ilmpay.model.Benefit;
import uz.pdp.ilmpay.repository.BenefitRepository;

import java.util.List;
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

    /**
     * 📚 Get all active benefits - Collecting all the goodies in one place
     */
    public List<BenefitCardDTO> findAllActive() {
        return benefitRepository.findByActiveTrueOrderByDisplayOrderAsc()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * 🎯 Get benefit by ID - The benefit treasure hunt
     */
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
    public void delete(Long id) {
        Benefit benefit = benefitRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Benefit not found with id: " + id));
            
        benefit.setActive(false);
        log.info("Soft deleting benefit with id: {}", id);
        benefitRepository.save(benefit);
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
        return new BenefitCardDTO(
            benefit.getId(),
            benefit.getTitle(),
            benefit.getDescription(),
            benefit.getDisplayOrder(),
            benefit.isActive()
        );
    }
}
