package org.example.model.dao;

import org.example.model.entity.Car;
import org.example.model.entity.Client;
import org.example.model.entity.Contrat;
import org.example.model.entity.Reservation;
import org.example.model.entity.StatutContrat;
import org.example.controller.Controller;
import org.example.model.DataAccessLayer;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implémentation de l'interface DataAccessLayer pour gérer les données de l'application.
 * Cette classe utilise la sérialisation d'objets Java pour stocker les données dans des fichiers.
 */
public class DAOLocation implements DataAccessLayer {
    // Chemins des fichiers de données
    private static final String DATA_DIR = "data";
    private static final String RESERVATIONS_FILE = DATA_DIR + File.separator + "reservations.ser";
    private static final String CARS_FILE = DATA_DIR + File.separator + "cars.ser";
    private static final String CONTRACTS_FILE = DATA_DIR + File.separator + "contracts.ser";
    private static final String CLIENTS_FILE = DATA_DIR + File.separator + "clients.ser";
    private static final String COUNTERS_FILE = DATA_DIR + File.separator + "counters.ser";

    private AtomicInteger reservationIdGenerator;
    private AtomicInteger carIdGenerator;
    private AtomicInteger contractIdGenerator;
    private AtomicInteger clientIdGenerator;

    private Map<Integer, Reservation> reservations;
    private Map<String, Car> cars;
    private Map<String, Contrat> contracts;
    private Map<Integer, Client> clients;

    // Référence au controller pour mettre à jour les tables
    private Controller controller;

    /**
     * Constructeur qui initialise les collections et charge les données depuis les fichiers.
     */
    public DAOLocation() {
        // Initialiser les collections
        reservations = new HashMap<>();
        cars = new HashMap<>();
        contracts = new HashMap<>();
        clients = new HashMap<>();

        // Initialiser les générateurs d'ID
        reservationIdGenerator = new AtomicInteger(1);
        carIdGenerator = new AtomicInteger(1);
        contractIdGenerator = new AtomicInteger(1);
        clientIdGenerator = new AtomicInteger(1);


        // Créer le répertoire de données s'il n'existe pas
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        // Charger les données depuis les fichiers
        loadData();
    }

    /**
     * Définit le controller à utiliser pour mettre à jour les tables
     * @param controller Le controller à utiliser
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Charge les données depuis les fichiers sérialisés.
     */
    @SuppressWarnings("unchecked")
    public void loadData() {
        try {
            // Vider les collections actuelles
            reservations.clear();
            cars.clear();
            contracts.clear();
            clients.clear();

            // Charger les compteurs
            File countersFile = new File(COUNTERS_FILE);
            if (countersFile.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(countersFile))) {
                    Map<String, Integer> counters = (Map<String, Integer>) ois.readObject();
                    reservationIdGenerator = new AtomicInteger(counters.getOrDefault("reservationId", 1));
                    carIdGenerator = new AtomicInteger(counters.getOrDefault("carId", 1));
                    contractIdGenerator = new AtomicInteger(counters.getOrDefault("contractId", 1));
                    clientIdGenerator = new AtomicInteger(counters.getOrDefault("clientId", 1));
                }
            }

