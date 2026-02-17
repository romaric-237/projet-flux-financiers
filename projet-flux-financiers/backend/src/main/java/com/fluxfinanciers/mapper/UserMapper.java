package com.fluxfinanciers.mapper;

import com.fluxfinanciers.dto.request.UserRequest;
import com.fluxfinanciers.dto.response.UserResponse;
import com.fluxfinanciers.entity.User;
import com.fluxfinanciers.enums.RoleUser;

public final class UserMapper {

    private UserMapper() {}

    public static User toEntity(UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole() != null ? request.getRole() : RoleUser.GESTIONNAIRE);
        return user;
    }

    public static UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        return response;
    }
}
