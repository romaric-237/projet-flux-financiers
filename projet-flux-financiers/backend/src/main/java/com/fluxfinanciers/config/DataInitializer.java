package com.fluxfinanciers.config;

import com.fluxfinanciers.entity.User;
import com.fluxfinanciers.enums.RoleUser;
import com.fluxfinanciers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        var existing = userRepository.findByUsername("admin");
        if (existing.isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(RoleUser.GESTIONNAIRE);
            userRepository.save(admin);
            log.info("Utilisateur admin créé (mot de passe: admin123)");
        } else {
            // Réinitialise le mot de passe pour garantir le bon hash BCrypt
            User admin = existing.get();
            admin.setPassword(passwordEncoder.encode("admin123"));
            userRepository.save(admin);
            log.info("Mot de passe admin réinitialisé (admin123)");
        }
    }
}
