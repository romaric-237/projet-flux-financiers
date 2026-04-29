package com.fluxfinanciers.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargeRecurrenteResponse {

    private Long id;
    private Long chargeId;
    private String chargeLibelle;
    private BigDecimal montantDefaut;
    private boolean actif;
    private Long createdById;
    private String createdByUsername;
}