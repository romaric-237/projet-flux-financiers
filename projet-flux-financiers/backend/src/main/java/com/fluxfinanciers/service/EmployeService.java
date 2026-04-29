package com.fluxfinanciers.service;

import com.fluxfinanciers.dto.request.EmployeRequest;
import com.fluxfinanciers.entity.Employe;
import com.fluxfinanciers.entity.User;
import com.fluxfinanciers.enums.ActionAudit;
import com.fluxfinanciers.exception.ResourceNotFoundException;
import com.fluxfinanciers.mapper.EmployeMapper;
import com.fluxfinanciers.repository.EmployeRepository;
import com.fluxfinanciers.repository.PaiementEmployeRepository;
import com.fluxfinanciers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeService {

    private final EmployeRepository employeRepository;
    private final UserRepository userRepository;
    private final PaiementEmployeRepository paiementEmployeRepository;
    private final AuditLogService auditLogService;

    public List<Employe> findAll() {
        return employeRepository.findAll();
    }

    public Employe findById(Long id) {
        return employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", id));
    }

    @Transactional
    public Employe create(EmployeRequest request) {
        User createdBy = userRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getCreatedById()));
        Employe employe = EmployeMapper.toEntity(request, createdBy);
        Employe saved = employeRepository.save(employe);
        auditLogService.log(ActionAudit.CREATION, "Employe", saved.getId(), "Employé créé: " + saved.getFullName());
        return saved;
    }

    @Transactional
    public Employe update(Long id, EmployeRequest request) {
        Employe existing = findById(id);
        existing.setNom(request.getNom());
        existing.setPrenom(request.getPrenom());
        existing.setEmail(request.getEmail());
        existing.setStatut(request.getStatut());
        Employe saved = employeRepository.save(existing);
        auditLogService.log(ActionAudit.MODIFICATION, "Employe", id, "Employé modifié: " + saved.getFullName());
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Employe existing = findById(id);
        if (paiementEmployeRepository.existsByEmployeId(id)) {
            throw new IllegalStateException("Impossible de supprimer un employé ayant des paiements. Passez-le en INACTIF.");
        }
        employeRepository.delete(existing);
        auditLogService.log(ActionAudit.SUPPRESSION, "Employe", id, "Employé supprimé: " + existing.getFullName());
    }
}