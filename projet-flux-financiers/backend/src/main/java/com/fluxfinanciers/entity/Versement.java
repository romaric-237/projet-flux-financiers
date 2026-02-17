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
 * Entité Versement - Recettes des clients
 */
@Entity
@Table(name = "versement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Versement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @NotNull(message = "Le client est obligatoire")
    private Client client;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    @Column(name = "montant_ttc", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTTC;

    @NotNull(message = "La date de versement est obligatoire")
    @PastOrPresent(message = "La date ne peut pas être dans le futur")
    @Column(name = "date_versement", nullable = false)
    private LocalDate dateVersement;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement")
    private ModePaiement modePaiement;

    @Size(max = 500, message = "La remarque ne peut pas dépasser 500 caractères")
    @Column(length = 500)
    private String remarque;
}