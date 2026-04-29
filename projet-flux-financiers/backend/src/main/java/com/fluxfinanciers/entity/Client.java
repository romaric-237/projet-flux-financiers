package com.fluxfinanciers.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "client")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Client extends Personne {

    @Size(max = 20)
    @Column(length = 20)
    private String telephone;

    @NotNull(message = "Le créateur est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "client")
    private List<Versement> versements = new ArrayList<>();

    public String getFullName() {
        return getPrenom() + " " + getNom();
    }

    public BigDecimal getTotalVersements() {
        return versements.stream()
                .map(Versement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getNombreVersements() {
        return versements.size();
    }
}