package com.fluxfinanciers.service;

import com.fluxfinanciers.dto.request.EmployeRequest;
import com.fluxfinanciers.entity.Employe;
import com.fluxfinanciers.entity.User;
import com.fluxfinanciers.exception.ResourceNotFoundException;
import com.fluxfinanciers.mapper.EmployeMapper;
import com.fluxfinanciers.repository.EmployeRepository;
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
        return employeRepository.save(employe);
    }

    @Transactional
    public Employe update(Long id, EmployeRequest request) {
        Employe existing = findById(id);
        existing.setNom(request.getNom());
        existing.setPrenom(request.getPrenom());
        existing.setStatut(request.getStatut());
        return employeRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Employe existing = findById(id);
        employeRepository.delete(existing);
    }
}
