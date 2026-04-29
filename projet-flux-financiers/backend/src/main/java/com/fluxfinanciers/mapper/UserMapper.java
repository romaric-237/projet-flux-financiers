package com.fluxfinanciers.mapper;

import com.fluxfinanciers.dto.request.UserRequest;
import com.fluxfinanciers.dto.response.UserResponse;
import com.fluxfinanciers.entity.User;
import com.fluxfinanciers.enums.Role;

public final class UserMapper {

    private UserMapper() {}

    public static User toEntity(UserRequest request) {
        User user = new User();
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole() != null ? request.getRole() : Role.GESTIONNAIRE);
        user.setActif(true);
        return user;
    }

    public static UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setNom(user.getNom());
        response.setPrenom(user.getPrenom());
        response.setEmail(user.getEmail());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setActif(user.isActif());
        return response;
    }
}