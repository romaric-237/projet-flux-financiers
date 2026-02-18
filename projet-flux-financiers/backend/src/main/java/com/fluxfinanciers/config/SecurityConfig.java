package com.fluxfinanciers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration minimale de Spring Security
 * Pour l'instant : tout est permis (pas d'authentification requise)
 * Le JWT sera ajouté plus tard
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Bean PasswordEncoder pour hasher les mots de passe avec BCrypt
     * Utilisé par UserService pour encoder les mots de passe avant de les sauvegarder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuration minimale : TOUS les endpoints sont accessibles sans authentification
     * CSRF désactivé (pour faciliter les tests avec Postman)
     *
     * À compléter plus tard avec :
     * - JWT Authentication Filter
     * - Gestion des rôles
     * - Endpoints protégés
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Désactiver CSRF (pour tester facilement avec Postman/Insomnia)
                .csrf(csrf -> csrf.disable())

                // Autoriser TOUTES les requêtes sans authentification (temporaire)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}