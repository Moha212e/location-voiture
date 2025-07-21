package org.example.utils;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import org.example.model.entity.Contrat;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.awt.Color;

/**
 * Classe utilitaire pour l'export PDF des contrats
 */
public class PDFExporter {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Génère un PDF de contrat
     * @param contrat Le contrat à exporter
     * @param filePath Le chemin du fichier PDF à créer
     * @throws Exception En cas d'erreur d'écriture
     */
    public static void exportContratToPDF(Contrat contrat, String filePath) throws Exception {
        // Marges intermédiaires
        Document document = new Document(PageSize.A4, 35, 35, 35, 35);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Police intermédiaire
        Font titleFont = new Font(Font.HELVETICA, 13, Font.BOLD);
        Font subtitleFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 11, Font.NORMAL);

        // Couleurs
        Color blueDark = new Color(41, 128, 185);
        Color blueLight = new Color(230, 240, 255);
        Color blueHeader = new Color(52, 152, 219);
        Color white = Color.WHITE;

        // Ajout du logo en haut à gauche
        try {
            Image logo = Image.getInstance("C:/Users/pasch/Documents/Mes-codes/Java/projetJava2/c1a294a2-5924-43dd-9cef-2fa04c2345df - Copie.png");
            logo.scaleToFit(120, 60);
            logo.setAlignment(Image.ALIGN_RIGHT);
            document.add(logo);
        } catch (Exception ex) {
            // Si le logo n'est pas trouvé, on continue sans
            System.err.println("Logo non trouvé ou erreur de chargement : " + ex.getMessage());
        }
        // Titre en bleu foncé
        Font titleFontDark = new Font(Font.HELVETICA, 23, Font.BOLD, blueDark);
        Paragraph title = new Paragraph("CONTRAT DE LOCATION DE VÉHICULE (ORIGINAL)", titleFontDark);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(4f);
        document.add(title);
        // Ligne colorée sous le titre
        Paragraph line = new Paragraph(" ");
        line.setSpacingAfter(2f);
        document.add(line);
        PdfPTable colorLine = new PdfPTable(1);
        colorLine.setWidthPercentage(100);
        PdfPCell colorCell = new PdfPCell();
        colorCell.setFixedHeight(3f);
        colorCell.setBackgroundColor(blueHeader);
        colorCell.setBorder(Rectangle.NO_BORDER);
        colorLine.addCell(colorCell);
        document.add(colorLine);

        // Tableau unique pour toutes les infos
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingBefore(8f);
        infoTable.setSpacingAfter(10f);
        infoTable.setKeepTogether(true);
        infoTable.getDefaultCell().setBorderColor(blueHeader);

        // En-tête du tableau
        Font headerFont = new Font(Font.HELVETICA, 11, Font.BOLD, white);
        PdfPCell h1 = new PdfPCell(new Phrase("Champ", headerFont));
        h1.setBackgroundColor(blueHeader);
        h1.setBorderColor(blueHeader);
        h1.setHorizontalAlignment(Element.ALIGN_CENTER);
        PdfPCell h2 = new PdfPCell(new Phrase("Valeur", headerFont));
        h2.setBackgroundColor(blueHeader);
        h2.setBorderColor(blueHeader);
        h2.setHorizontalAlignment(Element.ALIGN_CENTER);
        infoTable.addCell(h1);
        infoTable.addCell(h2);

        // Contrat
        addTableRow(infoTable, "Numéro de contrat:", contrat.getIdContrat(), normalFont);
        addTableRow(infoTable, "Date de signature:", java.time.LocalDate.now().format(DATE_FORMATTER), normalFont);
        addTableRow(infoTable, "Statut:", contrat.getStatutContrat().toString(), normalFont);
        addTableRow(infoTable, "Type d'assurance:", contrat.getTypeAssurance(), normalFont);
        addTableRow(infoTable, "Caution:", String.format("%.2f €", contrat.getCaution()), normalFont);
        addTableRow(infoTable, "Prix assurance:", String.format("%.2f €", contrat.getPrixAssurance()), normalFont);
        addTableRow(infoTable, "Prix total:", String.format("%.2f €", contrat.getPrixTotal()), normalFont);

