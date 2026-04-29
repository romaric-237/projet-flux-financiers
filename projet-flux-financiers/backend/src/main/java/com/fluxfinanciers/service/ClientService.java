package com.fluxfinanciers.service;

import com.fluxfinanciers.dto.request.ClientRequest;
import com.fluxfinanciers.entity.Client;
import com.fluxfinanciers.entity.User;
import com.fluxfinanciers.enums.ActionAudit;
import com.fluxfinanciers.exception.ResourceNotFoundException;
import com.fluxfinanciers.mapper.ClientMapper;
import com.fluxfinanciers.repository.ClientRepository;
import com.fluxfinanciers.repository.UserRepository;
import com.fluxfinanciers.repository.VersementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final VersementRepository versementRepository;
    private final AuditLogService auditLogService;

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
    }

    @Transactional
    public Client create(ClientRequest request) {
        User createdBy = userRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getCreatedById()));
        Client client = ClientMapper.toEntity(request, createdBy);
        Client saved = clientRepository.save(client);
        auditLogService.log(ActionAudit.CREATION, "Client", saved.getId(), "Client créé: " + saved.getFullName());
        return saved;
    }

    @Transactional
    public Client update(Long id, ClientRequest request) {
        Client existing = findById(id);
        existing.setNom(request.getNom());
        existing.setPrenom(request.getPrenom());
        existing.setEmail(request.getEmail());
        existing.setTelephone(request.getTelephone());
        Client saved = clientRepository.save(existing);
        auditLogService.log(ActionAudit.MODIFICATION, "Client", id, "Client modifié: " + saved.getFullName());
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Client existing = findById(id);
        if (versementRepository.existsByClientId(id)) {
            throw new IllegalStateException("Impossible de supprimer un client ayant des versements associés.");
        }
        clientRepository.delete(existing);
        auditLogService.log(ActionAudit.SUPPRESSION, "Client", id, "Client supprimé: " + existing.getFullName());
    }
}