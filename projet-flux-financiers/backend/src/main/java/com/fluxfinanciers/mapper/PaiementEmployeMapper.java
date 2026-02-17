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
        paiement.setTypePaiement(request.getTypePaiement());
        paiement.setModePaiement(request.getModePaiement());
        paiement.setMontant(request.getMontant());
        paiement.setDatePaiement(request.getDatePaiement());
        paiement.setRemarque(request.getRemarque());
        return paiement;
    }

    public static PaiementEmployeResponse toResponse(PaiementEmploye paiement) {
        PaiementEmployeResponse response = new PaiementEmployeResponse();
        response.setId(paiement.getId());
        response.setEmployeId(paiement.getEmploye().getId());
        response.setEmployeNom(paiement.getEmploye().getFullName());
        response.setTypePaiement(paiement.getTypePaiement());
        response.setModePaiement(paiement.getModePaiement());
        response.setMontant(paiement.getMontant());
        response.setDatePaiement(paiement.getDatePaiement());
        response.setRemarque(paiement.getRemarque());
        return response;
    }
}
