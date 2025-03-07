package uz.pdp.ilmpay.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uz.pdp.ilmpay.service.TranslationManagementService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 🌍 Translation Controller
 * Provides the admin interface for managing translations.
 * 
 * @author Your Polyglot Developer
 * @version 1.0 (The "Babel UI" Edition)
 */
@Controller
@RequestMapping("/admin/content/translations")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class TranslationController {

    private final TranslationManagementService translationService;

    /**
     * 📝 Translations management page
     */
    @GetMapping
    public String translationsPage(Model model, HttpServletRequest request) {
        log.info("🌐 Loading translations management page");

        // Track the current path for navigation
        model.addAttribute("currentPath", request.getRequestURI());

        // Add supported languages
        model.addAttribute("supportedLanguages", translationService.getSupportedLanguages());

        // Import translations from properties files if the database is empty
        if (translationService.getAllTranslationKeys().isEmpty()) {
            log.info("📥 No translations found in database, importing from properties files");
            int count = translationService.importFromProperties();
            log.info("✅ Imported {} translations from properties files", count);
        }

        log.info("✨ Translations page loaded successfully");
        return "admin/content/translations";
    }
}