        // Client
        if (contrat.getClientName() != null && contrat.getClientSurname() != null) {
            addTableRow(infoTable, "Nom:", contrat.getClientName(), normalFont);
            addTableRow(infoTable, "Prénom:", contrat.getClientSurname(), normalFont);
            addTableRow(infoTable, "ID Client:", contrat.getClientId(), normalFont);
        }
        // Véhicule
        if (contrat.getCarBrand() != null && contrat.getCarModel() != null) {
            addTableRow(infoTable, "Marque:", contrat.getCarBrand(), normalFont);
            addTableRow(infoTable, "Modèle:", contrat.getCarModel(), normalFont);
            addTableRow(infoTable, "Immatriculation:", contrat.getCarId(), normalFont);
        }
        // Réservation
        addTableRow(infoTable, "ID Réservation:", String.valueOf(contrat.getReservationId()), normalFont);
        // Période de location
        if (contrat.getReservation() != null) {
            java.time.LocalDate start = contrat.getReservation().getStartDate();
            java.time.LocalDate end = contrat.getReservation().getEndDate();
            String startStr = (start != null) ? start.format(DATE_FORMATTER) : "-";
            String endStr = (end != null) ? end.format(DATE_FORMATTER) : "-";
            addTableRow(infoTable, "Date début location:", startStr, normalFont);
            addTableRow(infoTable, "Date fin location:", endStr, normalFont);
        }

        // Options
        if (contrat.getOptions() != null && !contrat.getOptions().isEmpty()) {
            StringBuilder optionsStr = new StringBuilder();
            for (String option : contrat.getOptions()) {
                if (optionsStr.length() > 0) optionsStr.append(", ");
                optionsStr.append(option);
            }
            addTableRow(infoTable, "Options:", optionsStr.toString(), normalFont);
        }
        document.add(infoTable);

        // Titres de section en bleu
        Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD, blueDark);
        Paragraph conditionsTitle = new Paragraph("CONDITIONS GÉNÉRALES", sectionFont);
        conditionsTitle.setSpacingBefore(8f);
        document.add(conditionsTitle);
        String conditions =
                "1. Le locataire s'engage à restituer le véhicule dans l'état où il l'a reçu.\n" +
                "2. Toute détérioration sera facturée au locataire.\n" +
                "3. Le locataire est responsable du véhicule pendant toute la durée de la location.\n" +
                "4. En cas de retard, des frais supplémentaires pourront être appliqués.\n" +
                "5. Le contrat est soumis aux conditions d'assurance souscrites." +
                "6. Dans le cas ou il y a un probleme et que l'assurance ne peut pas couvrir les frais le locataire s'engage a payer la totalite de la caution ";
        Paragraph conditionsPara = new Paragraph(conditions, normalFont);
        conditionsPara.setSpacingAfter(8f);
        document.add(conditionsPara);

        Paragraph signatureTitle = new Paragraph("SIGNATURE", sectionFont);
        signatureTitle.setSpacingBefore(8f);
        document.add(signatureTitle);
        
        // Vérifier si le contrat est signé
        if (contrat.isEstSigne()) {
            // Construire le nom complet du client pour la signature (seulement si signé)
            String clientFullName = "";
            if (contrat.getClientName() != null && contrat.getClientSurname() != null) {
                clientFullName = contrat.getClientName() + " " + contrat.getClientSurname();
            } else if (contrat.getClientName() != null) {
                clientFullName = contrat.getClientName();
            } else if (contrat.getClientSurname() != null) {
                clientFullName = contrat.getClientSurname();
            } else {
                clientFullName = "Client non identifié";
            }
            
            Paragraph signature = new Paragraph(
                    STR."Signature du locataire: \{clientFullName} \n    Date: \{LocalDate.now().format(DATE_FORMATTER)}",
                    normalFont);
            document.add(signature);
        } else {
            // Si le contrat n'est pas signé, afficher les underscores
            Paragraph signature = new Paragraph(
                    STR."Signature du locataire: _________________________________(signature electronique ou manuel) \n    Date: \{LocalDate.now().format(DATE_FORMATTER)}",
                    normalFont);
            document.add(signature);
        }
        document.close();
    }

    /**
     * Ajoute une ligne au tableau
     */
    private static void addTableRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Paragraph(label, font));
        labelCell.setPadding(5);
        labelCell.setBackgroundColor(new Color(240, 240, 240)); // Gris clair
        
        PdfPCell valueCell = new PdfPCell(new Paragraph(value != null ? value : "", font));
        valueCell.setPadding(5);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
} 