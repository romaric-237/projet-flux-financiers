package com.fluxfinanciers.controller;

import com.fluxfinanciers.dto.request.PaiementChargeRequest;
import com.fluxfinanciers.dto.response.PaiementChargeResponse;
import com.fluxfinanciers.mapper.PaiementChargeMapper;
import com.fluxfinanciers.service.PaiementChargeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paiement-charges")
@RequiredArgsConstructor
public class PaiementChargeController {

    private final PaiementChargeService paiementChargeService;

    @GetMapping
    public ResponseEntity<List<PaiementChargeResponse>> getAll() {
        List<PaiementChargeResponse> responses = paiementChargeService.findAll().stream()
                .map(PaiementChargeMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaiementChargeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(PaiementChargeMapper.toResponse(paiementChargeService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<PaiementChargeResponse> create(@Valid @RequestBody PaiementChargeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PaiementChargeMapper.toResponse(paiementChargeService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaiementChargeResponse> update(@PathVariable Long id, @Valid @RequestBody PaiementChargeRequest request) {
        return ResponseEntity.ok(PaiementChargeMapper.toResponse(paiementChargeService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paiementChargeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
