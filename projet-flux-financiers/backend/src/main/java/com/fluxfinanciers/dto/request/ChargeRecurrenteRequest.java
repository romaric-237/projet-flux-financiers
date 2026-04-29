package com.fluxfinanciers.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargeRecurrenteRequest {

    @NotNull(message = "La charge est obligatoire")
    private Long chargeId;

    @NotNull(message = "Le montant par défaut est obligatoire")
    @DecimalMin(value = "0.01")
    private BigDecimal montantDefaut;

    private boolean actif = true;

    @NotNull(message = "Le créateur est obligatoire")
    private Long createdById;
}