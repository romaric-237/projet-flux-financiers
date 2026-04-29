package com.fluxfinanciers.service;

import com.fluxfinanciers.dto.request.VersementRequest;
import com.fluxfinanciers.entity.Client;
import com.fluxfinanciers.entity.Versement;
import com.fluxfinanciers.enums.ActionAudit;
import com.fluxfinanciers.exception.ResourceNotFoundException;
import com.fluxfinanciers.mapper.VersementMapper;
import com.fluxfinanciers.repository.ClientRepository;
import com.fluxfinanciers.repository.VersementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VersementService {

    private final VersementRepository versementRepository;
    private final ClientRepository clientRepository;
    private final AuditLogService auditLogService;

    public List<Versement> findAll() {
        return versementRepository.findAll();
    }

    public Versement findById(Long id) {
        return versementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Versement", id));
    }

    @Transactional
    public Versement create(VersementRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", request.getClientId()));
        Versement versement = VersementMapper.toEntity(request, client);
        Versement saved = versementRepository.save(versement);
        auditLogService.log(ActionAudit.CREATION, "Versement", saved.getId(),
                "Versement créé: " + saved.getMontant() + "€ pour " + client.getFullName());
        return saved;
    }

    @Transactional
    public Versement update(Long id, VersementRequest request) {
        Versement existing = findById(id);
        if (!existing.getClient().getId().equals(request.getClientId())) {
            Client client = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Client", request.getClientId()));
            existing.setClient(client);
        }
        existing.setMontant(request.getMontant());
        existing.setDate(request.getDate());
        existing.setModePaiement(request.getModePaiement());
        existing.setRemarque(request.getRemarque());
        Versement saved = versementRepository.save(existing);
        auditLogService.log(ActionAudit.MODIFICATION, "Versement", id, "Versement modifié: " + saved.getMontant() + "€");
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Versement existing = findById(id);
        versementRepository.delete(existing);
        auditLogService.log(ActionAudit.SUPPRESSION, "Versement", id, "Versement supprimé");
    }
}