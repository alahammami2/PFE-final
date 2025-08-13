package com.volleyball.authservice.service;

import com.volleyball.authservice.model.User;
import com.volleyball.authservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Tentative de chargement de l'utilisateur avec l'email: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé avec l'email: {}", email);
                    return new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email);
                });

        if (!user.getActif()) {
            logger.error("Utilisateur inactif avec l'email: {}", email);
            throw new UsernameNotFoundException("Utilisateur inactif: " + email);
        }

        logger.debug("Utilisateur trouvé: {} avec le rôle: {}", email, user.getRole());
        return user;
    }
}
