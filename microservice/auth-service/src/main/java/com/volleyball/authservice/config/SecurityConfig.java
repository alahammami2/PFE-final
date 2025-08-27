package com.volleyball.authservice.config;

import com.volleyball.authservice.security.JwtAuthenticationFilter;
import com.volleyball.authservice.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

 

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Endpoints publics
                .requestMatchers("/login", "/create-user", "/health", "/check-email").permitAll()
                .requestMatchers("/api/auth/users/count").permitAll()
                .requestMatchers("/api/auth/users/count/joueurs").permitAll()
                // Autoriser temporairement la récupération de la liste des utilisateurs pour les tests UI
                .requestMatchers("/api/auth/users").permitAll()
                
                // DEV-ONLY: Autoriser toutes les opérations pendant l'intégration front.
                // À RESTREINDRE en production (ex: hasRole("ADMIN")).
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/auth/users/**").permitAll()
                .requestMatchers("/users/**").permitAll()
                .requestMatchers("/api/auth/login", "/api/auth/create-user", "/api/auth/health", "/api/auth/check-email").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                // Endpoints de gestion des utilisateurs (ADMIN seulement)
                // .requestMatchers("/users/**").hasRole("ADMIN") // REACTIVER EN PROD
                // Endpoints protégés
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/coach/**").hasAnyRole("ADMIN", "COACH")
                .requestMatchers("/joueur/**").hasAnyRole("ADMIN", "COACH", "JOUEUR")
                // Tous les autres endpoints nécessitent une authentification
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Pour H2 Console
        http.headers(headers -> headers.frameOptions().disable());

        return http.build();
    }
}
