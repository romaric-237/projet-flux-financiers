package com.fluxfinanciers.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "versement")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverrides({
    @AttributeOverride(name = "montant", column = @Column(name = "montant_ttc", nullable = false, precision = 10, scale = 2)),
    @AttributeOverride(name = "date",    column = @Column(name = "date_versement", nullable = false))
})
public class Versement extends AbstractPaiement {

    @NotNull(message = "Le client est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Size(max = 500)
    @Column(length = 500)
    private String remarque;
}