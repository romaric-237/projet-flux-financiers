package com.fluxfinanciers.mapper;

import com.fluxfinanciers.dto.request.PaiementEmployeRequest;
import com.fluxfinanciers.dto.response.PaiementEmployeResponse;
import com.fluxfinanciers.entity.Employe;
import com.fluxfinanciers.entity.PaiementEmploye;

public final class PaiementEmployeMapper {

    private PaiementEmployeMapper() {}

    public static PaiementEmploye toEntity(PaiementEmployeRequest request, Employe employe) {
        PaiementEmploye paiement = new PaiementEmploye();
        paiement.setEmploye(employe);
        paiement.setType(request.getType());
        paiement.setModePaiement(request.getModePaiement());
        paiement.setMontant(request.getMontant());
        paiement.setDate(request.getDate());
        paiement.setRemarque(request.getRemarque());
        return paiement;
    }

    public static PaiementEmployeResponse toResponse(PaiementEmploye paiement) {
        PaiementEmployeResponse response = new PaiementEmployeResponse();
        response.setId(paiement.getId());
        response.setEmployeId(paiement.getEmploye().getId());
        response.setEmployeNom(paiement.getEmploye().getFullName());
        response.setType(paiement.getType());
        response.setModePaiement(paiement.getModePaiement());
        response.setMontant(paiement.getMontant());
        response.setDatePaiement(paiement.getDate());
        response.setRemarque(paiement.getRemarque());
        return response;
    }
}