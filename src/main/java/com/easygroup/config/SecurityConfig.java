package com.easygroup.config;

import com.easygroup.security.Argon;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.Customizer;

import java.util.List;

/**
 * Configuration de la sécurité globale de l'application.
 * 
 * Cette classe centralise :
 * - la définition du PasswordEncoder (utilisé pour le hashage sécurisé des mots de passe)
 * - la configuration CORS (nécessaire pour permettre les appels entre frontend et backend)
 * - la configuration de la chaîne de filtres HTTP Spring Security (autorisation des routes)
 */
@Configuration
public class SecurityConfig {

    /**
     * Déclare le bean `PasswordEncoder` utilisé pour le chiffrement des mots de passe.
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

    /**
     * Déclare le bean `CorsConfigurationSource`, qui définit les règles CORS globales.
     *
     * Cette configuration permet à l'application Angular (sur localhost:4200) 
     * de faire des appels HTTP vers l'API backend sécurisée.
     *
     * ⚠️ Important : autorise les en-têtes, méthodes, et les credentials (ex: cookie/session).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200")); // Frontend Angular local
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Méthodes autorisées
        config.setAllowedHeaders(List.of("*")); // Tous les headers acceptés
        config.setAllowCredentials(true); // Permet l'envoi des cookies/session

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
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // ✅ version à jour, pas dépréciée
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(
                                        new AntPathRequestMatcher("/auth/**"),
                                        new AntPathRequestMatcher("/swagger-ui/**"),
                                        new AntPathRequestMatcher("/v3/api-docs/**")
                                ).permitAll()
                                .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
}
}
