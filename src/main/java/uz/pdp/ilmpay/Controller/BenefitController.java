package uz.pdp.ilmpay.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.ilmpay.dto.BenefitCardDTO;
import uz.pdp.ilmpay.dto.ReorderItemDTO;
import uz.pdp.ilmpay.service.BenefitService;
import uz.pdp.ilmpay.service.TranslationService;
import uz.pdp.ilmpay.payload.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.Valid;
import java.util.List;


/**
 * 🎯 BenefitController: The API endpoints for our benefits!
 * 
 * This controller handles all the CRUD operations for benefits
 * through our REST API. It's like a magical wand that makes
 * benefits appear and disappear! ✨
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/benefits")
@RequiredArgsConstructor
public class BenefitController {
    private final BenefitService benefitService;
    private final TranslationService translationService;

    /**
     * 📋 Gets all active benefits
     * Like taking inventory of our treasure chest!
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityResponse<List<BenefitCardDTO>>> getAllBenefits() {
        try {
            log.info("🔍 Fetching all active benefits");
            List<BenefitCardDTO> benefits = benefitService.findAllActive();
            log.info("✨ Found {} active benefits", benefits.size());
            
            return ResponseEntity.ok(
                EntityResponse.<List<BenefitCardDTO>>builder()
                    .success(true)
                    .message("Benefits retrieved successfully")
                    .funnyMessage("🎯 Look at all these shiny benefits!")
                    .data(benefits)
                    .build()
            );
        } catch (Exception e) {
            log.error("❌ Failed to fetch benefits", e);
            return ResponseEntity.badRequest().body(
                EntityResponse.<List<BenefitCardDTO>>builder()
                    .success(false)
                    .message("Failed to fetch benefits: " + e.getMessage())
                    .errorCode("BENEFITS_FETCH_ERROR")
                    .funnyMessage("🔍 Our benefits are playing hide and seek!")
                    .build()
            );
        }
    }

    /**
     * 🔍 Fetches a single benefit by its ID
     * Like finding that one special present in Santa's workshop!
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityResponse<BenefitCardDTO>> getBenefit(@PathVariable Long id) {
        try {
            log.info("🔍 Looking for benefit with id: {}", id);
            BenefitCardDTO benefit = benefitService.findById(id);
            log.info("✨ Found benefit: {}", benefit.getTitle());
            
            return ResponseEntity.ok(
                EntityResponse.<BenefitCardDTO>builder()
                    .success(true)
                    .message("Benefit found successfully")
                    .funnyMessage("🎯 Found it! This benefit was hiding in plain sight!")
                    .data(benefit)
                    .build()
            );
        } catch (Exception e) {
            log.error("❌ Failed to fetch benefit with id: {}", id, e);
            return ResponseEntity.badRequest().body(
                EntityResponse.<BenefitCardDTO>builder()
                    .success(false)
                    .message("Failed to fetch benefit: " + e.getMessage())
                    .errorCode("BENEFIT_FETCH_ERROR")
                    .funnyMessage("🔍 Oops! This benefit is playing hide and seek!")
                    .build()
            );
        }
    }

    /**
     * 🎁 Adds a new benefit to our collection
     * Every new benefit is like unwrapping a present!
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityResponse<BenefitCardDTO>> createBenefit(
            @Valid @RequestBody(required = false) BenefitCardDTO jsonDto,
            @Valid @ModelAttribute(binding = false) BenefitCardDTO formDto) {
        try {
            // Use JSON DTO if available, otherwise use form DTO
            BenefitCardDTO dto = jsonDto != null ? jsonDto : formDto;
            log.info("🎁 Creating new benefit: {}", dto);

            // Create the benefit
            BenefitCardDTO created = benefitService.create(dto);
            log.info("✨ Successfully created benefit with id: {}", created.getId());
            
            return ResponseEntity.ok(
                EntityResponse.<BenefitCardDTO>builder()
                    .success(true)
                    .message("Benefit created successfully")
                    .funnyMessage("🎁 A new perk joins the party! Let's celebrate!")
                    .data(created)
                    .build()
            );
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Invalid benefit data: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                EntityResponse.<BenefitCardDTO>builder()
                    .success(false)
                    .message(e.getMessage())
                    .errorCode("BENEFIT_VALIDATION_ERROR")
                    .funnyMessage("🎪 Oops! Let's double-check those details!")
                    .build()
            );
        } catch (Exception e) {
            log.error("❌ Failed to create benefit", e);
            return ResponseEntity.badRequest().body(
                EntityResponse.<BenefitCardDTO>builder()
                    .success(false)
                    .message("Failed to create benefit: " + e.getMessage())
                    .errorCode("BENEFIT_CREATE_ERROR")
                    .funnyMessage("🎪 Oops! This benefit is being a bit shy. Let's try again!")
                    .build()
            );
        }
    }

    /**
     * ✨ Updates an existing benefit
     * Because even the best benefits need a makeover sometimes!
     */
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityResponse<BenefitCardDTO>> updateBenefit(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) BenefitCardDTO jsonDto,
            @Valid @ModelAttribute(binding = false) BenefitCardDTO formDto) {
        try {
            // Use JSON DTO if available, otherwise use form DTO
            BenefitCardDTO dto = jsonDto != null ? jsonDto : formDto;
            log.info("🔄 Updating benefit {} with data: {}", id, dto);

            // Update the benefit
            BenefitCardDTO updated = benefitService.update(id, dto);
            log.info("✨ Successfully updated benefit {}", id);
            
            return ResponseEntity.ok(
                EntityResponse.<BenefitCardDTO>builder()
                    .success(true)
                    .message("Benefit updated successfully")
                    .funnyMessage("✨ This benefit just got a shiny new makeover!")
                    .data(updated)
                    .build()
            );
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Invalid benefit data for update: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                EntityResponse.<BenefitCardDTO>builder()
                    .success(false)
                    .message(e.getMessage())
                    .errorCode("BENEFIT_VALIDATION_ERROR")
                    .funnyMessage("🎪 Hmm, something's not quite right with those details!")
                    .build()
            );
        } catch (Exception e) {
            log.error("❌ Failed to update benefit {}", id, e);
            return ResponseEntity.badRequest().body(
                EntityResponse.<BenefitCardDTO>builder()
                    .success(false)
                    .message("Failed to update benefit: " + e.getMessage())
                    .errorCode("BENEFIT_UPDATE_ERROR")
                    .funnyMessage("🎪 Oops! This benefit is being stubborn. Let's try again!")
                    .build()
            );
        }
    }

    /**
     * 🗑️ Removes a benefit from our collection
     * Sometimes we need to make room for new awesome features!
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityResponse<Void>> deleteBenefit(@PathVariable Long id) {
        try {
            log.info("🗑️ Deleting benefit {}", id);
            benefitService.delete(id);
            log.info("✨ Successfully deleted benefit {}", id);
            
            return ResponseEntity.ok(
                EntityResponse.<Void>builder()
                    .success(true)
                    .message("Benefit deleted successfully")
                    .funnyMessage("👋 Farewell, dear benefit! You served us well!")
                    .build()
            );
        } catch (Exception e) {
            log.error("❌ Failed to delete benefit {}", id, e);
            return ResponseEntity.badRequest().body(
                EntityResponse.<Void>builder()
                    .success(false)
                    .message("Failed to delete benefit: " + e.getMessage())
                    .errorCode("BENEFIT_DELETE_ERROR")
                    .funnyMessage("🎪 This benefit is putting up a fight! Let's try again!")
                    .build()
            );
        }
    }

    /**
     * 🔄 Reorders benefits
     * Like rearranging furniture, but more fun! 🎨
     */
    @PostMapping(value = "/reorder", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityResponse<List<BenefitCardDTO>>> reorderBenefits(@RequestBody List<ReorderItemDTO> reorderItems) {
        try {
            log.info("🔄 Reordering {} benefits", reorderItems.size());
            List<BenefitCardDTO> benefits = benefitService.reorder(reorderItems);
            log.info("✨ Successfully reordered benefits");
            
            return ResponseEntity.ok(
                EntityResponse.<List<BenefitCardDTO>>builder()
                    .success(true)
                    .message("Benefits reordered successfully")
                    .funnyMessage("🎯 Benefits are now lined up like ducks in a row!")
                    .data(benefits)
                    .build()
            );
        } catch (Exception e) {
            log.error("❌ Failed to reorder benefits", e);
            return ResponseEntity.badRequest().body(
                EntityResponse.<List<BenefitCardDTO>>builder()
                    .success(false)
                    .message("Failed to reorder benefits: " + e.getMessage())
                    .errorCode("BENEFITS_REORDER_ERROR")
                    .funnyMessage("🔄 Our benefits are feeling a bit dizzy!")
                    .build()
            );
        }
    }
}
