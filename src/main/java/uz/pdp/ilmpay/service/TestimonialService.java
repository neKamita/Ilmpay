    package uz.pdp.ilmpay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.ilmpay.dto.TestimonialDTO;
import uz.pdp.ilmpay.dto.ReorderItemDTO;
import uz.pdp.ilmpay.exception.ResourceNotFoundException;
import uz.pdp.ilmpay.model.Testimonial;
import uz.pdp.ilmpay.repository.TestimonialRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 🌟 Testimonial Service
 * Handles business logic for student testimonials
 * 
 * @author neKamita
 * @version 1.0
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TestimonialService {
    private final TestimonialRepository testimonialRepository;
    private final S3Service s3Service;
    private final TranslationService translationService;

    // S3 folder for testimonial avatars
    private static final String S3_FOLDER = "testimonials";

    public List<TestimonialDTO> findAllActive() {
        log.info("🔍 Fetching all active testimonials");
        List<TestimonialDTO> testimonials = testimonialRepository.findByIsActiveTrueOrderByOrderAsc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        log.info("✨ Found {} active testimonials", testimonials.size());
        return testimonials;
    }

    public TestimonialDTO create(TestimonialDTO dto) {
        log.info("🎭 Creating new testimonial from: {}", dto.getName());
        validateTestimonial(dto);

        Testimonial testimonial = new Testimonial();
        testimonial.setName(dto.getName());
        testimonial.setComment(dto.getComment());
        testimonial.setRating(dto.getRating());
        testimonial.setActive(true);
        testimonial.setCreatedAt(LocalDateTime.now());
        testimonial.setOrder(dto.getOrder()); // Add order support

        // Handle avatar upload if present
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            log.debug("📤 Uploading avatar for testimonial: {}, size: {} bytes",
                    dto.getName(), dto.getImageFile().getSize());

            String fileUrl = s3Service.uploadFile(dto.getImageFile(), S3_FOLDER);
            testimonial.setAvatarUrl(fileUrl);
            log.info("✅ Avatar uploaded successfully to: {}", fileUrl);
        }

        log.debug("📝 Testimonial details - Name: {}, Rating: {}, Comment length: {}",
                dto.getName(), dto.getRating(), dto.getComment().length());

        Testimonial saved = testimonialRepository.save(testimonial);
        log.info("✅ Successfully created testimonial with id: {}", saved.getId());
        return toDTO(saved);
    }

    public TestimonialDTO update(Long id, TestimonialDTO dto) {
        log.info("🔄 Updating testimonial id: {}", id);
        validateTestimonial(dto);

        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ Testimonial not found with id: {}", id);
                    return new ResourceNotFoundException("Testimonial not found with id: " + id);
                });

        testimonial.setName(dto.getName());
        testimonial.setComment(dto.getComment());
        testimonial.setRating(dto.getRating());
        if (dto.getOrder() != null) {
            testimonial.setOrder(dto.getOrder()); // Add order support
        }

        // Handle avatar update if new file is provided
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            log.debug("📤 Uploading new avatar for testimonial: {}, size: {} bytes",
                    dto.getName(), dto.getImageFile().getSize());

            // Delete old avatar if exists
            if (testimonial.getAvatarUrl() != null) {
                log.debug("🗑️ Deleting old avatar: {}", testimonial.getAvatarUrl());
                s3Service.deleteFile(testimonial.getAvatarUrl());
            }

            String fileUrl = s3Service.uploadFile(dto.getImageFile(), S3_FOLDER);
            testimonial.setAvatarUrl(fileUrl);
            log.info("✅ New avatar uploaded successfully to: {}", fileUrl);
        }

        log.debug("📝 Updated testimonial details - Name: {}, Rating: {}, Comment length: {}",
                dto.getName(), dto.getRating(), dto.getComment().length());

        Testimonial saved = testimonialRepository.save(testimonial);
        log.info("✅ Successfully updated testimonial id: {}", saved.getId());
        return toDTO(saved);
    }

    public TestimonialDTO findById(Long id) {
        log.info("🔍 Finding testimonial by id: {}", id);
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ Testimonial not found with id: {}", id);
                    return new ResourceNotFoundException("Testimonial not found with id: " + id);
                });
        log.info("✅ Found testimonial: {}", testimonial.getName());
        return toDTO(testimonial);
    }

    public void delete(Long id) {
        log.info("🗑️ Soft deleting testimonial id: {}", id);
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ Testimonial not found with id: {}", id);
                    return new ResourceNotFoundException("Testimonial not found with id: " + id);
                });

        testimonial.setActive(false);
        testimonialRepository.save(testimonial);
        log.info("✅ Successfully soft deleted testimonial id: {}", id);
    }

    /**
     * 🔄 Reorders testimonials based on the provided order list
     * Making our testimonials shine in perfect sequence! ⭐
     *
     * @param reorderItems List of items with their new display order
     * @return List of reordered testimonials
     */
    @Transactional
    public List<TestimonialDTO> reorder(List<ReorderItemDTO> reorderItems) {
        log.info("🔄 Starting reorder of {} testimonials", reorderItems.size());

        try {
            // Create a map of id to order for quick lookup
            Map<Long, Integer> orderMap = reorderItems.stream()
                    .collect(Collectors.toMap(
                            ReorderItemDTO::getId,
                            ReorderItemDTO::getDisplayOrder
                    ));

            // Get all active testimonials
            List<Testimonial> testimonials = testimonialRepository.findByIsActiveTrueOrderByOrderAsc();

            // Update orders
            testimonials.forEach(testimonial -> {
                Integer newOrder = orderMap.get(testimonial.getId());
                if (newOrder != null) {
                    testimonial.setOrder(newOrder);
                    log.debug("📋 Updated order for testimonial {}: {} -> {}", 
                            testimonial.getName(), testimonial.getOrder(), newOrder);
                }
            });

            // Save all updated testimonials
            List<Testimonial> savedTestimonials = testimonialRepository.saveAll(testimonials);
            log.info("✨ Successfully reordered {} testimonials", savedTestimonials.size());

            // Convert to DTOs and return
            return savedTestimonials.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("❌ Failed to reorder testimonials: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to reorder testimonials: " + e.getMessage(), e);
        }
    }

   private TestimonialDTO toDTO(Testimonial testimonial) {
        String currentLanguage = LocaleContextHolder.getLocale().getLanguage();
        TestimonialDTO dto = new TestimonialDTO();
        dto.setId(testimonial.getId());
        dto.setName(testimonial.getName());
        dto.setComment(testimonial.getComment());
        if(!currentLanguage.equals("en")){
            dto.setName(translationService.translate(testimonial.getName(),"en",currentLanguage));
            dto.setComment(translationService.translate(testimonial.getComment(),"en",currentLanguage));
        }
        dto.setRating(testimonial.getRating());
        dto.setAvatarUrl(testimonial.getAvatarUrl());
        dto.setActive(testimonial.isActive());
        dto.setOrder(testimonial.getOrder());
        return dto;
    }

    private void validateTestimonial(TestimonialDTO dto) {
        log.debug("🔍 Validating testimonial data");
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            log.error("❌ Validation failed: Name is required");
            throw new IllegalArgumentException("Name is required");
        }
        if (dto.getComment() == null || dto.getComment().trim().isEmpty()) {
            log.error("❌ Validation failed: Comment is required");
            throw new IllegalArgumentException("Comment is required");
        }
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            log.error("❌ Validation failed: Rating must be between 1 and 5, got: {}", dto.getRating());
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        log.debug("✅ Testimonial validation passed");
    }
}
