package uz.pdp.ilmpay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.ilmpay.dto.BenefitCardDTO;
import uz.pdp.ilmpay.exception.ResourceNotFoundException;
import uz.pdp.ilmpay.model.BenefitCard;
import uz.pdp.ilmpay.repository.BenefitCardRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BenefitCardService {
    private final BenefitCardRepository benefitCardRepository;

    public List<BenefitCardDTO> findAllActive() {
        return benefitCardRepository.findByActiveTrueOrderByDisplayOrderAsc()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public BenefitCardDTO create(BenefitCardDTO dto) {
        validateBenefitCard(dto);
        
        BenefitCard card = new BenefitCard();
        card.setTitle(dto.getTitle());
        card.setDescription(dto.getDescription());
        card.setIconUrl(dto.getIconUrl());
        card.setDisplayOrder(dto.getDisplayOrder());
        card.setActive(true);
        card.setCreatedAt(LocalDateTime.now());

        log.info("Creating new benefit card: {}", dto.getTitle());
        return toDTO(benefitCardRepository.save(card));
    }

    public BenefitCardDTO update(Long id, BenefitCardDTO dto) {
        validateBenefitCard(dto);
        
        BenefitCard card = benefitCardRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Benefit card not found with id: " + id));

        card.setTitle(dto.getTitle());
        card.setDescription(dto.getDescription());
        card.setIconUrl(dto.getIconUrl());
        card.setDisplayOrder(dto.getDisplayOrder());
        card.setActive(dto.isActive());

        log.info("Updating benefit card with id: {}", id);
        return toDTO(benefitCardRepository.save(card));
    }

    public void delete(Long id) {
        BenefitCard card = benefitCardRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Benefit card not found with id: " + id));
            
        card.setActive(false);
        log.info("Soft deleting benefit card with id: {}", id);
        benefitCardRepository.save(card);
    }

    private void validateBenefitCard(BenefitCardDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }
        if (dto.getIconUrl() == null || dto.getIconUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("Icon URL is required");
        }
    }

    private BenefitCardDTO toDTO(BenefitCard card) {
        return new BenefitCardDTO(
            card.getId(),
            card.getTitle(),
            card.getDescription(),
            card.getIconUrl(),
            card.getDisplayOrder(),
            card.isActive()
        );
    }
}