            // 1. Charger les voitures (entité indépendante)
            File carsFile = new File(CARS_FILE);
            if (carsFile.exists() && carsFile.length() > 0) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(carsFile))) {
                    cars = (Map<String, Car>) ois.readObject();
                    System.out.println("Voitures chargées: " + cars.size());
                }
            }

            // 2. Charger les clients (entité indépendante)
            File clientsFile = new File(CLIENTS_FILE);
            if (clientsFile.exists() && clientsFile.length() > 0) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(clientsFile))) {
                    clients = (Map<Integer, Client>) ois.readObject();
                    System.out.println("Clients chargés: " + clients.size());
                }
            }

            // 3. Charger les contrats (dépend des réservations)
            File contractsFile = new File(CONTRACTS_FILE);
            if (contractsFile.exists() && contractsFile.length() > 0) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(contractsFile))) {
                    contracts = (Map<String, Contrat>) ois.readObject();
                    System.out.println("Contrats chargés: " + contracts.size());
                }
            }

            // 4. Charger les réservations (dépend des voitures et clients)
            File reservationsFile = new File(RESERVATIONS_FILE);
            if (reservationsFile.exists() && reservationsFile.length() > 0) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(reservationsFile))) {
                    reservations = (Map<Integer, Reservation>) ois.readObject();
                    System.out.println("Réservations chargées: " + reservations.size());
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors du chargement des données sérialisées: " + e.getMessage());
            e.printStackTrace();

        }
    }

    @Override
    public void updateContrat(Contrat contrat) {

    }


    public int addReservation(Reservation reservation) {
        if (reservation.getIdReservation() == 0) {
            int id = reservationIdGenerator.getAndIncrement();
            reservation.setIdReservation(id);
        }

        // Stocker uniquement les identifiants des objets référencés
        if (reservation.getCar() != null) {
            String carId = reservation.getCar().getIdCar();
            reservation.setCarId(carId);
            reservation.setCarRegistration(carId);

            // Ne pas modifier la voiture, juste récupérer son état de disponibilité
            Car car = cars.get(carId);
            if (car != null) {
                car.setAvailable(false);
                cars.put(carId, car); //maj de la voiture
                System.out.println("Voiture " + carId + " marquée comme non disponible");
            }
        }

        if (reservation.getClient() != null) {
            int clientId = reservation.getClient().getIdClient();
            reservation.setClientId(clientId);

            // Récupérer le nom complet du client sans modifier l'objet client
            Client client = clients.get(clientId);
            if (client != null) {
                reservation.setClientFullName(client.getName() + " " + client.getSurname());
            }
        }

        if (reservation.getContrat() != null) {
            String contratId = reservation.getContrat().getIdContrat();
            reservation.setContratId(contratId);
        }

        // Stocker la réservation
        reservations.put(reservation.getIdReservation(), reservation);
        saveData(); // Sauvegarder après modification
        return reservation.getIdReservation();
    }

    public int addCar(Car car) {
        if (car.getIdCar() == null || car.getIdCar().isEmpty()) {
            // Générer un ID d'immatriculation si nécessaire
            String id = "1-XYZ-" + carIdGenerator.getAndIncrement();
            car.setIdCar(id);
        }
        cars.put(car.getIdCar(), car);
        saveData(); // Sauvegarder après modification
        return 1; // Succès
    }

    public int addContract(Contrat contrat) {
        if (contrat.getIdContrat() == null || contrat.getIdContrat().isEmpty()) {
            String id = "C" + contractIdGenerator.getAndIncrement();
            contrat.setIdContrat(id);
        }
        contracts.put(contrat.getIdContrat(), contrat);
        saveData(); // Sauvegarder après modification
        return 1; // Succès
    }

    public int addClient(Client client) {
        if (client.getIdClient() == 0) {
            int id = clientIdGenerator.getAndIncrement();
            client.setIdClient(id);
        }
        clients.put(client.getIdClient(), client);
        saveData(); // Sauvegarder après modification
        return client.getIdClient();
    }

    /**
     *  Supprime une reservation de la collection
     * @param reservation
     * @return true si la reservation a ete supprimée, false sinon
     */
    public boolean deleteReservation(Reservation reservation) {
        // Récupérer la réservation avant de la supprimer
        Reservation existingReservation = reservations.get(reservation.getIdReservation());

        if (existingReservation != null) {
            // Marquer la voiture comme disponible
            if (existingReservation.getCarId() != null && !existingReservation.getCarId().isEmpty()) {
                // Si seul l'ID de la voiture est disponible
                Car car = cars.get(existingReservation.getCarId());
                if (car != null) {
                    car.setAvailable(true);
                    cars.put(car.getIdCar(), car);
                    System.out.println("Voiture " + car.getIdCar() + " marquée comme disponible");
                }
            }
        }

        boolean result = reservations.remove(reservation.getIdReservation()) != null;
        if (result) {
            saveData(); // Sauvegarder après modification
        }
        return result;
    }

    /**
     * Supprime une voiture de la collection
     * @param car La voiture à supprimer
     * @return true si la voiture a été supprimée, false sinon
     */
    public boolean deleteCar(Car car) {
        // Supprimer la voiture de la collection
        boolean result = cars.remove(car.getIdCar()) != null;
        if (result) {
            saveData(); // Sauvegarder après modification
            System.out.println("Voiture supprimée avec succès : " + car.getIdCar());
        } else {
            System.out.println("Erreur lors de la suppression de la voiture : " + car.getIdCar());
        }
        return result;
    }

    /**
     *  Supprime un contrat de la collection
     * @param contrat
     * @return true si le contrat a ete supprimé, false sinon
     */
    public boolean deleteContract(Contrat contrat) {
        // Supprimer le contrat de la collection
        boolean result = contracts.remove(contrat.getIdContrat()) != null; // Supprimer le contrat de la collection de contrats
        if (result) {
            saveData(); // Sauvegarder après modification
            System.out.println("Contrat supprimé avec succès : " + contrat.getIdContrat());
        } else {
            System.out.println("Erreur lors de la suppression du contrat : " + contrat.getIdContrat());
        }
        return result;
    }

    /**
     *
     * Supprime un client de la collection
     * @param client
     * @return true si le client a ete supprimé, false sinon
     */
    public boolean deleteClient(Client client) {
        // Au lieu de marquer comme supprimé, on pourrait simplement supprimer le client
        // ou implémenter une autre logique métier (désactiver le compte, etc.)
        Client existingClient = clients.get(client.getIdClient());
        if (existingClient != null) {
            // On pourrait ajouter une propriété 'active' à Client si nécessaire
            // Pour l'instant, on supprime simplement le client
            clients.remove(client.getIdClient());
            saveData(); // Sauvegarder après modification
            return true;
        }
        return false;
    }

    /**
     * Met à jour une voiture dans la liste et sauvegarde les changements
     * @param car La voiture à mettre à jour
     */
    @Override
    public void updateCar(Car car) {
        if (car == null) return;

        // Vérifier si la voiture existe dans la Map
        if (cars.containsKey(car.getIdCar())) {
            // Remplacer l'ancienne voiture par la nouvelle
            cars.put(car.getIdCar(), car);
            // Sauvegarder les changements
            saveData();
            System.out.println("Voiture mise à jour avec succès : " + car.getIdCar());
        } else {
            System.out.println("Voiture non trouvée pour la mise à jour : " + car.getIdCar());
        }
    }

    /**
     * Met à jour un client dans la liste et sauvegarde les changements
     * @param client Le client à mettre à jour
     */
    @Override
    public void updateClient(Client client) {
        if (client == null) return;

        // Vérifier si le client existe dans la Map
        if (clients.containsKey(client.getIdClient())) {
            // Remplacer l'ancien client par le nouveau
            clients.put(client.getIdClient(), client);
            // Sauvegarder les changements
            saveData();
            System.out.println("Client mis à jour avec succès : " + client.getIdClient());

        } else {
            System.out.println("Client non trouvé pour la mise à jour : " + client.getIdClient());
        }
    }

    /**
     * Met à jour un contrat dans la liste et sauvegarde les changements
     * @param contrat Le contrat à mettre à jour
     */
    @Override
    public void updateContract(Contrat contrat) {
        if (contrat == null) return;

        // Vérifier si le contrat existe dans la Map
        if (contracts.containsKey(contrat.getIdContrat())) {
            // Remplacer l'ancien contrat par le nouveau
            contracts.put(contrat.getIdContrat(), contrat);
            // Sauvegarder les changements
            saveData();
            System.out.println("Contrat mis à jour avec succès : " + contrat.getIdContrat());

        } else {
            System.out.println("Contrat non trouvé pour la mise à jour : " + contrat.getIdContrat());
        }
    }

    /**
     * Met à jour une réservation dans la liste et sauvegarde les changements
     * @param reservation La réservation à mettre à jour
     */
    @Override
    public void updateReservation(Reservation reservation) {
        // Vérifier si la réservation existe
        Reservation existingReservation = reservations.get(reservation.getIdReservation());
        if (existingReservation != null) {
            // Récupérer l'ancienne voiture pour la marquer comme disponible
            String oldCarId = existingReservation.getCarId();
            if (oldCarId != null && !oldCarId.isEmpty()) {
                Car oldCar = cars.get(oldCarId);
                if (oldCar != null) {
                    oldCar.setAvailable(true);
                    cars.put(oldCarId, oldCar);
                    System.out.println("Ancienne voiture " + oldCarId + " marquée comme disponible");
                }
            }

            // Mettre à jour les identifiants des objets référencés
            if (reservation.getCar() != null) {
                String carId = reservation.getCar().getIdCar();
                reservation.setCarId(carId);
                reservation.setCarRegistration(carId);

                // Marquer la nouvelle voiture comme non disponible
                Car newCar = cars.get(carId);
                if (newCar != null) {
                    newCar.setAvailable(false);
                    cars.put(carId, newCar);
                    System.out.println("Nouvelle voiture " + carId + " marquée comme non disponible");
                }
            }

            if (reservation.getClient() != null) {
                int clientId = reservation.getClient().getIdClient();
                reservation.setClientId(clientId);

                // Récupérer le nom complet du client sans modifier l'objet client
                Client client = clients.get(clientId);
                if (client != null) {
                    reservation.setClientFullName(client.getName() + " " + client.getSurname());
                }
            }

            if (reservation.getContrat() != null) {
                String contratId = reservation.getContrat().getIdContrat();
                reservation.setContratId(contratId);
            }

            // Remplacer l'ancienne réservation par la nouvelle
            reservations.put(reservation.getIdReservation(), reservation);
            // Sauvegarder les changements
            saveData();
            System.out.println("Réservation mise à jour avec succès : " + reservation.getIdReservation());
            // Mettre à jour la table des réservations

        } else {
            System.out.println("Réservation non trouvée pour la mise à jour : " + reservation.getIdReservation());
        }
    }

    /**
     * Récupère toutes les réservations.
     *
     * @return Liste de toutes les réservations
     */
    public List<Reservation> getAllReservations() {
        List<Reservation> result = new ArrayList<>(reservations.values());
        
        // S'assurer que chaque réservation a ses objets Client et Car correctement chargés
        for (Reservation reservation : result) {
            // Charger l'objet Client si on a un ID client
            if (reservation.getClientId() > 0 && reservation.getClient() == null) {
                Client client = clients.get(reservation.getClientId());
                reservation.setClient(client);
            }
            
            // Charger l'objet Car si on a un ID voiture
            if (reservation.getCarId() != null && !reservation.getCarId().isEmpty() && reservation.getCar() == null) {
                Car car = cars.get(reservation.getCarId());
                reservation.setCar(car);
            }
        }
        
        return result;
    }

    public List<Car> getAllCars() {
        return new ArrayList<>(cars.values());
    }

    public List<Contrat> getAllContracts() {
        return new ArrayList<>(contracts.values());
    }

    public List<Client> getAllClients() {
        return new ArrayList<>(clients.values());
    }



    /**
     * Récupère une voiture par son ID.
     *
     * @param id ID de la voiture
     * @return La voiture ou null si non trouvée
     */
    public Car getCarById(String id) {
        return cars.get(id);
    }

    /**
     * S'assure que toutes les propriétés d'une voiture sont définies (non nulles)
     * @param car La voiture à vérifier
     */
    private void ensureCarPropertiesAreSet(Car car) {
        if (car.getFuelType() == null) {
            car.setFuelType("Essence"); // Valeur par défaut
        }
        if (car.getTransmission() == null) {
            car.setTransmission("Manuelle"); // Valeur par défaut
        }
        if (car.getSeats() == 0) {
            car.setSeats(5); // Valeur par défaut
        }
        if (car.getMileage() == 0) {
            car.setMileage(0); // Valeur par défaut
        }
        if (car.getImage() == null) {
            car.setImage(""); // Valeur par défaut
        }
    }

    /**
     * S'assure que toutes les propriétés d'un client sont définies (non nulles)
     * @param client Le client à vérifier
     */
    private void ensureClientPropertiesAreSet(Client client) {
        if (client.getBirthDate() == null) {
            client.setBirthDate(LocalDate.of(1990, 1, 1)); // Valeur par défaut
        }
        if (client.getLicenseNumber() == null) {
            client.setLicenseNumber(""); // Valeur par défaut
        }
        if (client.getAddress() == null) {
            client.setAddress(""); // Valeur par défaut
        }
        if (client.getPhoneNumber() == null) {
            client.setPhoneNumber(""); // Valeur par défaut
        }
    }

    // Méthodes d'importation et d'exportation en format texte/CSV
    public void importCars(String filePath) throws IOException {
        System.out.println("Début de l'importation des voitures depuis: " + filePath);
        // Vérifier si le fichier est CSV
        boolean isCsv = filePath.toLowerCase().endsWith(".csv");
        boolean isTxt = filePath.toLowerCase().endsWith(".txt");

        if (isCsv || isTxt) {
            // Importer à partir d'un CSV ou TXT
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

                String line; // Variable pour stocker les lignes lues

                String headerLine = reader.readLine();// Lire la première ligne pour obtenir les en-têtes

                if (headerLine == null) {
                    throw new IOException("Le fichier est vide ou mal formaté");
                }

                System.out.println("En-têtes lus: " + headerLine);

                // Définir les indices des colonnes en fonction des en-têtes
                String[] headers = headerLine.split(",");// Diviser la ligne en en-têtes
                Map<String, Integer> headerMap = new HashMap<>();

                for (int i = 0; i < headers.length; i++) {
                    headerMap.put(headers[i].trim(), i);
                    System.out.println("En-tête trouvé: " + headers[i].trim() + " à l'index " + i);
                }

                int carCount = 0;
                // Lire les données
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");

                    // Vérifier que nous avons suffisamment de données
                    if (data.length < headerMap.size()) { // Si le nombre de données est insuffisant
                        System.err.println("Ligne ignorée car incomplète: " + line);
                        continue;
                    }

                    try {
                        Car car = new Car();

                        // Remplir l'objet Car en utilisant les en-têtes
                        if (headerMap.containsKey("idCar") && data[headerMap.get("idCar")].trim().length() > 0) {
                            car.setIdCar(data[headerMap.get("idCar")].trim());
                        } else {
                            // Générer un ID unique si non fourni
                            car.setIdCar(generateUniqueCarId());
                        }

                        if (headerMap.containsKey("brand")) {
                            car.setBrand(data[headerMap.get("brand")].trim());
                        }

                        if (headerMap.containsKey("model")) {
                            car.setModel(data[headerMap.get("model")].trim());
                        }

                        if (headerMap.containsKey("year") && data[headerMap.get("year")].trim().length() > 0) {
                            try {
                                car.setYear(Integer.parseInt(data[headerMap.get("year")].trim()));
                            } catch (NumberFormatException e) {
                                System.err.println("Format d'année invalide: " + data[headerMap.get("year")]);
                                car.setYear(2020); // Valeur par défaut
                            }
                        }

                        if (headerMap.containsKey("priceday") && data[headerMap.get("priceday")].trim().length() > 0) {
                            try {
                                car.setPriceday(Float.parseFloat(data[headerMap.get("priceday")].trim()));
                            } catch (NumberFormatException e) {
                                System.err.println("Format de prix invalide: " + data[headerMap.get("priceday")]);
                                car.setPriceday(50.0f); // Valeur par défaut
                            }
                        }

                        if (headerMap.containsKey("mileage") && data[headerMap.get("mileage")].trim().length() > 0) {
                            try {
                                car.setMileage(Integer.parseInt(data[headerMap.get("mileage")].trim()));
                            } catch (NumberFormatException e) {
                                System.err.println("Format de kilométrage invalide: " + data[headerMap.get("mileage")]);
                                car.setMileage(0); // Valeur par défaut
                            }
                        }

                        if (headerMap.containsKey("fuelType")) {
                            car.setFuelType(data[headerMap.get("fuelType")].trim());
                        }

                        if (headerMap.containsKey("transmission")) {
                            car.setTransmission(data[headerMap.get("transmission")].trim());
                        }

                        if (headerMap.containsKey("seats") && data[headerMap.get("seats")].trim().length() > 0) {
                            try {
                                car.setSeats(Integer.parseInt(data[headerMap.get("seats")].trim()));
                            } catch (NumberFormatException e) {
                                System.err.println("Format de nombre de places invalide: " + data[headerMap.get("seats")]);
                                car.setSeats(5); // Valeur par défaut
                            }
                        }

                        if (headerMap.containsKey("available")) {
                            String availableStr = data[headerMap.get("available")].trim();
                            boolean available = availableStr.equalsIgnoreCase("true")
                                    || availableStr.equalsIgnoreCase("1")
                                    || availableStr.equalsIgnoreCase("yes")
                                    || availableStr.equalsIgnoreCase("oui")
                                    || availableStr.equalsIgnoreCase("disponible");
                            car.setAvailable(available);
                        }

                        if (headerMap.containsKey("image") && headerMap.get("image") < data.length) {
                            car.setImage(data[headerMap.get("image")].trim());
                        }

                        // S'assurer que les propriétés requises sont définies
                        ensureCarPropertiesAreSet(car);

                        // Ajouter la voiture à la collection
                        cars.put(car.getIdCar(), car);
                        carCount++;

                        System.out.println("Voiture importée: " + car.getIdCar() + " - " + car.getBrand() + " " + car.getModel());
                    } catch (Exception e) {
                        System.err.println("Erreur lors de l'importation d'une voiture: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                // IMPORTANT: Sauvegarder les données dans le fichier sérialisé après l'importation
                saveData();

                System.out.println("Importation terminée. " + carCount + " voitures importées avec succès.");

                // Recharger les données depuis les fichiers sérialisés pour s'assurer que tout est cohérent
                loadData();
            }
        } else {
            throw new IOException("Format de fichier non pris en charge. Utilisez un fichier CSV ou TXT.");
        }
    }

    public void importClients(String filePath) throws IOException {
        // Vérifier si le fichier est CSV
        boolean isCsv = filePath.toLowerCase().endsWith(".csv");

        if (isCsv) {
            // Importer à partir d'un CSV
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                // Lire la première ligne pour obtenir les en-têtes
                String headerLine = reader.readLine();

                if (headerLine == null) {
                    throw new IOException("Le fichier CSV est vide ou mal formaté");
                }

                // Définir les indices des colonnes en fonction des en-têtes
                String[] headers = headerLine.split(",");
                Map<String, Integer> headerMap = new HashMap<>();

                for (int i = 0; i < headers.length; i++) {
                    headerMap.put(headers[i].trim(), i);
                }

                // Lire les données
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");

                    // Vérifier que nous avons suffisamment de données
                    if (data.length < headerMap.size()) {
                        System.err.println("Ligne ignorée car incomplète: " + line);
                        continue;
                    }

                    Client client = new Client();

                    // Remplir l'objet Client en utilisant les en-têtes
                    if (headerMap.containsKey("idClient") && data[headerMap.get("idClient")].trim().length() > 0) {
                        try {
                            client.setIdClient(Integer.parseInt(data[headerMap.get("idClient")].trim()));
                        } catch (NumberFormatException e) {
                            // Générer un ID si le format est invalide
                            client.setIdClient(clientIdGenerator.incrementAndGet());
                        }
                    } else {
                        // Générer un ID unique si non fourni
                        client.setIdClient(clientIdGenerator.incrementAndGet());
                    }

                    if (headerMap.containsKey("name")) {
                        client.setName(data[headerMap.get("name")].trim());
                    }

                    if (headerMap.containsKey("surname")) {
                        client.setSurname(data[headerMap.get("surname")].trim());
                    }

                    if (headerMap.containsKey("email")) {
                        client.setEmail(data[headerMap.get("email")].trim());
                    }

                    if (headerMap.containsKey("licenseNumber")) {
                        client.setLicenseNumber(data[headerMap.get("licenseNumber")].trim());
                    }

                    if (headerMap.containsKey("birthDate") && data[headerMap.get("birthDate")].trim().length() > 0) {
                        try {
                            client.setBirthDate(LocalDate.parse(data[headerMap.get("birthDate")].trim()));
                        } catch (Exception e) {
                            System.err.println("Format de date de naissance invalide: " + data[headerMap.get("birthDate")]);
                        }
                    }

                    if (headerMap.containsKey("phoneNumber")) {
                        client.setPhoneNumber(data[headerMap.get("phoneNumber")].trim());
                    }

                    // S'assurer que les propriétés requises sont définies
                    ensureClientPropertiesAreSet(client);

                    // Ajouter le client à la collection
                    clients.put(client.getIdClient(), client);
                }

                // Sauvegarder les clients dans le fichier sérialisé
                saveData();
                System.out.println("Importation CSV terminée: " + clients.size() + " clients importés");
            }
        } else {
            // Méthode d'importation existante pour les fichiers texte
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                // Ignorer la première ligne (en-têtes)
                reader.readLine();

                while ((line = reader.readLine()) != null) {
                    // Format: idClient,nom,prénom,email,numéro de permis,date de naissance,téléphone
                    String[] data = line.split(",");
                    if (data.length >= 7) {
                        Client client = new Client();
                        client.setIdClient(Integer.parseInt(data[0].trim()));
                        client.setName(data[1].trim());
                        client.setSurname(data[2].trim());
                        client.setEmail(data[3].trim());
                        client.setLicenseNumber(data[4].trim());
                        client.setBirthDate(LocalDate.parse(data[5].trim()));
                        client.setPhoneNumber(data[6].trim());

                        // Ajouter le client à la collection
                        clients.put(client.getIdClient(), client);
                    }
                }

                // Sauvegarder les clients dans le fichier sérialisé
                saveData();

            }
        }
    }

    public void importContracts(String filePath) throws IOException {
        // Vérifier si le fichier est CSV
        boolean isCsv = filePath.toLowerCase().endsWith(".csv");

        if (isCsv) {
            // Importer à partir d'un CSV
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                // Lire la première ligne pour obtenir les en-têtes
                String headerLine = reader.readLine();

                if (headerLine == null) {
                    throw new IOException("Le fichier CSV est vide ou mal formaté");
                }

                // Définir les indices des colonnes en fonction des en-têtes
                String[] headers = headerLine.split(",");
                Map<String, Integer> headerMap = new HashMap<>();

                for (int i = 0; i < headers.length; i++) {
                    headerMap.put(headers[i].trim(), i);
                }

                // Lire les données
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");

                    // Vérifier que nous avons suffisamment de données
                    if (data.length < headerMap.size()) {
                        System.err.println("Ligne ignorée car incomplète: " + line);
                        continue;
                    }

                    Contrat contrat = new Contrat();

                    // Remplir l'objet Contrat en utilisant les en-têtes
                    if (headerMap.containsKey("idContrat") && data[headerMap.get("idContrat")].trim().length() > 0) {
                        contrat.setIdContrat(data[headerMap.get("idContrat")].trim());
                    } else {
                        // Générer un ID unique si non fourni
                        contrat.setIdContrat("CONT-" + contractIdGenerator.incrementAndGet());
                    }

                    if (headerMap.containsKey("caution") && data[headerMap.get("caution")].trim().length() > 0) {
                        try {
                            contrat.setCaution(Double.parseDouble(data[headerMap.get("caution")].trim()));
                        } catch (NumberFormatException e) {
                            System.err.println("Format de caution invalide: " + data[headerMap.get("caution")]);
                        }
                    }

                    if (headerMap.containsKey("typeAssurance")) {
                        contrat.setTypeAssurance(data[headerMap.get("typeAssurance")].trim());
                    }

                    if (headerMap.containsKey("options")) {
                        String optionsStr = data[headerMap.get("options")].trim();
                        if (!optionsStr.isEmpty()) {
                            String[] options = optionsStr.split(";");
                            for (String option : options) {
                                contrat.ajouterOption(option.trim());
                            }
                        }
                    }

                    if (headerMap.containsKey("estSigne") && data[headerMap.get("estSigne")].trim().length() > 0) {
                        contrat.setEstSigne(Boolean.parseBoolean(data[headerMap.get("estSigne")].trim()));
                    }

                    if (headerMap.containsKey("statutContrat") && data[headerMap.get("statutContrat")].trim().length() > 0) {
                        try {
                            contrat.setStatutContrat(StatutContrat.valueOf(data[headerMap.get("statutContrat")].trim()));
                        } catch (IllegalArgumentException e) {
                            System.err.println("Statut de contrat invalide: " + data[headerMap.get("statutContrat")]);
                            contrat.setStatutContrat(StatutContrat.EN_ATTENTE);
                        }
                    }

                    // Associer à une réservation si l'ID est fourni
                    if (headerMap.containsKey("reservationId") && data[headerMap.get("reservationId")].trim().length() > 0) {
                        try {
                            int reservationId = Integer.parseInt(data[headerMap.get("reservationId")].trim());
                            if (reservations.containsKey(reservationId)) {
                                contrat.setReservation(reservations.get(reservationId));
                                contrat.setReservationId(reservationId);
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("ID de réservation invalide: " + data[headerMap.get("reservationId")]);
                        }
                    }

                    // Ajouter le contrat à la collection
                    contracts.put(contrat.getIdContrat(), contrat);
                }

                // Sauvegarder les contrats dans le fichier sérialisé
                saveData();
                System.out.println("Importation CSV terminée: " + contracts.size() + " contrats importés");
                // Mettre à jour la table des contrats

            }
        } else {
            // Méthode d'importation existante pour les fichiers texte
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                // Ignorer la première ligne (en-têtes)
                reader.readLine();

                while ((line = reader.readLine()) != null) {
                    // Format: idContrat,caution,typeAssurance,options
                    String[] data = line.split(",");
                    if (data.length >= 4) {
                        Contrat contrat = new Contrat();
                        contrat.setIdContrat(data[0].trim());
                        contrat.setCaution(Double.parseDouble(data[1].trim()));
                        contrat.setTypeAssurance(data[2].trim());

                        // Ajouter des détails si disponibles
                        if (data.length > 3) {
                            // Ajouter les détails comme options
                            contrat.ajouterOption(data[3].trim());
                        }

                        // Ajouter le contrat à la collection
                        contracts.put(contrat.getIdContrat(), contrat);
                    }
                }

                // Sauvegarder les contrats dans le fichier sérialisé
                saveData();

            }
        }
        loadData();
    }

    public void importReservations(String filePath) throws IOException {
        // Vérifier si le fichier est CSV
        boolean isCsv = filePath.toLowerCase().endsWith(".csv");

        if (isCsv) {
            // Importer à partir d'un CSV
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                // Lire la première ligne pour obtenir les en-têtes
                String headerLine = reader.readLine();

                if (headerLine == null) {
                    throw new IOException("Le fichier CSV est vide ou mal formaté");
                }

                // Définir les indices des colonnes en fonction des en-têtes
                String[] headers = headerLine.split(",");
                Map<String, Integer> headerMap = new HashMap<>();

                for (int i = 0; i < headers.length; i++) {
                    headerMap.put(headers[i].trim(), i);
                }

                // Lire les données
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");

                    // Vérifier que nous avons suffisamment de données
                    if (data.length < headerMap.size()) {
                        System.err.println("Ligne ignorée car incomplète: " + line);
                        continue;
                    }

                    Reservation reservation = new Reservation();

                    // Remplir l'objet Reservation en utilisant les en-têtes
                    if (headerMap.containsKey("idReservation") && data[headerMap.get("idReservation")].trim().length() > 0) {
                        try {
                            reservation.setIdReservation(Integer.parseInt(data[headerMap.get("idReservation")].trim()));
                        } catch (NumberFormatException e) {
                            // Générer un ID si le format est invalide
                            reservation.setIdReservation(reservationIdGenerator.incrementAndGet());
                        }
                    } else {
                        // Générer un ID unique si non fourni
                        reservation.setIdReservation(reservationIdGenerator.incrementAndGet());
                    }

                    // Récupérer la voiture et le client par leur ID
                    if (headerMap.containsKey("carId") && data[headerMap.get("carId")].trim().length() > 0) {
                        String carId = data[headerMap.get("carId")].trim();
                        if (cars.containsKey(carId)) {
                            reservation.setCar(cars.get(carId));
                        }
                    }

                    if (headerMap.containsKey("clientId") && data[headerMap.get("clientId")].trim().length() > 0) {
                        try {
                            int clientId = Integer.parseInt(data[headerMap.get("clientId")].trim());
                            if (clients.containsKey(clientId)) {
                                reservation.setClient(clients.get(clientId));
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("ID client invalide: " + data[headerMap.get("clientId")]);
                        }
                    }

                    // Dates de début et de fin
                    if (headerMap.containsKey("startDate") && data[headerMap.get("startDate")].trim().length() > 0) {
                        try {
                            // Essayer plusieurs formats de date courants et convertir au format YYYY-MM-DD
                            String dateStr = data[headerMap.get("startDate")].trim();
                            LocalDate startDate = parseDate(dateStr);
                            if (startDate != null) {
                                // Stocker la date au format YYYY-MM-DD
                                reservation.setStartDate(startDate);

                            } else {
                                System.err.println("Format de date de début invalide: " + dateStr);
                            }
                        } catch (Exception e) {
                            System.err.println("Format de date de début invalide: " + data[headerMap.get("startDate")]);
                        }
                    }

                    if (headerMap.containsKey("endDate") && data[headerMap.get("endDate")].trim().length() > 0) {
                        try {
                            // Essayer plusieurs formats de date courants et convertir au format YYYY-MM-DD
                            String dateStr = data[headerMap.get("endDate")].trim();
                            LocalDate endDate = parseDate(dateStr);
                            if (endDate != null) {
                                // Stocker la date au format YYYY-MM-DD
                                reservation.setEndDate(endDate);

                            } else {
                                System.err.println("Format de date de fin invalide: " + dateStr);
                            }
                        } catch (Exception e) {
                            System.err.println("Format de date de fin invalide: " + data[headerMap.get("endDate")]);
                        }
                    }

                    if (headerMap.containsKey("responsable")) {
                        reservation.setResponsable(data[headerMap.get("responsable")].trim());
                    }

                    if (headerMap.containsKey("price") && data[headerMap.get("price")].trim().length() > 0) {
                        try {
                            reservation.setPrice(Float.parseFloat(data[headerMap.get("price")].trim()));
                        } catch (NumberFormatException e) {
                            System.err.println("Format de prix invalide: " + data[headerMap.get("price")]);
                        }
                    }

                    if (headerMap.containsKey("notes")) {
                        reservation.setNotes(data[headerMap.get("notes")].trim());
                    }

                    if (headerMap.containsKey("clientFullName")) {
                        reservation.setClientFullName(data[headerMap.get("clientFullName")].trim());
                    }

                    if (headerMap.containsKey("carRegistration")) {
                        reservation.setCarRegistration(data[headerMap.get("carRegistration")].trim());
                    }

                    // Ajouter la réservation à la collection seulement si voiture et client sont présents
                    if (reservation.getCar() != null && reservation.getClient() != null) {
                        reservations.put(reservation.getIdReservation(), reservation);
                    } else {
                        System.err.println("Réservation ignorée car voiture ou client manquant: ID=" + reservation.getIdReservation());
                    }
                }

                // Sauvegarder les réservations dans le fichier sérialisé
                saveData();
                System.out.println("Importation CSV terminée: " + reservations.size() + " réservations importées");

            }
        } else {
            // Méthode d'importation existante pour les fichiers texte
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                // Ignorer la première ligne (en-têtes)
                reader.readLine();

                while ((line = reader.readLine()) != null) {
                    // Format: idReservation,carId,clientId,startDate,endDate,responsable,price,notes,clientFullName,carRegistration
                    String[] data = line.split(",");
                    if (data.length >= 10) {
                        Reservation reservation = new Reservation();
                        reservation.setIdReservation(Integer.parseInt(data[0].trim()));

                        // Récupérer la voiture et le client par leur ID
                        Car car = cars.get(data[1].trim());
                        Client client = clients.get(Integer.parseInt(data[2].trim()));

                        if (car != null && client != null) {
                            reservation.setCar(car);
                            reservation.setClient(client);

                            // Convertir les dates
                            try {
                                // Format attendu: yyyy-MM-dd
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                                Date startDateObj = dateFormat.parse(data[3].trim());
                                Date endDateObj = dateFormat.parse(data[4].trim());

                                // Convertir Date en LocalDate
                                LocalDate startDate = startDateObj.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                LocalDate endDate = endDateObj.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                                reservation.setStartDate(startDate);
                                reservation.setEndDate(endDate);
                            } catch (ParseException e) {
                                System.err.println("Erreur lors de la conversion des dates: " + e.getMessage());
                            }

                            reservation.setResponsable(data[5].trim());
                            reservation.setPrice(Float.parseFloat(data[6].trim()));
                            reservation.setNotes(data[7].trim());
                            reservation.setClientFullName(data[8].trim());
                            reservation.setCarRegistration(data[9].trim());

                            // Ajouter la réservation à la collection
                            reservations.put(reservation.getIdReservation(), reservation);
                        }
                    }
                }

                // Sauvegarder les réservations dans le fichier sérialisé
                saveData();
            }
        }
        loadData();
    }

    public void exportCars(String filePath) throws IOException {
        System.out.println("Exportation des voitures vers: " + filePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Écrire l'en-tête CSV
            writer.write("Car");
            writer.newLine(); //passe à la ligne suivante

            // Vérifier si la collection des voitures est vide
            if (cars.isEmpty()) {
                System.out.println("Aucune voiture à exporter. La collection est vide.");
                return;
            }

            // Écrire les données de chaque voiture au format CSV en utilisant toString()
            for (Car car : cars.values()) {
                writer.write(car.toString());
                writer.newLine();
            }

            System.out.println("Exportation des voitures terminée avec succès. Nombre de voitures exportées: " + cars.size());
        } catch (IOException e) {
            System.err.println("Erreur lors de l'exportation des voitures: " + e.getMessage());
            throw e;
        }
    }

    public void exportClients(String filePath) throws IOException {
        System.out.println("Exportation des clients vers: " + filePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Écrire l'en-tête CSV
            writer.write("Client");
            writer.newLine();

            // Vérifier si la collection des clients est vide
            if (clients.isEmpty()) {
                System.out.println("Aucun client à exporter. La collection est vide.");
                return;
            }

            // Écrire les données de chaque client au format CSV en utilisant toString()
            for (Client client : clients.values()) {
                writer.write(client.toString());
                writer.newLine();
            }

            System.out.println("Exportation des clients terminée avec succès. Nombre de clients exportés: " + clients.size());
        } catch (IOException e) {
            System.err.println("Erreur lors de l'exportation des clients: " + e.getMessage());
            throw e;
        }
    }

    public void exportContracts(String filePath) throws IOException {
        System.out.println("Exportation des contrats vers: " + filePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Écrire l'en-tête CSV
            writer.write("Contrat");
            writer.newLine();

            // Vérifier si la collection des contrats est vide
            if (contracts.isEmpty()) {
                System.out.println("Aucun contrat à exporter. La collection est vide.");
                return;
            }



            // Écrire les données de chaque contrat au format CSV en utilisant toString()
            for (Contrat contrat : contracts.values()) {
                writer.write(contrat.toString());
                writer.newLine();
            }

            System.out.println("Exportation des contrats terminée avec succès. Nombre de contrats exportés: " + contracts.size());
        } catch (IOException e) {
            System.err.println("Erreur lors de l'exportation des contrats: " + e.getMessage());
            throw e;
        }
    }

    public void exportReservations(String filePath) throws IOException {
        System.out.println("Exportation des réservations vers: " + filePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Écrire l'en-tête CSV
            writer.write("Reservation");
            writer.newLine();

            // Vérifier si la collection des réservations est vide
            if (reservations.isEmpty()) {
                System.out.println("Aucune réservation à exporter. La collection est vide.");
                return;
            }



            // Écrire les données de chaque réservation au format CSV en utilisant toString()
            for (Reservation reservation : reservations.values()) {
                writer.write(reservation.toString());
                writer.newLine();
            }

            System.out.println("Exportation des réservations terminée avec succès. Nombre de réservations exportées: " + reservations.size());
        } catch (IOException e) {
            System.err.println("Erreur lors de l'exportation des réservations: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Méthode utilitaire pour parser une date en essayant plusieurs formats courants.
     * @param dateStr La chaîne de date à parser
     * @return La date parsée ou null si aucun format ne correspond
     */
    // pour l'import
    private LocalDate parseDate(String dateStr) {
        // Liste des formats de date à essayer
        String[] dateFormats = {
                "yyyy-MM-dd",           // Format ISO standard
                "dd/MM/yyyy",           // Format français
                "MM/dd/yyyy",           // Format américain
                "dd-MM-yyyy",           // Format avec tirets
                "yyyy/MM/dd",           // Format avec slashes
                "EEE MMM dd HH:mm:ss zzz yyyy"  // Format Java par défaut (ex: Tue Apr 08 00:00:00 CEST 2025)
        };

        for (String format : dateFormats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // Continuer avec le format suivant
            }
        }
        return null; // Aucun format ne correspond
    }

    /**
     * Génère un identifiant unique pour une voiture au format "XX-XXX-XXX"
     * @return Un identifiant unique
     */
    private String generateUniqueCarId() {
        // Générer un identifiant de plaque d'immatriculation au format français
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        Random random = new Random();

        StringBuilder id = new StringBuilder();

        // Première partie: 2 lettres
        for (int i = 0; i < 2; i++) {
            id.append(letters.charAt(random.nextInt(letters.length())));
        }

        id.append('-');

        // Deuxième partie: 3 lettres
        for (int i = 0; i < 3; i++) {
            id.append(letters.charAt(random.nextInt(letters.length())));
        }

        id.append('-');

        // Troisième partie: 3 chiffres
        for (int i = 0; i < 3; i++) {
            id.append(digits.charAt(random.nextInt(digits.length())));
        }

        return id.toString();
    }

    /**
     * Sauvegarde les données dans des fichiers sérialisés.
     */
    private void saveData() {
        try {
            // Sauvegarder les compteurs
            Map<String, Integer> counters = new HashMap<>();
            counters.put("reservationId", reservationIdGenerator.get());
            counters.put("carId", carIdGenerator.get());
            counters.put("contractId", contractIdGenerator.get());
            counters.put("clientId", clientIdGenerator.get());

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(COUNTERS_FILE))) {
                oos.writeObject(counters);
            }

            // Sauvegarder les réservations
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RESERVATIONS_FILE))) {
                oos.writeObject(reservations);
            }

            // Sauvegarder les voitures
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CARS_FILE))) {
                oos.writeObject(cars);
            }

            // Sauvegarder les contrats
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CONTRACTS_FILE))) {
                oos.writeObject(contracts);
            }

            // Sauvegarder les clients
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CLIENTS_FILE))) {
                oos.writeObject(clients);
            }

            System.out.println("Données sauvegardées avec succès.");
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des données: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
