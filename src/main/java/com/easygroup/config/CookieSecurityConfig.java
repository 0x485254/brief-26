package com.easygroup.config;

import com.easygroup.security.CookieAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

/**
 * Configuration class for cookie-based security settings.
 * Provides cookie-based authentication and CSRF protection.
 */
@Configuration
@Order(1) // Higher priority than default security config
public class CookieSecurityConfig extends BaseSecurityConfig {

    private final CookieAuthenticationFilter cookieAuthFilter;

    public CookieSecurityConfig(
            CookieAuthenticationFilter cookieAuthFilter,
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        super(userDetailsService, passwordEncoder);
        this.cookieAuthFilter = cookieAuthFilter;
    }

    /**
     * Configures the security filter chain for cookie-based authentication.
     *
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs
     */
    @Bean
    public SecurityFilterChain cookieSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            // Enable CSRF protection with cookie-based token repository
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(cookieAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
