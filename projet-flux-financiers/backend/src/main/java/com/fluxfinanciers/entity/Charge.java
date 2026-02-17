package com.fluxfinanciers.entity;

import com.fluxfinanciers.enums.TypeCharge;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité Charge - Référentiel des charges
 */
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

    @NotBlank(message = "Le nom de la charge est obligatoire")
    @Size(max = 200, message = "Le nom ne peut pas dépasser 200 caractères")
    @Column(name = "nom_charge", nullable = false, length = 200)
    private String nomCharge;

    @NotNull(message = "Le type de charge est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_charge", nullable = false)
    private TypeCharge typeCharge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Relation OneToMany avec PaiementCharge
    @OneToMany(mappedBy = "charge", cascade = CascadeType.ALL)
    private List<PaiementCharge> paiements = new ArrayList<>();
}