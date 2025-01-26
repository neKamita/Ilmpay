package uz.pdp.ilmpay.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 🎨 Web MVC Configuration
 * Making our app look pretty and work smoothly!
 * 
 * @author Your Friendly Neighborhood Developer
 * @version 1.0 (The "Static Assets" Edition)
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 🎯 Configure validation for our methods
     * Because we like our data clean and tidy!
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    /**
     * 🗂️ Configure our static resource handlers
     * Telling Spring where to find all our goodies!
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 🖼️ Images - because a picture is worth a thousand words!
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");

        // 🎨 CSS - making things pretty since... well, forever!
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        // ⚡ JavaScript - where the magic happens
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
    }
}
