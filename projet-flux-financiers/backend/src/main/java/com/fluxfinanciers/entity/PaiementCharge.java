package com.fluxfinanciers.entity;

import com.fluxfinanciers.enums.ModePaiement;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entité PaiementCharge - Paiements des charges
 */
@Entity
@Table(name = "paiement_charge")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaiementCharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_id", nullable = false)
    @NotNull(message = "La charge est obligatoire")
    private Charge charge;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @NotNull(message = "La date de paiement est obligatoire")
    @PastOrPresent(message = "La date ne peut pas être dans le futur")
    @Column(name = "date_paiement", nullable = false)
    private LocalDate datePaiement;

    @NotNull(message = "Le mode de paiement est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement", nullable = false)
    private ModePaiement modePaiement;

    @Size(max = 500, message = "La remarque ne peut pas dépasser 500 caractères")
    @Column(length = 500)
    private String remarque;
}