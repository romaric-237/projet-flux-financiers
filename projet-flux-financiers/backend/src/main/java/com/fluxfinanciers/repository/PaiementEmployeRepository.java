package com.fluxfinanciers.repository;

import com.fluxfinanciers.entity.PaiementEmploye;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaiementEmployeRepository extends JpaRepository<PaiementEmploye, Long> {
}
