package uz.pdp.ilmpay.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import uz.pdp.ilmpay.dto.SupportLogoDTO;
import uz.pdp.ilmpay.service.BenefitCardService;
import uz.pdp.ilmpay.service.FaqService;
import uz.pdp.ilmpay.service.SupportLogoService;
import uz.pdp.ilmpay.service.TestimonialService;
import uz.pdp.ilmpay.payload.EntityResponse;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminRestController {
    private final SupportLogoService supportLogoService;
    private final BenefitCardService benefitCardService;
    private final TestimonialService testimonialService;
    private final FaqService faqService;

    // CRUD endpoints for each entity with proper response handling
    @GetMapping("/support-logos")
    public ResponseEntity<EntityResponse<List<SupportLogoDTO>>> getAllSupportLogos() {
        return ResponseEntity.ok(EntityResponse.success(supportLogoService.findAllActive()));
    }

    @GetMapping("/support-logos/{id}")
    public ResponseEntity<EntityResponse<SupportLogoDTO>> getSupportLogo(@PathVariable Long id) {
        try {
            SupportLogoDTO logo = supportLogoService.findById(id);
            return ResponseEntity.ok(EntityResponse.success(logo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(EntityResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/support-logos")
    public ResponseEntity<EntityResponse<SupportLogoDTO>> createSupportLogo(@RequestBody SupportLogoDTO dto) {
        try {
            return ResponseEntity.ok(EntityResponse.success(supportLogoService.create(dto)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(EntityResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/support-logos/{id}")
    public ResponseEntity<EntityResponse<SupportLogoDTO>> updateSupportLogo(@PathVariable Long id, @RequestBody SupportLogoDTO dto) {
        try {
            return ResponseEntity.ok(EntityResponse.success(supportLogoService.update(id, dto)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(EntityResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/support-logos/{id}")
    public ResponseEntity<EntityResponse<Void>> deleteSupportLogo(@PathVariable Long id) {
        try {
            supportLogoService.delete(id);
            return ResponseEntity.ok(EntityResponse.success(null));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(EntityResponse.error(e.getMessage()));
        }
    }

    // Similar endpoints for other entities...
} 