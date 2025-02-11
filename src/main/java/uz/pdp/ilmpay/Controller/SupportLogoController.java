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
import uz.pdp.ilmpay.dto.ReorderItemDTO;
import uz.pdp.ilmpay.payload.EntityResponse;
import uz.pdp.ilmpay.service.SupportLogoService;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;

/**
 * 🖼️ SupportLogoController: Managing our partner logos with style!
 * 
 * This controller handles all operations for support logos in our admin panel.
 * From adding new logos to reordering them - we've got it all covered! ✨
 */
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

    /**
     * 🔍 Gets a specific support logo by ID
     * Finding that one special partner in our collection! 🎯
     */
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse<SupportLogoDTO>> getSupportLogo(@PathVariable Long id) {
        try {
            log.info("🔍 Fetching support logo with id: {}", id);
            SupportLogoDTO logo = supportLogoService.findById(id);
            log.info("✨ Found support logo: {}", logo.getName());
            
            return ResponseEntity.ok(
                EntityResponse.<SupportLogoDTO>builder()
                    .success(true)
                    .message("Support logo retrieved successfully")
                    .funnyMessage("🎯 Found this gem in our collection!")
                    .data(logo)
                    .build()
            );
        } catch (Exception e) {
            log.error("❌ Error fetching support logo: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                EntityResponse.<SupportLogoDTO>builder()
                    .success(false)
                    .message("Failed to fetch support logo: " + e.getMessage())
                    .errorCode("LOGO_FETCH_ERROR")
                    .funnyMessage("🔍 This logo is playing hide and seek!")
                    .build()
            );
        }
    }

    /**
     * 🎨 Creates a new support logo
     * Adding another awesome partner to our wall of fame! ✨
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<EntityResponse<SupportLogoDTO>> create(@Valid @RequestBody SupportLogoDTO dto) {
        try {
            log.info("🎨 Creating new support logo: {}", dto.getName());
            SupportLogoDTO created = supportLogoService.create(dto);
            log.info("✨ Successfully created support logo");
            
            return ResponseEntity.ok(
                EntityResponse.<SupportLogoDTO>builder()
                    .success(true)
                    .message("Support logo created successfully")
                    .funnyMessage("🎨 A new masterpiece joins our gallery!")
                    .data(created)
                    .build()
            );
        } catch (Exception e) {
            log.error("❌ Error creating support logo: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                EntityResponse.<SupportLogoDTO>builder()
                    .success(false)
                    .message("Failed to create support logo: " + e.getMessage())
                    .errorCode("LOGO_CREATE_ERROR")
                    .funnyMessage("🎨 Our artist needs more practice!")
                    .build()
            );
        }
    }

    /**
     * 🖌️ Updates an existing support logo
     * Even masterpieces need a touch-up sometimes! ✨
     */
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse<SupportLogoDTO>> update(@PathVariable Long id, @Valid @RequestBody SupportLogoDTO dto) {
        try {
            log.info("🖌️ Updating support logo with id: {}", id);
            SupportLogoDTO updated = supportLogoService.update(id, dto);
            log.info("✨ Successfully updated support logo");
            
            return ResponseEntity.ok(
                EntityResponse.<SupportLogoDTO>builder()
                    .success(true)
                    .message("Support logo updated successfully")
                    .funnyMessage("✨ This masterpiece just got better!")
                    .data(updated)
                    .build()
            );
        } catch (Exception e) {
            log.error("❌ Error updating support logo: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                EntityResponse.<SupportLogoDTO>builder()
                    .success(false)
                    .message("Failed to update support logo: " + e.getMessage())
                    .errorCode("LOGO_UPDATE_ERROR")
                    .funnyMessage("🖌️ The touch-up didn't quite work out!")
                    .build()
            );
        }
    }

    /**
     * 🗑️ Deletes a support logo
     * Making space for new masterpieces! 🎨
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse<Void>> delete(@PathVariable Long id) {
        try {
            log.info("🗑️ Deleting support logo with id: {}", id);
            supportLogoService.delete(id);
            log.info("✨ Successfully deleted support logo");
            
            return ResponseEntity.ok(
                EntityResponse.<Void>builder()
                    .success(true)
                    .message("Support logo deleted successfully")
                    .funnyMessage("🎨 Making space in our gallery!")
                    .build()
            );
        } catch (Exception e) {
            log.error("❌ Error deleting support logo: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                EntityResponse.<Void>builder()
                    .success(false)
                    .message("Failed to delete support logo: " + e.getMessage())
                    .errorCode("LOGO_DELETE_ERROR")
                    .funnyMessage("🖼️ This masterpiece isn't ready to go!")
                    .build()
            );
        }
    }

    /**
     * 🔄 Reorders support logos
     * Rearranging our gallery for the perfect showcase! 🖼️
     */
    @PostMapping("/reorder")
    @ResponseBody
    public ResponseEntity<EntityResponse<List<SupportLogoDTO>>> reorderLogos(@RequestBody List<ReorderItemDTO> reorderItems) {
        try {
            log.info("🔄 Reordering {} support logos", reorderItems.size());
            
            // Validate input
            if (reorderItems == null || reorderItems.isEmpty()) {
                throw new IllegalArgumentException("No reorder items provided");
            }
            
            // Log the incoming reorder items for debugging
            reorderItems.forEach(item -> 
                log.debug("📋 Reorder request - ID: {}, Order: {}", item.getId(), item.getDisplayOrder())
            );
            
            List<SupportLogoDTO> logos = supportLogoService.reorder(reorderItems);
            log.info("✨ Successfully reordered support logos");
            
            return ResponseEntity.ok(
                EntityResponse.<List<SupportLogoDTO>>builder()
                    .success(true)
                    .message("Support logos reordered successfully")
                    .funnyMessage("🖼️ Our gallery just got a makeover!")
                    .data(logos)
                    .build()
            );
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Invalid reorder request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                EntityResponse.<List<SupportLogoDTO>>builder()
                    .success(false)
                    .message(e.getMessage())
                    .funnyMessage("🤔 Something's not quite right with that request!")
                    .build()
            );
        } catch (Exception e) {
            log.error("❌ Failed to reorder support logos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                EntityResponse.<List<SupportLogoDTO>>builder()
                    .success(false)
                    .message("Failed to reorder support logos: " + e.getMessage())
                    .funnyMessage("🙈 Oops! Our logos are playing hide and seek!")
                    .errors(Collections.singletonList(e.getMessage()))
                    .build()
            );
        }
    }
}