package uz.pdp.ilmpay.Controller;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uz.pdp.ilmpay.dto.SupportLogoDTO;
import uz.pdp.ilmpay.dto.BenefitCardDTO;
import uz.pdp.ilmpay.exception.ResourceNotFoundException;
import uz.pdp.ilmpay.service.BenefitCardService;
import uz.pdp.ilmpay.service.FaqService;
import uz.pdp.ilmpay.service.SupportLogoService;
import uz.pdp.ilmpay.service.TestimonialService;
import uz.pdp.ilmpay.payload.EntityResponse;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class AdminRestController {
    private final SupportLogoService supportLogoService;
    private final BenefitCardService benefitCardService;
    private final TestimonialService testimonialService;
    private final FaqService faqService;

    // CRUD endpoints for each entity with proper response handling
    @GetMapping("/support-logos")
    public ResponseEntity<EntityResponse<List<SupportLogoDTO>>> getAllSupportLogos() {
        return ResponseEntity.ok(EntityResponse.customResponse("Support logos retrieved successfully", "They are looking fancy indeed!", supportLogoService.findAllActive()));
    }

    @GetMapping("/support-logos/{id}")
    public ResponseEntity<EntityResponse<SupportLogoDTO>> getSupportLogo(@PathVariable Long id) {
        try {
            SupportLogoDTO logo = supportLogoService.findById(id);
            return ResponseEntity.ok(EntityResponse.customResponse("Support logo retrieved successfully", "One logo to rule them all!", logo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(EntityResponse.error(e.getMessage(), "LOGO_NOT_FOUND"));
        }
    }

    @PostMapping("/support-logos")
    public ResponseEntity<EntityResponse<SupportLogoDTO>> createSupportLogo(
            @RequestParam(required = false) MultipartFile imageFile,
            @RequestParam(required = false) String imageUrl,
            @RequestParam String name,
            @RequestParam String websiteUrl,
            @RequestParam(required = false) Integer order) {
        
        try {
            // Create DTO from form data
            SupportLogoDTO dto = new SupportLogoDTO();
            dto.setName(name);
            dto.setWebsiteUrl(websiteUrl);
            dto.setImageFile(imageFile);
            dto.setImageUrl(imageUrl); // Add imageUrl to DTO
            dto.setOrder(order);
            dto.setActive(true);

            // Create the support logo
            SupportLogoDTO created = supportLogoService.create(dto);
            log.info("✨ Successfully created support logo: {}", created.getName());
            return ResponseEntity.ok(EntityResponse.success(created));
        } catch (Exception e) {
            log.error("❌ Failed to create support logo", e);
            return ResponseEntity.badRequest()
                    .body(EntityResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/support-logos/{id}")
    public ResponseEntity<EntityResponse<SupportLogoDTO>> updateSupportLogo(
            @PathVariable Long id,
            @RequestParam(required = false) MultipartFile imageFile,
            @RequestParam String name,
            @RequestParam String websiteUrl,
            @RequestParam(required = false) Integer order) {
        
        log.info("🔄 Received update request for support logo id: {}", id);
        log.debug("📦 Update payload - Name: {}, WebsiteUrl: {}, HasFile: {}", 
            name, websiteUrl, imageFile != null);

        try {
            // Create DTO from form data
            SupportLogoDTO dto = new SupportLogoDTO();
            dto.setId(id);
            dto.setName(name);
            dto.setWebsiteUrl(websiteUrl);
            dto.setImageFile(imageFile);
            dto.setOrder(order);
            dto.setActive(true);

            // Update the support logo
            SupportLogoDTO updated = supportLogoService.update(id, dto);
            log.info("✨ Successfully updated support logo: {}", updated.getName());
            return ResponseEntity.ok(
                EntityResponse.success("Support logo updated successfully", updated)
            );
        } catch (ResourceNotFoundException e) {
            log.error("❌ Support logo not found: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(EntityResponse.error(e.getMessage(), "LOGO_NOT_FOUND"));
        } catch (Exception e) {
            log.error("❌ Failed to update support logo", e);
            return ResponseEntity.badRequest()
                    .body(EntityResponse.error(
                        "Failed to update support logo: " + e.getMessage(),
                        "UPDATE_ERROR"
                    ));
        }
    }

    @DeleteMapping("/support-logos/{id}")
    public ResponseEntity<EntityResponse<Void>> deleteSupportLogo(@PathVariable Long id) {
        try {
            supportLogoService.delete(id);
            return ResponseEntity.ok(EntityResponse.success("Support logo deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(EntityResponse.error(e.getMessage(), "LOGO_DELETE_ERROR"));
        }
    }

    // Benefits CRUD endpoints
    @GetMapping("/benefits")
    public ResponseEntity<EntityResponse<List<BenefitCardDTO>>> getAllBenefits() {
        return ResponseEntity.ok(EntityResponse.customResponse("Benefits retrieved successfully", "Your benefits are loaded with awesomeness!", benefitCardService.findAllActive()));
    }

    @GetMapping("/benefits/{id}")
    public ResponseEntity<EntityResponse<BenefitCardDTO>> getBenefit(@PathVariable Long id) {
        try {
            BenefitCardDTO benefit = benefitCardService.findById(id);
            return ResponseEntity.ok(EntityResponse.customResponse("Benefit retrieved successfully", "Benefit power activated!", benefit));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(EntityResponse.error(e.getMessage(), "BENEFIT_NOT_FOUND"));
        }
    }

    @PostMapping("/benefits")
    public ResponseEntity<EntityResponse<BenefitCardDTO>> createBenefit(@Valid @RequestBody BenefitCardDTO dto) {
        BenefitCardDTO created = benefitCardService.create(dto);
        return ResponseEntity.ok(EntityResponse.customResponse("Benefit created successfully", "A new benefit emerges!", created));
    }

    @PutMapping("/benefits/{id}")
    public ResponseEntity<EntityResponse<BenefitCardDTO>> updateBenefit(@PathVariable Long id, @Valid @RequestBody BenefitCardDTO dto) {
        BenefitCardDTO updated = benefitCardService.update(id, dto);
        return ResponseEntity.ok(EntityResponse.customResponse("Benefit updated successfully", "Benefit updated with a twist!", updated));
    }

    @DeleteMapping("/benefits/{id}")
    public ResponseEntity<EntityResponse<Void>> deleteBenefit(@PathVariable Long id) {
        benefitCardService.delete(id);
        return ResponseEntity.ok(EntityResponse.customResponse("Benefit deleted successfully", "The benefit has been banished!", null));
    }
}