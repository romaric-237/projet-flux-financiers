package com.fluxfinanciers.mapper;

import com.fluxfinanciers.dto.request.PaiementChargeRequest;
import com.fluxfinanciers.dto.response.PaiementChargeResponse;
import com.fluxfinanciers.entity.Charge;
import com.fluxfinanciers.entity.PaiementCharge;

public final class PaiementChargeMapper {

    private PaiementChargeMapper() {}

    public static PaiementCharge toEntity(PaiementChargeRequest request, Charge charge) {
        PaiementCharge paiement = new PaiementCharge();
        paiement.setCharge(charge);
        paiement.setMontant(request.getMontant());
        paiement.setDate(request.getDate());
        paiement.setModePaiement(request.getModePaiement());
        paiement.setRemarque(request.getRemarque());
        return paiement;
    }

    public static PaiementChargeResponse toResponse(PaiementCharge paiement) {
        PaiementChargeResponse response = new PaiementChargeResponse();
        response.setId(paiement.getId());
        response.setChargeId(paiement.getCharge().getId());
        response.setChargeLibelle(paiement.getCharge().getLibelle());
        response.setMontant(paiement.getMontant());
        response.setDate(paiement.getDate());
        response.setModePaiement(paiement.getModePaiement());
        response.setRemarque(paiement.getRemarque());
        return response;
    }
}