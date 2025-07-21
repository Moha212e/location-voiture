package org.example.model;

import org.example.model.entity.*;
import java.util.List;
import java.io.IOException;

public interface DataAccessLayer {

    // Ajouter une nouvelle entité
    int addReservation(Reservation reservation);
    int addCar(Car car);
    int addContract(Contrat contrat);
    int addClient(Client client);

    // Supprimer une entité
    boolean deleteReservation(Reservation reservation);
    boolean deleteCar(Car car);
    boolean deleteContract(Contrat contrat);
    boolean deleteClient(Client client);

    // Modifier une entité
    void updateReservation(Reservation reservation);
    void updateCar(Car car);
    void updateContract(Contrat contrat);
    void updateClient(Client client);
    
    // Méthodes pour récupérer toutes les entités
    List<Reservation> getAllReservations();
    List<Car> getAllCars();
    List<Contrat> getAllContracts();
    List<Client> getAllClients();
    
    // Méthodes pour récupérer des entités par ID
    Car getCarById(String idCar);

    // Méthodes d'importation
    void importCars(String filePath) throws IOException;
    void importClients(String filePath) throws IOException;
    void importContracts(String filePath) throws IOException;
    void importReservations(String filePath) throws IOException;

    // Méthodes d'exportation
    void exportCars(String filePath) throws IOException;
    void exportClients(String filePath) throws IOException;
    void exportContracts(String filePath) throws IOException;
    void exportReservations(String filePath) throws IOException;
    
    // Méthode pour charger les données
    void loadData();

    void updateContrat(Contrat contrat);
}
