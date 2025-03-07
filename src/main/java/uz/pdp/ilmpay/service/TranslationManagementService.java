package uz.pdp.ilmpay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.ilmpay.dto.TranslationDTO;
import uz.pdp.ilmpay.model.Translation;
import uz.pdp.ilmpay.repository.TranslationRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 🌍 Translation Management Service
 * Manages translations for static text in the application.
 * Allows admins to update translations without changing properties files.
 * 
 * @author Your Polyglot Developer
 * @version 1.1 (The "Speedy Babel Fish" Edition)
 */
@Service
@Slf4j
public class TranslationManagementService {

    private final TranslationRepository translationRepository;
    private final MessageSource messageSource;

    // Explicit constructor with @Qualifier
    public TranslationManagementService(
            TranslationRepository translationRepository,
            @org.springframework.beans.factory.annotation.Qualifier("resourceBundleMessageSource") MessageSource messageSource) {
        this.translationRepository = translationRepository;
        this.messageSource = messageSource;
    }

    // Supported language codes
    private final List<String> supportedLanguages = Arrays.asList("en", "ru", "uz");

    /**
     * 🔍 Get all translations for a specific language
     */
    @Cacheable(value = "translationsByLanguage", key = "#languageCode")
    public List<TranslationDTO> getTranslationsByLanguage(String languageCode) {
        log.info("🌐 Getting all translations for language: {}", languageCode);
        return translationRepository.findByLanguageCodeOrderByKeyAsc(languageCode)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 🔍 Get all translations with pagination
     * This optimized method fetches translations in batches to improve performance
     */
    @Cacheable(value = "allTranslations", key = "{#page, #size, #languageCode, #searchText, #notTranslatedOnly}")
    public Map<String, Object> getAllTranslationsPaginated(int page, int size, String languageCode, String searchText,
            boolean notTranslatedOnly) {
        log.info(
                "🌐 Getting paginated translations - Page: {}, Size: {}, Language: {}, Search: {}, NotTranslatedOnly: {}",
                page, size, languageCode, searchText, notTranslatedOnly);

        Pageable pageable = PageRequest.of(page, size, Sort.by("key").ascending());
        Page<Translation> translationsPage;

        // Apply filters if provided
        if (languageCode != null && !languageCode.equals("all")) {
            if (searchText != null && !searchText.isEmpty()) {
                // Filter by language and search text
                translationsPage = translationRepository
                        .findByLanguageCodeAndKeyContainingIgnoreCaseOrLanguageCodeAndTranslatedTextContainingIgnoreCase(
                                languageCode, searchText, languageCode, searchText, pageable);
            } else {
                // Filter by language only
                translationsPage = translationRepository.findByLanguageCode(languageCode, pageable);
            }
        } else if (searchText != null && !searchText.isEmpty()) {
            // Filter by search text only
            translationsPage = translationRepository.findByKeyContainingIgnoreCaseOrTranslatedTextContainingIgnoreCase(
                    searchText, searchText, pageable);
        } else {
            // No filters, get all translations
            translationsPage = translationRepository.findAll(pageable);
        }

        // Convert to DTOs
        List<TranslationDTO> translationDTOs = translationsPage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        // Group by key
        Map<String, Map<String, TranslationDTO>> translationsByKey = new HashMap<>();

        for (TranslationDTO dto : translationDTOs) {
            translationsByKey.computeIfAbsent(dto.getKey(), k -> new HashMap<>())
                    .put(dto.getLanguageCode(), dto);
        }

        // Removed notTranslatedOnly filter logic

        // Prepare result
        Map<String, Object> result = new HashMap<>();
        result.put("content", translationsByKey);
        result.put("totalPages", translationsPage.getTotalPages());
        result.put("totalElements", translationsPage.getTotalElements());
        result.put("currentPage", translationsPage.getNumber());

        return result;
    }

    /**
     * 🔍 Get all translations for a specific key across all languages
     */
    @Cacheable(value = "translationsByKey", key = "#key")
    public Map<String, TranslationDTO> getTranslationsByKey(String key) {
        log.info("🔑 Getting translations for key: {}", key);
        return translationRepository.findByKeyOrderByLanguageCodeAsc(key)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toMap(TranslationDTO::getLanguageCode, dto -> dto));
    }

    /**
     * 🔍 Get all unique translation keys
     */
    @Cacheable(value = "allTranslationKeys")
    public List<String> getAllTranslationKeys() {
        log.info("🔑 Getting all unique translation keys");
        return translationRepository.findAllUniqueKeys();
    }

