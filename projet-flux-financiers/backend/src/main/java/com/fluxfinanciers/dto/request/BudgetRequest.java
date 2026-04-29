package com.fluxfinanciers.dto.request;

import com.fluxfinanciers.enums.CategorieCharge;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRequest {

    @NotNull(message = "La catégorie est obligatoire")
    private CategorieCharge categorie;

    @Min(1) @Max(12)
    private int mois;

    @Min(2000)
    private int annee;

    @NotNull(message = "Le montant budgété est obligatoire")
    @DecimalMin(value = "0.00")
    private BigDecimal montantBudgete;

    @NotNull(message = "Le créateur est obligatoire")
    private Long createdById;
}