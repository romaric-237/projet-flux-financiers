package com.fluxfinanciers.dto.request;

import com.fluxfinanciers.enums.CategorieCharge;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeuilBudgetRequest {

    @NotNull(message = "La catégorie est obligatoire")
    private CategorieCharge categorie;

    @NotNull(message = "Le plafond est obligatoire")
    @DecimalMin(value = "0.01")
    private BigDecimal plafond;

    @NotNull(message = "Le créateur est obligatoire")
    private Long createdById;
}