package com.fluxfinanciers.controller;

import com.fluxfinanciers.dto.request.VersementRequest;
import com.fluxfinanciers.dto.response.VersementResponse;
import com.fluxfinanciers.mapper.VersementMapper;
import com.fluxfinanciers.service.VersementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/versements")
@RequiredArgsConstructor
public class VersementController {

    private final VersementService versementService;

    @GetMapping
    public ResponseEntity<List<VersementResponse>> getAll() {
        List<VersementResponse> responses = versementService.findAll().stream()
                .map(VersementMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VersementResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(VersementMapper.toResponse(versementService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<VersementResponse> create(@Valid @RequestBody VersementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(VersementMapper.toResponse(versementService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VersementResponse> update(@PathVariable Long id, @Valid @RequestBody VersementRequest request) {
        return ResponseEntity.ok(VersementMapper.toResponse(versementService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        versementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
