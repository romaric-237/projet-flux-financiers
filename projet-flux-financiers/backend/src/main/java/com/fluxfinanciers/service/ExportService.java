package com.fluxfinanciers.service;

import com.fluxfinanciers.dto.request.ExportDto;
import com.fluxfinanciers.entity.PaiementCharge;
import com.fluxfinanciers.entity.PaiementEmploye;
import com.fluxfinanciers.entity.Versement;
import com.fluxfinanciers.repository.PaiementChargeRepository;
import com.fluxfinanciers.repository.PaiementEmployeRepository;
import com.fluxfinanciers.repository.VersementRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExportService {

    private final VersementRepository versementRepository;
    private final PaiementChargeRepository paiementChargeRepository;
    private final PaiementEmployeRepository paiementEmployeRepository;

    // ─── VERSEMENTS ─────────────────────────────────────────────────────────────

    public byte[] exportVersements(ExportDto dto) {
        List<Versement> data = fetchVersements(dto.getDateDebut(), dto.getDateFin());
        if ("CSV".equalsIgnoreCase(dto.getFormat())) {
            return buildVersementsCsv(data).getBytes();
        }
        return buildVersementsExcel(data);
    }

    private List<Versement> fetchVersements(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut != null && dateFin != null) {
            return versementRepository.findByDateVersementBetween(dateDebut, dateFin);
        }
        return versementRepository.findAll();
    }

    private byte[] buildVersementsExcel(List<Versement> versements) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Versements");
            String[] headers = {"ID", "Client", "Montant TTC (€)", "Date Versement", "Mode Paiement", "Remarque"};
            createHeaderRow(workbook, sheet, headers);

            int rowNum = 1;
            for (Versement v : versements) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(v.getId());
                row.createCell(1).setCellValue(v.getClient().getNom() + " " + v.getClient().getPrenom());
                row.createCell(2).setCellValue(v.getMontantTTC().doubleValue());
                row.createCell(3).setCellValue(v.getDateVersement().toString());
                row.createCell(4).setCellValue(v.getModePaiement() != null ? v.getModePaiement().name() : "");
                row.createCell(5).setCellValue(v.getRemarque() != null ? v.getRemarque() : "");
            }
            autoSize(sheet, headers.length);
            return toBytes(workbook);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération Excel des versements", e);
        }
    }

    private String buildVersementsCsv(List<Versement> versements) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Client,Montant TTC (€),Date Versement,Mode Paiement,Remarque\n");
        for (Versement v : versements) {
            sb.append(v.getId()).append(",")
              .append(escapeCsv(v.getClient().getNom() + " " + v.getClient().getPrenom())).append(",")
              .append(v.getMontantTTC()).append(",")
              .append(v.getDateVersement()).append(",")
              .append(v.getModePaiement() != null ? v.getModePaiement().name() : "").append(",")
              .append(escapeCsv(v.getRemarque())).append("\n");
        }
        return sb.toString();
    }

    // ─── PAIEMENTS CHARGES ───────────────────────────────────────────────────────

    public byte[] exportPaiementsCharges(ExportDto dto) {
        List<PaiementCharge> data = fetchPaiementsCharges(dto.getDateDebut(), dto.getDateFin());
        if ("CSV".equalsIgnoreCase(dto.getFormat())) {
            return buildPaiementsChargesCsv(data).getBytes();
        }
        return buildPaiementsChargesExcel(data);
    }

    private List<PaiementCharge> fetchPaiementsCharges(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut != null && dateFin != null) {
            return paiementChargeRepository.findByDatePaiementBetween(dateDebut, dateFin);
        }
        return paiementChargeRepository.findAll();
    }

    private byte[] buildPaiementsChargesExcel(List<PaiementCharge> paiements) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Paiements Charges");
            String[] headers = {"ID", "Charge", "Montant (€)", "Date Paiement", "Mode Paiement", "Remarque"};
            createHeaderRow(workbook, sheet, headers);

            int rowNum = 1;
            for (PaiementCharge p : paiements) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(p.getId());
                row.createCell(1).setCellValue(p.getCharge().getNomCharge());
                row.createCell(2).setCellValue(p.getMontant().doubleValue());
                row.createCell(3).setCellValue(p.getDatePaiement().toString());
                row.createCell(4).setCellValue(p.getModePaiement().name());
                row.createCell(5).setCellValue(p.getRemarque() != null ? p.getRemarque() : "");
            }
            autoSize(sheet, headers.length);
            return toBytes(workbook);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération Excel des paiements charges", e);
        }
    }

    private String buildPaiementsChargesCsv(List<PaiementCharge> paiements) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Charge,Montant (€),Date Paiement,Mode Paiement,Remarque\n");
        for (PaiementCharge p : paiements) {
            sb.append(p.getId()).append(",")
              .append(escapeCsv(p.getCharge().getNomCharge())).append(",")
              .append(p.getMontant()).append(",")
              .append(p.getDatePaiement()).append(",")
              .append(p.getModePaiement().name()).append(",")
              .append(escapeCsv(p.getRemarque())).append("\n");
        }
        return sb.toString();
    }

    // ─── PAIEMENTS EMPLOYÉS ──────────────────────────────────────────────────────

    public byte[] exportPaiementsEmployes(ExportDto dto) {
        List<PaiementEmploye> data = fetchPaiementsEmployes(dto.getDateDebut(), dto.getDateFin());
        if ("CSV".equalsIgnoreCase(dto.getFormat())) {
            return buildPaiementsEmployesCsv(data).getBytes();
        }
        return buildPaiementsEmployesExcel(data);
    }

    private List<PaiementEmploye> fetchPaiementsEmployes(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut != null && dateFin != null) {
            return paiementEmployeRepository.findByDatePaiementBetween(dateDebut, dateFin);
        }
        return paiementEmployeRepository.findAll();
    }

    private byte[] buildPaiementsEmployesExcel(List<PaiementEmploye> paiements) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Paiements Employés");
            String[] headers = {"ID", "Employé", "Type Paiement", "Montant (€)", "Date Paiement", "Mode Paiement", "Remarque"};
            createHeaderRow(workbook, sheet, headers);

            int rowNum = 1;
            for (PaiementEmploye p : paiements) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(p.getId());
                row.createCell(1).setCellValue(p.getEmploye().getNom() + " " + p.getEmploye().getPrenom());
                row.createCell(2).setCellValue(p.getTypePaiement().name());
                row.createCell(3).setCellValue(p.getMontant().doubleValue());
                row.createCell(4).setCellValue(p.getDatePaiement().toString());
                row.createCell(5).setCellValue(p.getModePaiement().name());
                row.createCell(6).setCellValue(p.getRemarque() != null ? p.getRemarque() : "");
            }
            autoSize(sheet, headers.length);
            return toBytes(workbook);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération Excel des paiements employés", e);
        }
    }

    private String buildPaiementsEmployesCsv(List<PaiementEmploye> paiements) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Employé,Type Paiement,Montant (€),Date Paiement,Mode Paiement,Remarque\n");
        for (PaiementEmploye p : paiements) {
            sb.append(p.getId()).append(",")
              .append(escapeCsv(p.getEmploye().getNom() + " " + p.getEmploye().getPrenom())).append(",")
              .append(p.getTypePaiement().name()).append(",")
              .append(p.getMontant()).append(",")
              .append(p.getDatePaiement()).append(",")
              .append(p.getModePaiement().name()).append(",")
              .append(escapeCsv(p.getRemarque())).append("\n");
        }
        return sb.toString();
    }

    // ─── UTILITAIRES ────────────────────────────────────────────────────────────

    private void createHeaderRow(Workbook workbook, Sheet sheet, String[] headers) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void autoSize(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private byte[] toBytes(Workbook workbook) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return out.toByteArray();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}