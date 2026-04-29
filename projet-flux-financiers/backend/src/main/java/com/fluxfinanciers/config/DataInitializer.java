package com.fluxfinanciers.config;

import com.fluxfinanciers.entity.User;
import com.fluxfinanciers.enums.Role;
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
            admin.setNom("Administrateur");
            admin.setPrenom("Système");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setActif(true);
            userRepository.save(admin);
            log.info("Utilisateur admin créé (mot de passe: admin123)");
        } else {
            User admin = existing.get();
            admin.setRole(Role.ADMIN);
            admin.setActif(true);
            if (admin.getNom() == null) admin.setNom("Administrateur");
            if (admin.getPrenom() == null) admin.setPrenom("Système");
            userRepository.save(admin);
            log.info("Compte admin mis à jour");
        }
    }
}