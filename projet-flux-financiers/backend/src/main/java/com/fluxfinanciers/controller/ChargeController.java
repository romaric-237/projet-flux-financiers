package com.fluxfinanciers.controller;

import com.fluxfinanciers.dto.request.ChargeRequest;
import com.fluxfinanciers.dto.response.ChargeResponse;
import com.fluxfinanciers.mapper.ChargeMapper;
import com.fluxfinanciers.service.ChargeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/charges")
@RequiredArgsConstructor
public class ChargeController {

    private final ChargeService chargeService;

    @GetMapping
    public ResponseEntity<List<ChargeResponse>> getAll() {
        List<ChargeResponse> responses = chargeService.findAll().stream()
                .map(ChargeMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChargeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ChargeMapper.toResponse(chargeService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ChargeResponse> create(@Valid @RequestBody ChargeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ChargeMapper.toResponse(chargeService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChargeResponse> update(@PathVariable Long id, @Valid @RequestBody ChargeRequest request) {
        return ResponseEntity.ok(ChargeMapper.toResponse(chargeService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        chargeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
