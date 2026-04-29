package com.fluxfinanciers.entity;

import com.fluxfinanciers.enums.CategorieCharge;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "budget")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La catégorie est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategorieCharge categorie;

    @Min(1) @Max(12)
    @Column(nullable = false)
    private int mois;

    @Min(2000)
    @Column(nullable = false)
    private int annee;

    @NotNull(message = "Le montant budgété est obligatoire")
    @DecimalMin(value = "0.00")
    @Column(name = "montant_budgete", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantBudgete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    public BigDecimal calculerEcart(BigDecimal realise) {
        return montantBudgete.subtract(realise);
    }

    public String getCodeCouleur(BigDecimal realise) {
        BigDecimal ecart = calculerEcart(realise);
        if (ecart.compareTo(BigDecimal.ZERO) < 0) return "RED";
        if (ecart.compareTo(montantBudgete.multiply(new BigDecimal("0.10"))) < 0) return "ORANGE";
        return "GREEN";
    }
}