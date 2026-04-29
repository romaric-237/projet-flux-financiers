package com.fluxfinanciers.repository;

import com.fluxfinanciers.entity.SeuilBudget;
import com.fluxfinanciers.enums.CategorieCharge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeuilBudgetRepository extends JpaRepository<SeuilBudget, Long> {
    Optional<SeuilBudget> findByCategorie(CategorieCharge categorie);
}