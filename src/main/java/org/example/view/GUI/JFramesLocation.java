package org.example.view.GUI;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIconColors;
import com.formdev.flatlaf.FlatLightLaf;
import org.example.controller.Controller;
import org.example.controller.ControllerActions;
import org.example.model.entity.*;
import org.example.model.DataAccessLayer;
import org.example.utils.DateFormatter;
import org.example.model.dao.DAOLocation;
import org.example.view.GUI.ExportImportDialog;
import org.example.utils.WrapLayout;
import org.example.view.ViewLocation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

import javax.swing.plaf.basic.BasicButtonUI;

public class JFramesLocation extends JFrame implements ViewLocation {
    // Palette de couleurs centralisée
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219); // Bleu principal
    private static final Color PRIMARY_HOVER = new Color(41, 128, 185); // Bleu hover
    private static final Color ADD_COLOR = new Color(46, 204, 113); // Vert pour ajouter
    private static final Color ADD_HOVER = new Color(39, 174, 96); // Vert hover
    private static final Color EDIT_COLOR = new Color(241, 196, 15); // Orange pour modifier
    private static final Color EDIT_HOVER = new Color(243, 156, 18); // Orange hover
    private static final Color DELETE_COLOR = new Color(231, 76, 60); // Rouge pour supprimer
    private static final Color DELETE_HOVER = new Color(192, 57, 43); // Rouge hover
    private static final Color EXPORT_COLOR = new Color(155, 89, 182); // Violet pour exporter
    private static final Color EXPORT_HOVER = new Color(142, 68, 173); // Violet hover
    private static final Color TEXT_COLOR = Color.WHITE; // Texte blanc
    private static final Color PANEL_BG = new Color(245, 247, 250); // Fond des panels

    private JTabbedPane tabbedPane;
    private JPanel panel1, panel2, panel3, panel4, panel5;
    private JTable table1, table2, table3, table4;
    private JButton buttonAddCar, buttonModifyCar, buttonDeleteCar;
    private JButton buttonAddClient, buttonModifyClient, buttonDeleteClient;
    private JButton buttonAddLocation, buttonModifyLocation, buttonDeleteLocation;
    private JButton buttonAddContrat, buttonModifyContrat, buttonDeleteContrat;
    private JButton buttonExportContratPDF;
    private JLabel labelImage;
    private int tailleBorder = 30;
    private int posBouton = 0;
    private Controller controller;
    private JButton sessionButton; // Ajout de la référence directe
    private JButton lightThemeButton, darkThemeButton; // Boutons pour changer de thème
    private JTextField searchField; // Champ de recherche
    private JButton searchButton; // Bouton de recherche

    // Formulaires et dialogues
    private ImprovedSessionDialog sessionDialog;
    private AddCarForm addCarForm;
    private AddClientForm addClientForm;
    private AddLocationForm addLocationForm;
    private AddContratForm addContratForm;

    public JFramesLocation() {
        super("LocaDrive");
        UIManager.put("control", new Color(245, 247, 250));
        UIManager.put("info", new Color(245, 247, 250));
        UIManager.put("nimbusBase", new Color(80, 90, 170));
        UIManager.put("nimbusBlueGrey", new Color(100, 110, 180));
        UIManager.put("nimbusLightBackground", Color.WHITE);
        UIManager.put("text", new Color(40, 40, 40));
        UIManager.put("Table.alternateRowColor", new Color(240, 242, 250));
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 15));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 16));
        UIManager.put("TableHeader.background", new Color(80, 90, 170));
        UIManager.put("TableHeader.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 15));
        UIManager.put("TabbedPane.selected", new Color(80, 90, 170));
        UIManager.put("TabbedPane.background", new Color(230, 232, 240));
        UIManager.put("TabbedPane.foreground", new Color(80, 90, 170));

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- BARRE SUPERIEURE MODERNE ---
        JPanel topPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color start = new Color(44, 62, 80); // bleu foncé
                Color end = new Color(52, 152, 219); // bleu clair
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, start, w, 0, end);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        topPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("LocaDrive", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Bouton de session avec style moderne
        sessionButton = createModernButton("Se connecter", PRIMARY_COLOR, TEXT_COLOR, PRIMARY_HOVER, "login");
        JPanel sessionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sessionPanel.setOpaque(false);
        sessionPanel.add(sessionButton);
        topPanel.add(sessionPanel, BorderLayout.EAST);
        
        // Ajout du panel de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setOpaque(true);
        
        // Création du champ de recherche avec placeholder
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setToolTipText("Rechercher dans tous les onglets");
        
        // Bouton de recherche avec icône
        searchButton = createModernButton("Rechercher", PRIMARY_COLOR, TEXT_COLOR, PRIMARY_HOVER, "search");
        searchButton.setActionCommand(ControllerActions.SEARCH);
        
        // Ajout d'un listener pour la touche Entrée
        searchField.addActionListener(e -> {
            if (controller != null) {
                controller.actionPerformed(new ActionEvent(searchField, ActionEvent.ACTION_PERFORMED, ControllerActions.SEARCH));
            }
        });
        
        searchPanel.add(new JLabel("Rechercher: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        topPanel.add(searchPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);

        // Création de la barre de menu
        JMenuBar menuBar = new JMenuBar();

        // Menu Fichier
        JMenu fileMenu = new JMenu("Fichier");
        fileMenu.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Sous-menu Importer
        JMenu importMenu = new JMenu("Importer");
        importMenu.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JMenuItem importCars = new JMenuItem("Voitures");
        importCars.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        importCars.setActionCommand(ControllerActions.IMPORT_CARS);

        JMenuItem importClients = new JMenuItem("Clients");
        importClients.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        importClients.setActionCommand(ControllerActions.IMPORT_CLIENTS);

        JMenuItem importContracts = new JMenuItem("Contrats");
        importContracts.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        importContracts.setActionCommand(ControllerActions.IMPORT_CONTRACTS);

        JMenuItem importReservations = new JMenuItem("Réservations");
        importReservations.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        importReservations.setActionCommand(ControllerActions.IMPORT_RESERVATIONS);

        importMenu.add(importCars);
        importMenu.add(importClients);
        importMenu.add(importContracts);
        importMenu.add(importReservations);

        // Sous-menu Exporter
        JMenu exportMenu = new JMenu("Exporter");
        exportMenu.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JMenuItem exportCars = new JMenuItem("Voitures");
        exportCars.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        exportCars.setActionCommand(ControllerActions.EXPORT_CARS);

        JMenuItem exportClients = new JMenuItem("Clients");
        exportClients.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        exportClients.setActionCommand(ControllerActions.EXPORT_CLIENTS);

        JMenuItem exportContracts = new JMenuItem("Contrats");
        exportContracts.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        exportContracts.setActionCommand(ControllerActions.EXPORT_CONTRACTS);

        JMenuItem exportReservations = new JMenuItem("Réservations");
        exportReservations.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        exportReservations.setActionCommand(ControllerActions.EXPORT_RESERVATIONS);

        exportMenu.add(exportCars);
        exportMenu.add(exportClients);
        exportMenu.add(exportContracts);
        exportMenu.add(exportReservations);

        fileMenu.add(importMenu);
        fileMenu.add(exportMenu);
        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Quitter");
        exitItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        exitItem.setActionCommand("EXIT");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        // Menu Thèmes
        JMenu themeMenu = new JMenu("Thèmes");
        themeMenu.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JMenuItem lightThemeItem = new JMenuItem("Thème Clair");
        lightThemeItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lightThemeItem.addActionListener(e -> changeTheme("light"));

        JMenuItem darkThemeItem = new JMenuItem("Thème Dracula");
        darkThemeItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        darkThemeItem.addActionListener(e -> changeTheme("dark"));

        themeMenu.add(lightThemeItem);
        themeMenu.add(darkThemeItem);

        // Menu Paramètres
        JMenu settingsMenu = new JMenu("Paramètres");
        settingsMenu.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Sous-menu Format de date
        JMenu dateFormatMenu = new JMenu("Format de date");
        dateFormatMenu.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JMenuItem defaultFormatItem = new JMenuItem("Standard (dd/MM/yyyy)");
        defaultFormatItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        defaultFormatItem.addActionListener(e -> changeDateFormat(DateFormatter.FORMAT_DEFAULT));

        JMenuItem isoFormatItem = new JMenuItem("ISO (yyyy-MM-dd)");
        isoFormatItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        isoFormatItem.addActionListener(e -> changeDateFormat(DateFormatter.FORMAT_ISO));

        JMenuItem longFormatItem = new JMenuItem("Long (dd MMMM yyyy)");
        longFormatItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        longFormatItem.addActionListener(e -> changeDateFormat(DateFormatter.FORMAT_LONG));

        JMenuItem shortFormatItem = new JMenuItem("Court (dd/MM/yy)");
        shortFormatItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        shortFormatItem.addActionListener(e -> changeDateFormat(DateFormatter.FORMAT_SHORT));

        dateFormatMenu.add(defaultFormatItem);
        dateFormatMenu.add(isoFormatItem);
        dateFormatMenu.add(longFormatItem);
        dateFormatMenu.add(shortFormatItem);


        settingsMenu.add(dateFormatMenu);

        // Menu Export/Import Avancé
        JMenu advancedMenu = new JMenu("Export/Import Avancé");
        advancedMenu.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JMenuItem exportImportItem = new JMenuItem("Gestionnaire Export/Import");
        exportImportItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        exportImportItem.addActionListener(e -> showExportImportDialog());

        advancedMenu.add(exportImportItem);

        menuBar.add(fileMenu);
        menuBar.add(themeMenu);
        menuBar.add(settingsMenu);
        menuBar.add(advancedMenu);

        setJMenuBar(menuBar);

        // Création du JTabbedPane
        tabbedPane = new JTabbedPane();

        // Onglet 1: Lister véhicules
        panel1 = createModernPanel(Color.WHITE, true);
        String[] carColumns = {"ID Car", "Marque", "Modèle", "Année", "Prix/jour", "Kilometrages", "Carburant", "Transmission", "Places", "Disponibilité"};
        Object[][] carData = {};  // Les données seront ajoutées dynamiquement
        table1 = new JTable(carData, carColumns);
        stylizeTable(table1);
        JScrollPane scrollPane1 = new JScrollPane(table1);
        JPanel buttonPanel1 = new JPanel(new FlowLayout(posBouton));
        buttonPanel1.setOpaque(true);
        buttonPanel1.setBackground(PANEL_BG);
        buttonAddCar = createModernButton("Ajouter", ADD_COLOR, TEXT_COLOR, ADD_HOVER, "add");
        buttonAddCar.setActionCommand(ControllerActions.ADD_CAR);
        buttonModifyCar = createModernButton("Modifier", EDIT_COLOR, TEXT_COLOR, EDIT_HOVER, "edit");
        buttonModifyCar.setActionCommand(ControllerActions.MODIFY_CAR);
        buttonDeleteCar = createModernButton("Supprimer", DELETE_COLOR, TEXT_COLOR, DELETE_HOVER, "delete");
        buttonDeleteCar.setActionCommand(ControllerActions.DELETE_CAR);
        buttonPanel1.add(buttonAddCar);
        buttonPanel1.add(buttonModifyCar);
        buttonPanel1.add(buttonDeleteCar);
        panel1.add(buttonPanel1, BorderLayout.NORTH);
        panel1.add(scrollPane1, BorderLayout.CENTER);

        // Onglet 2: Lister clients
        panel2 = createModernPanel(Color.WHITE, true);
        String[] clientColumns = {"ID Client", "Name", "Surname", "Email", "Password", "Birthdate", "Phone"};
        Object[][] clientData = {};  // Les données seront ajoutées dynamiquement
        table2 = new JTable(clientData, clientColumns);
        stylizeTable(table2);
        JScrollPane scrollPane2 = new JScrollPane(table2);
        JPanel buttonPanel2 = new JPanel(new FlowLayout(posBouton));
        buttonPanel2.setOpaque(true);
        buttonPanel2.setBackground(PANEL_BG);
        buttonAddClient = createModernButton("Ajouter", ADD_COLOR, TEXT_COLOR, ADD_HOVER, "add");
        buttonAddClient.setActionCommand(ControllerActions.ADD_CLIENT);
        buttonModifyClient = createModernButton("Modifier", EDIT_COLOR, TEXT_COLOR, EDIT_HOVER, "edit");
        buttonModifyClient.setActionCommand(ControllerActions.MODIFY_CLIENT);
        buttonDeleteClient = createModernButton("Supprimer", DELETE_COLOR, TEXT_COLOR, DELETE_HOVER, "delete");
        buttonDeleteClient.setActionCommand(ControllerActions.DELETE_CLIENT);
        buttonPanel2.add(buttonAddClient);
        buttonPanel2.add(buttonModifyClient);
        buttonPanel2.add(buttonDeleteClient);
        panel2.add(buttonPanel2, BorderLayout.NORTH);
        panel2.add(scrollPane2, BorderLayout.CENTER);

        // Onglet 3: Lister réservations
        panel3 = createModernPanel(Color.WHITE, true);
        String[] locationColumns = {"ID Reservation", "Immatriculation", "Nom Complet", "Date Début", "Date Fin", "Responsable", "Prix Total", "Notes"};
        Object[][] locationData = {};  // Les données seront ajoutées dynamiquement
        table3 = new JTable(locationData, locationColumns);
        stylizeTable(table3);
        JScrollPane scrollPane3 = new JScrollPane(table3);
        JPanel buttonPanel3 = new JPanel(new FlowLayout(posBouton));
        buttonPanel3.setOpaque(true);
        buttonPanel3.setBackground(PANEL_BG);
        buttonAddLocation = createModernButton("Ajouter", ADD_COLOR, TEXT_COLOR, ADD_HOVER, "add");
        buttonAddLocation.setActionCommand(ControllerActions.ADD_LOCATION);
        buttonModifyLocation = createModernButton("Modifier", EDIT_COLOR, TEXT_COLOR, EDIT_HOVER, "edit");
        buttonModifyLocation.setActionCommand(ControllerActions.MODIFY_LOCATION);
        buttonDeleteLocation = createModernButton("Supprimer", DELETE_COLOR, TEXT_COLOR, DELETE_HOVER, "delete");
        buttonDeleteLocation.setActionCommand(ControllerActions.DELETE_LOCATION);
        buttonPanel3.add(buttonAddLocation);
        buttonPanel3.add(buttonModifyLocation);
        buttonPanel3.add(buttonDeleteLocation);
        panel3.add(buttonPanel3, BorderLayout.NORTH);
        panel3.add(scrollPane3, BorderLayout.CENTER);

        // Onglet 4: Afficher images véhicules
        panel4 = new JPanel(new GridLayout(2, 3, 10, 10));
        panel4.setBorder(BorderFactory.createEmptyBorder(tailleBorder, tailleBorder, tailleBorder, tailleBorder));
        panel4.setBackground(new Color(245, 247, 250));

        // Onglet 5: Lister contrats
        panel5 = createModernPanel(Color.WHITE, true);
        String[] contratColumns = {"ID Contrat", "Véhicule", "Client", "Caution", "Type Assurance", "Options", "Signé", "Statut"};
        Object[][] contratData = {}; // À remplir dynamiquement
        table4 = new JTable(contratData, contratColumns);
        stylizeTable(table4);
        JScrollPane scrollPane4 = new JScrollPane(table4);
        JPanel buttonPanel4 = new JPanel(new FlowLayout(posBouton));
        buttonPanel4.setOpaque(true);
        buttonPanel4.setBackground(PANEL_BG);
        buttonAddContrat = createModernButton("Ajouter", ADD_COLOR, TEXT_COLOR, ADD_HOVER, "add");
        buttonAddContrat.setActionCommand(ControllerActions.ADD_CONTRAT);
        buttonModifyContrat = createModernButton("Modifier", EDIT_COLOR, TEXT_COLOR, EDIT_HOVER, "edit");
        buttonModifyContrat.setActionCommand(ControllerActions.MODIFY_CONTRAT);
        buttonDeleteContrat = createModernButton("Supprimer", DELETE_COLOR, TEXT_COLOR, DELETE_HOVER, "delete");
        buttonDeleteContrat.setActionCommand(ControllerActions.DELETE_CONTRAT);
        buttonExportContratPDF = createModernButton("Exporter PDF", EXPORT_COLOR, TEXT_COLOR, EXPORT_HOVER, "export");
        buttonPanel4.add(buttonAddContrat);
        buttonPanel4.add(buttonModifyContrat);
        buttonPanel4.add(buttonDeleteContrat);
        buttonPanel4.add(buttonExportContratPDF);
        panel5.add(buttonPanel4, BorderLayout.NORTH);
        panel5.add(scrollPane4, BorderLayout.CENTER);
        
        // Action du bouton Exporter PDF
        buttonExportContratPDF.addActionListener(e -> {
            Contrat selectedContrat = getSelectedContrat();
            if (selectedContrat == null) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un contrat à exporter.", "Aucun contrat sélectionné", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Enregistrer le contrat au format PDF");
            fileChooser.setSelectedFile(new java.io.File("contrat_" + selectedContrat.getIdContrat() + java.time.LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)+".pdf"));
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                try {
                    org.example.utils.PDFExporter.exportContratToPDF(selectedContrat, fileToSave.getAbsolutePath());
                    JOptionPane.showMessageDialog(this, "Contrat exporté avec succès !", "Export PDF", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erreur lors de l'export PDF : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Ajout des onglets
        tabbedPane.addTab("Lister Véhicules", panel1);
        tabbedPane.addTab("Lister Clients", panel2);
        tabbedPane.addTab("Lister Locations", panel3);
        tabbedPane.addTab("Véhicule", panel4);
        tabbedPane.addTab("Contrats", panel5);
        tabbedPane.setSelectedIndex(0);
        add(tabbedPane, BorderLayout.CENTER);

        // Maximiser la fenêtre pour qu'elle occupe tout l'écran
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Taille par défaut si l'utilisateur restaure la fenêtre
        setSize(1200, 800);
        setLocationRelativeTo(null);
    }

    // Implémentation des méthodes de l'interface ViewLocation
    @Override
    public void run() {
        // S'assurer que tous les composants sont correctement initialisés avant d'afficher la fenêtre
        SwingUtilities.invokeLater(() -> {
            this.setVisible(true);
        });
    }

    @Override
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
        // Initialiser les formulaires et dialogues
        sessionDialog = new ImprovedSessionDialog(this, controller);
        addCarForm = new AddCarForm(this, controller);
        addClientForm = new AddClientForm(this, controller);
        addLocationForm = new AddLocationForm(this, controller);
        
        // Centraliser l'ajout des ActionListeners
        setupEventListeners();
        
        // Associer le controller aux menus d'import/export
        updateMenuListeners();
    }

    /**
     * Configure les écouteurs d'événements pour tous les boutons
     */
    private void setupEventListeners() {
        // Configurer les écouteurs pour les boutons de voitures
        buttonAddCar.addActionListener(controller);
        buttonModifyCar.addActionListener(controller);
        buttonDeleteCar.addActionListener(controller);

        // Configurer les écouteurs pour les boutons de clients
        buttonAddClient.addActionListener(controller);
        buttonModifyClient.addActionListener(controller);
        buttonDeleteClient.addActionListener(controller);

        // Configurer les écouteurs pour les boutons de réservations
        buttonAddLocation.addActionListener(controller);
        buttonModifyLocation.addActionListener(controller);
        buttonDeleteLocation.addActionListener(controller);

        // Configurer les écouteurs pour les boutons de contrats
        buttonAddContrat.addActionListener(controller);
        buttonModifyContrat.addActionListener(controller);
        buttonDeleteContrat.addActionListener(controller);
        
        // Configurer l'écouteur pour le bouton de recherche
        searchButton.addActionListener(controller);

        // Configurer l'écouteur pour le bouton de session
        sessionButton.addActionListener(e -> {
            if (sessionButton.getText().equals("Se connecter")) {
                controller.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ControllerActions.SESSION));
            } else {
                controller.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ControllerActions.LOGOUT));
            }
        });
    }

    // Méthodes pour exposer les fonctionnalités au controller
    public void showSessionDialogFromController() {
        sessionDialog.showDialog();
    }

    // Implémentation des méthodes pour mettre à jour les tables
    @Override
    public void updateCarTable(List<Car> cars) {
        // Créer un nouveau modèle de table avec les données des voitures
        Object[][] data = new Object[cars.size()][10];
        int i = 0;
        for (Car car : cars) {
            data[i][0] = car.getIdCar();
            data[i][1] = car.getBrand();
            data[i][2] = car.getModel();
            data[i][3] = car.getYear();
            data[i][4] = car.getPriceday() + " €/jour";
            data[i][5] = car.getMileage() + " km";
            data[i][6] = car.getFuelType();
            data[i][7] = car.getTransmission();
            data[i][8] = car.getSeats();
            data[i][9] = car.isAvailable() ? "Disponible" : "Non disponible";
            i++;
        }

        // Mettre à jour la table avec les nouvelles données
        String[] columns = {"ID Car", "Marque", "Modèle", "Année", "Prix/jour", "Kilometrages", "Carburant", "Transmission", "Places", "Disponibilité"};
        table1.setModel(new javax.swing.table.DefaultTableModel(data, columns));
        System.out.println("Table des voitures mise à jour avec " + i + " voitures");

        // Mettre à jour les images des voitures dans l'onglet Véhicule
        updateCarImages(cars);
    }

    @Override
    public void updateClientTable(List<Client> clients) {
        // Créer un nouveau modèle de table avec les données des clients
        Object[][] data = new Object[clients.size()][7];
        int i = 0;
        for (Client client : clients) {
            data[i][0] = client.getIdClient();
            data[i][1] = client.getName();
            data[i][2] = client.getSurname();
            data[i][3] = client.getEmail();
            data[i][4] = client.getLicenseNumber();
            // Utiliser DateFormatter pour formater la date de naissance
            data[i][5] = client.getBirthDate() != null ? DateFormatter.format(client.getBirthDate()) : "N/A";
            data[i][6] = client.getPhoneNumber();
            i++;
        }

        // Mettre à jour la table avec les nouvelles données
        String[] columns = {"ID Client", "Nom", "Prénom", "Email","Numéro de permis", "Date de naissance", "Téléphone"};
        table2.setModel(new javax.swing.table.DefaultTableModel(data, columns));
        System.out.println("Table des clients mise à jour avec " + i + " clients");
    }

    @Override
    public void updateReservationTable(List<Reservation> reservations) {
        // Créer un nouveau modèle de table avec les données des réservations
        Object[][] data = new Object[reservations.size()][8];
        int i = 0;
        for (Reservation reservation : reservations) {
            data[i][0] = reservation.getIdReservation();

            // Immatriculation de la voiture (nouvel attribut)
            data[i][1] = reservation.getCarRegistration() != null ? reservation.getCarRegistration() : "N/A";

            // Nom complet du client (nouvel attribut)
            data[i][2] = reservation.getClientFullName() != null ? reservation.getClientFullName() : "N/A";

            // Dates et autres informations - Utiliser DateFormatter pour formater les dates
            data[i][3] = reservation.getStartDate() != null ? DateFormatter.format(reservation.getStartDate()) : "N/A";
            data[i][4] = reservation.getEndDate() != null ? DateFormatter.format(reservation.getEndDate()) : "N/A";
            data[i][5] = reservation.getResponsable();
            data[i][6] = reservation.getPrice();
            data[i][7] = reservation.getNotes();
            i++;
        }

        // Mettre à jour la table avec les nouvelles données
        String[] columns = {"ID Réservation", "Immatriculation",  "Nom Complet", "Date Début", "Date Fin", "Responsable", "Prix Total", "Notes"};
        table3.setModel(new javax.swing.table.DefaultTableModel(data, columns));
        System.out.println("Table des réservations mise à jour avec " + i + " réservations");
    }

    /**
     * Récupère la voiture sélectionnée dans la table des voitures
     * @return La voiture sélectionnée ou null si aucune sélection
     */
    @Override
    public Car getSelectedCar() {
        int selectedRow = table1.getSelectedRow();
        if (selectedRow >= 0) {
            // Récupérer l'ID de la voiture sélectionnée
            String idCar = (String) table1.getValueAt(selectedRow, 0);

            // Demander au contrôleur de récupérer la voiture complète
            return controller.getCarById(idCar);
        }
        return null;
    }

    /**
     * Affiche le formulaire de modification d'une voiture
     * @param car La voiture à modifier
     */
    @Override
    public void showModifyCarFormFromController(Car car) {
        if (car != null) {
            ModifyCarForm modifyCarForm = new ModifyCarForm(this, controller, car);
            boolean carModified = modifyCarForm.showForm();

            if (carModified) {
                // Mettre à jour la table des voitures
                List<Car> cars = controller.getAllCars();
                updateCarTable(cars);
            }
        }
    }

    @Override
    public Car promptAddCar() {
        // Créer une nouvelle instance du formulaire pour éviter les problèmes de données
        addCarForm = new AddCarForm(this, controller);
        addCarForm.showForm(); // Afficher le formulaire
        addCarForm.setVisible(true);
        Car car = addCarForm.getCar();
        return car;
    }

    /**
     * Affiche le formulaire d'ajout de client et retourne le client créé
     * @return Le client créé ou null si annulé
     */
    public Client promptAddClient() {
        // Créer une nouvelle instance du formulaire pour éviter les problèmes de données
        addClientForm = new AddClientForm(this, controller);
        addClientForm.showForm();
        addClientForm.setVisible(true);
        Client client = addClientForm.getClient();
        return client;
    }

    /**
     * Affiche le formulaire d'ajout de location et retourne la réservation créée
     * @return La réservation créée ou null si annulée
     */
    public Reservation promptAddLocation() {
        // Créer une nouvelle instance du formulaire pour éviter les problèmes de données
        addLocationForm = new AddLocationForm(this, controller);
        addLocationForm.showForm();
        addLocationForm.setVisible(true);
        Reservation reservation = addLocationForm.getReservation();
        return reservation;
    }

    @Override
    public Contrat promptAddContrat() {
        addContratForm = new AddContratForm(this, controller);
        addContratForm.showForm();

        // Récupérer les réservations existantes et les charger dans le formulaire
        List<Reservation> reservations = controller.getAllReservations();
        addContratForm.loadReservations(reservations);

        addContratForm.setVisible(true);
        Contrat contrat = addContratForm.getContrat();

        // Si un contrat a été créé et associé à une réservation, mettre à jour la réservation
        if (contrat != null && addContratForm.getSelectedReservation() != null) {
            Reservation selectedReservation = addContratForm.getSelectedReservation();
            selectedReservation.setContrat(contrat);
            controller.updateReservation(selectedReservation);
        }

        return contrat;
    }

    @Override
    public void updateContratTable(List<Contrat> contrats) {
        // Créer un modèle de table pour les contrats
        DefaultTableModel model = new DefaultTableModel();

        // Définir les colonnes
        model.addColumn("ID Contrat");
        model.addColumn("Véhicule");
        model.addColumn("Client");
        model.addColumn("Caution");
        model.addColumn("Type Assurance");
        model.addColumn("Options");
        model.addColumn("Signé");
        model.addColumn("Statut");
        model.addColumn("Prix Total");

        // Ajouter les données des contrats
        for (Contrat contrat : contrats) {
            // Préparer les informations du véhicule et du client
            String vehiculeInfo = contrat.getCarBrand() + " " + contrat.getCarModel();
            String clientInfo = contrat.getClientName() + " " + contrat.getClientSurname();

            // Formater les options en une chaîne lisible
            String optionsStr = String.join(", ", contrat.getOptions());

            // Ajouter une ligne pour chaque contrat
            model.addRow(new Object[] {
                    contrat.getIdContrat(),
                    vehiculeInfo,
                    clientInfo,
                    contrat.getCaution() + " €",
                    contrat.getTypeAssurance(),
                    optionsStr,
                    contrat.isEstSigne() ? "Oui" : "Non",
                    contrat.getStatutContrat().toString(),
                    contrat.getPrixTotal()
            });
        }

        // Appliquer le modèle à la table
        table4.setModel(model);

        // Ajuster la largeur des colonnes
        table4.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID Contrat
        table4.getColumnModel().getColumn(1).setPreferredWidth(150); // Véhicule
        table4.getColumnModel().getColumn(2).setPreferredWidth(150); // Client
        table4.getColumnModel().getColumn(3).setPreferredWidth(80);  // Caution
        table4.getColumnModel().getColumn(4).setPreferredWidth(120); // Type Assurance
        table4.getColumnModel().getColumn(5).setPreferredWidth(200); // Options
        table4.getColumnModel().getColumn(6).setPreferredWidth(60);  // Signé
        table4.getColumnModel().getColumn(7).setPreferredWidth(100); // Statut

        // Appliquer le style à la table
        stylizeTable(table4);
    }

    /**
     * Récupère le contrat sélectionné dans la table des contrats
     * @return Le contrat sélectionné ou null si aucun contrat n'est sélectionné
     */
    public Contrat getSelectedContrat() {
        int selectedRow = table4.getSelectedRow();
        if (selectedRow >= 0) {
            // Récupérer l'ID du contrat sélectionné
            String idContrat = table4.getValueAt(selectedRow, 0).toString();

            // Demander au contrôleur de récupérer le contrat complet
            return controller.getContratById(idContrat);
        }
        return null;
    }

    /**
     * Affiche le formulaire de modification d'un contrat
     * @param contrat Le contrat à modifier
     */
    public void showModifyContratFormFromController(Contrat contrat) {
        if (contrat != null) {
            // Créer une instance du formulaire de modification
            ModifyContratForm modifyContratForm = new ModifyContratForm(this, controller, contrat);

            // Initialiser le formulaire d'abord pour que les composants soient créés
            modifyContratForm.showForm();

            // Récupérer les réservations et les charger dans le formulaire après l'initialisation
            List<Reservation> reservations = controller.getAllReservations();
            modifyContratForm.loadReservations(reservations);

            // Afficher le formulaire
            modifyContratForm.setVisible(true);

            // Récupérer le contrat modifié
            Contrat modifiedContrat = modifyContratForm.getContrat();

            // Si un contrat a été modifié, mettre à jour la réservation associée
            if (modifiedContrat != null && modifyContratForm.getSelectedReservation() != null) {
                Reservation selectedReservation = modifyContratForm.getSelectedReservation();
                controller.updateContrat(modifiedContrat);
            }
        }
    }

    /**
     * Affiche le formulaire de modification d'un client
     * @param client Le client à modifier
     */
    public void showModifyClientFormFromController(Client client) {
        if (client != null) {
            ModifyClientForm modifyClientForm = new ModifyClientForm(this, controller, client);
            boolean clientModified = modifyClientForm.showForm();

            if (clientModified) {
                // Mettre à jour la table des clients
                List<Client> clients = controller.getAllClients();
                updateClientTable(clients);
            }
        }
    }

    /**
     * Affiche le formulaire de modification d'une réservation
     * @param reservation La réservation à modifier
     */
    @Override
    public void showModifyLocationFormFromController(Reservation reservation) {
        if (reservation != null) {
            // Créer et afficher le formulaire de modification
            ModifyLocationForm modifyLocationForm = new ModifyLocationForm(this, controller, reservation);
            Reservation modifiedReservation = modifyLocationForm.showForm();

            // Si une réservation modifiée est retournée, mettre à jour la réservation
            if (modifiedReservation != null) {
                // Mettre à jour la réservation dans le modèle
                controller.updateReservation(modifiedReservation);
            }
        }
    }

    /**
     * Récupère la liste des voitures pour les formulaires
     * @return La liste des voitures
     */
    public List<Car> getCarList() {
        List<Car> cars = new ArrayList<>();
        // Récupérer les données du tableau des voitures
        if (table1 != null && table1.getModel() instanceof DefaultTableModel) {
            DefaultTableModel model = (DefaultTableModel) table1.getModel();
            int rowCount = model.getRowCount();
            System.out.println("Nombre de lignes dans le tableau des voitures: " + rowCount);

            // Afficher les noms des colonnes pour débogage
            int columnCount = model.getColumnCount();
            System.out.println("Nombre de colonnes: " + columnCount);
            for (int j = 0; j < columnCount; j++) {
                System.out.println("Colonne " + j + ": " + model.getColumnName(j));
            }

            for (int i = 0; i < rowCount; i++) {
                try {
                    // Créer une voiture à partir des données du tableau
                    String idCar = (String) model.getValueAt(i, 0);
                    String brand = (String) model.getValueAt(i, 1);
                    String modelName = (String) model.getValueAt(i, 2);
                    int year = Integer.parseInt(model.getValueAt(i, 3).toString());

                    // Extraction du prix numérique de la chaîne (en supprimant "€/jour")
                    String priceString = model.getValueAt(i, 4).toString();
                    float priceday;
                    if (priceString.contains("€")) {
                        priceString = priceString.replace("€/jour", "").trim();
                        priceday = Float.parseFloat(priceString);
                    } else {
                        priceday = Float.parseFloat(priceString);
                    }

                    // Correction: conversion de String en boolean
                    boolean available = false;
                    Object availableObj = model.getValueAt(i, 9);
                    if (availableObj instanceof Boolean) {
                        available = (Boolean) availableObj;
                    } else if (availableObj instanceof String) {
                        String availableStr = availableObj.toString();
                        available = availableStr.equalsIgnoreCase("Disponible") || availableStr.equalsIgnoreCase("true");
                    }

                    Car car = new Car(idCar, brand, modelName, year, priceday);
                    car.setAvailable(available);
                    cars.add(car);

                    System.out.println("Voiture ajoutée: " + idCar + " - " + brand + " " + modelName);
                } catch (Exception e) {
                    System.out.println("Erreur lors de la récupération de la voiture à la ligne " + i + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Le tableau des voitures est vide ou n'est pas un DefaultTableModel");
        }
        System.out.println("Nombre total de voitures récupérées: " + cars.size());
        return cars;
    }

    /**
     * Récupère la liste des clients pour les formulaires
     * @return La liste des clients
     */
    public List<Client> getClientList() {
        List<Client> clients = new ArrayList<>();
        // Récupérer les données du tableau des clients
        if (table2 != null && table2.getModel() instanceof DefaultTableModel) {
            DefaultTableModel model = (DefaultTableModel) table2.getModel();
            int rowCount = model.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                // Créer un client à partir des données du tableau
                int idClient = Integer.parseInt(model.getValueAt(i, 0).toString());
                String name = (String) model.getValueAt(i, 1);
                String surname = (String) model.getValueAt(i, 2);
                String email = (String) model.getValueAt(i, 3);
                String password = (String) model.getValueAt(i, 4);

                Client client = new Client();
                client.setIdClient(idClient);
                client.setName(name);
                client.setSurname(surname);
                client.setEmail(email);
                clients.add(client);
            }
        }
        return clients;
    }

    private JButton createModernButton(String text, Color bg, Color fg, Color hover, String iconName) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        // Animation douce de transition de couleur au hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            private javax.swing.Timer timer;
            private float progress = 0f;
            private boolean entering = false;
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                entering = true;
                if (timer != null && timer.isRunning()) timer.stop();
                timer = new javax.swing.Timer(10, e -> {
                    progress += 0.08f;
                    if (progress >= 1f) progress = 1f;
                    button.setBackground(interpolateColor(bg, hover, progress));
                    if (progress >= 1f) timer.stop();
                });
                timer.start();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                entering = false;
                if (timer != null && timer.isRunning()) timer.stop();
                timer = new javax.swing.Timer(10, e -> {
                    progress -= 0.08f;
                    if (progress <= 0f) progress = 0f;
                    button.setBackground(interpolateColor(bg, hover, progress));
                    if (progress <= 0f) timer.stop();
                });
                timer.start();
            }
        });
        // Ajout d'une icône moderne si disponible (optionnel)
        // ...
        return button;
    }

    // Interpolation de couleur pour animation
    private Color interpolateColor(Color c1, Color c2, float t) {
        int r = (int) (c1.getRed() + t * (c2.getRed() - c1.getRed()));
        int g = (int) (c1.getGreen() + t * (c2.getGreen() - c1.getGreen()));
        int b = (int) (c1.getBlue() + t * (c2.getBlue() - c1.getBlue()));
        return new Color(r, g, b);
    }

    // --- UTILITAIRE : Panel moderne ---
    private JPanel createModernPanel(Color bg, boolean shadow) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (shadow) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(0,0,0,40));
                    g2.fillRoundRect(8, 8, getWidth()-16, getHeight()-16, 20, 20);
                    g2.dispose();
                }
            }
        };
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(tailleBorder, tailleBorder, tailleBorder, tailleBorder),
                BorderFactory.createLineBorder(new Color(230, 232, 240), 2, true)));
        panel.setBackground(bg);
        panel.setOpaque(false);
        return panel;
    }

    // --- UTILITAIRE : JTable moderne ---
    private void stylizeTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(52, 152, 219));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setGridColor(new Color(230, 232, 240));
        table.setSelectionBackground(new Color(120, 130, 200));
        table.setSelectionForeground(Color.WHITE);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        // Lignes alternées
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(244, 246, 248));
                } else {
                    c.setBackground(new Color(120, 130, 200));
                }
                // Badge de statut pour la colonne Statut (contrats)
                if (table.getColumnName(column).equalsIgnoreCase("Statut") && value != null) {
                    String statut = value.toString();
                    JLabel label = new JLabel(statut, JLabel.CENTER);
                    label.setOpaque(true);
                    label.setForeground(Color.WHITE);
                    switch (statut) {
                        case "SIGNE":
                        case "TERMINE":
                            label.setBackground(new Color(39, 174, 96)); // vert
                            break;
                        case "EN_ATTENTE":
                            label.setBackground(new Color(243, 156, 18)); // orange
                            break;
                        case "ANNULE":
                        case "EXPIRE":
                            label.setBackground(new Color(231, 76, 60)); // rouge
                            break;
                        default:
                            label.setBackground(new Color(52, 152, 219)); // bleu
                    }
                    return label;
                }
                return c;
            }
        });
    }

    @Override
    public void updateCarImages(List<Car> cars) {
        panel4.removeAll();

        // Panel principal centré
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(248, 250, 252));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        // Titre/statistiques centré
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sectionTitle = new JLabel("Nos Véhicules Disponibles");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        sectionTitle.setForeground(new Color(30, 41, 59));
        sectionTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        long availableCars = cars.stream().filter(Car::isAvailable).count();
        long totalCars = cars.size();
        JLabel statsLabel = new JLabel(String.format("📊 %d véhicules disponibles sur %d au total", availableCars, totalCars));
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        statsLabel.setForeground(new Color(71, 85, 105));
        statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        titlePanel.add(sectionTitle);
        titlePanel.add(statsLabel);
        titlePanel.setMaximumSize(new Dimension(700, 60));
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createVerticalStrut(25));

        // Séparateur discret
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(226, 232, 240));
        separator.setMaximumSize(new Dimension(700, 1));
        mainPanel.add(separator);
        mainPanel.add(Box.createVerticalStrut(25));

        // Panel de la grille de cartes, centré et largeur max
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.setMaximumSize(new Dimension(1600, Integer.MAX_VALUE));
        gridWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel gridPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 32, 32));
        gridPanel.setBackground(new Color(248, 250, 252));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        for (Car car : cars) {
            JPanel carCard = createModernCarCard(car);
            gridPanel.add(carCard);
        }
        gridWrapper.add(gridPanel, BorderLayout.CENTER);
        mainPanel.add(gridWrapper);

        // ScrollPane sur le mainPanel
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(25);
        scrollPane.setBackground(new Color(248, 250, 252));

        panel4.setLayout(new BorderLayout());
        panel4.setBackground(new Color(248, 250, 252));
        panel4.add(scrollPane, BorderLayout.CENTER);
        panel4.revalidate();
        panel4.repaint();
    }

    // Amélioration de l'effet carte : coins arrondis + ombre
    private JPanel createModernCarCard(Car car) {
        JPanel card = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Ombre portée
                g2.setColor(new Color(0,0,0,18));
                g2.fillRoundRect(6, 8, getWidth()-12, getHeight()-12, 28, 28);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        JPanel content = new JPanel(new BorderLayout(0, 0));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        content.setPreferredSize(new Dimension(350, 420));
        content.setOpaque(true);
        
        // Effet d'ombre
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                        BorderFactory.createEmptyBorder(0, 0, 0, 0)
                )
        ));

        // Header avec image
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(new Color(241, 245, 249));
        imagePanel.setPreferredSize(new Dimension(350, 220));

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(241, 245, 249));

        String imagePath = car.getImage();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                String fullImagePath = imagePath;
                if (!imagePath.startsWith("images/") && !imagePath.startsWith("/")) {
                    fullImagePath = "images/" + imagePath;
                }
                File imageFile = new File(fullImagePath);
                if (imageFile.exists()) {
                    ImageIcon originalIcon = new ImageIcon(fullImagePath);
                    int targetWidth = 350;
                    int targetHeight = 220;
                    Image resizedImage = originalIcon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(resizedImage));
                    imageLabel.setHorizontalAlignment(JLabel.CENTER);
                    imageLabel.setVerticalAlignment(JLabel.CENTER);
                } else {
                    imageLabel.setText("🚗");
                    imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
                    imageLabel.setForeground(new Color(148, 163, 184));
                    System.out.println("Image non trouvée: " + fullImagePath);
                }
            } catch (Exception e) {
                imageLabel.setText("🚗");
                imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
                imageLabel.setForeground(new Color(148, 163, 184));
                System.out.println("Erreur lors du chargement de l'image: " + e.getMessage());
            }
        } else {
            imageLabel.setText("🚗");
            imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
            imageLabel.setForeground(new Color(148, 163, 184));
        }

        // Badge de disponibilité
        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        badgePanel.setOpaque(false);
        
        JLabel statusBadge = new JLabel(car.isAvailable() ? "✓ Disponible" : "✗ Indisponible");
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusBadge.setForeground(Color.WHITE);
        statusBadge.setBackground(car.isAvailable() ? new Color(34, 197, 94) : new Color(239, 68, 68));
        statusBadge.setOpaque(true);
        statusBadge.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        
        badgePanel.add(statusBadge);
        imagePanel.add(badgePanel, BorderLayout.NORTH);
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // Contenu principal
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Titre du véhicule
        JLabel titleLabel = new JLabel(car.getBrand() + " " + car.getModel());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(15, 23, 42));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Année
        JLabel yearLabel = new JLabel("Année " + car.getYear());
        yearLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        yearLabel.setForeground(new Color(100, 116, 139));
        yearLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Prix
        JLabel priceLabel = new JLabel(car.getPriceday() + " €/jour");
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLabel.setForeground(new Color(59, 130, 246));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Séparateur
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(226, 232, 240));
        separator.setMaximumSize(new Dimension(300, 1));

        // Informations techniques
        JPanel specsPanel = new JPanel(new GridLayout(2, 2, 10, 8));
        specsPanel.setBackground(Color.WHITE);
        specsPanel.setMaximumSize(new Dimension(300, 80));

        // Carburant
        JLabel fuelLabel = createSpecLabel("⛽ " + car.getFuelType(), new Color(34, 197, 94));
        // Transmission
        JLabel transLabel = createSpecLabel("⚙️ " + car.getTransmission(), new Color(59, 130, 246));
        // Places
        JLabel seatsLabel = createSpecLabel("👥 " + car.getSeats() + " places", new Color(168, 85, 247));
        // Kilométrage
        JLabel mileageLabel = createSpecLabel("🛣️ " + car.getMileage() + " km", new Color(245, 158, 11));

        specsPanel.add(fuelLabel);
        specsPanel.add(transLabel);
        specsPanel.add(seatsLabel);
        specsPanel.add(mileageLabel);

        // Assemblage du contenu
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(yearLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(priceLabel);
        contentPanel.add(Box.createVerticalStrut(12));
        contentPanel.add(separator);
        contentPanel.add(Box.createVerticalStrut(12));
        contentPanel.add(specsPanel);

        content.add(imagePanel, BorderLayout.NORTH);
        content.add(contentPanel, BorderLayout.CENTER);

        // Effet hover avec animation
        content.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                content.setCursor(new Cursor(Cursor.HAND_CURSOR));
                content.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(3, 3, 7, 3),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(59, 130, 246), 2, true),
                                BorderFactory.createEmptyBorder(0, 0, 0, 0)
                        )
                ));
                content.setBackground(new Color(248, 250, 252));
            }

            public void mouseExited(MouseEvent e) {
                content.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                content.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(0, 0, 0, 0),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                                BorderFactory.createEmptyBorder(0, 0, 0, 0)
                        )
                ));
                content.setBackground(Color.WHITE);
            }

            public void mouseClicked(MouseEvent e) {
                showCarDetails(car);
            }
        });

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    /**
     * Crée un label stylisé pour les spécifications
     */
    private JLabel createSpecLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(color);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        return label;
    }

    /**
     * Affiche les détails d'une voiture dans une fenêtre modale
     * @param car La voiture dont on veut afficher les détails
     */
    @Override
    public void showCarDetails(Car car) {
        if (car == null) return;

        JDialog detailsDialog = new JDialog(this, "Détails du véhicule", true);
        detailsDialog.setSize(900, 600);
        detailsDialog.setLocationRelativeTo(this);
        detailsDialog.setLayout(new BorderLayout());

        // Header avec gradient
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color start = new Color(59, 130, 246); // Bleu
                Color end = new Color(147, 51, 234); // Violet
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, start, w, 0, end);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setPreferredSize(new Dimension(900, 80));
        headerPanel.setOpaque(false);

        // Titre du header
        JLabel headerTitle = new JLabel(car.getBrand() + " " + car.getModel());
        headerTitle.setForeground(Color.WHITE);
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerTitle.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Badge de statut dans le header
        JLabel statusBadge = new JLabel(car.isAvailable() ? "✓ Disponible" : "✗ Indisponible");
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusBadge.setForeground(Color.WHITE);
        statusBadge.setBackground(car.isAvailable() ? new Color(34, 197, 94) : new Color(239, 68, 68));
        statusBadge.setOpaque(true);
        statusBadge.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JPanel headerContent = new JPanel(new BorderLayout());
        headerContent.setOpaque(false);
        headerContent.add(headerTitle, BorderLayout.WEST);
        headerContent.add(statusBadge, BorderLayout.EAST);
        headerPanel.add(headerContent, BorderLayout.CENTER);

        detailsDialog.add(headerPanel, BorderLayout.NORTH);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(25, 25));
        mainPanel.setBackground(new Color(248, 250, 252));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Panel gauche : image et prix
        JPanel leftPanel = new JPanel(new BorderLayout(15, 15));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setPreferredSize(new Dimension(400, 450));
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Image du véhicule
        JPanel imageContainer = new JPanel(new BorderLayout());
        imageContainer.setBackground(new Color(241, 245, 249));
        imageContainer.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true));

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(241, 245, 249));
        imageLabel.setPreferredSize(new Dimension(360, 280));

        String imagePath = car.getImage();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                String fullImagePath = imagePath;
                if (!imagePath.startsWith("images/") && !imagePath.startsWith("/")) {
                    fullImagePath = "images/" + imagePath;
                }
                File imageFile = new File(fullImagePath);
                if (imageFile.exists()) {
                    ImageIcon originalIcon = new ImageIcon(fullImagePath);
                    int targetWidth = 350;
                    int targetHeight = 220;
                    Image resizedImage = originalIcon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(resizedImage));
                    imageLabel.setHorizontalAlignment(JLabel.CENTER);
                    imageLabel.setVerticalAlignment(JLabel.CENTER);
                } else {
                    imageLabel.setText("🚗");
                    imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
                    imageLabel.setForeground(new Color(148, 163, 184));
                    System.out.println("Image non trouvée: " + fullImagePath);
                }
            } catch (Exception e) {
                imageLabel.setText("🚗");
                imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
                imageLabel.setForeground(new Color(148, 163, 184));
                System.out.println("Erreur lors du chargement de l'image: " + e.getMessage());
            }
        } else {
            imageLabel.setText("🚗");
            imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
            imageLabel.setForeground(new Color(148, 163, 184));
        }
        imageContainer.add(imageLabel, BorderLayout.CENTER);

        // Panel du prix
        JPanel pricePanel = new JPanel(new BorderLayout());
        pricePanel.setBackground(new Color(59, 130, 246));
        pricePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel priceLabel = new JLabel(car.getPriceday() + " €/jour");
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        priceLabel.setForeground(Color.WHITE);
        priceLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel priceSubtitle = new JLabel("Prix de location");
        priceSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        priceSubtitle.setForeground(new Color(191, 219, 254));
        priceSubtitle.setHorizontalAlignment(JLabel.CENTER);

        JPanel priceContent = new JPanel(new BorderLayout(5, 0));
        priceContent.setOpaque(false);
        priceContent.add(priceLabel, BorderLayout.CENTER);
        priceContent.add(priceSubtitle, BorderLayout.SOUTH);
        pricePanel.add(priceContent, BorderLayout.CENTER);

        leftPanel.add(imageContainer, BorderLayout.CENTER);
        leftPanel.add(pricePanel, BorderLayout.SOUTH);

        // Panel droit : informations détaillées
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        // Titre de la section
        JLabel infoTitle = new JLabel("Informations du véhicule");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        infoTitle.setForeground(new Color(15, 23, 42));
        infoTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Grille d'informations
        JPanel infoGrid = new JPanel(new GridLayout(0, 2, 20, 15));
        infoGrid.setBackground(Color.WHITE);
        infoGrid.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Informations principales
        infoGrid.add(createInfoItem("🚗 Immatriculation", car.getIdCar(), new Color(59, 130, 246)));
        infoGrid.add(createInfoItem("📅 Année", String.valueOf(car.getYear()), new Color(34, 197, 94)));
        infoGrid.add(createInfoItem("⛽ Carburant", car.getFuelType(), new Color(245, 158, 11)));
        infoGrid.add(createInfoItem("⚙️ Transmission", car.getTransmission(), new Color(168, 85, 247)));
        infoGrid.add(createInfoItem("👥 Places", String.valueOf(car.getSeats()), new Color(236, 72, 153)));
        infoGrid.add(createInfoItem("🛣️ Kilométrage", car.getMileage() + " km", new Color(16, 185, 129)));

        rightPanel.add(infoTitle);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(infoGrid);

        // Section des caractéristiques
        JLabel featuresTitle = new JLabel("Caractéristiques");
        featuresTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        featuresTitle.setForeground(new Color(15, 23, 42));
        featuresTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel featuresPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        featuresPanel.setBackground(Color.WHITE);
        featuresPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        // Badges des caractéristiques
        featuresPanel.add(createFeatureBadge("✓ Disponible", new Color(34, 197, 94)));
        featuresPanel.add(createFeatureBadge("🔧 Entretien OK", new Color(16, 185, 129)));
        featuresPanel.add(createFeatureBadge("📋 Assurance", new Color(59, 130, 246)));

        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(featuresTitle);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(featuresPanel);

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(248, 250, 252));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Bouton Réserver (si disponible)
        if (car.isAvailable()) {
            JButton reserveButton = createModernButton("Réserver ce véhicule", ADD_COLOR, TEXT_COLOR, ADD_HOVER, "reserve");
            reserveButton.setPreferredSize(new Dimension(180, 40));
            reserveButton.addActionListener(e -> {
                detailsDialog.dispose();
                
                // Vérifier si l'utilisateur est connecté
                if (controller != null && controller.isUserLoggedIn()) {
                    // Ouvrir le formulaire de réservation avec la voiture pré-sélectionnée
                    AddLocationForm reservationForm = new AddLocationForm(this, controller, car);
                    reservationForm.showForm();
                    reservationForm.setVisible(true);
                    
                    // Récupérer la réservation créée
                    Reservation newReservation = reservationForm.getReservation();
                    
                    if (newReservation != null) {
                        // Demander si l'utilisateur veut créer un contrat immédiatement
                        int choice = JOptionPane.showConfirmDialog(this,
                            "Réservation créée avec succès !\n" +
                            "Voulez-vous créer un contrat de location maintenant ?",
                            "Créer un contrat",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                        
                        if (choice == JOptionPane.YES_OPTION) {
                            // Basculer vers l'onglet des contrats
                            tabbedPane.setSelectedIndex(4); // Index de l'onglet Contrats
                            
                            // Ouvrir le formulaire de contrat
                            Contrat newContrat = promptAddContrat();
                            if (newContrat != null) {
                                JOptionPane.showMessageDialog(this,
                                    "Contrat créé avec succès !\n" +
                                    "ID Contrat: " + newContrat.getIdContrat(),
                                    "Contrat confirmé",
                                    JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                } else {
                    // Si l'utilisateur n'est pas connecté, afficher un message
                    JOptionPane.showMessageDialog(this,
                        "Vous devez être connecté pour effectuer une réservation.\n" +
                        "Veuillez vous connecter d'abord.",
                        "Connexion requise",
                        JOptionPane.WARNING_MESSAGE);
                }
            });
            buttonPanel.add(reserveButton);
        }

        // Bouton Fermer
        JButton closeButton = createModernButton("Fermer", new Color(100, 116, 139), TEXT_COLOR, new Color(71, 85, 105), "close");
        closeButton.setPreferredSize(new Dimension(120, 40));
        closeButton.addActionListener(e -> detailsDialog.dispose());
        buttonPanel.add(closeButton);

        detailsDialog.add(mainPanel, BorderLayout.CENTER);
        detailsDialog.add(buttonPanel, BorderLayout.SOUTH);

        detailsDialog.setVisible(true);
    }

    /**
     * Crée un élément d'information stylisé
     */
    private JPanel createInfoItem(String label, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setBackground(Color.WHITE);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComponent.setForeground(new Color(71, 85, 105));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueComponent.setForeground(color);

        panel.add(labelComponent, BorderLayout.NORTH);
        panel.add(valueComponent, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crée un badge de caractéristique
     */
    private JLabel createFeatureBadge(String text, Color color) {
        JLabel badge = new JLabel(text);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setForeground(Color.WHITE);
        badge.setBackground(color);
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        return badge;
    }

    /**
     * Récupère le client sélectionné dans la table des clients
     * @return Le client sélectionné ou null si aucun client n'est sélectionné
     */
    @Override
    public Client getSelectedClient() {
        int selectedRow = table2.getSelectedRow();
        if (selectedRow >= 0) {
            // Récupérer l'ID du client sélectionné
            int idClient = Integer.parseInt(table2.getValueAt(selectedRow, 0).toString());

            // Demander au contrôleur de récupérer le client complet
            return controller.getClientById(idClient);
        }
        return null;
    }

    /**
     * Récupère la réservation sélectionnée dans la table des réservations
     * @return La réservation sélectionnée ou null si aucune réservation n'est sélectionnée
     */
    @Override
    public Reservation getSelectedReservation() {
        int selectedRow = table3.getSelectedRow();
        if (selectedRow >= 0) {
            // Récupérer l'ID de la réservation sélectionnée
            int idReservation = Integer.parseInt(table3.getValueAt(selectedRow, 0).toString());

            // Demander au contrôleur de récupérer la réservation complète
            return controller.getReservationById(idReservation);
        }
        return null;
    }

    @Override
    public ImprovedSessionDialog getSessionDialog() {
        if (sessionDialog == null) {
            sessionDialog = new ImprovedSessionDialog(this, controller);
        }
        return sessionDialog;
    }

    @Override
    public void lockInterface() {
        // Garder l'interface visible mais désactiver les fonctionnalités

        // Désactiver les boutons d'action
        buttonAddCar.setEnabled(false);
        buttonModifyCar.setEnabled(false);
        buttonDeleteCar.setEnabled(false);
        buttonAddClient.setEnabled(false);
        buttonModifyClient.setEnabled(false);
        buttonDeleteClient.setEnabled(false);
        buttonAddLocation.setEnabled(false);
        buttonModifyLocation.setEnabled(false);
        buttonDeleteLocation.setEnabled(false);
        buttonAddContrat.setEnabled(false);
        buttonModifyContrat.setEnabled(false);
        buttonDeleteContrat.setEnabled(false);

        // Désactiver les tables pour qu'elles ne soient pas interactives
        table1.setEnabled(false);
        table2.setEnabled(false);
        table3.setEnabled(false);
        table4.setEnabled(false);

        // Changer le texte du bouton de session
        sessionButton.setText("Se connecter");
    }

    @Override
    public void unlockInterface() {
        // Activer les boutons d'action
        buttonAddCar.setEnabled(true);
        buttonModifyCar.setEnabled(true);
        buttonDeleteCar.setEnabled(true);
        buttonAddClient.setEnabled(true);
        buttonModifyClient.setEnabled(true);
        buttonDeleteClient.setEnabled(true);
        buttonAddLocation.setEnabled(true);
        buttonModifyLocation.setEnabled(true);
        buttonDeleteLocation.setEnabled(true);
        buttonAddContrat.setEnabled(true);
        buttonModifyContrat.setEnabled(true);
        buttonDeleteContrat.setEnabled(true);

        // Activer les tables pour qu'elles soient interactives
        table1.setEnabled(true);
        table2.setEnabled(true);
        table3.setEnabled(true);
        table4.setEnabled(true);

        // Changer le texte du bouton de session
        sessionButton.setText("Déconnexion");

        // Rafraîchir l'interface
        revalidate();
        repaint();
    }

    @Override
    public void clearAllTables() {
        try {
            // Créer des modèles vides pour chaque table
            String[] carColumns = {"ID Car", "Marque", "Modèle", "Année", "Prix/jour", "Kilometrages", "Carburant", "Transmission", "Places", "Disponibilité"};
            table1.setModel(new DefaultTableModel(new Object[0][carColumns.length], carColumns));

            String[] clientColumns = {"ID Client", "Nom", "Prénom", "Email","Numéro de permis", "Date de naissance", "Téléphone"};
            table2.setModel(new DefaultTableModel(new Object[0][clientColumns.length], clientColumns));

            String[] locationColumns = {"ID Réservation", "Véhicule", "Immatriculation", "Client", "Nom Complet", "Date Début", "Date Fin", "Responsable", "Prix Total", "Notes"};
            table3.setModel(new DefaultTableModel(new Object[0][locationColumns.length], locationColumns));

            String[] contratColumns = {"ID Contrat", "Type Assurance", "Caution", "Détails"};
            table4.setModel(new DefaultTableModel(new Object[0][contratColumns.length], contratColumns));

            // Vider également la section d'affichage des voitures (panel4)
            if (panel4 != null) {
                panel4.removeAll();
                panel4.revalidate();
                panel4.repaint();
                System.out.println("Panel d'affichage des images de voitures vidé");
            }

            // Vider également la section d'affichage des voitures dans panel5 si elle existe
            if (panel5 != null && panel5.getComponentCount() > 0) {
                Component comp = panel5.getComponent(0);
                if (comp instanceof JPanel) {
                    JPanel carDisplayPanel = (JPanel) comp;
                    carDisplayPanel.removeAll();
                    carDisplayPanel.revalidate();
                    carDisplayPanel.repaint();
                }
            }

            System.out.println("Toutes les tables ont été vidées");
        } catch (Exception e) {
            System.err.println("Erreur lors du vidage des tables : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Met à jour les listeners des menus pour utiliser le controller courant
     * Cette méthode est appelée après l'initialisation du controller
     */
    private void updateMenuListeners() {
        // Vérifier si le controller est disponible
        if (controller == null) return;

        // Récupérer la barre de menu
        JMenuBar menuBar = getJMenuBar();
        if (menuBar == null) return;

        // Parcourir tous les menus et sous-menus pour trouver les éléments d'import/export
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if (menu != null) {
                // Traiter chaque menu de façon récursive
                attachListenersToMenu(menu);
            }
        }

        System.out.println("Listeners des menus mis à jour avec le controller");
    }

    /**
     * Attache les listeners aux éléments de menu d'import/export
     */
    private void attachListenersToMenu(JMenu menu) {
        // Parcourir tous les éléments du menu
        for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem item = menu.getItem(i);

            // Ignorer les éléments null
            if (item == null) continue;

            // Traiter les sous-menus récursivement
            if (item instanceof JMenu) {
                attachListenersToMenu((JMenu) item);
            }
            // Traiter les éléments d'import/export
            else {
                String cmd = item.getActionCommand();
                if (cmd != null && (cmd.startsWith("EXPORT_") || cmd.startsWith("IMPORT_"))) {
                    // Ajouter le controller comme listener
                    item.addActionListener(controller);
                    System.out.println("Listener ajouté pour: " + cmd);
                }
            }
        }
    }

    /**
     * Récupère les valeurs de connexion (email, mot de passe)
     * @return Un tableau contenant l'email et le mot de passe
     */
    @Override
    public String[] getLoginValues() {
        if (sessionDialog != null) {
            String email = sessionDialog.getEmail();
            String password = sessionDialog.getPassword();
            if (email != null && !email.isEmpty() && password != null && !password.isEmpty()) {
                return new String[]{email, password};
            }
        }
        return null;
    }

    /**
     * Récupère les valeurs d'inscription (email, mot de passe, confirmation)
     * @return Un tableau contenant l'email, le mot de passe et la confirmation
     */
    @Override
    public String[] getRegisterValues() {
        if (sessionDialog != null) {
            String email = sessionDialog.getEmail();
            String password = sessionDialog.getPassword();
            String confirmPassword = sessionDialog.getConfirmPassword();
            if (email != null && !email.isEmpty() &&
                    password != null && !password.isEmpty() &&
                    confirmPassword != null && !confirmPassword.isEmpty()) {
                return new String[]{email, password, confirmPassword};
            }
        }
        return null;
    }

    /**
     * Affiche une boîte de dialogue pour sélectionner un fichier
     * @param title Titre de la boîte de dialogue
     * @param extensions Extensions de fichiers acceptées
     * @return Le chemin du fichier sélectionné ou null si annulé
     */
    @Override
    public String promptForFilePath(String title, String... extensions) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        // Créer un filtre pour les extensions spécifiées
        StringBuilder description = new StringBuilder("Fichiers (");
        for (int i = 0; i < extensions.length; i++) {
            description.append("*.").append(extensions[i]);
            if (i < extensions.length - 1) {
                description.append(", ");
            }
        }
        description.append(")");

        javax.swing.filechooser.FileNameExtensionFilter filter =
                new javax.swing.filechooser.FileNameExtensionFilter(description.toString(), extensions);
        fileChooser.addChoosableFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    /**
     * Affiche une boîte de dialogue pour sauvegarder un fichier
     * @param title Titre de la boîte de dialogue
     * @param extension Extension de fichier par défaut
     * @return Le chemin du fichier à sauvegarder ou null si annulé
     */
    @Override
    public String promptForSaveFilePath(String title, String extension) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        // Créer un filtre pour l'extension spécifiée
        String description = "Fichiers (*." + extension + ")";
        javax.swing.filechooser.FileNameExtensionFilter filter =
                new javax.swing.filechooser.FileNameExtensionFilter(description, extension);
        fileChooser.addChoosableFileFilter(filter);

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    /**
     * Change le thème de l'application
     * @param themeName Le nom du thème à appliquer ("light" ou "dark")
     */
    private void changeTheme(String themeName) {
        try {
            if ("dark".equals(themeName)) {
                // Appliquer le thème Dracula (sombre)
                FlatDarculaLaf.setup();
            } else {
                // Appliquer le thème clair (par défaut)
                FlatLightLaf.setup();
                FlatIconColors.values();
            }

            // Mettre à jour tous les composants de l'interface
            SwingUtilities.updateComponentTreeUI(this);

            // Mettre à jour les dialogues s'ils sont ouverts
            if (sessionDialog != null) {
                SwingUtilities.updateComponentTreeUI(sessionDialog);
            }

            // Comme les formulaires ne sont pas des composants Swing directs,
            // nous devons recréer les formulaires si nécessaire lors de leur prochaine utilisation

            // Informer l'utilisateur du changement de thème
            JOptionPane.showMessageDialog(this,
                    "Le thème " + (themeName.equals("dark") ? "Dracula" : "Clair") + " a été appliqué avec succès.",
                    "Changement de thème", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du changement de thème: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Change le format d'affichage des dates dans l'application
     * @param format Le nouveau format à utiliser
     */
    private void changeDateFormat(String format) {
        // Changer le format dans la classe utilitaire
        DateFormatter.setDateFormat(format);

        // Mettre à jour les tables pour refléter le nouveau format
        if (controller != null) {
            controller.updateAllTables();
        }

        // Informer l'utilisateur
        showMessage("Format de date changé en : " + format);
    }

    /**
     * Retourne le texte de recherche
     * @return Le texte de recherche
     */
    public String getSearchQuery() {
        return searchField != null ? searchField.getText() : "";
    }
    
    /**
     * Retourne l'index de l'onglet actif
     * @return L'index de l'onglet actif
     */
    public int getActiveTabIndex() {
        return tabbedPane != null ? tabbedPane.getSelectedIndex() : -1;
    }
    
    /**
     * Affiche les voitures filtrées dans le tableau
     * @param cars Liste des voitures à afficher
     */
    @Override
    public void displayCars(List<Car> cars) {
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        model.setRowCount(0); // Effacer toutes les lignes
        
        for (Car car : cars) {
            model.addRow(new Object[]{
                car.getIdCar(),
                car.getBrand(),
                car.getModel(),
                car.getTransmission(),
                car.getYear(),
                car.getPriceday(),
                car.isAvailable() ? "Disponible" : "Indisponible"
            });
        }
    }
    
    /**
     * Affiche les clients filtrés dans le tableau
     * @param clients Liste des clients à afficher
     */
    @Override
    public void displayClients(List<Client> clients) {
        DefaultTableModel model = (DefaultTableModel) table2.getModel();
        model.setRowCount(0); // Effacer toutes les lignes
        
        for (Client client : clients) {
            model.addRow(new Object[]{
                client.getIdClient(),
                client.getName(),
                client.getSurname(),
                client.getEmail(),
                client.getPhoneNumber(),
                client.getAddress()
            });
        }
    }
    
    /**
     * Affiche les réservations filtrées dans le tableau
     * @param reservations Liste des réservations à afficher
     */
    @Override
    public void displayReservations(List<Reservation> reservations) {
        DefaultTableModel model = (DefaultTableModel) table3.getModel();
        model.setRowCount(0); // Effacer toutes les lignes
        
        for (Reservation reservation : reservations) {
            model.addRow(new Object[]{
                reservation.getIdReservation(),
                DateFormatter.format(reservation.getStartDate()),
                DateFormatter.format(reservation.getEndDate()),
                reservation.getClientId(),
                reservation.getCarId(),
                reservation.getResponsable()
            });
        }
    }
    
    /**
     * Affiche les contrats filtrés dans le tableau
     * @param contrats Liste des contrats à afficher
     */
    @Override
    public void displayContrats(List<Contrat> contrats) {
        DefaultTableModel model = (DefaultTableModel) table4.getModel();
        model.setRowCount(0); // Effacer toutes les lignes
        
        for (Contrat contrat : contrats) {
            model.addRow(new Object[]{
                contrat.getIdContrat(),
                contrat.getReservationId(),
                contrat.getStatutContrat(),
                contrat.getPrixTotal()
            });
        }
    }

    /**
     * Affiche le dialogue d'export/import avancé
     */
    public void showExportImportDialog() {
        if (controller != null) {
            DataAccessLayer model = controller.getModel();
            if (model instanceof DAOLocation) {
                ExportImportDialog dialog = new ExportImportDialog(this, (DAOLocation) model);
                dialog.setVisible(true);
            }
        }
    }
}
