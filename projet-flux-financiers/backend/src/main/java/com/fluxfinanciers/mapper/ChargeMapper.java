package com.fluxfinanciers.mapper;

import com.fluxfinanciers.dto.request.ChargeRequest;
import com.fluxfinanciers.dto.response.ChargeResponse;
import com.fluxfinanciers.entity.Charge;
import com.fluxfinanciers.entity.User;

public final class ChargeMapper {

    private ChargeMapper() {}

    public static Charge toEntity(ChargeRequest request, User createdBy) {
        Charge charge = new Charge();
        charge.setNomCharge(request.getNomCharge());
        charge.setTypeCharge(request.getTypeCharge());
        charge.setCreatedBy(createdBy);
        return charge;
    }

    public static ChargeResponse toResponse(Charge charge) {
        ChargeResponse response = new ChargeResponse();
        response.setId(charge.getId());
        response.setNomCharge(charge.getNomCharge());
        response.setTypeCharge(charge.getTypeCharge());
        response.setCreatedAt(charge.getCreatedAt());
        response.setCreatedById(charge.getCreatedBy().getId());
        response.setCreatedByUsername(charge.getCreatedBy().getUsername());
        return response;
    }
}
