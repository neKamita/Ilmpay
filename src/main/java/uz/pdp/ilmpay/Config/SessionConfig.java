package uz.pdp.ilmpay.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 🔒 Session Configuration
 * Configures session management with improved security and tracking
 *
 * @author Your Friendly Neighborhood Developer
 * @version 1.0 (The "Session Master" Edition)
 */
@Configuration
@EnableJdbcHttpSession(
    maxInactiveIntervalInSeconds = 1800,  // 30 minutes
    tableName = "SPRING_SESSION"
)
public class SessionConfig implements WebMvcConfigurer {

    @Value("${session.timeout.minutes:30}")
    private int sessionTimeoutMinutes;

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("ILMPAY_SESSION");          // Custom cookie name
        serializer.setCookieMaxAge(sessionTimeoutMinutes * 60); // Convert to seconds
        serializer.setSameSite("Lax");                       // Reasonable security default
        serializer.setUseSecureCookie(true);                 // Require HTTPS
        serializer.setUseHttpOnlyCookie(true);              // Prevent XSS access to cookie
        return serializer;
    }
}
