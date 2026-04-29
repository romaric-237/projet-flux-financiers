package com.fluxfinanciers.mapper;

import com.fluxfinanciers.dto.request.ClientRequest;
import com.fluxfinanciers.dto.response.ClientResponse;
import com.fluxfinanciers.entity.Client;
import com.fluxfinanciers.entity.User;

public final class ClientMapper {

    private ClientMapper() {}

    public static Client toEntity(ClientRequest request, User createdBy) {
        Client client = new Client();
        client.setNom(request.getNom());
        client.setPrenom(request.getPrenom());
        client.setEmail(request.getEmail());
        client.setTelephone(request.getTelephone());
        client.setCreatedBy(createdBy);
        return client;
    }

    public static ClientResponse toResponse(Client client) {
        ClientResponse response = new ClientResponse();
        response.setId(client.getId());
        response.setNom(client.getNom());
        response.setPrenom(client.getPrenom());
        response.setEmail(client.getEmail());
        response.setTelephone(client.getTelephone());
        response.setDateCreation(client.getDateCreation());
        if (client.getCreatedBy() != null) {
            response.setCreatedById(client.getCreatedBy().getId());
            response.setCreatedByUsername(client.getCreatedBy().getUsername());
        }
        return response;
    }
}