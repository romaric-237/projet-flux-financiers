package com.fluxfinanciers.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private Long id;
    private String token;
    private String username;
    private String nom;
    private String prenom;
    private String role;
}