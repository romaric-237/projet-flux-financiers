package com.fluxfinanciers.dto.response;

import com.fluxfinanciers.enums.TypeCharge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargeResponse {

    private Long id;
    private String nomCharge;
    private TypeCharge typeCharge;
    private LocalDateTime createdAt;
    private Long createdById;
    private String createdByUsername;
}
