package com.fluxfinanciers.repository;

import com.fluxfinanciers.entity.Charge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargeRepository extends JpaRepository<Charge, Long> {
}