package com.fluxfinanciers.dto.request;

import com.fluxfinanciers.enums.ModePaiement;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VersementRequest {

    @NotNull(message = "Le client est obligatoire")
    private Long clientId;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    private BigDecimal montantTTC;

    @NotNull(message = "La date de versement est obligatoire")
    private LocalDate dateVersement;

    private ModePaiement modePaiement;

    @Size(max = 500, message = "La remarque ne peut pas dépasser 500 caractères")
    private String remarque;
}
