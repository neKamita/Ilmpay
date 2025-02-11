package uz.pdp.ilmpay.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.ilmpay.dto.TestimonialDTO;
import uz.pdp.ilmpay.dto.ReorderItemDTO;
import uz.pdp.ilmpay.service.TestimonialService;
import uz.pdp.ilmpay.payload.EntityResponse;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 🌟 TestimonialController: Managing our shining testimonials!
 * 
 * This controller handles all the CRUD operations for testimonials
 * through our REST API. It's like a guestbook, but way cooler! ✨
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/testimonials")
@RequiredArgsConstructor
public class TestimonialController {
    private final TestimonialService testimonialService;

    /**
     * 📋 Gets all active testimonials
     * Like reading through our wall of fame! ⭐
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityResponse<List<TestimonialDTO>>> getAllTestimonials() {
        try {
            log.info("🔍 Fetching all active testimonials");
            List<TestimonialDTO> testimonials = testimonialService.findAllActive();
            log.info("✨ Found {} active testimonials", testimonials.size());
            
            return ResponseEntity.ok(
                EntityResponse.<List<TestimonialDTO>>builder()
                    .success(true)
                    .message("Testimonials retrieved successfully")
                    .funnyMessage("⭐ Look at all these happy customers!")
                    .data(testimonials)
                    .build()
            );
        } catch (Exception e) {
            log.error("❌ Failed to fetch testimonials", e);
            return ResponseEntity.badRequest().body(
                EntityResponse.<List<TestimonialDTO>>builder()
                    .success(false)
                    .message("Failed to fetch testimonials: " + e.getMessage())
                    .errorCode("TESTIMONIALS_FETCH_ERROR")
                    .funnyMessage("😅 Our testimonials are being a bit shy!")
                    .build()
            );
        }
    }

    /**
     * 🔍 Fetches a single testimonial by its ID
     * Like finding that one special review in our guestbook! 📖
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityResponse<TestimonialDTO>> getTestimonial(@PathVariable Long id) {
        try {
            log.info("🔍 Looking for testimonial with id: {}", id);
            TestimonialDTO testimonial = testimonialService.findById(id);
            log.info("✨ Found testimonial from: {}", testimonial.getName());
            
            return ResponseEntity.ok(
                EntityResponse.<TestimonialDTO>builder()
                    .success(true)
                    .message("Testimonial found successfully")
                    .funnyMessage("📖 Found this gem in our collection!")
                    .data(testimonial)
                    .build()
            );
        } catch (Exception e) {
            log.error("❌ Failed to fetch testimonial with id: {}", id, e);
            return ResponseEntity.badRequest().body(
                EntityResponse.<TestimonialDTO>builder()
                    .success(false)
                    .message("Failed to fetch testimonial: " + e.getMessage())
                    .errorCode("TESTIMONIAL_FETCH_ERROR")
                    .funnyMessage("🔍 This testimonial is playing hide and seek!")
                    .build()
            );
        }
    }

    /**
     * ⭐ Adds a new testimonial to our collection
     * Another happy customer sharing their story! 🎉
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityResponse<TestimonialDTO>> createTestimonial(@Valid @RequestBody TestimonialDTO dto) {
        try {
            log.info("✨ Creating new testimonial from: {}", dto.getName());
            TestimonialDTO created = testimonialService.create(dto);
            log.info("🎉 Successfully created testimonial");
            
            return ResponseEntity.ok(
                EntityResponse.<TestimonialDTO>builder()
                    .success(true)
                    .message("Testimonial created successfully")
                    .funnyMessage("🌟 Another star in our constellation!")
                    .data(created)
                    .build()
            );
        } catch (Exception e) {
            log.error("❌ Failed to create testimonial", e);
            return ResponseEntity.badRequest().body(
                EntityResponse.<TestimonialDTO>builder()
                    .success(false)
                    .message("Failed to create testimonial: " + e.getMessage())
                    .errorCode("TESTIMONIAL_CREATE_ERROR")
                    .funnyMessage("😅 This testimonial is camera shy!")
                    .build()
            );
        }
    }

    /**
     * 📝 Updates an existing testimonial
     * Sometimes even stars need a makeover! ✨
     */
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityResponse<TestimonialDTO>> updateTestimonial(
            @PathVariable Long id,
            @Valid @RequestBody TestimonialDTO dto) {
        try {
            log.info("✏️ Updating testimonial with id: {}", id);
            TestimonialDTO updated = testimonialService.update(id, dto);
            log.info("✨ Successfully updated testimonial");
            
            return ResponseEntity.ok(
                EntityResponse.<TestimonialDTO>builder()
                    .success(true)
                    .message("Testimonial updated successfully")
                    .funnyMessage("✨ This star just got brighter!")
                    .data(updated)
                    .build()
            );
        } catch (Exception e) {
            log.error("❌ Failed to update testimonial with id: {}", id, e);
            return ResponseEntity.badRequest().body(
                EntityResponse.<TestimonialDTO>builder()
                    .success(false)
                    .message("Failed to update testimonial: " + e.getMessage())
                    .errorCode("TESTIMONIAL_UPDATE_ERROR")
                    .funnyMessage("🎨 The makeover didn't quite work out!")
                    .build()
            );
        }
    }

    /**
     * 🗑️ Removes a testimonial
     * Sometimes stars need to make way for new ones! 🌠
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityResponse<Void>> deleteTestimonial(@PathVariable Long id) {
        try {
            log.info("🗑️ Deleting testimonial with id: {}", id);
            testimonialService.delete(id);
            log.info("✨ Successfully deleted testimonial");
            
            return ResponseEntity.ok(
                EntityResponse.<Void>builder()
                    .success(true)
                    .message("Testimonial deleted successfully")
                    .funnyMessage("🌠 A shooting star just passed by!")
                    .build()
            );
        } catch (Exception e) {
            log.error("❌ Failed to delete testimonial with id: {}", id, e);
            return ResponseEntity.badRequest().body(
                EntityResponse.<Void>builder()
                    .success(false)
                    .message("Failed to delete testimonial: " + e.getMessage())
                    .errorCode("TESTIMONIAL_DELETE_ERROR")
                    .funnyMessage("🌟 This star isn't ready to fade yet!")
                    .build()
            );
        }
    }

    /**
     * 🔄 Reorders testimonials
     * Like rearranging stars in the sky! ⭐
     */
    @PostMapping(value = "/reorder", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityResponse<List<TestimonialDTO>>> reorderTestimonials(@RequestBody List<ReorderItemDTO> reorderItems) {
        try {
            log.info("🔄 Reordering {} testimonials", reorderItems.size());
            List<TestimonialDTO> testimonials = testimonialService.reorder(reorderItems);
            log.info("✨ Successfully reordered testimonials");
            
            return ResponseEntity.ok(
                EntityResponse.<List<TestimonialDTO>>builder()
                    .success(true)
                    .message("Testimonials reordered successfully")
                    .funnyMessage("⭐ Our constellation just got a makeover!")
                    .data(testimonials)
                    .build()
            );
        } catch (Exception e) {
            log.error("❌ Failed to reorder testimonials", e);
            return ResponseEntity.badRequest().body(
                EntityResponse.<List<TestimonialDTO>>builder()
                    .success(false)
                    .message("Failed to reorder testimonials: " + e.getMessage())
                    .errorCode("TESTIMONIALS_REORDER_ERROR")
                    .funnyMessage("🌠 Our stars are feeling a bit dizzy!")
                    .build()
            );
        }
    }
}
