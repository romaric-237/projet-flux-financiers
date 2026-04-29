package com.fluxfinanciers.dto.response;

import com.fluxfinanciers.enums.ActionAudit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {

    private Long id;
    private ActionAudit action;
    private String entiteType;
    private Long entiteId;
    private String details;
    private LocalDateTime dateAction;
    private Long userId;
    private String username;
}