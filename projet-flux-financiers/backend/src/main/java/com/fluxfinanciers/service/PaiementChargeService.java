package com.fluxfinanciers.service;

import com.fluxfinanciers.dto.request.PaiementChargeRequest;
import com.fluxfinanciers.entity.Charge;
import com.fluxfinanciers.entity.PaiementCharge;
import com.fluxfinanciers.enums.ActionAudit;
import com.fluxfinanciers.exception.ResourceNotFoundException;
import com.fluxfinanciers.mapper.PaiementChargeMapper;
import com.fluxfinanciers.repository.ChargeRepository;
import com.fluxfinanciers.repository.PaiementChargeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaiementChargeService {

    private final PaiementChargeRepository paiementChargeRepository;
    private final ChargeRepository chargeRepository;
    private final AuditLogService auditLogService;

    public List<PaiementCharge> findAll() {
        return paiementChargeRepository.findAll();
    }

    public PaiementCharge findById(Long id) {
        return paiementChargeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PaiementCharge", id));
    }

    @Transactional
    public PaiementCharge create(PaiementChargeRequest request) {
        Charge charge = chargeRepository.findById(request.getChargeId())
                .orElseThrow(() -> new ResourceNotFoundException("Charge", request.getChargeId()));
        PaiementCharge paiement = PaiementChargeMapper.toEntity(request, charge);
        PaiementCharge saved = paiementChargeRepository.save(paiement);
        auditLogService.log(ActionAudit.CREATION, "PaiementCharge", saved.getId(),
                "Paiement charge créé: " + saved.getMontant() + "€ pour " + charge.getLibelle());
        return saved;
    }

    @Transactional
    public PaiementCharge update(Long id, PaiementChargeRequest request) {
        PaiementCharge existing = findById(id);
        if (!existing.getCharge().getId().equals(request.getChargeId())) {
            Charge charge = chargeRepository.findById(request.getChargeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Charge", request.getChargeId()));
            existing.setCharge(charge);
        }
        existing.setMontant(request.getMontant());
        existing.setDate(request.getDate());
        existing.setModePaiement(request.getModePaiement());
        existing.setRemarque(request.getRemarque());
        PaiementCharge saved = paiementChargeRepository.save(existing);
        auditLogService.log(ActionAudit.MODIFICATION, "PaiementCharge", id, "Paiement charge modifié: " + saved.getMontant() + "€");
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        PaiementCharge existing = findById(id);
        paiementChargeRepository.delete(existing);
        auditLogService.log(ActionAudit.SUPPRESSION, "PaiementCharge", id, "Paiement charge supprimé");
    }
}