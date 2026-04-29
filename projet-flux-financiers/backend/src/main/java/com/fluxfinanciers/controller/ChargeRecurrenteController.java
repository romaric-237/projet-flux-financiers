package com.fluxfinanciers.controller;

import com.fluxfinanciers.dto.request.ChargeRecurrenteRequest;
import com.fluxfinanciers.dto.response.ChargeRecurrenteResponse;
import com.fluxfinanciers.entity.ChargeRecurrente;
import com.fluxfinanciers.service.ChargeRecurrenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/charges-recurrentes")
@RequiredArgsConstructor
public class ChargeRecurrenteController {

    private final ChargeRecurrenteService chargeRecurrenteService;

    @GetMapping
    public ResponseEntity<List<ChargeRecurrenteResponse>> getAll() {
        return ResponseEntity.ok(chargeRecurrenteService.findAll().stream().map(this::toResponse).toList());
    }

    @GetMapping("/actives")
    public ResponseEntity<List<ChargeRecurrenteResponse>> getActives() {
        return ResponseEntity.ok(chargeRecurrenteService.findActives().stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChargeRecurrenteResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(chargeRecurrenteService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ChargeRecurrenteResponse> create(@Valid @RequestBody ChargeRecurrenteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(chargeRecurrenteService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChargeRecurrenteResponse> update(@PathVariable Long id, @Valid @RequestBody ChargeRecurrenteRequest request) {
        return ResponseEntity.ok(toResponse(chargeRecurrenteService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        chargeRecurrenteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ChargeRecurrenteResponse toResponse(ChargeRecurrente cr) {
        ChargeRecurrenteResponse r = new ChargeRecurrenteResponse();
        r.setId(cr.getId());
        r.setMontantDefaut(cr.getMontantDefaut());
        r.setActif(cr.isActif());
        if (cr.getCharge() != null) {
            r.setChargeId(cr.getCharge().getId());
            r.setChargeLibelle(cr.getCharge().getLibelle());
        }
        if (cr.getCreatedBy() != null) {
            r.setCreatedById(cr.getCreatedBy().getId());
            r.setCreatedByUsername(cr.getCreatedBy().getUsername());
        }
        return r;
    }
}