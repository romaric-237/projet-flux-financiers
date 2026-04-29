package com.fluxfinanciers.entity;

import com.fluxfinanciers.enums.TypePaiement;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "paiement_employe")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class PaiementEmploye extends AbstractPaiement {

    @NotNull(message = "L'employé est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @NotNull(message = "Le type de paiement est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_paiement", nullable = false)
    private TypePaiement type;

    @Size(max = 500)
    @Column(length = 500)
    private String remarque;
}