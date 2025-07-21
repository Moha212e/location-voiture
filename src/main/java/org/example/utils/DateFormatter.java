package org.example.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.prefs.Preferences;

/**
 * Classe utilitaire pour formater les dates dans l'application.
 * Permet de changer dynamiquement le format d'affichage des dates.
 */

public class DateFormatter {
    // Formats disponibles
    public static final String FORMAT_DEFAULT = "dd/MM/yyyy";
    public static final String FORMAT_ISO = "yyyy-MM-dd";
    public static final String FORMAT_LONG = "dd MMMM yyyy";
    public static final String FORMAT_SHORT = "dd/MM/yy";

    // Format actuel
    private static String currentFormat = FORMAT_DEFAULT;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(currentFormat);

    // Clé pour les préférences utilisateur
    private static final String PREF_DATE_FORMAT = "dateFormat";
    private static final Preferences prefs = Preferences.userNodeForPackage(DateFormatter.class);

    // Initialisation du format à partir des préférences
    static {
        String savedFormat = prefs.get(PREF_DATE_FORMAT, FORMAT_DEFAULT);
        try {
            setDateFormat(savedFormat);
        } catch (IllegalArgumentException e) {
            // Si le format sauvegardé est invalide, revenir au format par défaut
            setDateFormat(FORMAT_DEFAULT);
        }
    }

    /**
     * Définit le format d'affichage des dates.
     * @param format Le nouveau format à utiliser
     */
    public static void setDateFormat(String format) {
        // Vérifier que le format ne contient pas d'heure
        if (format.matches(".*[HhmsS].*")) {
            throw new IllegalArgumentException("Le format de date ne doit pas contenir d'heure (H, m, s, S) pour LocalDate.");
        }
        currentFormat = format;
        formatter = DateTimeFormatter.ofPattern(format);
        // Sauvegarder le format dans les préférences
        prefs.put(PREF_DATE_FORMAT, format);
    }

    /**
     * Récupère le format actuel d'affichage des dates.
     * @return Le format actuel
     */
    public static String getCurrentFormat() {
        return currentFormat;
    }

    /**
     * Formate une date java.util.Date selon le format actuel.
     * @param date La date à formater
     * @return La date formatée sous forme de chaîne
     */
    public static String format(Date date) {
        if (date == null) return "";
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return format(localDate);
    }

    /**
     * Formate une date LocalDate selon le format actuel.
     * @param date La date à formater
     * @return La date formatée sous forme de chaîne
     */
    public static String format(LocalDate date) {
        if (date == null) return "";
        return date.format(formatter);
    }

    /**
     * Convertit une chaîne en date LocalDate selon le format actuel.
     * @param dateStr La chaîne à convertir
     * @return La date convertie
     * @throws Exception Si la chaîne ne peut pas être convertie
     */
    public static LocalDate parseLocalDate(String dateStr) throws Exception {
        return LocalDate.parse(dateStr, formatter);
    }

    /**
     * Convertit une chaîne en date java.util.Date selon le format actuel.
     * @param dateStr La chaîne à convertir
     * @return La date convertie
     * @throws Exception Si la chaîne ne peut pas être convertie
     */
    public static Date parseDate(String dateStr) throws Exception {
        LocalDate localDate = parseLocalDate(dateStr);
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
