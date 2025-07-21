package org.example.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

/**
 * Classe représentant un véhicule disponible à la location.
 */
public class Car implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    
    @JsonProperty("idCar")
    private String idCar; // Immatriculation belge (ex: 1-ABC-123)
    @JsonProperty("brand")
    private String brand;
    @JsonProperty("model")
    private String model;
    @JsonProperty("year")
    private int year;
    @JsonProperty("priceday")
    private float priceday;
    @JsonProperty("mileage")
    private int mileage; // Kilométrage
    @JsonProperty("fuelType")
    private String fuelType; // Type de carburant
    @JsonProperty("transmission")
    private String transmission; // Type de transmission
    @JsonProperty("seats")
    private int seats; // Nombre de places
    @JsonProperty("available")
    private boolean available; // Disponibilité
    @JsonProperty("image")
    private String image;
    
    /**
     * Constructeur par défaut.
     */
    public Car() {
        this.idCar = "";
        this.brand = "";
        this.model = "";
        this.year = 0;
        this.priceday = 0;
        this.available = true;
        this.image = "";
    }

    /**
     * Constructeur avec les attributs de base.
     */
    public Car(String idCar, String brand, String model, int year, float priceday) {
        this.idCar = idCar;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.priceday = priceday;
        this.available = true;
        this.image = "";
    }

    /**
     * Constructeur complet avec tous les attributs.
     */
    public Car(String idCar, String brand, String model, int year, float priceday, 
               int mileage, String fuelType, String transmission, int seats, 
               boolean available, String image) {
        this.idCar = idCar;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.priceday = priceday;
        this.mileage = mileage;
        // S'assurer que fuelType n'est jamais null
        this.fuelType = (fuelType != null) ? fuelType : "Essence";
        // S'assurer que transmission n'est jamais null
        this.transmission = (transmission != null) ? transmission : "Manuelle";
        this.seats = seats;
        this.available = available;
        this.image = (image != null) ? image : "";
    }

    @JsonProperty("idCar")
    public String getIdCar() {
        return idCar;
    }

    @JsonProperty("idCar")
    public void setIdCar(String idCar) {
        this.idCar = idCar;
    }

    @JsonProperty("brand")
    public String getBrand() {
        return brand;
    }

    @JsonProperty("brand")
    public void setBrand(String brand) {
        this.brand = brand;
    }

    @JsonProperty("model")
    public String getModel() {
        return model;
    }

    @JsonProperty("model")
    public void setModel(String model) {
        this.model = model;
    }

    @JsonProperty("year")
    public int getYear() {
        return year;
    }

    @JsonProperty("year")
    public void setYear(int year) {
        this.year = year;
    }

    @JsonProperty("priceday")
    public float getPriceday() {
        return priceday;
    }

    @JsonProperty("priceday")
    public void setPriceday(float priceday) {
        this.priceday = priceday;
    }

    @JsonProperty("mileage")
    public int getMileage() {
        return mileage;
    }

    @JsonProperty("mileage")
    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    @JsonProperty("fuelType")
    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        // S'assurer que fuelType n'est jamais null
        this.fuelType = (fuelType != null) ? fuelType : "Essence";
    }

    @JsonProperty("transmission")
    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        // S'assurer que transmission n'est jamais null
        this.transmission = (transmission != null) ? transmission : "Manuelle";
    }

    @JsonProperty("seats")
    public int getSeats() {
        return seats;
    }

    @JsonProperty("seats")
    public void setSeats(int seats) {
        this.seats = seats;
    }

    @JsonProperty("available")
    public boolean isAvailable() {
        return available;
    }

    @JsonProperty("available")
    public void setAvailable(boolean available) {
        this.available = available;
    }

    @JsonProperty("image")
    public String getImage() {
        return image;
    }

    @JsonProperty("image")
    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Car{idCar='" + idCar + "', brand='" + brand + "', model='" + model + "', year=" + year + ", priceday=" + priceday + ", mileage=" + mileage + ", fuelType='" + fuelType + "', transmission='" + transmission + "', seats=" + seats + ", available=" + available + ", image='" + image + "'}";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return idCar.equals(car.idCar);
    }

    public String getType() {
        return transmission;
    }

    public String getFuel() {
        return fuelType;
    }
}
