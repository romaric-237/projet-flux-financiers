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
    private String chargeNom;
    private BigDecimal montant;
    private LocalDate datePaiement;
    private ModePaiement modePaiement;
    private String remarque;
}
