package com.fluxfinanciers.service;

import com.fluxfinanciers.dto.request.ChargeRecurrenteRequest;
import com.fluxfinanciers.entity.Charge;
import com.fluxfinanciers.entity.ChargeRecurrente;
import com.fluxfinanciers.entity.User;
import com.fluxfinanciers.enums.ActionAudit;
import com.fluxfinanciers.exception.ResourceNotFoundException;
import com.fluxfinanciers.repository.ChargeRecurrenteRepository;
import com.fluxfinanciers.repository.ChargeRepository;
import com.fluxfinanciers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChargeRecurrenteService {

    private final ChargeRecurrenteRepository chargeRecurrenteRepository;
    private final ChargeRepository chargeRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public List<ChargeRecurrente> findAll() {
        return chargeRecurrenteRepository.findAll();
    }

    public List<ChargeRecurrente> findActives() {
        return chargeRecurrenteRepository.findByActifTrue();
    }

    public ChargeRecurrente findById(Long id) {
        return chargeRecurrenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChargeRecurrente", id));
    }

    @Transactional
    public ChargeRecurrente create(ChargeRecurrenteRequest request) {
        Charge charge = chargeRepository.findById(request.getChargeId())
                .orElseThrow(() -> new ResourceNotFoundException("Charge", request.getChargeId()));
        User createdBy = userRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getCreatedById()));
        ChargeRecurrente cr = new ChargeRecurrente();
        cr.setCharge(charge);
        cr.setMontantDefaut(request.getMontantDefaut());
        cr.setActif(request.isActif());
        cr.setCreatedBy(createdBy);
        ChargeRecurrente saved = chargeRecurrenteRepository.save(cr);
        auditLogService.log(ActionAudit.CREATION, "ChargeRecurrente", saved.getId(),
                "Charge récurrente créée pour: " + charge.getLibelle());
        return saved;
    }

    @Transactional
    public ChargeRecurrente update(Long id, ChargeRecurrenteRequest request) {
        ChargeRecurrente existing = findById(id);
        existing.setMontantDefaut(request.getMontantDefaut());
        existing.setActif(request.isActif());
        ChargeRecurrente saved = chargeRecurrenteRepository.save(existing);
        auditLogService.log(ActionAudit.MODIFICATION, "ChargeRecurrente", id, "Charge récurrente modifiée");
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        ChargeRecurrente existing = findById(id);
        chargeRecurrenteRepository.delete(existing);
        auditLogService.log(ActionAudit.SUPPRESSION, "ChargeRecurrente", id, "Charge récurrente supprimée");
    }
}