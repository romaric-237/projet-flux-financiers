package com.fluxfinanciers.repository;

import com.fluxfinanciers.entity.PaiementCharge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PaiementChargeRepository extends JpaRepository<PaiementCharge, Long> {
    List<PaiementCharge> findByDateBetween(LocalDate dateDebut, LocalDate dateFin);
}