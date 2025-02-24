package uz.pdp.ilmpay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.i18n.LocaleContextHolder;
import uz.pdp.ilmpay.dto.BenefitCardDTO;
import uz.pdp.ilmpay.exception.ResourceNotFoundException;
import uz.pdp.ilmpay.model.BenefitCard;
import uz.pdp.ilmpay.repository.BenefitCardRepository;
import uz.pdp.ilmpay.service.TranslationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BenefitCardService {
    private final BenefitCardRepository benefitCardRepository;
    private final TranslationService translationService;

    @Cacheable(value = "benefitCards", key = "'allActive'")
    public List<BenefitCardDTO> findAllActive() {
        return benefitCardRepository.findByActiveTrueOrderByDisplayOrderAsc()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @CacheEvict(value = "benefitCards", allEntries = true)
    public BenefitCardDTO create(BenefitCardDTO dto) {
        validateBenefitCard(dto);

        BenefitCard card = new BenefitCard();
        card.setTitle(dto.getTitle());
        card.setDescription(dto.getDescription());
        card.setDisplayOrder(dto.getDisplayOrder());
        card.setActive(true);
        card.setCreatedAt(LocalDateTime.now());

        log.info("Creating new benefit card: {}", dto.getTitle());
        return toDTO(benefitCardRepository.save(card));
    }

    @CachePut(value = "benefitCards", key = "#id")
    @CacheEvict(value = "benefitCards", allEntries = true)
    public BenefitCardDTO update(Long id, BenefitCardDTO dto) {
        validateBenefitCard(dto);

        BenefitCard card = benefitCardRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Benefit card not found with id: " + id));

        card.setTitle(dto.getTitle());
        card.setDescription(dto.getDescription());
        card.setDisplayOrder(dto.getDisplayOrder());
        card.setActive(dto.isActive());

        log.info("Updating benefit card with id: {}", id);
        return toDTO(benefitCardRepository.save(card));
    }

    @CacheEvict(value = "benefitCards", allEntries = true)
    public void delete(Long id) {
        BenefitCard card = benefitCardRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Benefit card not found with id: " + id));

        card.setActive(false);
        log.info("Soft deleting benefit card with id: {}", id);
        benefitCardRepository.save(card);
    }

    

    @Cacheable(value = "benefitCards", key = "#id")
    public BenefitCardDTO findById(Long id) {
        return benefitCardRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Benefit not found"));
    }

    private void validateBenefitCard(BenefitCardDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }
    }

  private BenefitCardDTO toDTO(BenefitCard card) {
       String currentLanguage = LocaleContextHolder.getLocale().getLanguage();
       return BenefitCardDTO.builder()
               .id(card.getId())
               .title(currentLanguage.equals("en") ? card.getTitle() : translationService.translate(card.getTitle(),"en",currentLanguage))
               .description(currentLanguage.equals("en") ? card.getDescription() : translationService.translate(card.getDescription(),"en",currentLanguage))
               .displayOrder(card.getDisplayOrder())
               .active(card.isActive())
               .build();
   }
}
