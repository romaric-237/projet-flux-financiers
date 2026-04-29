package com.fluxfinanciers.service;

import com.fluxfinanciers.dto.request.PaiementEmployeRequest;
import com.fluxfinanciers.entity.Employe;
import com.fluxfinanciers.entity.PaiementEmploye;
import com.fluxfinanciers.enums.ActionAudit;
import com.fluxfinanciers.enums.StatutEmploye;
import com.fluxfinanciers.exception.ResourceNotFoundException;
import com.fluxfinanciers.mapper.PaiementEmployeMapper;
import com.fluxfinanciers.repository.EmployeRepository;
import com.fluxfinanciers.repository.PaiementEmployeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaiementEmployeService {

    private final PaiementEmployeRepository paiementEmployeRepository;
    private final EmployeRepository employeRepository;
    private final AuditLogService auditLogService;

    public List<PaiementEmploye> findAll() {
        return paiementEmployeRepository.findAll();
    }

    public PaiementEmploye findById(Long id) {
        return paiementEmployeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PaiementEmploye", id));
    }

    @Transactional
    public PaiementEmploye create(PaiementEmployeRequest request) {
        Employe employe = employeRepository.findById(request.getEmployeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employe", request.getEmployeId()));
        if (employe.getStatut() != StatutEmploye.ACTIF) {
            throw new IllegalStateException("Impossible d'enregistrer un paiement pour un employé INACTIF");
        }
        PaiementEmploye paiement = PaiementEmployeMapper.toEntity(request, employe);
        PaiementEmploye saved = paiementEmployeRepository.save(paiement);
        auditLogService.log(ActionAudit.CREATION, "PaiementEmploye", saved.getId(),
                "Paiement créé: " + saved.getMontant() + "€ pour " + employe.getFullName());
        return saved;
    }

    @Transactional
    public PaiementEmploye update(Long id, PaiementEmployeRequest request) {
        PaiementEmploye existing = findById(id);
        if (!existing.getEmploye().getId().equals(request.getEmployeId())) {
            Employe employe = employeRepository.findById(request.getEmployeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employe", request.getEmployeId()));
            existing.setEmploye(employe);
        }
        existing.setType(request.getType());
        existing.setModePaiement(request.getModePaiement());
        existing.setMontant(request.getMontant());
        existing.setDate(request.getDate());
        existing.setRemarque(request.getRemarque());
        PaiementEmploye saved = paiementEmployeRepository.save(existing);
        auditLogService.log(ActionAudit.MODIFICATION, "PaiementEmploye", id, "Paiement modifié: " + saved.getMontant() + "€");
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        PaiementEmploye existing = findById(id);
        paiementEmployeRepository.delete(existing);
        auditLogService.log(ActionAudit.SUPPRESSION, "PaiementEmploye", id, "Paiement supprimé");
    }
}