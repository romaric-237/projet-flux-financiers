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
            return versementRepository.findByDateBetween(dateDebut, dateFin);
        }
        return versementRepository.findAll();
    }

    private byte[] buildVersementsExcel(List<Versement> versements) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Versements");
            String[] headers = {"ID", "Client", "Montant (€)", "Date", "Mode Paiement", "Remarque"};
            createHeaderRow(workbook, sheet, headers);

            int rowNum = 1;
            for (Versement v : versements) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(v.getId());
                row.createCell(1).setCellValue(v.getClient().getNom() + " " + v.getClient().getPrenom());
                row.createCell(2).setCellValue(v.getMontant().doubleValue());
                row.createCell(3).setCellValue(v.getDate().toString());
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
        sb.append("ID,Client,Montant (€),Date,Mode Paiement,Remarque\n");
        for (Versement v : versements) {
            sb.append(v.getId()).append(",")
              .append(escapeCsv(v.getClient().getNom() + " " + v.getClient().getPrenom())).append(",")
              .append(v.getMontant()).append(",")
              .append(v.getDate()).append(",")
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
            return paiementChargeRepository.findByDateBetween(dateDebut, dateFin);
        }
        return paiementChargeRepository.findAll();
    }

    private byte[] buildPaiementsChargesExcel(List<PaiementCharge> paiements) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Paiements Charges");
            String[] headers = {"ID", "Charge", "Montant (€)", "Date", "Mode Paiement", "Remarque"};
            createHeaderRow(workbook, sheet, headers);

            int rowNum = 1;
            for (PaiementCharge p : paiements) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(p.getId());
                row.createCell(1).setCellValue(p.getCharge().getLibelle());
                row.createCell(2).setCellValue(p.getMontant().doubleValue());
                row.createCell(3).setCellValue(p.getDate().toString());
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
        sb.append("ID,Charge,Montant (€),Date,Mode Paiement,Remarque\n");
        for (PaiementCharge p : paiements) {
            sb.append(p.getId()).append(",")
              .append(escapeCsv(p.getCharge().getLibelle())).append(",")
              .append(p.getMontant()).append(",")
              .append(p.getDate()).append(",")
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
            return paiementEmployeRepository.findByDateBetween(dateDebut, dateFin);
        }
        return paiementEmployeRepository.findAll();
    }

    private byte[] buildPaiementsEmployesExcel(List<PaiementEmploye> paiements) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Paiements Employés");
            String[] headers = {"ID", "Employé", "Type", "Montant (€)", "Date", "Mode Paiement", "Remarque"};
            createHeaderRow(workbook, sheet, headers);

            int rowNum = 1;
            for (PaiementEmploye p : paiements) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(p.getId());
                row.createCell(1).setCellValue(p.getEmploye().getNom() + " " + p.getEmploye().getPrenom());
                row.createCell(2).setCellValue(p.getType().name());
                row.createCell(3).setCellValue(p.getMontant().doubleValue());
                row.createCell(4).setCellValue(p.getDate().toString());
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
        sb.append("ID,Employé,Type,Montant (€),Date,Mode Paiement,Remarque\n");
        for (PaiementEmploye p : paiements) {
            sb.append(p.getId()).append(",")
              .append(escapeCsv(p.getEmploye().getNom() + " " + p.getEmploye().getPrenom())).append(",")
              .append(p.getType().name()).append(",")
              .append(p.getMontant()).append(",")
              .append(p.getDate()).append(",")
              .append(p.getModePaiement().name()).append(",")
              .append(escapeCsv(p.getRemarque())).append("\n");
        }
        return sb.toString();
    }

    // ─── EXPORT COMPLET ──────────────────────────────────────────────────────────

    public byte[] exportComplet(LocalDate dateDebut, LocalDate dateFin) {
        List<Versement> versements = fetchVersements(dateDebut, dateFin);
        List<PaiementCharge> paiementsCharges = fetchPaiementsCharges(dateDebut, dateFin);
        List<PaiementEmploye> paiementsEmployes = fetchPaiementsEmployes(dateDebut, dateFin);

        try (Workbook workbook = new XSSFWorkbook()) {

            // ── Feuille 1 : Recettes ──
            Sheet sheetRecettes = workbook.createSheet("Recettes");
            String[] headersRecettes = {"ID", "Client", "Montant (€)", "Date", "Mode Paiement", "Remarque"};
            createHeaderRow(workbook, sheetRecettes, headersRecettes);
            int rowNum = 1;
            for (Versement v : versements) {
                Row row = sheetRecettes.createRow(rowNum++);
                row.createCell(0).setCellValue(v.getId());
                row.createCell(1).setCellValue(v.getClient().getNom() + " " + v.getClient().getPrenom());
                row.createCell(2).setCellValue(v.getMontant().doubleValue());
                row.createCell(3).setCellValue(v.getDate().toString());
                row.createCell(4).setCellValue(v.getModePaiement() != null ? v.getModePaiement().name() : "");
                row.createCell(5).setCellValue(v.getRemarque() != null ? v.getRemarque() : "");
            }
            CellStyle totalStyle = createTotalStyle(workbook);
            Row totalRecettes = sheetRecettes.createRow(rowNum + 1);
            Cell totalLabelR = totalRecettes.createCell(1);
            totalLabelR.setCellValue("TOTAL");
            totalLabelR.setCellStyle(totalStyle);
            Cell totalValR = totalRecettes.createCell(2);
            totalValR.setCellValue(versements.stream().mapToDouble(v -> v.getMontant().doubleValue()).sum());
            totalValR.setCellStyle(totalStyle);
            autoSize(sheetRecettes, headersRecettes.length);

            // ── Feuille 2 : Dépenses Personnel ──
            Sheet sheetPersonnel = workbook.createSheet("Dépenses Personnel");
            String[] headersPersonnel = {"ID", "Employé", "Type", "Montant (€)", "Date", "Mode Paiement", "Remarque"};
            createHeaderRow(workbook, sheetPersonnel, headersPersonnel);
            rowNum = 1;
            for (PaiementEmploye p : paiementsEmployes) {
                Row row = sheetPersonnel.createRow(rowNum++);
                row.createCell(0).setCellValue(p.getId());
                row.createCell(1).setCellValue(p.getEmploye().getNom() + " " + p.getEmploye().getPrenom());
                row.createCell(2).setCellValue(p.getType().name());
                row.createCell(3).setCellValue(p.getMontant().doubleValue());
                row.createCell(4).setCellValue(p.getDate().toString());
                row.createCell(5).setCellValue(p.getModePaiement().name());
                row.createCell(6).setCellValue(p.getRemarque() != null ? p.getRemarque() : "");
            }
            Row totalPersonnel = sheetPersonnel.createRow(rowNum + 1);
            Cell totalLabelP = totalPersonnel.createCell(2);
            totalLabelP.setCellValue("TOTAL");
            totalLabelP.setCellStyle(totalStyle);
            Cell totalValP = totalPersonnel.createCell(3);
            totalValP.setCellValue(paiementsEmployes.stream().mapToDouble(p -> p.getMontant().doubleValue()).sum());
            totalValP.setCellStyle(totalStyle);
            autoSize(sheetPersonnel, headersPersonnel.length);

            // ── Feuille 3 : Dépenses Charges ──
            Sheet sheetCharges = workbook.createSheet("Dépenses Charges");
            String[] headersCharges = {"ID", "Charge", "Montant (€)", "Date", "Mode Paiement", "Remarque"};
            createHeaderRow(workbook, sheetCharges, headersCharges);
            rowNum = 1;
            for (PaiementCharge p : paiementsCharges) {
                Row row = sheetCharges.createRow(rowNum++);
                row.createCell(0).setCellValue(p.getId());
                row.createCell(1).setCellValue(p.getCharge().getLibelle());
                row.createCell(2).setCellValue(p.getMontant().doubleValue());
                row.createCell(3).setCellValue(p.getDate().toString());
                row.createCell(4).setCellValue(p.getModePaiement().name());
                row.createCell(5).setCellValue(p.getRemarque() != null ? p.getRemarque() : "");
            }
            Row totalCharges = sheetCharges.createRow(rowNum + 1);
            Cell totalLabelC = totalCharges.createCell(1);
            totalLabelC.setCellValue("TOTAL");
            totalLabelC.setCellStyle(totalStyle);
            Cell totalValC = totalCharges.createCell(2);
            totalValC.setCellValue(paiementsCharges.stream().mapToDouble(p -> p.getMontant().doubleValue()).sum());
            totalValC.setCellStyle(totalStyle);
            autoSize(sheetCharges, headersCharges.length);

            // ── Feuille 4 : Synthèse ──
            Sheet sheetSynthese = workbook.createSheet("Synthèse");
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle synthLabelStyle = createSynthLabelStyle(workbook);
            CellStyle synthValueStyle = createSynthValueStyle(workbook);

            Row titreRow = sheetSynthese.createRow(0);
            Cell titreCell = titreRow.createCell(0);
            titreCell.setCellValue("SYNTHÈSE FINANCIÈRE");
            titreCell.setCellStyle(titleStyle);

            Row periodeRow = sheetSynthese.createRow(1);
            String periode = (dateDebut != null && dateFin != null)
                    ? "Période : " + dateDebut + " → " + dateFin
                    : "Période : Toutes les données";
            periodeRow.createCell(0).setCellValue(periode);

            sheetSynthese.createRow(2);

            Row synthHeader = sheetSynthese.createRow(3);
            Cell h1 = synthHeader.createCell(0); h1.setCellValue("Catégorie"); h1.setCellStyle(synthLabelStyle);
            Cell h2 = synthHeader.createCell(1); h2.setCellValue("Montant (€)"); h2.setCellStyle(synthLabelStyle);

            double totalRecettesMontant = versements.stream().mapToDouble(v -> v.getMontant().doubleValue()).sum();
            double totalPersonnelMontant = paiementsEmployes.stream().mapToDouble(p -> p.getMontant().doubleValue()).sum();
            double totalChargesMontant = paiementsCharges.stream().mapToDouble(p -> p.getMontant().doubleValue()).sum();
            double totalDepenses = totalPersonnelMontant + totalChargesMontant;
            double soldeNet = totalRecettesMontant - totalDepenses;

            Object[][] lignes = {
                {"Total Recettes",     totalRecettesMontant},
                {"Dépenses Personnel", totalPersonnelMontant},
                {"Dépenses Charges",   totalChargesMontant},
                {"Total Dépenses",     totalDepenses},
                {"Solde Net",          soldeNet},
            };

            CellStyle soldePositif = createSoldeStyle(workbook, true);
            CellStyle soldeNegatif = createSoldeStyle(workbook, false);

            for (int i = 0; i < lignes.length; i++) {
                Row r = sheetSynthese.createRow(4 + i);
                Cell labelCell = r.createCell(0);
                labelCell.setCellValue((String) lignes[i][0]);
                labelCell.setCellStyle(synthLabelStyle);
                Cell valCell = r.createCell(1);
                valCell.setCellValue((double) lignes[i][1]);
                if ("Solde Net".equals(lignes[i][0])) {
                    valCell.setCellStyle(soldeNet >= 0 ? soldePositif : soldeNegatif);
                } else {
                    valCell.setCellStyle(synthValueStyle);
                }
            }

            sheetSynthese.setColumnWidth(0, 6000);
            sheetSynthese.setColumnWidth(1, 4000);

            return toBytes(workbook);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération du fichier Excel complet", e);
        }
    }

    // ─── UTILITAIRES ────────────────────────────────────────────────────────────

    private CellStyle createTotalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        return style;
    }

    private CellStyle createSynthLabelStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createSynthValueStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.WHITE1.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createSoldeStyle(Workbook workbook, boolean positif) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(positif ? IndexedColors.GREEN.getIndex() : IndexedColors.RED.getIndex());
        style.setFont(font);
        return style;
    }

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