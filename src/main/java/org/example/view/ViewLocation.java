package org.example.view;

import org.example.controller.Controller;
import org.example.model.entity.Car;
import org.example.model.entity.Client;
import org.example.model.entity.Contrat;
import org.example.model.entity.Reservation;
import org.example.view.GUI.ImprovedSessionDialog;
import javax.swing.JFrame;

import java.util.ArrayList;
import java.util.List;

public interface ViewLocation {

    void run();
    void showErrorMessage(String message);
    void showMessage(String message);
    void setController(Controller controller);
    void showSessionDialogFromController();
    
    // Nouvelles méthodes pour mettre à jour les tables
    void updateCarTable(List<Car> cars);
    void updateClientTable(List<Client> clients);
    void updateReservationTable(List<Reservation> reservations);
    
    // Méthode pour mettre à jour l'affichage des images de voitures
    void updateCarImages(List<Car> cars);
    
    // Méthodes pour la modification des voitures
    Car getSelectedCar();
    void showModifyCarFormFromController(Car car);

    Car promptAddCar();

    Client promptAddClient();

    Reservation promptAddLocation();

    Contrat promptAddContrat();

    void updateContratTable(List<Contrat> contrats);
    
    // Récupère le contrat sélectionné dans la table des contrats
    Contrat getSelectedContrat();
    
    // Affiche le formulaire de modification d'un contrat
    void showModifyContratFormFromController(Contrat contrat);
    
    // Récupère le client sélectionné dans la table des clients
    Client getSelectedClient();
    
    // Affiche le formulaire de modification d'un client
    void showModifyClientFormFromController(Client client);
    
    // Récupère la réservation sélectionnée dans la table des réservations
    Reservation getSelectedReservation();
    
    // Affiche le formulaire de modification d'une réservation
    void showModifyLocationFormFromController(Reservation reservation);
    
    // Méthode pour afficher les détails d'une voiture
    void showCarDetails(Car car);
    
    // Méthode pour récupérer le dialogue de session
    ImprovedSessionDialog getSessionDialog();
    
    // Méthodes pour verrouiller/déverrouiller l'interface
    void lockInterface();
    void unlockInterface();
    
    // Méthode pour vider toutes les tables
    void clearAllTables();

    
    /**
     * Récupère les valeurs de connexion (email, mot de passe)
     * @return Un tableau contenant l'email et le mot de passe
     */
    String[] getLoginValues();
    
    /**
     * Récupère les valeurs d'inscription (email, mot de passe, confirmation)
     * @return Un tableau contenant l'email, le mot de passe et la confirmation
     */
    String[] getRegisterValues();
    
    /**
     * Affiche une boîte de dialogue pour sélectionner un fichier
     * @param title Titre de la boîte de dialogue
     * @param extensions Extensions de fichiers acceptées
     * @return Le chemin du fichier sélectionné ou null si annulé
     */
    String promptForFilePath(String title, String... extensions);
    
    /**
     * Affiche une boîte de dialogue pour sauvegarder un fichier
     * @param title Titre de la boîte de dialogue
     * @param extension Extension de fichier par défaut
     * @return Le chemin du fichier à sauvegarder ou null si annulé
     */
    String promptForSaveFilePath(String title, String extension);
    
    /**
     * Retourne le texte de recherche entré par l'utilisateur
     * @return Le texte de recherche
     */
    String getSearchQuery();
    
    /**
     * Retourne l'index de l'onglet actif
     * @return L'index de l'onglet actif
     */
    int getActiveTabIndex();
    
    /**
     * Affiche les voitures filtrées dans le tableau
     * @param cars Liste des voitures à afficher
     */
    void displayCars(List<Car> cars);
    
    /**
     * Affiche les clients filtrés dans le tableau
     * @param clients Liste des clients à afficher
     */
    void displayClients(List<Client> clients);
    
    /**
     * Affiche les réservations filtrées dans le tableau
     * @param reservations Liste des réservations à afficher
     */
    void displayReservations(List<Reservation> reservations);
    
    /**
     * Affiche les contrats filtrés dans le tableau
     * @param contrats Liste des contrats à afficher
     */
    void displayContrats(List<Contrat> contrats);
}
