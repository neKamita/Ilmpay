package uz.pdp.ilmpay.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.pdp.ilmpay.model.Translation;

import java.util.List;
import java.util.Optional;

/**
 * 🔍 Translation Repository
 * Provides database operations for translation management.
 * Optimized with pagination and efficient query methods.
 */
@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {

        /**
         * Find a translation by its key and language code
         */
        Optional<Translation> findByKeyAndLanguageCode(String key, String languageCode);

        /**
         * Find all translations for a specific language
         */
        List<Translation> findByLanguageCodeOrderByKeyAsc(String languageCode);

        /**
         * Find all translations for a specific language (without ordering)
         */
        List<Translation> findByLanguageCode(String languageCode);

        /**
         * Find all translations for a specific key across all languages
         */
        List<Translation> findByKeyOrderByLanguageCodeAsc(String key);

        /**
         * Find all unique translation keys
         */
        @Query("SELECT DISTINCT t.key FROM Translation t ORDER BY t.key")
        List<String> findAllUniqueKeys();

        /**
         * Find all unique language codes
         */
        @Query("SELECT DISTINCT t.languageCode FROM Translation t ORDER BY t.languageCode")
        List<String> findAllUniqueLanguageCodes();

        /**
         * Search translations by key containing the given text
         */
        List<Translation> findByKeyContainingIgnoreCaseOrderByKeyAsc(String keyText);

        /**
         * Search translations by translated text containing the given text
         */
        List<Translation> findByTranslatedTextContainingIgnoreCaseOrderByKeyAsc(String text);

        /**
         * Find all translations for a specific language with pagination
         */
        Page<Translation> findByLanguageCode(String languageCode, Pageable pageable);

        /**
         * Search translations by key or translated text containing the given text with
         * pagination
         */
        Page<Translation> findByKeyContainingIgnoreCaseOrTranslatedTextContainingIgnoreCase(
                        String keyText, String translatedText, Pageable pageable);

        /**
         * Search translations by language and key or translated text containing the
         * given text with pagination
         */
        Page<Translation> findByLanguageCodeAndKeyContainingIgnoreCaseOrLanguageCodeAndTranslatedTextContainingIgnoreCase(
                        String languageCode1, String keyText, String languageCode2, String translatedText,
                        Pageable pageable);
}
