package org.example.model.authentication;

import java.io.*;
import java.util.Properties;

/**
 * Implémentation du LoginTemplate qui utilise un fichier properties pour stocker
 * les informations d'authentification des utilisateurs.
 */
public class PropertiesLogin extends LoginTemplate {
    private Properties userProperties;
    private final String propertiesFilePath;
    
    /**
     * Constructeur qui initialise le chemin du fichier properties.
     * @param propertiesFilePath Chemin du fichier properties
     */
    public PropertiesLogin(String propertiesFilePath) {
        this.propertiesFilePath = propertiesFilePath;
        this.userProperties = new Properties();
        loadProperties();
    }
    
    /**
     * Charge les propriétés depuis le fichier
     */
    private void loadProperties() {
        try (InputStream input = new FileInputStream(propertiesFilePath)) {
            userProperties.load(input);
            System.out.println("Fichier properties chargé avec succès: " + propertiesFilePath);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du fichier properties: " + e.getMessage());
            // Si le fichier n'existe pas, on le crée
            File file = new File(propertiesFilePath);
            if (!file.exists()) {
                try {
                    file.getParentFile().mkdirs(); // Crée les répertoires parents si nécessaire
                    file.createNewFile();

                    System.out.println("Fichier properties créé avec des utilisateurs par défaut: " + propertiesFilePath);
                } catch (IOException ex) {
                    System.err.println("Erreur lors de la création du fichier properties: " + ex.getMessage());
                }
            }
        }
    }
    
    /**
     * Sauvegarde les propriétés dans le fichier.
     */
    private void saveProperties() {
        try (OutputStream output = new FileOutputStream(propertiesFilePath)) {
            userProperties.store(output, "LocaDrive User Properties");
            System.out.println("Fichier properties sauvegardé avec succès: " + propertiesFilePath);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde du fichier properties: " + e.getMessage());
        }
    }

    
    @Override
    protected String encryptPassword(String password) {
        // Retourner une chaîne vide si le mot de passe est null
        if (password == null) {
            return "";
        }
        return password;
    }
    
    @Override
    protected boolean authenticate(String username, String encryptedPassword) {
        // Vérifier si les identifiants sont null ou vides
        if (username == null || encryptedPassword == null || 
            username.isEmpty() || encryptedPassword.isEmpty()) {
            return false;
        }
        
        if (!userProperties.containsKey(username)) {
            return false;
        }
        
        String storedValue = userProperties.getProperty(username);
        String[] parts = storedValue.split(":");
        
        if (parts.length < 1) {
            return false;
        }
        
        String storedPassword = parts[0];
        return storedPassword.equals(encryptedPassword);
    }
    
    /**
     * Ajoute un nouvel utilisateur.
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     * @param role Rôle de l'utilisateur (admin, user, etc.)
     * @return true si l'utilisateur a été ajouté avec succès, false sinon
     */
    public boolean addUser(String username, String password, String role) {
        if (username == null || password == null || role == null || 
            username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            return false;
        }
        
        if (userProperties.containsKey(username)) {
            return false; // L'utilisateur existe déjà
        }
        
        userProperties.setProperty(username, password + ":" + role);
        saveProperties();
        return true;
    }


}
