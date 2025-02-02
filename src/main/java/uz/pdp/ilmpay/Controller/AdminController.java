package uz.pdp.ilmpay.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import uz.pdp.ilmpay.payload.EntityResponse;
import uz.pdp.ilmpay.dto.SupportLogoDTO;
import uz.pdp.ilmpay.service.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 👑 Admin Controller
 * Where the magic happens behind the scenes!
 * 
 * @author Your Admin Panel Architect
 * @version 1.0 (The "Control Room" Edition)
 */
@Slf4j
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final SupportLogoService supportLogoService;
    private final BenefitCardService benefitCardService;
    private final TestimonialService testimonialService;
    private final FaqService faqService;
    private final VisitorService visitorService;

    @GetMapping("/login")
    public String login() {
        return "admin/login";  // 🔑 Show the login page
    }

    /**
     * 🎮 Dashboard
     * The command center of our operation!
     * Where we monitor our empire of knowledge 📚
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        // 🧭 Track the current path for navigation
        model.addAttribute("currentPath", request.getRequestURI());
        
        // 📊 Load all the stats that make us look good
        model.addAttribute("totalVisitors", visitorService.getTotalVisitors());
        model.addAttribute("todayVisitors", visitorService.getTodayVisitors());
        model.addAttribute("appDownloads", visitorService.getAppDownloads());
        model.addAttribute("activeUsers", visitorService.getActiveUsers());
        
        // 🎨 Load content for management
        model.addAttribute("supportLogos", supportLogoService.findAllActive());
        model.addAttribute("benefitCards", benefitCardService.findAllActive());
        model.addAttribute("testimonials", testimonialService.findAllActive());
        model.addAttribute("faqs", faqService.findAllActive());
        
        return "admin/dashboard";  // 🚀 Launch the dashboard!
    }

    @GetMapping("/content")
    public String contentManagement(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("supportLogos", supportLogoService.findAllActive());
        model.addAttribute("benefitCards", benefitCardService.findAllActive());
        model.addAttribute("testimonials", testimonialService.findAllActive());
        model.addAttribute("faqs", faqService.findAllActive());
        
        return "admin/content/index";
    }


    @GetMapping("/content/benefits")
    public String benefitsPage(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("benefits", benefitCardService.findAllActive());
        return "admin/content/benefits";
    }

    @GetMapping("/content/testimonials")
    public String testimonialsPage(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("testimonials", testimonialService.findAllActive());
        return "admin/content/testimonials";
    }

    @GetMapping("/content/faqs")
    public String faqsPage(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
            model.addAttribute("faqs", faqService.findAllActive());
        return "admin/content/faqs";
    }

    // Support Logos CRUD
    @PostMapping("/support-logos")
    @ResponseBody
    public ResponseEntity<EntityResponse<SupportLogoDTO>> createSupportLogo(@Valid @RequestBody SupportLogoDTO dto) {
        log.info("Creating new support logo: {}", dto.getName());
        try {
            SupportLogoDTO created = supportLogoService.create(dto);
            log.info("Successfully created support logo with id: {}", created.getId());
            return ResponseEntity.ok(EntityResponse.success(created));
        } catch (Exception e) {
            log.error("Failed to create support logo", e);
            return ResponseEntity.badRequest()
                .body(EntityResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/support-logos/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse<SupportLogoDTO>> updateSupportLogo(
            @PathVariable Long id, 
            @RequestBody SupportLogoDTO dto) {
        try {
            SupportLogoDTO updated = supportLogoService.update(id, dto);
            return ResponseEntity.ok(EntityResponse.success(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(EntityResponse.error("Failed to update support logo: " + e.getMessage()));
        }
    }

    @DeleteMapping("/support-logos/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse<Void>> deleteSupportLogo(@PathVariable Long id) {
        try {
            supportLogoService.delete(id);
            return ResponseEntity.ok(EntityResponse.success(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(EntityResponse.error("Failed to delete support logo: " + e.getMessage()));
        }
    }

}
