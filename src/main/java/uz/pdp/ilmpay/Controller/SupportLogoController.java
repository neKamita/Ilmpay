package uz.pdp.ilmpay.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import uz.pdp.ilmpay.dto.SupportLogoDTO;
import uz.pdp.ilmpay.payload.EntityResponse;
import uz.pdp.ilmpay.service.SupportLogoService;

@Controller
@Slf4j
@RequestMapping("/admin/content/support-logos")
public class SupportLogoController {
    private final SupportLogoService supportLogoService;

    public SupportLogoController(SupportLogoService supportLogoService) {
        this.supportLogoService = supportLogoService;
    }

    @GetMapping
    public String getSupportLogosPage(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("supportLogos", supportLogoService.findAllActive());
        return "admin/content/support-logos";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse<SupportLogoDTO>> getSupportLogo(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(EntityResponse.success(supportLogoService.findById(id)));
        } catch (Exception e) {
            log.error("Error fetching support logo: {}", e.getMessage());
            return ResponseEntity.badRequest().body(EntityResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<EntityResponse<SupportLogoDTO>> create(@Valid @RequestBody SupportLogoDTO dto) {
        try {
            return ResponseEntity.ok(EntityResponse.success(supportLogoService.create(dto)));
        } catch (Exception e) {
            log.error("Error creating support logo: {}", e.getMessage());
            return ResponseEntity.badRequest().body(EntityResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse<SupportLogoDTO>> update(@PathVariable Long id, @Valid @RequestBody SupportLogoDTO dto) {
        try {
            return ResponseEntity.ok(EntityResponse.success(supportLogoService.update(id, dto)));
        } catch (Exception e) {
            log.error("Error updating support logo: {}", e.getMessage());
            return ResponseEntity.badRequest().body(EntityResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse<Void>> delete(@PathVariable Long id) {
        try {
            supportLogoService.delete(id);
            return ResponseEntity.ok(EntityResponse.success(null));
        } catch (Exception e) {
            log.error("Error deleting support logo: {}", e.getMessage());
            return ResponseEntity.badRequest().body(EntityResponse.error(e.getMessage()));
        }
    }
}