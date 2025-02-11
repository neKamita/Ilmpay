package uz.pdp.ilmpay.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.ilmpay.dto.FaqDTO;
import uz.pdp.ilmpay.payload.EntityResponse;
import uz.pdp.ilmpay.service.FaqService;

import java.util.List;

/**
 * 🤔 FAQ Controller
 * Handling all those burning questions with style! 
 */
@RestController
@RequestMapping("/api/admin/faqs")
@RequiredArgsConstructor
@Slf4j
public class FaqController {
    private final FaqService faqService;

    /**
     * 📚 Get all active FAQs
     * @return List of FAQs, ordered by display order
     */
    @GetMapping
    public ResponseEntity<EntityResponse<List<FaqDTO>>> getAllActive() {
        log.info("🔍 Fetching all active FAQs");
        return ResponseEntity.ok(
            EntityResponse.<List<FaqDTO>>builder()
                .success(true)
                .message("FAQs retrieved successfully")
                .data(faqService.findAllActive())
                .build()
        );
    }

    /**
     * 🔍 Get a single FAQ by ID
     * @param id FAQ ID
     * @return FAQ details
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityResponse<FaqDTO>> getById(@PathVariable Long id) {
        log.info("🔍 Fetching FAQ with id: {}", id);
        return ResponseEntity.ok(
            EntityResponse.<FaqDTO>builder()
                .success(true)
                .message("FAQ retrieved successfully")
                .data(faqService.findById(id))
                .build()
        );
    }

    /**
     * ➕ Create a new FAQ
     * @param dto FAQ data
     * @return Created FAQ
     */
    @PostMapping
    public ResponseEntity<EntityResponse<FaqDTO>> create(@Valid @RequestBody FaqDTO dto) {
        log.info("✨ Creating new FAQ: {}", dto.getQuestion());
        return ResponseEntity.ok(
            EntityResponse.<FaqDTO>builder()
                .success(true)
                .message("FAQ created successfully")
                .data(faqService.create(dto))
                .build()
        );
    }

    /**
     * 📝 Update an existing FAQ
     * @param id FAQ ID
     * @param dto Updated FAQ data
     * @return Updated FAQ
     */
    @PutMapping("/{id}")
    public ResponseEntity<EntityResponse<FaqDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody FaqDTO dto) {
        log.info("✏️ Updating FAQ {}: {}", id, dto.getQuestion());
        return ResponseEntity.ok(
            EntityResponse.<FaqDTO>builder()
                .success(true)
                .message("FAQ updated successfully")
                .data(faqService.update(id, dto))
                .build()
        );
    }

    /**
     * 🗑️ Delete a FAQ
     * @param id FAQ ID
     * @return Success response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<EntityResponse<Void>> delete(@PathVariable Long id) {
        log.info("🗑️ Deleting FAQ {}", id);
        faqService.delete(id);
        return ResponseEntity.ok(
            EntityResponse.<Void>builder()
                .success(true)
                .message("FAQ deleted successfully")
                .build()
        );
    }

    /**
     * 🔄 Reorder FAQs
     * @param dtos List of FAQs with updated display orders
     * @return Updated FAQs
     */
    @PostMapping("/reorder")
    public ResponseEntity<EntityResponse<List<FaqDTO>>> reorder(@RequestBody List<FaqDTO> dtos) {
        log.info("🔄 Reordering {} FAQs", dtos.size());
        return ResponseEntity.ok(
            EntityResponse.<List<FaqDTO>>builder()
                .success(true)
                .message("FAQs reordered successfully")
                .data(faqService.reorder(dtos))
                .build()
        );
    }
}
