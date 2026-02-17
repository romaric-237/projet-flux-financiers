package com.fluxfinanciers.dto.response;

import com.fluxfinanciers.enums.ModePaiement;
import com.fluxfinanciers.enums.TypePaiementEmploye;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaiementEmployeResponse {

    private Long id;
    private Long employeId;
    private String employeNom;
    private TypePaiementEmploye typePaiement;
    private ModePaiement modePaiement;
    private BigDecimal montant;
    private LocalDate datePaiement;
    private String remarque;
}
