package uz.pdp.ilmpay.Controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import uz.pdp.ilmpay.payload.EntityResponse;
import uz.pdp.ilmpay.dto.SupportLogoDTO;
import uz.pdp.ilmpay.dto.TestimonialDTO;
import uz.pdp.ilmpay.service.*;

/**
 * 👑 Admin Controller
 * Where the magic happens behind the scenes!
 * 
 * @author Your Admin Panel Architect
 * @version 1.0 (The "Control Room" Edition)
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final SupportLogoService supportLogoService;
    private final BenefitCardService benefitCardService;
    private final TestimonialService testimonialService;
    private final FaqService faqService;

    @GetMapping("/login")
    public String login() {
        log.info("🔑 Admin login page requested");
        return "admin/login"; // 🔑 Show the login page
    }

    /**
     * 🎮 Dashboard
     * The command center of our operation!
     * Where we monitor our empire of knowledge 📚
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        log.info("🎮 Loading admin dashboard");

        // 🧭 Track the current path for navigation
        model.addAttribute("currentPath", request.getRequestURI());

        // 🎨 Load content for management
        model.addAttribute("supportLogos", supportLogoService.findAllActive());
        model.addAttribute("benefitCards", benefitCardService.findAllActive());
        model.addAttribute("testimonials", testimonialService.findAllActive());
        model.addAttribute("faqs", faqService.findAllActive());

        log.info("✨ Dashboard loaded successfully");
        return "admin/dashboard"; // 🚀 Launch the dashboard!
    }

    @GetMapping("/content")
    public String contentManagement(Model model, HttpServletRequest request) {
        log.info("📝 Loading content management page");
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("supportLogos", supportLogoService.findAllActive());
        model.addAttribute("benefitCards", benefitCardService.findAllActive());
        model.addAttribute("testimonials", testimonialService.findAllActive());
        model.addAttribute("faqs", faqService.findAllActive());

        log.info("✨ Content management page loaded successfully");
        return "admin/content/index";
    }

    @GetMapping("/content/benefits")
    public String benefitsPage(Model model, HttpServletRequest request) {
        log.info("📈 Loading benefits management page");
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("benefits", benefitCardService.findAllActive());
        log.info("✨ Benefits page loaded successfully");
        return "admin/content/benefits";
    }

    @GetMapping("/content/testimonials")
    public String testimonialsPage(Model model, HttpServletRequest request) {
        log.info("🎭 Loading testimonials management page");
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("testimonials", testimonialService.findAllActive());
        log.info("✨ Testimonials page loaded successfully");
        return "admin/content/testimonials";
    }

    @GetMapping("/content/faqs")
    public String faqsPage(Model model, HttpServletRequest request) {
        log.info("📚 Loading faqs management page");
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("faqs", faqService.findAllActive());
        log.info("✨ Faqs page loaded successfully");
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
        log.info("Updating support logo id: {}", id);
        try {
            SupportLogoDTO updated = supportLogoService.update(id, dto);
            log.info("Successfully updated support logo id: {}", id);
            return ResponseEntity.ok(EntityResponse.success(updated));
        } catch (Exception e) {
            log.error("Failed to update support logo", e);
            return ResponseEntity.badRequest()
                    .body(EntityResponse.error("Failed to update support logo: " + e.getMessage()));
        }
    }

    @DeleteMapping("/support-logos/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse<Void>> deleteSupportLogo(@PathVariable Long id) {
        log.info("Deleting support logo id: {}", id);
        try {
            supportLogoService.delete(id);
            log.info("Successfully deleted support logo id: {}", id);
            return ResponseEntity.ok(EntityResponse.success(null));
        } catch (Exception e) {
            log.error("Failed to delete support logo", e);
            return ResponseEntity.badRequest()
                    .body(EntityResponse.error("Failed to delete support logo: " + e.getMessage()));
        }
    }

    // Testimonials CRUD
    @GetMapping("/testimonials/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse<TestimonialDTO>> getTestimonial(@PathVariable Long id) {
        log.info("🔍 Getting testimonial id: {}", id);
        try {
            TestimonialDTO testimonial = testimonialService.findById(id);
            log.info("✅ Successfully retrieved testimonial id: {}", id);
            return ResponseEntity.ok(EntityResponse.success(testimonial));
        } catch (Exception e) {
            log.error("❌ Failed to get testimonial. Error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(EntityResponse.error("Failed to get testimonial: " + e.getMessage()));
        }
    }

    @PostMapping("/testimonials")
    @ResponseBody
    public ResponseEntity<EntityResponse<TestimonialDTO>> createTestimonial(
            @RequestParam("name") String name,
            @RequestParam("comment") String comment,
            @RequestParam("rating") int rating,
            @RequestParam(value = "imageFile", required = false) MultipartFile avatarFile) {
        log.info("✨ Creating new testimonial - Name: {}, Rating: {}, Has Avatar: {}",
                name, rating, avatarFile != null);
        try {
            TestimonialDTO dto = new TestimonialDTO();
            dto.setName(name);
            dto.setComment(comment);
            dto.setRating(rating);
            dto.setImageFile(avatarFile);
            dto.setActive(true);

            TestimonialDTO created = testimonialService.create(dto);
            log.info("✅ Successfully created testimonial with id: {}", created.getId());
            return ResponseEntity.ok(EntityResponse.success(created));
        } catch (Exception e) {
            log.error("❌ Failed to create testimonial. Error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(EntityResponse.error("Failed to create testimonial: " + e.getMessage()));
        }
    }

    @PutMapping("/testimonials/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse<TestimonialDTO>> updateTestimonial(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("comment") String comment,
            @RequestParam("rating") int rating,
            @RequestParam(value = "imageFile", required = false) MultipartFile avatarFile) {
        log.info("🔄 Updating testimonial id: {}", id);
        try {
            TestimonialDTO dto = new TestimonialDTO();
            dto.setId(id);
            dto.setName(name);
            dto.setComment(comment);
            dto.setRating(rating);
            dto.setImageFile(avatarFile);
            dto.setActive(true);

            TestimonialDTO updated = testimonialService.update(id, dto);
            log.info("✅ Successfully updated testimonial id: {}", id);
            return ResponseEntity.ok(EntityResponse.success(updated));
        } catch (Exception e) {
            log.error("❌ Failed to update testimonial. Error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(EntityResponse.error("Failed to update testimonial: " + e.getMessage()));
        }
    }

    @DeleteMapping("/testimonials/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse<Void>> deleteTestimonial(@PathVariable Long id) {
        log.info("🗑️ Deleting testimonial id: {}", id);
        try {
            testimonialService.delete(id);
            log.info("✅ Successfully deleted testimonial id: {}", id);
            return ResponseEntity.ok(EntityResponse.success(null));
        } catch (Exception e) {
            log.error("❌ Failed to delete testimonial", e);
            return ResponseEntity.badRequest()
                    .body(EntityResponse.error("Failed to delete testimonial: " + e.getMessage()));
        }
    }
}
