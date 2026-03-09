package com.fluxfinanciers.repository;

import com.fluxfinanciers.entity.PaiementEmploye;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PaiementEmployeRepository extends JpaRepository<PaiementEmploye, Long> {
    List<PaiementEmploye> findByDatePaiementBetween(LocalDate dateDebut, LocalDate dateFin);
}
