package com.fluxfinanciers.repository;

import com.fluxfinanciers.entity.Employe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeRepository extends JpaRepository<Employe, Long> {
}
