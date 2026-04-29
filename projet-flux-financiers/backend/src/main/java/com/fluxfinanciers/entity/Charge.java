package com.fluxfinanciers.entity;

import com.fluxfinanciers.enums.CategorieCharge;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "charge")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Charge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le libellé de la charge est obligatoire")
    @Size(max = 200)
    @Column(name = "nom_charge", nullable = false, length = 200)
    private String libelle;

    @NotNull(message = "La catégorie de charge est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_charge", nullable = false)
    private CategorieCharge categorie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "charge")
    private List<PaiementCharge> paiements = new ArrayList<>();

    public BigDecimal getTotalPaiements() {
        return paiements.stream()
                .map(PaiementCharge::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}