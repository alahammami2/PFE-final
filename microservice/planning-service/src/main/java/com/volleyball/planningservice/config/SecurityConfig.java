package com.volleyball.planningservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Désactiver CSRF pour les API REST
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Session stateless
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll() // Permettre l'accès libre à tous les endpoints pour le développement
            )
            .httpBasic(httpBasic -> httpBasic.disable()) // Désactiver l'authentification HTTP Basic
            .formLogin(formLogin -> formLogin.disable()); // Désactiver le formulaire de login
        
        return http.build();
    }
}
