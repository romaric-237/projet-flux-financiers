package com.fluxfinanciers.repository;

import com.fluxfinanciers.entity.Budget;
import com.fluxfinanciers.enums.CategorieCharge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByMoisAndAnnee(int mois, int annee);
    Optional<Budget> findByCategorieAndMoisAndAnnee(CategorieCharge categorie, int mois, int annee);
    List<Budget> findByAnnee(int annee);
}