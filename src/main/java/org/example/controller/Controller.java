package org.example.controller;

import org.example.model.DataAccessLayer;
import org.example.model.dao.DAOLocation;
import org.example.model.entity.Car;
import org.example.model.entity.Client;
import org.example.model.entity.Contrat;
import org.example.model.entity.Reservation;
import org.example.model.authentication.LoginTemplate;
import org.example.model.authentication.PropertiesLogin;
import org.example.utils.DateFormatter;
import org.example.view.ViewLocation;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.lang.reflect.Method;

public final class Controller implements ActionListener {

    private DataAccessLayer model;
    private ViewLocation view;
    private LoginTemplate loginManager;
    private boolean isLoggedIn = false; // État de connexion

    public Controller() {
        // Constructeur par défaut
    }

    public Controller(DataAccessLayer model, ViewLocation view) {
        this.model = model;
        this.view = view;
        this.view.setController(this); // tout ce que la vue doit faire avec le controller par moi (this)
        String propertiesFilePath = "data/users.properties";
        this.loginManager = new PropertiesLogin(propertiesFilePath); // Utiliser PropertiesLogin comme gestionnaire de connexion

        // L'interface est verrouillée au démarrage
        view.lockInterface();


    }

    public void run(){
        view.run();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        System.out.println("Action reçue: " + command);

        switch (command) {
            case ControllerActions.SESSION:
                handleSession();
                break;
            case ControllerActions.ADD_CAR:
                handleAddCar();
                break;
            case ControllerActions.MODIFY_CAR:
                handleModifyCar();
                break;
            case ControllerActions.DELETE_CAR:
                handleDeleteCar();
                break;
            case ControllerActions.ADD_CLIENT:
                handleAddClient();
                break;
            case ControllerActions.MODIFY_CLIENT:
                handleModifyClient();
                break;
            case ControllerActions.DELETE_CLIENT:
                handleDeleteClient();
                break;
            case ControllerActions.ADD_LOCATION:
                handleAddLocation();
                break;
            case ControllerActions.MODIFY_LOCATION:
                handleModifyLocation();
                break;
            case ControllerActions.DELETE_LOCATION:
                handleDeleteLocation();
                break;
            case ControllerActions.LOGIN:
                handleLogin();
                break;
            case ControllerActions.REGISTER:
                handleRegister();
                break;
            case ControllerActions.LOGOUT:
                handleLogout();
                break;
            case ControllerActions.IMPORT_CARS:
                handleImport("voitures");
                break;
            case ControllerActions.IMPORT_CLIENTS:
                handleImport("clients");
                break;
            case ControllerActions.IMPORT_CONTRACTS:
                handleImport("contrats");
                break;
            case ControllerActions.IMPORT_RESERVATIONS:
                handleImport("réservations");
                break;
            case ControllerActions.EXPORT_CARS:
                handleExport("voitures");
                break;
            case ControllerActions.EXPORT_CLIENTS:
                handleExport("clients");
                break;
            case ControllerActions.EXPORT_CONTRACTS:
                handleExport("contrats");
                break;
            case ControllerActions.EXPORT_RESERVATIONS:
                handleExport("réservations");
                break;
            case ControllerActions.ADD_CONTRAT:
                handleAddContrat();
                break;
            case ControllerActions.MODIFY_CONTRAT:
                handleModifyContrat();
                break;
            case ControllerActions.DELETE_CONTRAT:
                handleDeleteContrat();
                break;
            case ControllerActions.SHOW_CAR_DETAILS:
                if (e.getSource() instanceof javax.swing.JComponent source) { // il verifie si l'objet source est un composant Swing
                    if (source.getClientProperty("car") instanceof Car car) { // il verifie si le client property "car" est une voiture
                        handleShowCarDetails(car); // appel de la methode
                    }
                }
                break;
            case ControllerActions.SEARCH:
                handleSearch();
                break;
            default:
                System.out.println("Commande non reconnue: " + command);
                break;
        }
    }

    // Méthodes de gestion des actions
    private void handleSession() {
        System.out.println("Gestion de la session");
        view.showSessionDialogFromController();
    }

    private void handleAddCar() {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour ajouter une voiture");
            return;
        }

