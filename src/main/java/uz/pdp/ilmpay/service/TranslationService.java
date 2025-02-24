package uz.pdp.ilmpay.service;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * 🌍 Translation Service
 * Making the world a smaller place, one translation at a time!
 */
@Service
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

    public String translate(String text, String targetLanguageCode) {
        Translation translation = translate.translate(
                text,
                Translate.TranslateOption.targetLanguage(targetLanguageCode)
        );
        return translation.getTranslatedText();
    }
    public String translate(String text, String sourceLanguageCode, String targetLanguageCode) {
        Translation translation = translate.translate(
                text,
                Translate.TranslateOption.sourceLanguage(sourceLanguageCode),
                Translate.TranslateOption.targetLanguage(targetLanguageCode)
        );
        return translation.getTranslatedText();
    }
}
