package com.fluxfinanciers.service;

import com.fluxfinanciers.dto.request.ChargeRequest;
import com.fluxfinanciers.entity.Charge;
import com.fluxfinanciers.entity.User;
import com.fluxfinanciers.enums.ActionAudit;
import com.fluxfinanciers.exception.ResourceNotFoundException;
import com.fluxfinanciers.mapper.ChargeMapper;
import com.fluxfinanciers.repository.ChargeRepository;
import com.fluxfinanciers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChargeService {

    private final ChargeRepository chargeRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public List<Charge> findAll() {
        return chargeRepository.findAll();
    }

    public Charge findById(Long id) {
        return chargeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Charge", id));
    }

    @Transactional
    public Charge create(ChargeRequest request) {
        User createdBy = userRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getCreatedById()));
        Charge charge = ChargeMapper.toEntity(request, createdBy);
        Charge saved = chargeRepository.save(charge);
        auditLogService.log(ActionAudit.CREATION, "Charge", saved.getId(), "Charge créée: " + saved.getLibelle());
        return saved;
    }

    @Transactional
    public Charge update(Long id, ChargeRequest request) {
        Charge existing = findById(id);
        existing.setLibelle(request.getLibelle());
        existing.setCategorie(request.getCategorie());
        Charge saved = chargeRepository.save(existing);
        auditLogService.log(ActionAudit.MODIFICATION, "Charge", id, "Charge modifiée: " + saved.getLibelle());
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Charge existing = findById(id);
        chargeRepository.delete(existing);
        auditLogService.log(ActionAudit.SUPPRESSION, "Charge", id, "Charge supprimée: " + existing.getLibelle());
    }
}