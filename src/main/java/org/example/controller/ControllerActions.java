package org.example.controller;

public class ControllerActions {

    // Actions d'authentification
    public static final String LOGIN = "LOGIN";
    public static final String LOGOUT = "LOGOUT";
    public static final String REGISTER = "REGISTER";
    
    // Actions spécifiques
    public static final String SESSION = "session";
    
    // Actions pour les voitures
    public static final String ADD_CAR = "addCar";
    public static final String MODIFY_CAR = "modifyCar";
    public static final String DELETE_CAR = "deleteCar";
    public static final String SHOW_CAR_DETAILS = "SHOW_CAR_DETAILS";
    public static final String CLOSE_DETAILS = "CLOSE_DETAILS";
    
    // Actions pour les clients
    public static final String ADD_CLIENT = "addClient";
    public static final String MODIFY_CLIENT = "modifyClient";
    public static final String DELETE_CLIENT = "deleteClient";
    
    // Actions pour les locations
    public static final String ADD_LOCATION = "addLocation";
    public static final String MODIFY_LOCATION = "modifyLocation";
    public static final String DELETE_LOCATION = "deleteLocation";
    
    // Actions pour les contrats
    public static final String ADD_CONTRAT = "addContrat";
    public static final String MODIFY_CONTRAT = "modifyContrat";
    public static final String DELETE_CONTRAT = "deleteContrat";

    // Actions d'importation et d'exportation
    public static final String IMPORT_CARS = "IMPORT_CARS";
    public static final String IMPORT_CLIENTS = "IMPORT_CLIENTS";
    public static final String IMPORT_CONTRACTS = "IMPORT_CONTRACTS";
    public static final String IMPORT_RESERVATIONS = "IMPORT_RESERVATIONS";
    public static final String EXPORT_CARS = "EXPORT_CARS";
    public static final String EXPORT_CLIENTS = "EXPORT_CLIENTS";
    public static final String EXPORT_CONTRACTS = "EXPORT_CONTRACTS";
    public static final String EXPORT_RESERVATIONS = "EXPORT_RESERVATIONS";
    
    // Action de recherche
    public static final String SEARCH = "SEARCH";
}
