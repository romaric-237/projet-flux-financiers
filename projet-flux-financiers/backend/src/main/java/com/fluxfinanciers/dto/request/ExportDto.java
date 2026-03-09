package com.fluxfinanciers.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportDto {

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String format; // "EXCEL" ou "CSV"
    private String type;   // "versements", "paiements_charges", "paiements_employes"
}