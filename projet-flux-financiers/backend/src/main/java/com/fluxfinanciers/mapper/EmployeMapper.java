package com.fluxfinanciers.mapper;

import com.fluxfinanciers.dto.request.EmployeRequest;
import com.fluxfinanciers.dto.response.EmployeResponse;
import com.fluxfinanciers.entity.Employe;
import com.fluxfinanciers.entity.User;
import com.fluxfinanciers.enums.StatutEmploye;

public final class EmployeMapper {

    private EmployeMapper() {}

    public static Employe toEntity(EmployeRequest request, User createdBy) {
        Employe employe = new Employe();
        employe.setNom(request.getNom());
        employe.setPrenom(request.getPrenom());
        employe.setStatut(request.getStatut() != null ? request.getStatut() : StatutEmploye.ACTIF);
        employe.setCreatedBy(createdBy);
        return employe;
    }

    public static EmployeResponse toResponse(Employe employe) {
        EmployeResponse response = new EmployeResponse();
        response.setId(employe.getId());
        response.setNom(employe.getNom());
        response.setPrenom(employe.getPrenom());
        response.setStatut(employe.getStatut());
        response.setCreatedAt(employe.getCreatedAt());
        response.setCreatedById(employe.getCreatedBy().getId());
        response.setCreatedByUsername(employe.getCreatedBy().getUsername());
        return response;
    }
}
