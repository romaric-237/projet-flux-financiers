package com.fluxfinanciers.controller;

import com.fluxfinanciers.dto.request.EmployeRequest;
import com.fluxfinanciers.dto.response.EmployeResponse;
import com.fluxfinanciers.mapper.EmployeMapper;
import com.fluxfinanciers.service.EmployeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employes")
@RequiredArgsConstructor
public class EmployeController {

    private final EmployeService employeService;

    @GetMapping
    public ResponseEntity<List<EmployeResponse>> getAll() {
        List<EmployeResponse> responses = employeService.findAll().stream()
                .map(EmployeMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(EmployeMapper.toResponse(employeService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<EmployeResponse> create(@Valid @RequestBody EmployeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EmployeMapper.toResponse(employeService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeResponse> update(@PathVariable Long id, @Valid @RequestBody EmployeRequest request) {
        return ResponseEntity.ok(EmployeMapper.toResponse(employeService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
