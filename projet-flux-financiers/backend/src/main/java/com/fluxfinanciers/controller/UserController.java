package com.fluxfinanciers.controller;

import com.fluxfinanciers.dto.request.ChangePasswordRequest;
import com.fluxfinanciers.dto.request.UserRequest;
import com.fluxfinanciers.dto.response.UserResponse;
import com.fluxfinanciers.mapper.UserMapper;
import com.fluxfinanciers.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(userService.findAll().stream().map(UserMapper::toResponse).toList());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        return ResponseEntity.ok(UserMapper.toResponse(userService.getCurrentUser()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(UserMapper.toResponse(userService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserMapper.toResponse(userService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(UserMapper.toResponse(userService.update(id, request)));
    }

    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<UserResponse> desactiver(@PathVariable Long id) {
        return ResponseEntity.ok(UserMapper.toResponse(userService.desactiver(id)));
    }

    @PatchMapping("/{id}/reactiver")
    public ResponseEntity<UserResponse> reactiver(@PathVariable Long id) {
        return ResponseEntity.ok(UserMapper.toResponse(userService.reactiver(id)));
    }

    @PostMapping("/changer-mot-de-passe")
    public ResponseEntity<Void> changerMotDePasse(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changerMotDePasse(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}