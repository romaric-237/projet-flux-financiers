package com.fluxfinanciers.service;

import com.fluxfinanciers.dto.request.ChargeRequest;
import com.fluxfinanciers.entity.Charge;
import com.fluxfinanciers.entity.User;
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
        return chargeRepository.save(charge);
    }

    @Transactional
    public Charge update(Long id, ChargeRequest request) {
        Charge existing = findById(id);
        existing.setNomCharge(request.getNomCharge());
        existing.setTypeCharge(request.getTypeCharge());
        return chargeRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Charge existing = findById(id);
        chargeRepository.delete(existing);
    }
}
