package com.easygroup.config;

import com.easygroup.entity.User;
import com.easygroup.repository.UserRepository;
import com.easygroup.security.Argon;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Arrays;

import java.util.List;

/**
 * Configuration de la sécurité globale de l'application.
 * 
 * Cette classe centralise :
 * - la définition du PasswordEncoder (utilisé pour le hashage sécurisé des mots
 * de passe)
 * - la configuration CORS (nécessaire pour permettre les appels entre frontend
 * et backend)
 * - la configuration de la chaîne de filtres HTTP Spring Security (autorisation
 * des routes)
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${cors.urls}")
    private String corsUrl;

    @Value("${cors.enabled:true}")
    private boolean corsEnabled;

    @Value("${csrf.enabled:false}")
    private boolean csrfEnabled;

    /**
     * Déclare le bean `PasswordEncoder` utilisé pour le chiffrement des mots de
     * passe.
     * 
     * L'encodeur ici est basé sur une implémentation personnalisée Argon2id,
     * réputée pour sa résistance aux attaques par GPU et side-channel.
     * 
     * Ce bean est injecté automatiquement par Spring là où nécessaire
     * (ex : services d'inscription, d'authentification...).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon();
    }

    private List<String> getAllowedOrigins() {
        return Arrays.stream(corsUrl.split(","))
                .map(String::trim)
                .toList();
    }

    /**
     * Déclare le bean `CorsConfigurationSource`, qui définit les règles CORS
     * globales.
     *
     * Cette configuration permet à l'application Angular (sur localhost:4200)
     * de faire des appels HTTP vers l'API backend sécurisée.
     *
     * ⚠️ Important : autorise les en-têtes, méthodes, et les credentials (ex:
     * cookie/session).
     * 
     * Le CORS peut être activé/désactivé via la propriété cors.enabled
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        System.out.println("-------------------------------------");
        System.out.println("CORS Enabled: " + corsEnabled);
        System.out.println("CORS URL : " + corsUrl);

        if (corsEnabled) {
            config.setAllowedOrigins(getAllowedOrigins()); // Frontend Angular local
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Méthodes autorisées
            config.setAllowedHeaders(List.of("*")); // Tous les headers acceptés
            config.setAllowCredentials(true); // Permet l'envoi des cookies/session
        } else {
            // Disable CORS by not setting any allowed origins
            config.setAllowedOrigins(List.of());
        }

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Applique à toutes les routes
        return source;
    }

    /**
     * Configure la chaîne de filtres de sécurité Spring.
     * 
     * Cette méthode définit les règles de sécurité HTTP :
     * - CORS activé (utilise le bean précédent)
     * - CSRF désactivé (car on utilise probablement JWT/session)
     * - Définition des routes publiques vs protégées
     *
     * Les routes publiques sont :
     * - /auth/** : login/register/confirm
     * - /swagger-ui/** et /v3/api-docs/** : documentation de l'API
     * 
     * Toutes les autres requêtes nécessitent une authentification.
     */
    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Configure CORS based on corsEnabled property
        if (corsEnabled) {
            http.cors(Customizer.withDefaults());
        } else {
            http.cors(cors -> cors.disable());
        }

        // Configure CSRF based on csrfEnabled property
        if (csrfEnabled) {
            http.csrf(Customizer.withDefaults());
            System.out.println("-------------------------------------");
            System.out.println("CSRF Protection: Enabled");
        } else {
            http.csrf(csrf -> csrf.disable());
            System.out.println("-------------------------------------");
            System.out.println("CSRF Protection: Disabled");
        }

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/"),
                                new AntPathRequestMatcher("/api/auth/**"),
                                new AntPathRequestMatcher("/swagger-ui/**"),
                                new AntPathRequestMatcher("/v3/api-docs/**"))
                        .permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return email -> {
            User user = userRepository.findByEmail(email)
                    .filter(User::getIsActivated)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found or inactive"));
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRole().name())
                    .build();
        };
    }

}
