package com.fluxfinanciers.entity;

import com.fluxfinanciers.enums.RoleUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité User - Utilisateur gestionnaire
 */
@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom d'utilisateur doit contenir entre 3 et 100 caractères")
    @Column(unique = true, nullable = false, length = 100)
    private String username;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleUser role = RoleUser.GESTIONNAIRE;

    // Relations (OneToMany)
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Client> clients = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Employe> employes = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Charge> charges = new ArrayList<>();
}