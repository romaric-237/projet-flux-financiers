package com.fluxfinanciers.service;

import com.fluxfinanciers.dto.request.ClientRequest;
import com.fluxfinanciers.entity.Client;
import com.fluxfinanciers.entity.User;
import com.fluxfinanciers.exception.ResourceNotFoundException;
import com.fluxfinanciers.mapper.ClientMapper;
import com.fluxfinanciers.repository.ClientRepository;
import com.fluxfinanciers.repository.UserRepository;
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
        return clientRepository.save(client);
    }

    @Transactional
    public Client update(Long id, ClientRequest request) {
        Client existing = findById(id);
        existing.setNom(request.getNom());
        existing.setPrenom(request.getPrenom());
        return clientRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Client existing = findById(id);
        clientRepository.delete(existing);
    }
}
