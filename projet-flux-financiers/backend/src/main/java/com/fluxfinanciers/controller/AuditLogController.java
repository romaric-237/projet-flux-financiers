package com.fluxfinanciers.controller;

import com.fluxfinanciers.dto.response.AuditLogResponse;
import com.fluxfinanciers.entity.AuditLog;
import com.fluxfinanciers.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getAll() {
        return ResponseEntity.ok(
                auditLogService.findForCurrentUserOrAll().stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    private AuditLogResponse toResponse(AuditLog log) {
        AuditLogResponse r = new AuditLogResponse();
        r.setId(log.getId());
        r.setAction(log.getAction());
        r.setEntiteType(log.getEntiteType());
        r.setEntiteId(log.getEntiteId());
        r.setDetails(log.getDetails());
        r.setDateAction(log.getDateAction());
        r.setUserId(log.getUser().getId());
        r.setUsername(log.getUser().getUsername());
        return r;
    }
}