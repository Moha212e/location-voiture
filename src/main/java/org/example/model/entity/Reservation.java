package org.example.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Classe représentant une réservation de véhicule.
 */
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int idReservation;
    
    @JsonIgnore
    private transient Car car;
    
    @JsonIgnore
    private transient Client client;
    
    private LocalDate startDate;
    private LocalDate endDate;
    private String responsable;
    private String notes;
    private float price;
    
    @JsonIgnore
    private transient Contrat contrat;
    
    // Attributs pour stocker les identifiants lors de la sérialisation
    private String carId;
    private int clientId;
    private String contratId;
    
    // Attributs pour stocker le nom complet du client et l'immatriculation de la voiture
    private String clientFullName;
    private String carRegistration;

    public Reservation() {
        this.idReservation = 0;
        this.startDate = null;
        this.endDate = null;
        this.responsable = "";
        this.notes = "";
        this.price = 0;
        this.clientFullName = "";
        this.carRegistration = "";
    }

    public Reservation(int idReservation, LocalDate startDate, LocalDate endDate, String responsable, String notes, float price) {
        this.idReservation = idReservation;
        this.startDate = startDate;
        this.endDate = endDate;
        this.responsable = responsable;
        this.notes = notes;
        this.price = price;
        this.clientFullName = "";
        this.carRegistration = "";
    }

    public Reservation(int idReservation, Car car, Client client, LocalDate startDate, LocalDate endDate, String responsable, float price, String notes, Contrat contrat) {
        this.idReservation = idReservation;
        this.car = car;
        this.client = client;
        this.startDate = startDate;
        this.endDate = endDate;
        this.responsable = responsable;
        this.notes = notes;
        this.price = price;
        this.contrat = contrat;
        
        // Stocker les identifiants
        if (car != null) {
            this.carId = car.getIdCar();
            this.carRegistration = car.getIdCar(); // L'immatriculation est stockée dans idCar
        }
        if (client != null) {
            this.clientId = client.getIdClient();
            this.clientFullName = client.getName() + " " + client.getSurname(); // Concaténation du nom et prénom
        }
        if (contrat != null) {
            this.contratId = contrat.getIdContrat();
        }
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    @JsonIgnore
    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
        if (car != null) {
            this.carId = car.getIdCar();
            this.carRegistration = car.getIdCar(); // L'immatriculation est stockée dans idCar
        } else {
            this.carId = null;
            this.carRegistration = null;
        }
    }

    @JsonIgnore
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
        if (client != null) {
            this.clientId = client.getIdClient();
            this.clientFullName = client.getName() + " " + client.getSurname(); // Concaténation du nom et prénom
        } else {
            this.clientId = 0;
            this.clientFullName = null;
        }
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public float getPrice() {
        return price;
    }
    
    public void setPrice(float price) {
        this.price = price;
    }
    
    @JsonIgnore
    public Contrat getContrat() {
        return contrat;
    }
    
    public void setContrat(Contrat contrat) {
        this.contrat = contrat;
        if (contrat != null) {
            this.contratId = contrat.getIdContrat();
        } else {
            this.contratId = null;
        }
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getContratId() {
        return contratId;
    }

    public void setContratId(String contratId) {
        this.contratId = contratId;
    }

    public String getClientFullName() {
        return clientFullName;
    }

    public void setClientFullName(String clientFullName) {
        this.clientFullName = clientFullName;
    }

    public String getCarRegistration() {
        return carRegistration;
    }

    public void setCarRegistration(String carRegistration) {
        this.carRegistration = carRegistration;
    }

    @Override
    public String toString() {
        return "Reservation{idReservation=" + idReservation + ", startDate=" + startDate + ", endDate=" + endDate + ", responsable='" + responsable + '\'' + ", notes='" + notes + '\'' + ", price=" + price + ", clientFullName='" + clientFullName + '\'' + ", carRegistration='" + carRegistration + '\'' + '}';
    }
}
