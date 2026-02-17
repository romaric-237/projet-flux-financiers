package com.fluxfinanciers.service;

import com.fluxfinanciers.dto.request.PaiementChargeRequest;
import com.fluxfinanciers.entity.Charge;
import com.fluxfinanciers.entity.PaiementCharge;
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
        return paiementChargeRepository.save(paiement);
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
        existing.setDatePaiement(request.getDatePaiement());
        existing.setModePaiement(request.getModePaiement());
        existing.setRemarque(request.getRemarque());
        return paiementChargeRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        PaiementCharge existing = findById(id);
        paiementChargeRepository.delete(existing);
    }
}
