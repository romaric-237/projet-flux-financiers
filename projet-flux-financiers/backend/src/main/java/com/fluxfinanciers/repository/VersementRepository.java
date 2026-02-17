package com.fluxfinanciers.repository;

import com.fluxfinanciers.entity.Versement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VersementRepository extends JpaRepository<Versement, Long> {
}
