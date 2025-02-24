package uz.pdp.ilmpay.service;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * 🌍 Translation Service
 * Making the world a smaller place, one translation at a time!
 */
@Service
@Slf4j
public class TranslationService {

    @Value("${google.cloud.translation.api-key}")
    private String apiKey;

    @Value("${google.cloud.project-id}")
    private String projectId;

    private Translate translate;

    @PostConstruct
    public void initialize() {
        // 🎯 Initialize the translation service with our API key
        translate = TranslateOptions.newBuilder()
                .setApiKey(apiKey)
                .setProjectId(projectId)
                .build()
                .getService();
    }

    /**
     * 🌐 Safely translates text to target language
     */
    public String translate(String text, String targetLanguageCode) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        try {
            Translation translation = translate.translate(
                    text,
                    Translate.TranslateOption.targetLanguage(targetLanguageCode)
            );
            log.debug("🔄 Translated text to {}: {} -> {}", targetLanguageCode, text, translation.getTranslatedText());
            return translation.getTranslatedText();
        } catch (Exception e) {
            log.error("❌ Translation failed for text: {}, target language: {}, error: {}", text, targetLanguageCode, e.getMessage());
            return text; // Return original text if translation fails
        }
    }

    /**
     * 🌐 Safely translates text from source to target language
     */
    public String translate(String text, String sourceLanguageCode, String targetLanguageCode) {
        if (text == null || text.trim().isEmpty() || sourceLanguageCode.equals(targetLanguageCode)) {
            return text;
        }
        try {
            Translation translation = translate.translate(
                    text,
                    Translate.TranslateOption.sourceLanguage(sourceLanguageCode),
                    Translate.TranslateOption.targetLanguage(targetLanguageCode)
            );
            log.debug("🔄 Translated text from {} to {}: {} -> {}", sourceLanguageCode, targetLanguageCode, text, translation.getTranslatedText());
            return translation.getTranslatedText();
        } catch (Exception e) {
            log.error("❌ Translation failed for text: {}, source: {}, target: {}, error: {}", text, sourceLanguageCode, targetLanguageCode, e.getMessage());
            return text; // Return original text if translation fails
        }
    }
}
