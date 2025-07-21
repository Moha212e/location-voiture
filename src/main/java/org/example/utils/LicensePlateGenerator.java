package org.example.utils;

import java.util.Random;

/**
 * Classe utilitaire pour générer des immatriculations belges au format 1-ABC-123
 */
public class LicensePlateGenerator {
    private static final Random random = new Random();
    private static final String LETTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ"; // Sans I et O pour éviter la confusion
    private static final String DIGITS = "0123456789";
    
    /**
     * Génère une immatriculation belge au format 1-ABC-123
     * @return Une immatriculation générée aléatoirement
     */
    public static String generateBelgianLicensePlate() {
        // Format belge: 1-ABC-123
        StringBuilder plate = new StringBuilder();
        
        // Premier chiffre (1-9)
        plate.append(1 + random.nextInt(9));
        plate.append('-');
        
        // Trois lettres (A-Z, sans I et O)
        for (int i = 0; i < 3; i++) {
            plate.append(LETTERS.charAt(random.nextInt(LETTERS.length())));
        }
        plate.append('-');
        
        // Trois chiffres (0-9)
        for (int i = 0; i < 3; i++) {
            plate.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        }
        
        return plate.toString();
    }
    
    /**
     * Vérifie si une immatriculation est au format belge valide
     * @param plate L'immatriculation à vérifier
     * @return true si l'immatriculation est valide, false sinon
     */
    public static boolean isValidBelgianLicensePlate(String plate) {
        // Vérification du format 1-ABC-123
        return plate.matches("[1-9]-[A-HJ-NP-Z]{3}-[0-9]{3}");
    }
}
