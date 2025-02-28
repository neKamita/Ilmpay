package uz.pdp.ilmpay.Config;

import org.springframework.boot.web.server.Compression;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

/**
 * 🚀 Web Resource Optimization Configuration
 * Turbocharging our static resources with caching, compression, and optimization!
 * 
 * @author High-Performance Wizard
 * @version 1.0 (The "Speed Demon" Edition)
 */
@Configuration
public class WebResourceOptimizationConfig implements WebMvcConfigurer {

    /**
     * 🔄 Configure our resource handlers with proper caching
     * Making the web faster, one cache header at a time!
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 🖼️ Images - cached for 1 week
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic());

        // 🎨 CSS - cached for 1 day
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic());

        // ⚡ JavaScript - cached for 1 day
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic());
    }

    /**
     * 🗜️ Configure MIME mappings for modern web
     * Because it's 2025 and we should be using modern formats!
     */
    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
        return factory -> {
            // Set MIME mappings
            MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
            
            // Add WebP support
            mappings.add("webp", "image/webp");
            
            // Add proper MIME type for JavaScript modules
            mappings.add("mjs", "application/javascript");
            
            // Add woff2 font format
            mappings.add("woff2", "font/woff2");
            
            factory.setMimeMappings(mappings);
            
            // Set up compression
            Compression compression = new Compression();
            compression.setEnabled(true);
            compression.setMinResponseSize(DataSize.ofKilobytes(1)); // Compress responses larger than 1KB
            
            // Set MIME types for compression - common text formats
            compression.setMimeTypes(new String[] {
                "text/html", "text/xml", "text/plain", "text/css", 
                "text/javascript", "application/javascript", "application/json",
                "application/xml", "image/svg+xml"
            });
            
            factory.setCompression(compression);
        };
    }
}
