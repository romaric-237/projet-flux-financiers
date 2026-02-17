package com.fluxfinanciers.dto.response;

import com.fluxfinanciers.enums.StatutEmploye;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeResponse {

    private Long id;
    private String nom;
    private String prenom;
    private StatutEmploye statut;
    private LocalDateTime createdAt;
    private Long createdById;
    private String createdByUsername;
}
