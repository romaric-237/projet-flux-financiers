package com.fluxfinanciers.service;

import com.fluxfinanciers.dto.request.BudgetRequest;
import com.fluxfinanciers.entity.Budget;
import com.fluxfinanciers.entity.User;
import com.fluxfinanciers.enums.ActionAudit;
import com.fluxfinanciers.enums.CategorieCharge;
import com.fluxfinanciers.exception.ResourceNotFoundException;
import com.fluxfinanciers.repository.BudgetRepository;
import com.fluxfinanciers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public List<Budget> findAll() {
        return budgetRepository.findAll();
    }

    public List<Budget> findByMoisAnnee(int mois, int annee) {
        return budgetRepository.findByMoisAndAnnee(mois, annee);
    }

    public List<Budget> findByAnnee(int annee) {
        return budgetRepository.findByAnnee(annee);
    }

    public Budget findById(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", id));
    }

    @Transactional
    public Budget create(BudgetRequest request) {
        User createdBy = userRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getCreatedById()));
        Budget budget = new Budget();
        budget.setCategorie(request.getCategorie());
        budget.setMois(request.getMois());
        budget.setAnnee(request.getAnnee());
        budget.setMontantBudgete(request.getMontantBudgete());
        budget.setCreatedBy(createdBy);
        Budget saved = budgetRepository.save(budget);
        auditLogService.log(ActionAudit.CREATION, "Budget", saved.getId(),
                "Budget créé: " + saved.getCategorie() + " " + saved.getMois() + "/" + saved.getAnnee());
        return saved;
    }

    @Transactional
    public Budget update(Long id, BudgetRequest request) {
        Budget existing = findById(id);
        existing.setCategorie(request.getCategorie());
        existing.setMois(request.getMois());
        existing.setAnnee(request.getAnnee());
        existing.setMontantBudgete(request.getMontantBudgete());
        Budget saved = budgetRepository.save(existing);
        auditLogService.log(ActionAudit.MODIFICATION, "Budget", id, "Budget modifié");
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Budget existing = findById(id);
        budgetRepository.delete(existing);
        auditLogService.log(ActionAudit.SUPPRESSION, "Budget", id, "Budget supprimé");
    }
}