package uz.pdp.ilmpay.Config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import uz.pdp.ilmpay.model.Translation;
import uz.pdp.ilmpay.repository.TranslationRepository;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Optional;

/**
 * 🌍 Database Message Source
 * Custom MessageSource that checks the database for translations before falling
 * back to the properties files.
 * This allows us to override translations in the database without changing the
 * properties files.
 */
@Component
@Slf4j
public class DatabaseMessageSource implements MessageSource {

    private final TranslationRepository translationRepository;
    private final MessageSource fallbackMessageSource;

    public DatabaseMessageSource(TranslationRepository translationRepository,
            @org.springframework.beans.factory.annotation.Qualifier("resourceBundleMessageSource") MessageSource resourceBundleMessageSource) {
        this.translationRepository = translationRepository;
        this.fallbackMessageSource = resourceBundleMessageSource;
    }

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        try {
            return getMessage(code, args, locale);
        } catch (NoSuchMessageException e) {
            return defaultMessage;
        }
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        log.debug("🔍 Getting message for code: {} and locale: {}", code, locale);

        // Try to get the translation from the database
        Optional<Translation> translation = translationRepository.findByKeyAndLanguageCode(
                code, locale.getLanguage());

        if (translation.isPresent()) {
            log.debug("✅ Found translation in database for code: {} and locale: {}", code, locale);
            String message = translation.get().getTranslatedText();

            // Format message with arguments if provided
            if (args != null && args.length > 0) {
                return new MessageFormat(message, locale).format(args);
            }
            return message;
        }

        // If not found in database, fall back to properties file
        log.debug("⚠️ Translation not found in database, falling back to properties file for code: {} and locale: {}",
                code, locale);
        return fallbackMessageSource.getMessage(code, args, locale);
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        // For MessageSourceResolvable, we'll delegate directly to the fallback
        return fallbackMessageSource.getMessage(resolvable, locale);
    }
}
