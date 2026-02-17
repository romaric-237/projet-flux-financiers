package com.fluxfinanciers.controller;

import com.fluxfinanciers.dto.request.PaiementEmployeRequest;
import com.fluxfinanciers.dto.response.PaiementEmployeResponse;
import com.fluxfinanciers.mapper.PaiementEmployeMapper;
import com.fluxfinanciers.service.PaiementEmployeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paiement-employes")
@RequiredArgsConstructor
public class PaiementEmployeController {

    private final PaiementEmployeService paiementEmployeService;

    @GetMapping
    public ResponseEntity<List<PaiementEmployeResponse>> getAll() {
        List<PaiementEmployeResponse> responses = paiementEmployeService.findAll().stream()
                .map(PaiementEmployeMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaiementEmployeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(PaiementEmployeMapper.toResponse(paiementEmployeService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<PaiementEmployeResponse> create(@Valid @RequestBody PaiementEmployeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PaiementEmployeMapper.toResponse(paiementEmployeService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaiementEmployeResponse> update(@PathVariable Long id, @Valid @RequestBody PaiementEmployeRequest request) {
        return ResponseEntity.ok(PaiementEmployeMapper.toResponse(paiementEmployeService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paiementEmployeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
