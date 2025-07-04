package com.easygroup.config;

import com.easygroup.service.MailingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration de l'injection de dépendance pour le service d'envoi de mail.
 */
@Configuration
public class MailConfig {

    @Bean
    public MailingService mailingService(
            @Value("${smtp.server}") String host,
            @Value("${smtp.username}") String username,
            @Value("${smtp.password}") String password
    ) {
        // Utilise le constructeur par défaut : port 587, auth = true, starttls = true
        return new MailingService(host, username, password);
    }
}
