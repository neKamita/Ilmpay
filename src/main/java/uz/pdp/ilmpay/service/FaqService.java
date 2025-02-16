package uz.pdp.ilmpay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.i18n.LocaleContextHolder;
import uz.pdp.ilmpay.service.TranslationService;
import uz.pdp.ilmpay.dto.FaqDTO;
import uz.pdp.ilmpay.exception.ResourceNotFoundException;
import uz.pdp.ilmpay.model.Faq;
import uz.pdp.ilmpay.repository.FaqRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 🤔 FAQ Service
 * Where all the FAQ magic happens! ✨
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FaqService {
    private final FaqRepository faqRepository;
    private final TranslationService translationService;

    /**
     * 📚 Get all active FAQs
     */
    public List<FaqDTO> findAllActive() {
        return faqRepository.findByActiveTrueOrderByDisplayOrderAsc()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * 🔍 Find FAQ by ID
     */
    public FaqDTO findById(Long id) {
        log.info("🔍 Finding FAQ with id: {}", id);
        return toDTO(faqRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("FAQ not found with id: " + id)));
    }

    /**
     * ➕ Create a new FAQ
     */
    public FaqDTO create(FaqDTO dto) {
        validateFaq(dto);
        
        Faq faq = new Faq();
        faq.setQuestion(dto.getQuestion());
        faq.setAnswer(dto.getAnswer());
        faq.setDisplayOrder(getNextDisplayOrder());
        faq.setActive(true);
        faq.setCreatedAt(LocalDateTime.now());

        log.info("✨ Creating new FAQ: {}", dto.getQuestion());
        return toDTO(faqRepository.save(faq));
    }

    /**
     * 📝 Update an existing FAQ
     */
    public FaqDTO update(Long id, FaqDTO dto) {
        validateFaq(dto);
        
        Faq faq = faqRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("FAQ not found with id: " + id));

        faq.setQuestion(dto.getQuestion());
        faq.setAnswer(dto.getAnswer());
        faq.setDisplayOrder(dto.getDisplayOrder());
        faq.setUpdatedAt(LocalDateTime.now());

        log.info("✏️ Updating FAQ {}: {}", id, dto.getQuestion());
        return toDTO(faqRepository.save(faq));
    }

    /**
     * 🗑️ Delete a FAQ
     */
    public void delete(Long id) {
        Faq faq = faqRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("FAQ not found with id: " + id));
            
        log.info("🗑️ Deleting FAQ {}: {}", id, faq.getQuestion());
        faqRepository.delete(faq);
    }

    /**
     * 🔄 Reorder FAQs
     */
    public List<FaqDTO> reorder(List<FaqDTO> dtos) {
        log.info("🔄 Reordering {} FAQs", dtos.size());
        
        List<Faq> faqs = dtos.stream()
            .map(dto -> {
                Faq faq = faqRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("FAQ not found with id: " + dto.getId()));
                faq.setDisplayOrder(dto.getDisplayOrder());
                return faq;
            })
            .collect(Collectors.toList());

        return faqRepository.saveAll(faqs)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * 🎯 Get next display order
     */
    private int getNextDisplayOrder() {
        return faqRepository.findFirstByOrderByDisplayOrderDesc()
            .map(faq -> faq.getDisplayOrder() + 1)
            .orElse(0);
    }

    /**
     * ✅ Validate FAQ data
     */
    private void validateFaq(FaqDTO dto) {
        if (dto.getQuestion() == null || dto.getQuestion().trim().isEmpty()) {
            throw new IllegalArgumentException("Question is required");
        }
        if (dto.getAnswer() == null || dto.getAnswer().trim().isEmpty()) {
            throw new IllegalArgumentException("Answer is required");
        }
    }

    /**
     * 🔄 Convert FAQ entity to DTO
     */
   private FaqDTO toDTO(Faq faq) {
        String currentLanguage = LocaleContextHolder.getLocale().getLanguage();
        FaqDTO dto = new FaqDTO();
        dto.setId(faq.getId());
        dto.setQuestion(faq.getQuestion());
        dto.setAnswer(faq.getAnswer());
        if(!currentLanguage.equals("en")){
            dto.setQuestion(translationService.translate(faq.getQuestion(),"en",currentLanguage));
            dto.setAnswer(translationService.translate(faq.getAnswer(),"en",currentLanguage));
        }
        dto.setDisplayOrder(faq.getDisplayOrder());
        dto.setActive(faq.isActive());
        dto.setCreatedAt(faq.getCreatedAt());
        dto.setUpdatedAt(faq.getUpdatedAt());
        return dto;
    }
}
