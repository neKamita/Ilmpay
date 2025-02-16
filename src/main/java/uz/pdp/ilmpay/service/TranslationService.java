package uz.pdp.ilmpay.service;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.springframework.stereotype.Service;

@Service
public class TranslationService {

    private final Translate translate;

    public TranslationService() {
        translate = TranslateOptions.getDefaultInstance().getService();
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
