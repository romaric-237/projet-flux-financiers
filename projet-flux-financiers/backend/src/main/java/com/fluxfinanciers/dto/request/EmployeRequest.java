package com.fluxfinanciers.dto.request;

import com.fluxfinanciers.enums.StatutEmploye;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100)
    private String prenom;

    @Email(message = "Email invalide")
    @Size(max = 150)
    private String email;

    private StatutEmploye statut;

    @NotNull(message = "Le créateur est obligatoire")
    private Long createdById;
}