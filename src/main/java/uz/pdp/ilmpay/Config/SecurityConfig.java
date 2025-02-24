package uz.pdp.ilmpay.Config;

import java.util.HashSet;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

/**
 * 🔐 Security Configuration
 * Keeping the bad guys out while letting the good guys in!
 * 
 * @author Your Security Guard Developer
 * @version 1.0 (The "Fort Knox" Edition)
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 🛡️ Configure security filter chain
     * Where we decide who gets in and who stays out!
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 🎭 Setting up our security theater
        http
            .csrf(AbstractHttpConfigurer::disable)  // 🚫 CSRF? Ain't nobody got time for that!
            // 🎭 Allow HTMX headers
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
                .addHeaderWriter((request, response) -> {
                    response.setHeader("Access-Control-Allow-Headers", "HX-Request, HX-Trigger, HX-Target, HX-Swap");
                    response.setHeader("Access-Control-Expose-Headers", "HX-Push, HX-Redirect, HX-Refresh, HX-Trigger");
                })
            )
            .authorizeHttpRequests(auth -> {
                log.debug("🔐 Configuring security rules...");
                return auth
                    // 🎫 VIP areas - Admin only!
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    // Allow HTMX-specific endpoints
                    .requestMatchers("/hx/**").permitAll()
                    // Explicitly permit root path
                    .requestMatchers("/").permitAll()
                    // 🎪 Public areas - Everyone's welcome!
                    .anyRequest().permitAll();
            })
            .formLogin(form -> form
                .loginPage("/admin/login")  // 🎯 Custom login page
                .defaultSuccessUrl("/admin/dashboard")  // 🎉 Where to go after login
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")  // 👋 See you later!
                .permitAll()
            );

        return http.build();  // 🏗️ Building our fortress
    }

    /**
     * 🔑 Password encoder
     * Because plain text passwords are so 1990s!
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // 🔒 Making passwords unreadable since forever
    }

    /**
     * 👥 User details service
     * Creating our VIP list (just admin for now)
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // 🎩 Creating our admin user
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123"))  // 🤫 Shhh... it's a secret!
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);  // 🧠 Keeping our users in memory
    }

    @Bean
public SpringTemplateEngine templateEngine(ITemplateResolver templateResolver) {
    SpringTemplateEngine engine = new SpringTemplateEngine();
    engine.setTemplateResolver(templateResolver);
    engine.setEnableSpringELCompiler(true);
    // Add the following line to expose request attributes
    engine.setRenderHiddenMarkersBeforeCheckboxes(true);
    Set<IDialect> dialects = new HashSet<>();
    dialects.add(new SpringSecurityDialect());
    engine.setAdditionalDialects(dialects);
    return engine;
}
}
