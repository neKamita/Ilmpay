package uz.pdp.ilmpay.Config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 🚀 Cache Configuration
 * Supercharging our app with blazing fast response times!
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 🎯 Primary cache manager for general-purpose caching
     */
    @Bean
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(Arrays.asList(
                "translations", // For translation results
                "translationsByLanguage", // For translations by language
                "translationsByKey", // For translations by key
                "allTranslationKeys", // For all translation keys
                "allTranslations", // For paginated translations
                "benefits", // For benefit content
                "benefitCards", // For benefit cards content
                "faqs", // For FAQ content
                "supportLogos", // For support logos
                "testimonials" // For testimonials
        ));

        // Default cache configuration
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(1, TimeUnit.HOURS));

        return cacheManager;
    }

    /**
     * 🌐 Specialized cache manager for translations with longer TTL
     */
    @Bean
    public CacheManager translationCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "translations",
                "translationsByLanguage",
                "translationsByKey",
                "allTranslationKeys",
                "allTranslations");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(24, TimeUnit.HOURS));

        return cacheManager;
    }
}
