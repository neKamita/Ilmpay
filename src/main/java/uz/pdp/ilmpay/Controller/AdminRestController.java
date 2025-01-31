package uz.pdp.ilmpay.Controller;

import org.springframework.context.support.DefaultMessageSourceResolvable;
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
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

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
        return ResponseEntity.ok(EntityResponse.success("Support logos retrieved successfully", supportLogoService.findAllActive()));
    }

    @GetMapping("/support-logos/{id}")
    public ResponseEntity<EntityResponse<SupportLogoDTO>> getSupportLogo(@PathVariable Long id) {
        try {
            SupportLogoDTO logo = supportLogoService.findById(id);
            return ResponseEntity.ok(EntityResponse.success("Support logo retrieved successfully", logo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(EntityResponse.error(e.getMessage(), "LOGO_NOT_FOUND"));
        }
    }

    @PostMapping("/support-logos")
    public ResponseEntity<EntityResponse<SupportLogoDTO>> createSupportLogo(@Valid @RequestBody SupportLogoDTO dto, BindingResult bindingResult) {
        // Validate input
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(EntityResponse.validationError(errors));
        }

        try {
            return ResponseEntity.ok(EntityResponse.success("Support logo created successfully", supportLogoService.create(dto)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(EntityResponse.error(e.getMessage(), "LOGO_CREATE_ERROR"));
        }
    }

    @PutMapping("/support-logos/{id}")
    public ResponseEntity<EntityResponse<SupportLogoDTO>> updateSupportLogo(@PathVariable Long id, @Valid @RequestBody SupportLogoDTO dto, BindingResult bindingResult) {
        // Validate input
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(EntityResponse.validationError(errors));
        }

        try {
            return ResponseEntity.ok(EntityResponse.success("Support logo updated successfully", supportLogoService.update(id, dto)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(EntityResponse.error(e.getMessage(), "LOGO_UPDATE_ERROR"));
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

    // Similar endpoints for other entities...
}