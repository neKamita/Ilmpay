package uz.pdp.ilmpay.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.pdp.ilmpay.dto.TranslationDTO;
import uz.pdp.ilmpay.payload.EntityResponse;
import uz.pdp.ilmpay.service.TranslationManagementService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 🌍 Translation REST Controller
 * Provides REST endpoints for managing translations.
 * 
 * @author Your Polyglot Developer
 * @version 1.2 (The "Dynamic Babel API" Edition)
 */
@RestController
@RequestMapping("/api/admin/translations")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class TranslationRestController {

    private final TranslationManagementService translationService;

    /**
     * 🔍 Get all translations for a specific language
     */
    @GetMapping("/language/{languageCode}")
    public ResponseEntity<EntityResponse<List<TranslationDTO>>> getTranslationsByLanguage(
            @PathVariable String languageCode) {
        log.info("🌐 REST request to get all translations for language: {}", languageCode);
        try {
            List<TranslationDTO> translations = translationService.getTranslationsByLanguage(languageCode);
            return ResponseEntity.ok(EntityResponse.success(translations));
        } catch (Exception e) {
            log.error("❌ Error getting translations for language: {}", languageCode, e);
            return ResponseEntity.badRequest()
                    .body(EntityResponse.error("Failed to get translations: " + e.getMessage()));
        }
    }

    /**
     * 🔍 Get all translations for a specific key
     */
    @GetMapping("/key/{key}")
    public ResponseEntity<EntityResponse<Map<String, TranslationDTO>>> getTranslationsByKey(
            @PathVariable String key) {
        log.info("🔑 REST request to get translations for key: {}", key);
        try {
            Map<String, TranslationDTO> translations = translationService.getTranslationsByKey(key);
            return ResponseEntity.ok(EntityResponse.success(translations));
        } catch (Exception e) {
            log.error("❌ Error getting translations for key: {}", key, e);
            return ResponseEntity.badRequest()
                    .body(EntityResponse.error("Failed to get translations: " + e.getMessage()));
        }
    }

    /**
     * 🔍 Get all translations with pagination
     * This optimized endpoint reduces the number of database queries by fetching
     * translations in batches
     */
    @GetMapping("/paginated")
    public ResponseEntity<EntityResponse<Map<String, Object>>> getAllTranslationsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(required = false) String languageCode,
            @RequestParam(required = false) String searchText) {
        log.info(
                "🌐 REST request to get paginated translations - Page: {}, Size: {}, Language: {}, Search: {}",
                page, size, languageCode, searchText);
        try {
            // Passing false for notTranslatedOnly parameter since it's been removed
            Map<String, Object> result = translationService.getAllTranslationsPaginated(page, size, languageCode,
                    searchText, false);
            return ResponseEntity.ok(EntityResponse.success(result));
        } catch (Exception e) {
            log.error("❌ Error getting paginated translations", e);
            return ResponseEntity.badRequest()
                    .body(EntityResponse.error("Failed to get translations: " + e.getMessage()));
        }
    }

    /**
     * 🔍 Get all unique translation keys
     */
    @GetMapping("/keys")
    public ResponseEntity<EntityResponse<List<String>>> getAllTranslationKeys() {
        log.info("🔑 REST request to get all unique translation keys");
        try {
            List<String> keys = translationService.getAllTranslationKeys();
            return ResponseEntity.ok(EntityResponse.success(keys));
        } catch (Exception e) {
            log.error("❌ Error getting translation keys", e);
            return ResponseEntity.badRequest()
                    .body(EntityResponse.error("Failed to get translation keys: " + e.getMessage()));
        }
    }

    /**
     * 🔍 Get all supported languages
     */
    @GetMapping("/languages")
    public ResponseEntity<EntityResponse<List<String>>> getSupportedLanguages() {
        log.info("🌐 REST request to get all supported languages");
        try {
            List<String> languages = translationService.getSupportedLanguages();
            return ResponseEntity.ok(EntityResponse.success(languages));
        } catch (Exception e) {
            log.error("❌ Error getting supported languages", e);
            return ResponseEntity.badRequest()
                    .body(EntityResponse.error("Failed to get supported languages: " + e.getMessage()));
        }
    }

    /**
     * 🔍 Search translations by key
     */
    @GetMapping("/search/key")
    public ResponseEntity<EntityResponse<List<TranslationDTO>>> searchByKey(
            @RequestParam String query) {
        log.info("🔍 REST request to search translations by key containing: {}", query);
        try {
            List<TranslationDTO> translations = translationService.searchByKey(query);
            return ResponseEntity.ok(EntityResponse.success(translations));
        } catch (Exception e) {
            log.error("❌ Error searching translations by key", e);
            return ResponseEntity.badRequest()
                    .body(EntityResponse.error("Failed to search translations: " + e.getMessage()));
        }
    }

    /**
     * 🔍 Search translations by text
     */
    @GetMapping("/search/text")
    public ResponseEntity<EntityResponse<List<TranslationDTO>>> searchByText(
            @RequestParam String query) {
        log.info("🔍 REST request to search translations by text containing: {}", query);
        try {
            List<TranslationDTO> translations = translationService.searchByText(query);
            return ResponseEntity.ok(EntityResponse.success(translations));
        } catch (Exception e) {
            log.error("❌ Error searching translations by text", e);
            return ResponseEntity.badRequest()
                    .body(EntityResponse.error("Failed to search translations: " + e.getMessage()));
        }
    }

    /**
     * 💾 Save or update a translation
     */
    @PostMapping
    public ResponseEntity<EntityResponse<TranslationDTO>> saveTranslation(
            @RequestBody TranslationDTO dto) {
        log.info("💾 REST request to save translation - Key: {}, Language: {}", dto.getKey(), dto.getLanguageCode());
        try {
            TranslationDTO saved = translationService.saveTranslation(dto);
            return ResponseEntity.ok(EntityResponse.success(saved));
        } catch (Exception e) {
            log.error("❌ Error saving translation", e);
            return ResponseEntity.badRequest()
                    .body(EntityResponse.error("Failed to save translation: " + e.getMessage()));
        }
    }

    /**
     * 🔄 Import translations from properties files
     */
    @PostMapping("/import")
    public ResponseEntity<EntityResponse<Integer>> importFromProperties() {
        log.info("📥 REST request to import translations from properties files");
        try {
            int count = translationService.importFromProperties();
            return ResponseEntity.ok(EntityResponse.success(count));
        } catch (Exception e) {
            log.error("❌ Error importing translations", e);
            return ResponseEntity.badRequest()
                    .body(EntityResponse.error("Failed to import translations: " + e.getMessage()));
        }
    }

    /**
     * 🌐 Public endpoint to get translations for a specific language
     * This endpoint is accessible without admin privileges
     */
    @GetMapping("/public/{languageCode}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<EntityResponse<Map<String, String>>> getPublicTranslations(
            @PathVariable String languageCode) {
        log.info("🌐 REST request to get public translations for language: {}", languageCode);
        try {
            // Get all translations for the language
            List<TranslationDTO> translations = translationService.getTranslationsByLanguage(languageCode);

            // Convert to a simple key-value map
            Map<String, String> translationMap = translations.stream()
                    .collect(Collectors.toMap(TranslationDTO::getKey, TranslationDTO::getTranslatedText));

            return ResponseEntity.ok(EntityResponse.success(translationMap));
        } catch (Exception e) {
            log.error("❌ Error getting public translations for language: {}", languageCode, e);
            return ResponseEntity.badRequest()
                    .body(EntityResponse.error("Failed to get translations: " + e.getMessage()));
        }
    }
}
