package com.fluxfinanciers.repository;

import com.fluxfinanciers.entity.Versement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface VersementRepository extends JpaRepository<Versement, Long> {
    List<Versement> findByDateBetween(LocalDate dateDebut, LocalDate dateFin);
    boolean existsByClientId(Long clientId);
}