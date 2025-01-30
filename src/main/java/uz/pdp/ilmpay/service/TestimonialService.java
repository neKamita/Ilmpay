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

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TestimonialService {
    private final TestimonialRepository testimonialRepository;

    public List<TestimonialDTO> findAllActive() {
        return testimonialRepository.findByActiveTrueOrderByCreatedAtDesc()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public TestimonialDTO create(TestimonialDTO dto) {
        validateTestimonial(dto);
        
        Testimonial testimonial = new Testimonial();
        testimonial.setName(dto.getName());
        testimonial.setRole(dto.getRole());
        testimonial.setComment(dto.getComment());
        testimonial.setAvatarUrl(dto.getAvatarUrl());
        testimonial.setRating(dto.getRating());
        testimonial.setActive(true);
        testimonial.setCreatedAt(LocalDateTime.now());

        log.info("Creating new testimonial from: {}", dto.getName());
        return toDTO(testimonialRepository.save(testimonial));
    }

    public TestimonialDTO update(Long id, TestimonialDTO dto) {
        validateTestimonial(dto);
        
        Testimonial testimonial = testimonialRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Testimonial not found with id: " + id));

        testimonial.setName(dto.getName());
        testimonial.setRole(dto.getRole());
        testimonial.setComment(dto.getComment());
        testimonial.setAvatarUrl(dto.getAvatarUrl());
        testimonial.setRating(dto.getRating());
        testimonial.setActive(dto.isActive());

        log.info("Updating testimonial with id: {}", id);
        return toDTO(testimonialRepository.save(testimonial));
    }

    public void delete(Long id) {
        Testimonial testimonial = testimonialRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Testimonial not found with id: " + id));
            
        testimonial.setActive(false);
        log.info("Soft deleting testimonial with id: {}", id);
        testimonialRepository.save(testimonial);
    }

    private void validateTestimonial(TestimonialDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (dto.getComment() == null || dto.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment is required");
        }
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }

    private TestimonialDTO toDTO(Testimonial testimonial) {
        return new TestimonialDTO(
            testimonial.getId(),
            testimonial.getName(),
            testimonial.getRole(),
            testimonial.getComment(),
            testimonial.getAvatarUrl(),
            testimonial.getRating(),
            testimonial.isActive()
        );
    }
}
