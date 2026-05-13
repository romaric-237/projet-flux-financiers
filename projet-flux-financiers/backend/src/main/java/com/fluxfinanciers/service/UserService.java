package com.fluxfinanciers.service;

import com.fluxfinanciers.dto.request.ChangePasswordRequest;
import com.fluxfinanciers.dto.request.UserRequest;
import com.fluxfinanciers.entity.User;
import com.fluxfinanciers.enums.Role;
import com.fluxfinanciers.exception.ResourceNotFoundException;
import com.fluxfinanciers.mapper.UserMapper;
import com.fluxfinanciers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", -1L));
    }

    @Transactional
    public User create(UserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Le nom d'utilisateur '" + request.getUsername() + "' est déjà utilisé");
        }
        User user = UserMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public User update(Long id, UserRequest request) {
        User existing = findById(id);
        existing.setNom(request.getNom());
        existing.setPrenom(request.getPrenom());
        existing.setEmail(request.getEmail());
        existing.setUsername(request.getUsername());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            existing.setRole(request.getRole());
        }
        return userRepository.save(existing);
    }

    @Transactional
    public User desactiver(Long id) {
        User user = findById(id);
        // Empêcher la désactivation du dernier ADMIN
        if (user.getRole() == Role.ADMIN) {
            long nbAdmins = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.ADMIN && u.isActif())
                    .count();
            if (nbAdmins <= 1) {
                throw new IllegalStateException("Impossible de désactiver le dernier compte ADMIN");
            }
        }
        user.setActif(false);
        return userRepository.save(user);
    }

    @Transactional
    public User reactiver(Long id) {
        User user = findById(id);
        user.setActif(true);
        return userRepository.save(user);
    }

    @Transactional
    public void changerMotDePasse(ChangePasswordRequest request) {
        User user = getCurrentUser();
        if (!passwordEncoder.matches(request.getAncienMotDePasse(), user.getPassword())) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect");
        }
        if (!request.getNouveauMotDePasse().equals(request.getConfirmation())) {
            throw new IllegalArgumentException("La confirmation ne correspond pas au nouveau mot de passe");
        }
        user.setPassword(passwordEncoder.encode(request.getNouveauMotDePasse()));
        userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        User existing = findById(id);
        if (existing.getRole() == Role.ADMIN) {
            long nbAdmins = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.ADMIN && u.isActif())
                    .count();
            if (nbAdmins <= 1) {
                throw new IllegalStateException("Impossible de supprimer le dernier compte ADMIN");
            }
        }
        userRepository.delete(existing);
    }
}