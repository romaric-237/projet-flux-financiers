package com.fluxfinanciers.repository;

import com.fluxfinanciers.entity.ChargeRecurrente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChargeRecurrenteRepository extends JpaRepository<ChargeRecurrente, Long> {
    List<ChargeRecurrente> findByActifTrue();
    List<ChargeRecurrente> findByChargeId(Long chargeId);
}