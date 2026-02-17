package com.fluxfinanciers.service;

import com.fluxfinanciers.dto.request.PaiementEmployeRequest;
import com.fluxfinanciers.entity.Employe;
import com.fluxfinanciers.entity.PaiementEmploye;
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
        PaiementEmploye paiement = PaiementEmployeMapper.toEntity(request, employe);
        return paiementEmployeRepository.save(paiement);
    }

    @Transactional
    public PaiementEmploye update(Long id, PaiementEmployeRequest request) {
        PaiementEmploye existing = findById(id);
        if (!existing.getEmploye().getId().equals(request.getEmployeId())) {
            Employe employe = employeRepository.findById(request.getEmployeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employe", request.getEmployeId()));
            existing.setEmploye(employe);
        }
        existing.setTypePaiement(request.getTypePaiement());
        existing.setModePaiement(request.getModePaiement());
        existing.setMontant(request.getMontant());
        existing.setDatePaiement(request.getDatePaiement());
        existing.setRemarque(request.getRemarque());
        return paiementEmployeRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        PaiementEmploye existing = findById(id);
        paiementEmployeRepository.delete(existing);
    }
}
