package com.fluxfinanciers.entity;

import com.fluxfinanciers.enums.ActionAudit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionAudit action;

    @Column(name = "entite_type", nullable = false, length = 100)
    private String entiteType;

    @Column(name = "entite_id")
    private Long entiteId;

    @Column(length = 500)
    private String details;

    @Column(name = "date_action", nullable = false)
    private LocalDateTime dateAction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}