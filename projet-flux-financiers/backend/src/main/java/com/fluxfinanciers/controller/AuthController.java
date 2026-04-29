package com.fluxfinanciers.controller;

import com.fluxfinanciers.dto.request.LoginRequest;
import com.fluxfinanciers.dto.response.AuthResponse;
import com.fluxfinanciers.entity.User;
import com.fluxfinanciers.repository.UserRepository;
import com.fluxfinanciers.security.JwtService;
import com.fluxfinanciers.security.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String role = user.getRole().name();

        return ResponseEntity.ok(new AuthResponse(user.getId(), token, user.getUsername(), user.getNom(), user.getPrenom(), role));
    }
}