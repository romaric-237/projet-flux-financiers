package com.fluxfinanciers.mapper;

import com.fluxfinanciers.dto.request.VersementRequest;
import com.fluxfinanciers.dto.response.VersementResponse;
import com.fluxfinanciers.entity.Client;
import com.fluxfinanciers.entity.Versement;

public final class VersementMapper {

    private VersementMapper() {}

    public static Versement toEntity(VersementRequest request, Client client) {
        Versement versement = new Versement();
        versement.setClient(client);
        versement.setMontantTTC(request.getMontantTTC());
        versement.setDateVersement(request.getDateVersement());
        versement.setModePaiement(request.getModePaiement());
        versement.setRemarque(request.getRemarque());
        return versement;
    }

    public static VersementResponse toResponse(Versement versement) {
        VersementResponse response = new VersementResponse();
        response.setId(versement.getId());
        response.setClientId(versement.getClient().getId());
        response.setClientNom(versement.getClient().getFullName());
        response.setMontantTTC(versement.getMontantTTC());
        response.setDateVersement(versement.getDateVersement());
        response.setModePaiement(versement.getModePaiement());
        response.setRemarque(versement.getRemarque());
        return response;
    }
}
