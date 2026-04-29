package com.fluxfinanciers.entity;

import com.fluxfinanciers.enums.CategorieCharge;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "seuil_budget")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeuilBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La catégorie est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategorieCharge categorie;

    @NotNull(message = "Le plafond est obligatoire")
    @DecimalMin(value = "0.01", message = "Le plafond doit être supérieur à 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal plafond;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    public boolean verifierDepassement(BigDecimal total) {
        return total.compareTo(plafond) > 0;
    }

    public boolean estProcheDuSeuil(BigDecimal total) {
        BigDecimal seuil90 = plafond.multiply(new BigDecimal("0.90"));
        return total.compareTo(seuil90) >= 0 && total.compareTo(plafond) <= 0;
    }
}