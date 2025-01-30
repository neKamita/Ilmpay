package uz.pdp.ilmpay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.ilmpay.dto.FaqDTO;
import uz.pdp.ilmpay.exception.ResourceNotFoundException;
import uz.pdp.ilmpay.model.Faq;
import uz.pdp.ilmpay.repository.FaqRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FaqService {
    private final FaqRepository faqRepository;

    public List<FaqDTO> findAllActive() {
        return faqRepository.findByActiveTrueOrderByDisplayOrderAsc()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public FaqDTO create(FaqDTO dto) {
        validateFaq(dto);
        
        Faq faq = new Faq();
        faq.setQuestion(dto.getQuestion());
        faq.setAnswer(dto.getAnswer());
        faq.setDisplayOrder(dto.getDisplayOrder());
        faq.setActive(true);
        faq.setCreatedAt(LocalDateTime.now());

        log.info("Creating new FAQ: {}", dto.getQuestion());
        return toDTO(faqRepository.save(faq));
    }

    public FaqDTO update(Long id, FaqDTO dto) {
        validateFaq(dto);
        
        Faq faq = faqRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("FAQ not found with id: " + id));

        faq.setQuestion(dto.getQuestion());
        faq.setAnswer(dto.getAnswer());
        faq.setDisplayOrder(dto.getDisplayOrder());
        faq.setActive(dto.isActive());

        log.info("Updating FAQ with id: {}", id);
        return toDTO(faqRepository.save(faq));
    }

    public void delete(Long id) {
        Faq faq = faqRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("FAQ not found with id: " + id));
            
        faq.setActive(false);
        log.info("Soft deleting FAQ with id: {}", id);
        faqRepository.save(faq);
    }

    private void validateFaq(FaqDTO dto) {
        if (dto.getQuestion() == null || dto.getQuestion().trim().isEmpty()) {
            throw new IllegalArgumentException("Question is required");
        }
        if (dto.getAnswer() == null || dto.getAnswer().trim().isEmpty()) {
            throw new IllegalArgumentException("Answer is required");
        }
    }

    private FaqDTO toDTO(Faq faq) {
        return new FaqDTO(
            faq.getId(),
            faq.getQuestion(),
            faq.getAnswer(),
            faq.getDisplayOrder(),
            faq.isActive()
        );
    }
}
