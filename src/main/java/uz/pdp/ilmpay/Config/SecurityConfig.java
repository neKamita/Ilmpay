package uz.pdp.ilmpay.Config;

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

/**
 * 🔐 Security Configuration
 * Keeping the bad guys out while letting the good guys in!
 * 
 * @author Your Security Guard Developer
 * @version 1.0 (The "Fort Knox" Edition)
 */
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
            .authorizeHttpRequests(auth -> auth
                // 🎫 VIP areas - Admin only!
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // 🎪 Public areas - Everyone's welcome!
                .anyRequest().permitAll()
            )
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
}
