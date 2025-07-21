package org.example.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.model.entity.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Classe utilitaire pour l'export Excel des rapports
 */
public class ExcelExporter {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Génère un rapport Excel complet avec tous les onglets
     * @param cars Liste des voitures
     * @param clients Liste des clients
     * @param reservations Liste des réservations
     * @param contrats Liste des contrats
     * @param filePath Chemin du fichier Excel
     * @throws IOException En cas d'erreur d'écriture
     */
    public static void exportCompleteReportToExcel(List<Car> cars, List<Client> clients, 
                                                  List<Reservation> reservations, List<Contrat> contrats, 
                                                  String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            
            // Créer les différents onglets
            createCarsSheet(workbook, cars);
            createClientsSheet(workbook, clients);
            createReservationsSheet(workbook, reservations);
            createContratsSheet(workbook, contrats);
            createSummarySheet(workbook, cars, clients, reservations, contrats);
            
            // Sauvegarder le fichier
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        }
    }
    
    /**
     * Crée l'onglet des voitures
     */
    private static void createCarsSheet(Workbook workbook, List<Car> cars) {
        Sheet sheet = workbook.createSheet("Voitures");
        
        // Créer le style pour les en-têtes
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        // Créer les en-têtes
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Marque", "Modèle", "Année", "Prix/Jour", "Kilométrage", 
                           "Carburant", "Transmission", "Places", "Disponible", "Image"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Remplir les données
        int rowNum = 1;
        for (Car car : cars) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(car.getIdCar());
            row.createCell(1).setCellValue(car.getBrand());
            row.createCell(2).setCellValue(car.getModel());
            row.createCell(3).setCellValue(car.getYear());
            row.createCell(4).setCellValue(car.getPriceday());
            row.createCell(5).setCellValue(car.getMileage());
            row.createCell(6).setCellValue(car.getFuelType());
            row.createCell(7).setCellValue(car.getTransmission());
            row.createCell(8).setCellValue(car.getSeats());
            row.createCell(9).setCellValue(car.isAvailable() ? "Oui" : "Non");
            row.createCell(10).setCellValue(car.getImage());
        }
        
        // Ajuster la largeur des colonnes
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * Crée l'onglet des clients
     */
    private static void createClientsSheet(Workbook workbook, List<Client> clients) {
        Sheet sheet = workbook.createSheet("Clients");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Nom", "Prénom", "Email", "Téléphone", "Adresse", "Date de naissance"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        for (Client client : clients) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(client.getIdClient());
            row.createCell(1).setCellValue(client.getName());
            row.createCell(2).setCellValue(client.getSurname());
            row.createCell(3).setCellValue(client.getEmail());
            row.createCell(4).setCellValue(client.getPhoneNumber());
            row.createCell(5).setCellValue(client.getAddress());
            row.createCell(6).setCellValue(client.getBirthDate() != null ? 
                client.getBirthDate().format(DATE_FORMATTER) : "");
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * Crée l'onglet des réservations
     */
    private static void createReservationsSheet(Workbook workbook, List<Reservation> reservations) {
        Sheet sheet = workbook.createSheet("Réservations");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Date début", "Date fin", "Responsable", "Prix", "ID Client", "ID Voiture", "Notes"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        for (Reservation reservation : reservations) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(reservation.getIdReservation());
            row.createCell(1).setCellValue(reservation.getStartDate() != null ? 
                reservation.getStartDate().format(DATE_FORMATTER) : "");
            row.createCell(2).setCellValue(reservation.getEndDate() != null ? 
                reservation.getEndDate().format(DATE_FORMATTER) : "");
            row.createCell(3).setCellValue(reservation.getResponsable());
            row.createCell(4).setCellValue(reservation.getPrice());
            row.createCell(5).setCellValue(reservation.getClientId());
            row.createCell(6).setCellValue(reservation.getCarId());
            row.createCell(7).setCellValue(reservation.getNotes());
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * Crée l'onglet des contrats
     */
    private static void createContratsSheet(Workbook workbook, List<Contrat> contrats) {
        Sheet sheet = workbook.createSheet("Contrats");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Caution", "Assurance", "Prix Assurance", "Prix Total", 
                           "Statut", "Signé", "ID Réservation", "Client", "Voiture"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        for (Contrat contrat : contrats) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(contrat.getIdContrat());
            row.createCell(1).setCellValue(contrat.getCaution());
            row.createCell(2).setCellValue(contrat.getTypeAssurance());
            row.createCell(3).setCellValue(contrat.getPrixAssurance());
            row.createCell(4).setCellValue(contrat.getPrixTotal());
            row.createCell(5).setCellValue(contrat.getStatutContrat().toString());
            row.createCell(6).setCellValue(contrat.isEstSigne() ? "Oui" : "Non");
            row.createCell(7).setCellValue(contrat.getReservationId());
            row.createCell(8).setCellValue(
                (contrat.getClientName() != null ? contrat.getClientName() : "") + " " +
                (contrat.getClientSurname() != null ? contrat.getClientSurname() : "")
            );
            row.createCell(9).setCellValue(
                (contrat.getCarBrand() != null ? contrat.getCarBrand() : "") + " " +
                (contrat.getCarModel() != null ? contrat.getCarModel() : "")
            );
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * Crée l'onglet de résumé avec des statistiques
     */
    private static void createSummarySheet(Workbook workbook, List<Car> cars, List<Client> clients, 
                                         List<Reservation> reservations, List<Contrat> contrats) {
        Sheet sheet = workbook.createSheet("Résumé");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle boldStyle = createBoldStyle(workbook);
        
        int rowNum = 0;
        
        // Titre
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("RAPPORT GÉNÉRAL - " + java.time.LocalDate.now().format(DATE_FORMATTER));
        titleCell.setCellStyle(boldStyle);
        
        rowNum++; // Ligne vide
        
        // Statistiques générales
        Row statsRow1 = sheet.createRow(rowNum++);
        statsRow1.createCell(0).setCellValue("Statistiques générales");
        statsRow1.getCell(0).setCellStyle(headerStyle);
        
        rowNum++;
        Row statsRow2 = sheet.createRow(rowNum++);
        statsRow2.createCell(0).setCellValue("Nombre total de voitures:");
        statsRow2.createCell(1).setCellValue(cars.size());
        
        Row statsRow3 = sheet.createRow(rowNum++);
        statsRow3.createCell(0).setCellValue("Nombre total de clients:");
        statsRow3.createCell(1).setCellValue(clients.size());
        
        Row statsRow4 = sheet.createRow(rowNum++);
        statsRow4.createCell(0).setCellValue("Nombre total de réservations:");
        statsRow4.createCell(1).setCellValue(reservations.size());
        
        Row statsRow5 = sheet.createRow(rowNum++);
        statsRow5.createCell(0).setCellValue("Nombre total de contrats:");
        statsRow5.createCell(1).setCellValue(contrats.size());
        
        rowNum++; // Ligne vide
        
        // Statistiques des voitures
        Row carStatsRow = sheet.createRow(rowNum++);
        carStatsRow.createCell(0).setCellValue("Statistiques des voitures");
        carStatsRow.getCell(0).setCellStyle(headerStyle);
        
        rowNum++;
        long availableCars = cars.stream().filter(Car::isAvailable).count();
        Row carStatsRow2 = sheet.createRow(rowNum++);
        carStatsRow2.createCell(0).setCellValue("Voitures disponibles:");
        carStatsRow2.createCell(1).setCellValue(availableCars);
        
        Row carStatsRow3 = sheet.createRow(rowNum++);
        carStatsRow3.createCell(0).setCellValue("Voitures indisponibles:");
        carStatsRow3.createCell(1).setCellValue(cars.size() - availableCars);
        
        // Calcul du prix moyen
        double avgPrice = cars.stream().mapToDouble(Car::getPriceday).average().orElse(0.0);
        Row carStatsRow4 = sheet.createRow(rowNum++);
        carStatsRow4.createCell(0).setCellValue("Prix moyen par jour:");
        carStatsRow4.createCell(1).setCellValue(String.format("%.2f €", avgPrice));
        
        rowNum++; // Ligne vide
        
        // Statistiques financières
        Row financeRow = sheet.createRow(rowNum++);
        financeRow.createCell(0).setCellValue("Statistiques financières");
        financeRow.getCell(0).setCellStyle(headerStyle);
        
        rowNum++;
        double totalRevenue = contrats.stream().mapToDouble(Contrat::getPrixTotal).sum();
        Row financeRow2 = sheet.createRow(rowNum++);
        financeRow2.createCell(0).setCellValue("Revenus totaux:");
        financeRow2.createCell(1).setCellValue(String.format("%.2f €", totalRevenue));
        
        double avgContractValue = contrats.stream().mapToDouble(Contrat::getPrixTotal).average().orElse(0.0);
        Row financeRow3 = sheet.createRow(rowNum++);
        financeRow3.createCell(0).setCellValue("Valeur moyenne des contrats:");
        financeRow3.createCell(1).setCellValue(String.format("%.2f €", avgContractValue));
        
        // Ajuster la largeur des colonnes
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
    
    /**
     * Crée le style pour les en-têtes
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }
    
    /**
     * Crée le style pour le texte en gras
     */
    private static CellStyle createBoldStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
} 