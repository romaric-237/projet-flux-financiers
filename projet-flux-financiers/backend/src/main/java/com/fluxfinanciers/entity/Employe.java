package com.fluxfinanciers.entity;

import com.fluxfinanciers.enums.StatutEmploye;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employe")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Employe extends Personne {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEmploye statut = StatutEmploye.ACTIF;

    @NotNull(message = "Le créateur est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "employe")
    private List<PaiementEmploye> paiements = new ArrayList<>();

    public String getFullName() {
        return getPrenom() + " " + getNom();
    }

    public void desactiver() {
        this.statut = StatutEmploye.INACTIF;
    }

    public BigDecimal getTotalPaiements() {
        return paiements.stream()
                .map(PaiementEmploye::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean estSupprimable() {
        return paiements.isEmpty();
    }

    public boolean isActif() {
        return this.statut == StatutEmploye.ACTIF;
    }
}