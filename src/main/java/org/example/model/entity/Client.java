package org.example.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Classe représentant un client de l'application de location de véhicules.
 */
public class Client implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    
    @JsonProperty("idClient")
    private int idClient;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("surname")
    private String surname;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("phoneNumber")
    private String phoneNumber; // Numéro de téléphone
    
    @JsonProperty("birthDate")
    private LocalDate birthDate;
    
    @JsonProperty("licenseNumber")
    private String licenseNumber; // Numéro de permis
    
    @JsonProperty("address")
    private String address; // Adresse

    /**
     * Constructeur par défaut.
     */
    public Client() {
        this.idClient = 0;
        this.name = "";
        this.surname = "";
        this.email = "";
        this.birthDate = null;
        this.phoneNumber = "";
        this.address = "";
    }

    /**
     * Constructeur avec les attributs de base.
     */
    public Client(int idClient, String name, String surname, String email, LocalDate birthDate) {
        this.idClient = idClient;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.birthDate = birthDate;
        this.phoneNumber = "";
        this.address = "";
    }

    /**
     * Constructeur complet avec tous les attributs.
     */
    public Client(int idClient, String name, String surname, String email,
                 LocalDate birthDate, String phoneNumber, String address) {
        this.idClient = idClient;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }


    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Client{idClient=" + idClient + ", name='" + name + "', surname='" + surname + "', email='" + email + "', birthDate=" + birthDate + ", phoneNumber='" + phoneNumber + "', address='" + address + "'}";
    }
}