    /**
     * 🔍 Get all supported languages
     */
    public List<String> getSupportedLanguages() {
        return supportedLanguages;
    }

    /**
     * 🔍 Search translations by key
     */
    public List<TranslationDTO> searchByKey(String keyText) {
        log.info("🔍 Searching translations by key containing: {}", keyText);
        return translationRepository.findByKeyContainingIgnoreCaseOrderByKeyAsc(keyText)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 🔍 Search translations by text
     */
    public List<TranslationDTO> searchByText(String text) {
        log.info("🔍 Searching translations by text containing: {}", text);
        return translationRepository.findByTranslatedTextContainingIgnoreCaseOrderByKeyAsc(text)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 💾 Save or update a translation
     */
    @Transactional
    @CacheEvict(value = { "translationsByLanguage", "translationsByKey", "allTranslations" }, allEntries = true)
    public TranslationDTO saveTranslation(TranslationDTO dto) {
        log.info("💾 Saving translation - Key: {}, Language: {}", dto.getKey(), dto.getLanguageCode());

        Translation translation = translationRepository
                .findByKeyAndLanguageCode(dto.getKey(), dto.getLanguageCode())
                .orElse(new Translation());

        translation.setKey(dto.getKey());
        translation.setLanguageCode(dto.getLanguageCode());
        translation.setTranslatedText(dto.getTranslatedText());

        Translation saved = translationRepository.save(translation);
        log.info("✅ Translation saved successfully with ID: {}", saved.getId());

        return mapToDTO(saved);
    }

    /**
     * 🗑️ Delete a translation
     */
    @Transactional
    public void deleteTranslation(Long id) {
        log.info("🗑️ Deleting translation with ID: {}", id);
        translationRepository.deleteById(id);
        log.info("✅ Translation deleted successfully");
    }

    /**
     * 🔄 Import translations from properties files
     * This method loads translations from the message source and saves them to the
     * database if they don't already exist.
     */
    @Transactional
    @CacheEvict(value = { "translationsByLanguage", "translationsByKey", "allTranslationKeys",
            "allTranslations" }, allEntries = true)
    public int importFromProperties() {
        log.info("📥 Importing translations from properties files");
        int importedCount = 0;

        // Get all keys from the properties files
        ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.ENGLISH);
        Set<String> keys = bundle.keySet();

        for (String key : keys) {
            for (String lang : supportedLanguages) {
                Locale locale = Locale.forLanguageTag(lang);
                String text = messageSource.getMessage(key, null, locale);

                // Check if translation already exists
                Optional<Translation> existingTranslation = translationRepository.findByKeyAndLanguageCode(key, lang);

                if (existingTranslation.isEmpty()) {
                    // Create new translation
                    Translation translation = new Translation();
                    translation.setKey(key);
                    translation.setLanguageCode(lang);
                    translation.setTranslatedText(text);
                    translation.setLastUpdated(LocalDateTime.now());

                    translationRepository.save(translation);
                    importedCount++;
                }
            }
        }

        log.info("✅ Imported {} translations from properties files", importedCount);
        return importedCount;
    }

    /**
     * 🔄 Get translation by key and language
     * First checks the database, then falls back to properties file
     */
    public String getTranslation(String key, String languageCode) {
        // Try to get from database first
        Optional<Translation> dbTranslation = translationRepository.findByKeyAndLanguageCode(key, languageCode);

        if (dbTranslation.isPresent()) {
            return dbTranslation.get().getTranslatedText();
        }

        // Fall back to properties file
        Locale locale = Locale.forLanguageTag(languageCode);
        return messageSource.getMessage(key, null, key, locale);
    }

    /**
     * 🔄 Get current translation for a key
     * Uses the current locale from LocaleContextHolder
     */
    public String getCurrentTranslation(String key) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        return getTranslation(key, currentLocale.getLanguage());
    }

    /**
     * 🔄 Map entity to DTO
     */
    private TranslationDTO mapToDTO(Translation entity) {
        return new TranslationDTO(
                entity.getId(),
                entity.getKey(),
                entity.getLanguageCode(),
                entity.getTranslatedText(),
                entity.getLastUpdated());
    }

    /**
     * 🔄 Map DTO to entity
     */
    private Translation mapToEntity(TranslationDTO dto) {
        Translation entity = new Translation();
        entity.setId(dto.getId());
        entity.setKey(dto.getKey());
        entity.setLanguageCode(dto.getLanguageCode());
        entity.setTranslatedText(dto.getTranslatedText());
        entity.setLastUpdated(dto.getLastUpdated() != null ? dto.getLastUpdated() : LocalDateTime.now());
        return entity;
    }
}
