package com.fluxfinanciers.dto.response;

import com.fluxfinanciers.enums.CategorieCharge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetResponse {

    private Long id;
    private CategorieCharge categorie;
    private int mois;
    private int annee;
    private BigDecimal montantBudgete;
    private Long createdById;
    private String createdByUsername;
}