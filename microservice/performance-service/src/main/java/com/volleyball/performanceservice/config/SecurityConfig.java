package com.volleyball.performanceservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de sécurité pour le performance-service
 * Configuration permissive pour le développement et les tests
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Désactiver CSRF pour les API REST
            .csrf(csrf -> csrf.disable())
            
            // Configuration des autorisations
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/performance/**").permitAll()
                .requestMatchers("/**").permitAll()
                .anyRequest().permitAll()
            )
            
            // Désactiver l'authentification HTTP Basic
            .httpBasic(httpBasic -> httpBasic.disable())
            
            // Désactiver l'authentification par formulaire
            .formLogin(formLogin -> formLogin.disable())
            
            // Configuration des sessions (stateless pour API REST)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }
}
