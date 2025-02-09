package uz.pdp.ilmpay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.ilmpay.dto.TestimonialDTO;
import uz.pdp.ilmpay.exception.ResourceNotFoundException;
import uz.pdp.ilmpay.model.Testimonial;
import uz.pdp.ilmpay.repository.TestimonialRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    // S3 folder for testimonial avatars
    private static final String S3_FOLDER = "testimonials";

    public List<TestimonialDTO> findAllActive() {
        log.info("🔍 Fetching all active testimonials");
        List<TestimonialDTO> testimonials = testimonialRepository.findByIsActiveTrueOrderByCreatedAtDesc()
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

    private TestimonialDTO toDTO(Testimonial testimonial) {
        TestimonialDTO dto = new TestimonialDTO();
        dto.setId(testimonial.getId());
        dto.setName(testimonial.getName());
        dto.setComment(testimonial.getComment());
        dto.setRating(testimonial.getRating());
        dto.setActive(testimonial.isActive()    );
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
