package uz.pdp.ilmpay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 🌍 Translation Data Transfer Object
 * Used for transferring translation data between layers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslationDTO {
    private Long id;
    private String key;
    private String languageCode;
    private String translatedText;
    private LocalDateTime lastUpdated;

    // Constructor without id for creating new translations
    public TranslationDTO(String key, String languageCode, String translatedText) {
        this.key = key;
        this.languageCode = languageCode;
        this.translatedText = translatedText;
    }
}
