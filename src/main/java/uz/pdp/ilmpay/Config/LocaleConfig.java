package uz.pdp.ilmpay.Config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * 🌍 Internationalization Configuration
 * Making our app speak multiple languages - Uzbek, Russian, and English!
 * 
 * @author Your Polyglot Developer
 * @version 3.0 (The "Database Translations" Edition)
 */
@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    /**
     * 🎯 Sets up our base message source
     * This bean provides translations from properties files
     */
    @Bean(name = "resourceBundleMessageSource")
    public MessageSource resourceBundleMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * 🌐 Sets up our database-backed message source as the primary message source
     * This ensures that our database message source is used by default throughout
     * the application
     */
    @Bean
    @Primary
    public MessageSource primaryMessageSource(DatabaseMessageSource databaseMessageSource) {
        return databaseMessageSource;
    }

    /**
     * 🌐 Sets up our locale resolver
     * Supporting English (default), Russian, and Uzbek
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH); // 🇬🇧 Default to English
        return resolver;
    }

    /**
     * 🔄 Interceptor to switch languages
     * Changes language based on 'lang' parameter in URL
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang"); // 🎯 URL parameter to change language
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