        Car a = view.promptAddCar();
        if(a != null){
            model.addCar(a);
            updateAllTables();
            view.showMessage("Voiture ajoutée avec succès");
        }
    }

    private void handleModifyCar() {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour modifier une voiture");
            return;
        }

        Car selectedCar = view.getSelectedCar();
        if (selectedCar != null) {
            view.showModifyCarFormFromController(selectedCar);
            updateAllTables();
        } else {
            view.showMessage("Veuillez sélectionner une voiture à modifier");
        }
    }

    private void handleDeleteCar() {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour supprimer une voiture");
            return;
        }

        Car selectedCar = view.getSelectedCar();
        if (selectedCar != null) {
            model.deleteCar(selectedCar);
            updateAllTables();
            view.showMessage("Voiture supprimée avec succès");
        } else {
            view.showMessage("Veuillez sélectionner une voiture à supprimer");
        }
    }

    private void handleAddClient() {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour ajouter un client");
            return;
        }

        Client client = view.promptAddClient();
        if (client != null) {
            model.addClient(client);
            updateAllTables();
            view.showMessage("Client ajouté avec succès");
        }
    }

    private void handleModifyClient() {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour modifier un client");
            return;
        }

        Client selectedClient = view.getSelectedClient();
        if (selectedClient != null) {
            view.showModifyClientFormFromController(selectedClient);
            updateAllTables();
        } else {
            view.showMessage("Veuillez sélectionner un client à modifier");
        }
    }

    private void handleDeleteClient() {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour supprimer un client");
            return;
        }

        Client selectedClient = view.getSelectedClient();
        if (selectedClient != null) {
            model.deleteClient(selectedClient);
            updateAllTables();
            view.showMessage("Client supprimé avec succès");
        } else {
            view.showMessage("Veuillez sélectionner un client à supprimer");
        }
    }

    private void handleAddLocation() {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour ajouter une réservation");
            return;
        }

        Reservation reservation = view.promptAddLocation();
        if (reservation != null) {
            model.addReservation(reservation);
            updateAllTables();
            view.showMessage("Réservation ajoutée avec succès");
        }
    }

    private void handleModifyLocation() {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour modifier une réservation");
            return;
        }

        Reservation selectedReservation = view.getSelectedReservation();
        if (selectedReservation != null) {
            view.showModifyLocationFormFromController(selectedReservation);
            updateAllTables();
        } else {
            view.showMessage("Veuillez sélectionner une réservation à modifier");
        }
    }

    private void handleDeleteLocation() {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour supprimer une réservation");
            return;
        }

        Reservation selectedReservation = view.getSelectedReservation();
        if (selectedReservation != null) {
            model.deleteReservation(selectedReservation);
            updateAllTables();
            view.showMessage("Réservation supprimée avec succès");
        } else {
            view.showMessage("Veuillez sélectionner une réservation à supprimer");
        }
    }

    private void handleAddContrat() {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour ajouter un contrat");
            return;
        }

        Contrat contrat = view.promptAddContrat();
        if (contrat != null) {
            model.addContract(contrat);
            updateAllTables();
            view.showMessage("Contrat ajouté avec succès");
        }
    }

    private void handleModifyContrat() {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour modifier un contrat");
            return;
        }

        Contrat selectedContrat = view.getSelectedContrat();
        if (selectedContrat != null) {
            view.showModifyContratFormFromController(selectedContrat);
            updateAllTables();
        } else {
            view.showMessage("Veuillez sélectionner un contrat à modifier");
        }
    }

    private void handleDeleteContrat() {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour supprimer un contrat");
            return;
        }

        Contrat selectedContrat = view.getSelectedContrat();
        if (selectedContrat != null) {
            model.deleteContract(selectedContrat);
            updateAllTables();
            view.showMessage("Contrat supprimé avec succès");
        } else {
            view.showMessage("Veuillez sélectionner un contrat à supprimer");
        }
    }

    private void handleLogin() {
        // Récupérer les informations de connexion via l'interface ViewLocation
        String[] loginValues = view.getLoginValues();

        if (loginValues != null && loginValues.length == 2) {
            String username = loginValues[0];
            String password = loginValues[1];

            if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
                // Utiliser login() au lieu de authenticate() qui est protégée
                boolean success = loginManager.login(username, password);

                if (success) {
                    isLoggedIn = true;
                    view.unlockInterface();

                    // S'assurer que les données sont chargées avant de mettre à jour les tables
                    loadModelData();

                    // Mettre à jour les tables après la connexion
                    updateAllTables();

                    view.showMessage("Connexion réussie. Bienvenue, " + username + "!");
                    view.getSessionDialog().dispose();
                    updateLoginButtonText();
                } else {
                    view.showErrorMessage("Échec de la connexion. Veuillez vérifier vos identifiants.");
                }

        }
        }
    }

    private void handleRegister() {
        // Récupérer les informations d'inscription via l'interface ViewLocation
        String[] registerValues = view.getRegisterValues();

        if (registerValues != null && registerValues.length == 3) {
            String username = registerValues[0];
            String password = registerValues[1];
            String confirmPassword = registerValues[2];

            if (username != null && !username.isEmpty() &&
                    password != null && !password.isEmpty() &&
                    confirmPassword != null && !confirmPassword.isEmpty()) {

                if (!password.equals(confirmPassword)) {
                    view.showErrorMessage("Les mots de passe ne correspondent pas.");
                    return;
                }

                // Comme LoginTemplate n'a pas de méthode register, nous devons vérifier le type
                if (loginManager instanceof PropertiesLogin) {
                    PropertiesLogin propLogin = (PropertiesLogin) loginManager;
                    boolean success = propLogin.addUser(username, password, "user");

                    if (success) {
                        view.showMessage("Inscription réussie! Vous pouvez maintenant vous connecter.");
                        view.getSessionDialog().dispose();
                        updateLoginButtonText();
                        isLoggedIn = true;
                        view.unlockInterface();
                        updateAllTables();
                    } else {
                        view.showErrorMessage("Échec de l'inscription. Cet utilisateur existe peut-être déjà.");
                    }
                } else {
                    view.showErrorMessage("L'inscription n'est pas disponible avec ce système d'authentification.");
                }
            } else {
                view.showErrorMessage("Veuillez remplir tous les champs.");
            }
        }
    }

    private void handleLogout() {
        isLoggedIn = false;
        view.lockInterface();
        view.clearAllTables();
        view.showMessage("Vous avez été déconnecté.");
        updateLoginButtonText();
    }

    private void handleImport(String type) {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour importer des données");
            return;
        }

        try {
            // Déléguer la sélection du fichier à la vue
            String filePath = view.promptForFilePath("Importer", "csv", "txt");

            if (filePath == null || filePath.isEmpty()) {
                return; // L'utilisateur a annulé
            }

            // Vérifier si le fichier est au format CSV ou TXT
            if (!filePath.toLowerCase().endsWith(".csv") && !filePath.toLowerCase().endsWith(".txt")) {
                view.showErrorMessage("Veuillez sélectionner un fichier CSV ou TXT.");
                return;
            }

            switch (type) {
                case "voitures":
                    model.importCars(filePath);
                    break;
                case "clients":
                    model.importClients(filePath);
                    break;
                case "contrats":
                    model.importContracts(filePath);
                    break;
                case "réservations":
                    model.importReservations(filePath);
                    break;
                default:
                    view.showErrorMessage("Type de données non reconnu pour l'importation." + type);
                    return;
            }

            updateAllTables();
            view.showMessage("Importation des " + type + " réussie !");
        } catch (IOException e) {
            view.showErrorMessage("Erreur lors de l'importation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleExport(String type) {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour exporter des données");
            return;
        }

        try {
           // Déléguer la sélection du fichier à la vue
            String filePath = view.promptForSaveFilePath("Exporter", "csv");

            if (filePath == null || filePath.isEmpty()) {
                return; // L'utilisateur a annulé
            }

            // Ajouter l'extension .csv si elle n'est pas présente
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }

            switch (type) {
                case "voitures":
                    model.exportCars(filePath);
                    break;
                case "clients":
                    model.exportClients(filePath);
                    break;
                case "contrats":
                    model.exportContracts(filePath);
                    break;
                case "réservations":
                    model.exportReservations(filePath);
                    break;
                default:
                    view.showErrorMessage("Type de données non reconnu pour l'exportation.");
                    return;
            }

            view.showMessage("Exportation des " + type + " réussie !");
        } catch (IOException e) {
            view.showErrorMessage("Erreur lors de l'exportation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleShowCarDetails(Car car) {
        view.showCarDetails(car);
    }

    // Méthode pour mettre à jour le texte du bouton de connexion/déconnexion
    private void updateLoginButtonText() {
        // La vue s'occupera de gérer ce changement
        // La vue s'occupera de gérer ce changement
    }

    // Méthode pour mettre à jour toutes les tables
    public void updateAllTables() {
        if (isLoggedIn) {
            view.updateCarTable(model.getAllCars());
            view.updateClientTable(model.getAllClients());
            view.updateReservationTable(model.getAllReservations());
            view.updateContratTable(model.getAllContracts());
            view.updateCarImages(model.getAllCars());
        }
    }

    /**
     * Méthode pour charger les données du modèle
     * Cette méthode utilise directement l'interface DataAccessLayer
     */
    private void loadModelData() {
        try {
            // Appel direct de la méthode loadData de l'interface
            model.loadData();
            System.out.println("Données chargées avec succès");
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement des données: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Récupère toutes les voitures du modèle
     * @return Liste des voitures
     */
    public List<Car> getAllCars() {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour accéder aux données des voitures");
            return null;
        }
        return model.getAllCars();
    }

    /**
     * Récupère une voiture par son identifiant
     * @param idCar L'identifiant de la voiture
     * @return La voiture correspondante ou null si non trouvée
     */
    public Car getCarById(String idCar) {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour accéder aux données des voitures");
            return null;
        }
        return model.getCarById(idCar);
    }

    /**
     * Récupère tous les clients du modèle
     * @return Liste des clients
     */
    public List<Client> getAllClients() {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour accéder aux données des clients");
            return null;
        }
        return model.getAllClients();
    }

    /**
     * Récupère un client par son identifiant
     * @param idClient L'identifiant du client
     * @return Le client correspondant ou null si non trouvé
     */
    public Client getClientById(int idClient) {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour accéder aux données des clients");
            return null;
        }
        List<Client> clients = model.getAllClients();
        for (Client client : clients) {
            if (client.getIdClient() == idClient) {
                return client;
            }
        }
        return null;
    }

    /**
     * Récupère toutes les réservations du modèle
     * @return Liste des réservations
     */
    public List<Reservation> getAllReservations() {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour accéder aux données des réservations");
            return null;
        }
        return model.getAllReservations();
    }

    /**
     * Récupère une réservation par son identifiant
     * @param idReservation L'identifiant de la réservation
     * @return La réservation correspondante ou null si non trouvée
     */
    public Reservation getReservationById(int idReservation) {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour accéder aux données des réservations");
            return null;
        }
        List<Reservation> reservations = model.getAllReservations();
        for (Reservation reservation : reservations) {
            if (reservation.getIdReservation() == idReservation) {
                return reservation;
            }
        }
        return null;
    }

    /**
     * Récupère tous les contrats du modèle
     * @return Liste des contrats
     */
    public List<Contrat> getAllContracts() {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour accéder aux données des contrats");
            return null;
        }
        return model.getAllContracts();
    }

    /**
     * Récupère un contrat par son identifiant
     * @param idContrat L'identifiant du contrat
     * @return Le contrat correspondant ou null si non trouvé
     */
    public Contrat getContratById(String idContrat) {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour accéder aux données des contrats");
            return null;
        }
        List<Contrat> contrats = model.getAllContracts();
        for (Contrat contrat : contrats) {
            if (contrat.getIdContrat().equals(idContrat)) {
                return contrat;
            }
        }
        return null;
    }

    /**
     * Met à jour une voiture dans le modèle
     * @param car La voiture à mettre à jour
     */
    public void updateCar(Car car) {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour modifier une voiture");
            return;
        }
        if (car != null) {
            model.updateCar(car);
            updateAllTables();
        }
    }

    /**
     * Met à jour un client dans le modèle
     * @param client Le client à mettre à jour
     */
    public void updateClient(Client client) {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour modifier un client");
            return;
        }
        if (client != null) {
            model.updateClient(client);
            updateAllTables();
        }
    }

    /**
     * Met à jour une réservation dans le modèle
     * @param reservation La réservation à mettre à jour
     */
    public void updateReservation(Reservation reservation) {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour modifier une réservation");
            return;
        }
        if (reservation != null) {
            model.updateReservation(reservation);
            updateAllTables();
        }
    }

    /**
     * Met à jour un contrat dans le modèle
     * @param contrat Le contrat à mettre à jour
     */
    public void updateContrat(Contrat contrat) {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour modifier un contrat");
            return;
        }
        if (contrat != null) {
            model.updateContract(contrat);
            updateAllTables();
        }
    }

    /**
     * Vérifie si l'utilisateur est connecté
     * @return true si l'utilisateur est connecté, false sinon
     */
    public boolean isUserLoggedIn() {
        return isLoggedIn;
    }

    /**
     * Ajoute une réservation via le controller
     * @param reservation La réservation à ajouter
     */
    public void addReservation(Reservation reservation) {
        if (!isLoggedIn) {
            view.showMessage("Veuillez vous connecter pour ajouter une réservation");
            return;
        }
        if (reservation != null) {
            model.addReservation(reservation);
            updateAllTables();
        }
    }

    /**
     * Gère la recherche dans les tableaux
     */
    private void handleSearch() {
        if (isLoggedIn) {
            String query = view.getSearchQuery();
            int activeTab = view.getActiveTabIndex();

            if (query == null || query.trim().isEmpty()) {
                // Si la recherche est vide, réinitialiser tous les tableaux
                updateAllTables();
                return;
            }

        // Convertir la requête en minuscules pour une recherche insensible à la casse
        String lowercaseQuery = query.toLowerCase();
        
        switch (activeTab) {
            case 0: // Onglet Voitures
                filterAndDisplayCars(lowercaseQuery);
                break;
            case 1: // Onglet Clients
                filterAndDisplayClients(lowercaseQuery);
                break;
            case 2: // Onglet Réservations
                filterAndDisplayReservations(lowercaseQuery);
                break;
            case 3: // Onglet Contrats
                filterAndDisplayContrats(lowercaseQuery);
                break;
            default:
                // Par défaut, rechercher dans tous les onglets
                filterAndDisplayCars(lowercaseQuery);
                filterAndDisplayClients(lowercaseQuery);
                filterAndDisplayReservations(lowercaseQuery);
                filterAndDisplayContrats(lowercaseQuery);
                break;
        }
        }else{
            view.showMessage("pour chercher quelque chose tu dois te connecter avant !!!");
        }

    }
    
    /**
     * Filtre et affiche les voitures correspondant à la recherche
     * @param query La requête de recherche
     */
    private void filterAndDisplayCars(String query) {
        List<Car> allCars = model.getAllCars();
        List<Car> filteredCars = new ArrayList<>();
        
        for (Car car : allCars) {
            // Vérifier si la voiture correspond à la recherche
            if (car.getBrand().toLowerCase().contains(query) ||
                car.getModel().toLowerCase().contains(query) ||
                car.getIdCar().toLowerCase().contains(query) ||
                String.valueOf(car.getYear()).contains(query) ||
                String.valueOf(car.getPriceday()).contains(query)) {
                filteredCars.add(car);
            }
        }
        
        // Mettre à jour le tableau des voitures dans la vue
        view.displayCars(filteredCars);
    }
    
    /**
     * Filtre et affiche les clients correspondant à la recherche
     * @param query La requête de recherche
     */
    private void filterAndDisplayClients(String query) {
        List<Client> allClients = model.getAllClients();
        List<Client> filteredClients = new ArrayList<>();
        
        for (Client client : allClients) {
            // Vérifier si le client correspond à la recherche
            if (client.getName().toLowerCase().contains(query) ||
                client.getSurname().toLowerCase().contains(query) ||
                client.getEmail().toLowerCase().contains(query) ||
                client.getPhoneNumber().toLowerCase().contains(query) ||
                client.getAddress().toLowerCase().contains(query)) {
                filteredClients.add(client);
            }
        }
        
        // Mettre à jour le tableau des clients dans la vue
        view.displayClients(filteredClients);
    }
    
    /**
     * Filtre et affiche les réservations correspondant à la recherche
     * @param query La requête de recherche
     */
    private void filterAndDisplayReservations(String query) {
        List<Reservation> allReservations = model.getAllReservations();
        List<Reservation> filteredReservations = new ArrayList<>();
        
        for (Reservation reservation : allReservations) {
            // Vérifier si la réservation correspond à la recherche
            if (String.valueOf(reservation.getIdReservation()).contains(query) ||
                DateFormatter.format(reservation.getStartDate()).toLowerCase().contains(query) ||
                DateFormatter.format(reservation.getEndDate()).toLowerCase().contains(query) ||
                String.valueOf(reservation.getClientId()).contains(query) ||
                String.valueOf(reservation.getCarId()).contains(query)) {
                filteredReservations.add(reservation);
            }
        }
        
        // Mettre à jour le tableau des réservations dans la vue
        view.displayReservations(filteredReservations);
    }
    
    /**
     * Filtre et affiche les contrats correspondant à la recherche
     * @param query La requête de recherche
     */
    private void filterAndDisplayContrats(String query) {
        List<Contrat> allContrats = model.getAllContracts();
        List<Contrat> filteredContrats = new ArrayList<>();
        
        for (Contrat contrat : allContrats) {
            // Vérifier si le contrat correspond à la recherche
            if (String.valueOf(contrat.getIdContrat()).contains(query) ||
                String.valueOf(contrat.getReservationId()).contains(query) ||
                String.valueOf(contrat.getStatutContrat()).contains(query)) {
                filteredContrats.add(contrat);
            }
        }
        
        // Mettre à jour le tableau des contrats dans la vue
        view.displayContrats(filteredContrats);
    }

    /**
     * Retourne le modèle de données
     * @return Le modèle de données
     */
    public DataAccessLayer getModel() {
        return model;
    }
}
