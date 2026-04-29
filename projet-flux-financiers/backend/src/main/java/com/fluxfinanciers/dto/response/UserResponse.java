package com.fluxfinanciers.dto.response;

import com.fluxfinanciers.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String username;
    private Role role;
    private boolean actif;
}