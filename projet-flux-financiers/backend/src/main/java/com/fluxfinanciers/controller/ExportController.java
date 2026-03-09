package com.fluxfinanciers.controller;

import com.fluxfinanciers.dto.request.ExportDto;
import com.fluxfinanciers.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @GetMapping("/versements")
    public ResponseEntity<byte[]> exportVersements(
            @RequestParam(defaultValue = "EXCEL") String format,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {

        ExportDto dto = new ExportDto(dateDebut, dateFin, format, "versements");
        byte[] data = exportService.exportVersements(dto);
        return buildResponse(data, format, "versements");
    }

    @GetMapping("/paiements-charges")
    public ResponseEntity<byte[]> exportPaiementsCharges(
            @RequestParam(defaultValue = "EXCEL") String format,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {

        ExportDto dto = new ExportDto(dateDebut, dateFin, format, "paiements_charges");
        byte[] data = exportService.exportPaiementsCharges(dto);
        return buildResponse(data, format, "paiements-charges");
    }

    @GetMapping("/paiements-employes")
    public ResponseEntity<byte[]> exportPaiementsEmployes(
            @RequestParam(defaultValue = "EXCEL") String format,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {

        ExportDto dto = new ExportDto(dateDebut, dateFin, format, "paiements_employes");
        byte[] data = exportService.exportPaiementsEmployes(dto);
        return buildResponse(data, format, "paiements-employes");
    }

    private ResponseEntity<byte[]> buildResponse(byte[] data, String format, String nom) {
        boolean isExcel = !"CSV".equalsIgnoreCase(format);
        String contentType = isExcel
                ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                : "text/csv";
        String extension = isExcel ? ".xlsx" : ".csv";
        String filename = nom + "_" + LocalDate.now() + extension;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    }
}