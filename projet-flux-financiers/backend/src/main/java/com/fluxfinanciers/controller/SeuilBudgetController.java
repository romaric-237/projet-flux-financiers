package com.fluxfinanciers.controller;

import com.fluxfinanciers.dto.request.SeuilBudgetRequest;
import com.fluxfinanciers.dto.response.SeuilBudgetResponse;
import com.fluxfinanciers.entity.SeuilBudget;
import com.fluxfinanciers.service.SeuilBudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seuils-budget")
@RequiredArgsConstructor
public class SeuilBudgetController {

    private final SeuilBudgetService seuilBudgetService;

    @GetMapping
    public ResponseEntity<List<SeuilBudgetResponse>> getAll() {
        return ResponseEntity.ok(seuilBudgetService.findAll().stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeuilBudgetResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(seuilBudgetService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<SeuilBudgetResponse> create(@Valid @RequestBody SeuilBudgetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(seuilBudgetService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeuilBudgetResponse> update(@PathVariable Long id, @Valid @RequestBody SeuilBudgetRequest request) {
        return ResponseEntity.ok(toResponse(seuilBudgetService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        seuilBudgetService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private SeuilBudgetResponse toResponse(SeuilBudget s) {
        SeuilBudgetResponse r = new SeuilBudgetResponse();
        r.setId(s.getId());
        r.setCategorie(s.getCategorie());
        r.setPlafond(s.getPlafond());
        if (s.getCreatedBy() != null) {
            r.setCreatedById(s.getCreatedBy().getId());
            r.setCreatedByUsername(s.getCreatedBy().getUsername());
        }
        return r;
    }
}