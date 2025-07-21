package org.example.utils;

import org.example.model.dao.DAOLocation;
import org.example.model.entity.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Gestionnaire de sauvegarde automatique des données
 */
public class AutoBackupManager {
    
    private static final String BACKUP_DIR = "backups";
    private static final String CONFIG_FILE = "backup_config.properties";
    private static final DateTimeFormatter BACKUP_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    
    private final DAOLocation dao;
    private final ScheduledExecutorService scheduler;
    private boolean isEnabled = true;
    private int backupIntervalHours = 24; // Sauvegarde quotidienne par défaut
    
    public AutoBackupManager(DAOLocation dao) {
        this.dao = dao;
        this.scheduler = Executors.newScheduledThreadPool(1);
        loadConfiguration();
        createBackupDirectory();
        startScheduledBackup();
    }
    
    /**
     * Charge la configuration de sauvegarde
     */
    private void loadConfiguration() {
        Properties props = new Properties();
        File configFile = new File(CONFIG_FILE);
        
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                props.load(fis);
                isEnabled = Boolean.parseBoolean(props.getProperty("backup.enabled", "true"));
                backupIntervalHours = Integer.parseInt(props.getProperty("backup.interval.hours", "24"));
            } catch (IOException e) {
                System.err.println("Erreur lors du chargement de la configuration de sauvegarde: " + e.getMessage());
            }
        } else {
            // Créer la configuration par défaut
            saveConfiguration();
        }
    }
    
    /**
     * Sauvegarde la configuration
     */
    private void saveConfiguration() {
        Properties props = new Properties();
        props.setProperty("backup.enabled", String.valueOf(isEnabled));
        props.setProperty("backup.interval.hours", String.valueOf(backupIntervalHours));
        
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "Configuration de sauvegarde automatique");
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde de la configuration: " + e.getMessage());
        }
    }
    
    /**
     * Crée le répertoire de sauvegarde s'il n'existe pas
     */
    private void createBackupDirectory() {
        try {
            Files.createDirectories(Paths.get(BACKUP_DIR));
        } catch (IOException e) {
            System.err.println("Erreur lors de la création du répertoire de sauvegarde: " + e.getMessage());
        }
    }
    
    /**
     * Démarre la sauvegarde planifiée
     */
    private void startScheduledBackup() {
        if (isEnabled) {
            scheduler.scheduleAtFixedRate(
                this::performBackup,
                backupIntervalHours,
                backupIntervalHours,
                TimeUnit.HOURS
            );
            System.out.println("Sauvegarde automatique activée - Intervalle: " + backupIntervalHours + " heures");
        }
    }
    
    /**
     * Effectue une sauvegarde complète
     */
    public void performBackup() {
        if (!isEnabled) {
            return;
        }
        
        try {
            String timestamp = LocalDateTime.now().format(BACKUP_DATE_FORMAT);
            String backupPath = BACKUP_DIR + File.separator + "backup_" + timestamp;
            
            // Créer le répertoire de sauvegarde
            Files.createDirectories(Paths.get(backupPath));
            
            // Sauvegarder les données
            backupCars(backupPath);
            backupClients(backupPath);
            backupReservations(backupPath);
            backupContrats(backupPath);
            
            // Créer un fichier de métadonnées
            createBackupMetadata(backupPath, timestamp);
            
            // Nettoyer les anciennes sauvegardes
            cleanupOldBackups();
            
            System.out.println("Sauvegarde automatique effectuée: " + backupPath);
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde automatique: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Sauvegarde les voitures
     */
    private void backupCars(String backupPath) throws IOException {
        List<Car> cars = dao.getAllCars();
        String filePath = backupPath + File.separator + "cars.csv";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // En-têtes
            writer.println("ID,Marque,Modèle,Année,Prix/Jour,Kilométrage,Carburant,Transmission,Places,Disponible,Image");
            
            // Données
            for (Car car : cars) {
                writer.printf("%s,%s,%s,%d,%.2f,%d,%s,%s,%d,%s,%s%n",
                    car.getIdCar(),
                    escapeCsv(car.getBrand()),
                    escapeCsv(car.getModel()),
                    car.getYear(),
                    car.getPriceday(),
                    car.getMileage(),
                    escapeCsv(car.getFuelType()),
                    escapeCsv(car.getTransmission()),
                    car.getSeats(),
                    car.isAvailable() ? "Oui" : "Non",
                    escapeCsv(car.getImage())
                );
            }
        }
    }
    
    /**
     * Sauvegarde les clients
     */
    private void backupClients(String backupPath) throws IOException {
        List<Client> clients = dao.getAllClients();
        String filePath = backupPath + File.separator + "clients.csv";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // En-têtes
            writer.println("ID,Nom,Prénom,Email,Téléphone,Adresse,Date de naissance");
            
            // Données
            for (Client client : clients) {
                writer.printf("%d,%s,%s,%s,%s,%s,%s%n",
                    client.getIdClient(),
                    escapeCsv(client.getName()),
                    escapeCsv(client.getSurname()),
                    escapeCsv(client.getEmail()),
                    escapeCsv(client.getPhoneNumber()),
                    escapeCsv(client.getAddress()),
                    client.getBirthDate() != null ? client.getBirthDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : ""
                );
            }
        }
    }
    
    /**
     * Sauvegarde les réservations
     */
    private void backupReservations(String backupPath) throws IOException {
        List<Reservation> reservations = dao.getAllReservations();
        String filePath = backupPath + File.separator + "reservations.csv";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // En-têtes
            writer.println("ID,Date début,Date fin,Responsable,Prix,ID Client,ID Voiture,Notes");
            
            // Données
            for (Reservation reservation : reservations) {
                writer.printf("%d,%s,%s,%s,%.2f,%d,%s,%s%n",
                    reservation.getIdReservation(),
                    reservation.getStartDate() != null ? reservation.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : "",
                    reservation.getEndDate() != null ? reservation.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : "",
                    escapeCsv(reservation.getResponsable()),
                    reservation.getPrice(),
                    reservation.getClientId(),
                    escapeCsv(reservation.getCarId()),
                    escapeCsv(reservation.getNotes())
                );
            }
        }
    }
    
    /**
     * Sauvegarde les contrats
     */
    private void backupContrats(String backupPath) throws IOException {
        List<Contrat> contrats = dao.getAllContracts();
        String filePath = backupPath + File.separator + "contrats.csv";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // En-têtes
            writer.println("ID,Caution,Assurance,Prix Assurance,Prix Total,Statut,Signé,ID Réservation,Client,Véhicule");
            
            // Données
            for (Contrat contrat : contrats) {
                writer.printf("%s,%.2f,%s,%.2f,%.2f,%s,%s,%d,%s,%s%n",
                    contrat.getIdContrat(),
                    contrat.getCaution(),
                    escapeCsv(contrat.getTypeAssurance()),
                    contrat.getPrixAssurance(),
                    contrat.getPrixTotal(),
                    contrat.getStatutContrat().toString(),
                    contrat.isEstSigne() ? "Oui" : "Non",
                    contrat.getReservationId(),
                    escapeCsv((contrat.getClientName() != null ? contrat.getClientName() : "") + " " +
                             (contrat.getClientSurname() != null ? contrat.getClientSurname() : "")),
                    escapeCsv((contrat.getCarBrand() != null ? contrat.getCarBrand() : "") + " " +
                             (contrat.getCarModel() != null ? contrat.getCarModel() : ""))
                );
            }
        }
    }
    
    /**
     * Crée un fichier de métadonnées pour la sauvegarde
     */
    private void createBackupMetadata(String backupPath, String timestamp) throws IOException {
        String metadataPath = backupPath + File.separator + "backup_info.txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(metadataPath))) {
            writer.println("=== INFORMATIONS DE SAUVEGARDE ===");
            writer.println("Date de sauvegarde: " + timestamp);
            writer.println("Version de l'application: 1.0");
            writer.println("Nombre de voitures: " + dao.getAllCars().size());
            writer.println("Nombre de clients: " + dao.getAllClients().size());
            writer.println("Nombre de réservations: " + dao.getAllReservations().size());
            writer.println("Nombre de contrats: " + dao.getAllContracts().size());
            writer.println("Format des fichiers: CSV");
            writer.println("Encodage: UTF-8");
        }
    }
    
    /**
     * Nettoie les anciennes sauvegardes (garde les 10 plus récentes)
     */
    private void cleanupOldBackups() {
        try {
            Path backupDir = Paths.get(BACKUP_DIR);
            if (!Files.exists(backupDir)) {
                return;
            }
            
            // Lister tous les répertoires de sauvegarde
            var backupDirs = Files.list(backupDir)
                .filter(Files::isDirectory)
                .filter(path -> path.getFileName().toString().startsWith("backup_"))
                .sorted((p1, p2) -> p2.compareTo(p1)) // Tri décroissant (plus récent en premier)
                .toList();
            
            // Supprimer les sauvegardes au-delà de la 10ème
            if (backupDirs.size() > 10) {
                for (int i = 10; i < backupDirs.size(); i++) {
                    deleteDirectory(backupDirs.get(i));
                    System.out.println("Ancienne sauvegarde supprimée: " + backupDirs.get(i));
                }
            }
            
        } catch (IOException e) {
            System.err.println("Erreur lors du nettoyage des anciennes sauvegardes: " + e.getMessage());
        }
    }
    
    /**
     * Supprime récursivement un répertoire
     */
    private void deleteDirectory(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Files.list(path).forEach(child -> {
                try {
                    deleteDirectory(child);
                } catch (IOException e) {
                    System.err.println("Erreur lors de la suppression: " + e.getMessage());
                }
            });
        }
        Files.delete(path);
    }
    
    /**
     * Échappe les caractères spéciaux pour CSV
     */
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    /**
     * Active ou désactive la sauvegarde automatique
     */
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
        saveConfiguration();
        
        if (enabled) {
            startScheduledBackup();
        } else {
            scheduler.shutdown();
        }
    }
    
    /**
     * Définit l'intervalle de sauvegarde
     */
    public void setBackupInterval(int hours) {
        this.backupIntervalHours = hours;
        saveConfiguration();
        
        if (isEnabled) {
            scheduler.shutdown();
            startScheduledBackup();
        }
    }
    
    /**
     * Effectue une sauvegarde manuelle
     */
    public void performManualBackup() {
        System.out.println("Démarrage de la sauvegarde manuelle...");
        performBackup();
    }
    
    /**
     * Arrête le gestionnaire de sauvegarde
     */
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
} 