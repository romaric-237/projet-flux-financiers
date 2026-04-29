package com.fluxfinanciers.service;

import com.fluxfinanciers.dto.request.SeuilBudgetRequest;
import com.fluxfinanciers.entity.SeuilBudget;
import com.fluxfinanciers.entity.User;
import com.fluxfinanciers.enums.ActionAudit;
import com.fluxfinanciers.exception.ResourceNotFoundException;
import com.fluxfinanciers.repository.SeuilBudgetRepository;
import com.fluxfinanciers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeuilBudgetService {

    private final SeuilBudgetRepository seuilBudgetRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public List<SeuilBudget> findAll() {
        return seuilBudgetRepository.findAll();
    }

    public SeuilBudget findById(Long id) {
        return seuilBudgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SeuilBudget", id));
    }

    @Transactional
    public SeuilBudget create(SeuilBudgetRequest request) {
        User createdBy = userRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getCreatedById()));
        SeuilBudget seuil = new SeuilBudget();
        seuil.setCategorie(request.getCategorie());
        seuil.setPlafond(request.getPlafond());
        seuil.setCreatedBy(createdBy);
        SeuilBudget saved = seuilBudgetRepository.save(seuil);
        auditLogService.log(ActionAudit.CREATION, "SeuilBudget", saved.getId(),
                "Seuil budget créé: " + saved.getCategorie() + " → " + saved.getPlafond() + "€");
        return saved;
    }

    @Transactional
    public SeuilBudget update(Long id, SeuilBudgetRequest request) {
        SeuilBudget existing = findById(id);
        existing.setCategorie(request.getCategorie());
        existing.setPlafond(request.getPlafond());
        SeuilBudget saved = seuilBudgetRepository.save(existing);
        auditLogService.log(ActionAudit.MODIFICATION, "SeuilBudget", id, "Seuil budget modifié");
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        SeuilBudget existing = findById(id);
        seuilBudgetRepository.delete(existing);
        auditLogService.log(ActionAudit.SUPPRESSION, "SeuilBudget", id, "Seuil budget supprimé");
    }
}