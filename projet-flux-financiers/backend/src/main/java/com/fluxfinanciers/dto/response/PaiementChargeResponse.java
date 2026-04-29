package com.fluxfinanciers.dto.response;

import com.fluxfinanciers.enums.ModePaiement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaiementChargeResponse {

    private Long id;
    private Long chargeId;
    private String chargeLibelle;
    private BigDecimal montant;
    private LocalDate date;
    private ModePaiement modePaiement;
    private String remarque;
}