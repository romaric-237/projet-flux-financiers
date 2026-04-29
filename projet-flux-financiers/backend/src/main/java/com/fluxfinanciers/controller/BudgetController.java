package com.fluxfinanciers.controller;

import com.fluxfinanciers.dto.request.BudgetRequest;
import com.fluxfinanciers.dto.response.BudgetResponse;
import com.fluxfinanciers.entity.Budget;
import com.fluxfinanciers.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAll() {
        return ResponseEntity.ok(budgetService.findAll().stream().map(this::toResponse).toList());
    }

    @GetMapping("/mois/{mois}/annee/{annee}")
    public ResponseEntity<List<BudgetResponse>> getByMoisAnnee(@PathVariable int mois, @PathVariable int annee) {
        return ResponseEntity.ok(budgetService.findByMoisAnnee(mois, annee).stream().map(this::toResponse).toList());
    }

    @GetMapping("/annee/{annee}")
    public ResponseEntity<List<BudgetResponse>> getByAnnee(@PathVariable int annee) {
        return ResponseEntity.ok(budgetService.findByAnnee(annee).stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(budgetService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> create(@Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(budgetService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> update(@PathVariable Long id, @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.ok(toResponse(budgetService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        budgetService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private BudgetResponse toResponse(Budget b) {
        BudgetResponse r = new BudgetResponse();
        r.setId(b.getId());
        r.setCategorie(b.getCategorie());
        r.setMois(b.getMois());
        r.setAnnee(b.getAnnee());
        r.setMontantBudgete(b.getMontantBudgete());
        if (b.getCreatedBy() != null) {
            r.setCreatedById(b.getCreatedBy().getId());
            r.setCreatedByUsername(b.getCreatedBy().getUsername());
        }
        return r;
    }
}