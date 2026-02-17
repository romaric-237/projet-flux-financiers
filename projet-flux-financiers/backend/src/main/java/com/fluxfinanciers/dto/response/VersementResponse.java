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
public class VersementResponse {

    private Long id;
    private Long clientId;
    private String clientNom;
    private BigDecimal montantTTC;
    private LocalDate dateVersement;
    private ModePaiement modePaiement;
    private String remarque;
}
