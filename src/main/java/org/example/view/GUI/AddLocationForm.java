package org.example.view.GUI;

import org.example.controller.Controller;
import org.example.model.entity.Car;
import org.example.model.entity.Client;
import org.example.model.entity.Reservation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddLocationForm {
    private JFrame parent;
    private Controller controller;
    private JComboBox<CarItem> carComboBox;
    private JComboBox<ClientItem> clientComboBox;
    private JTextField startDateField;
    private JTextField endDateField;
    private JButton startDateButton;
    private JButton endDateButton;
    private JTextField responsibleField;
    private JTextField priceField;
    private JTextArea notesArea;
    private JDialog dialog;
    private Reservation result;
    private List<Car> availableCars;
    private List<Client> availableClients;
    private Car preSelectedCar; // Voiture pré-sélectionnée

    public AddLocationForm(JFrame parent, Controller controller) {
        this.parent = parent;
        this.controller = controller;
        
        // Récupérer la liste des voitures et des clients disponibles
        if (controller != null) {
            // Utiliser les méthodes disponibles dans JFramesLocation pour récupérer les données
            if (parent instanceof JFramesLocation) {
                JFramesLocation jFramesLocation = (JFramesLocation) parent;
                this.availableCars = jFramesLocation.getCarList();
                this.availableClients = jFramesLocation.getClientList();
            }
        }
    }

    /**
     * Constructeur avec voiture pré-sélectionnée pour la réservation
     */
    public AddLocationForm(JFrame parent, Controller controller, Car preSelectedCar) {
        this(parent, controller);
        this.preSelectedCar = preSelectedCar;
    }

    public void showForm() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 16, 16));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        Font labelFont = new Font("Segoe UI", Font.BOLD, 15);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 15);

        JLabel carLabel = new JLabel("Véhicule :");
        carLabel.setFont(labelFont);
        carComboBox = new JComboBox<>();
        carComboBox.setFont(fieldFont);
        
        // Remplir la liste déroulante des voitures
        if (availableCars != null) {
            // Afficher un message de débogage
            System.out.println("Nombre de voitures disponibles: " + availableCars.size());
            int availableCount = 0;
            for (Car car : availableCars) {
                // Afficher les informations de chaque voiture
                System.out.println("Voiture: " + car.getIdCar() + " - " + car.getBrand() + " " + car.getModel() + " - Disponible: " + car.isAvailable());
                // Ajouter seulement les voitures disponibles
                if (car.isAvailable()) {
                    carComboBox.addItem(new CarItem(car));
                    availableCount++;
                }
            }
            
            // Vérifier s'il y a des voitures disponibles
            if (availableCount == 0) {
                JOptionPane.showMessageDialog(parent, 
                    "Aucune voiture disponible pour la réservation.\n" +
                    "Toutes les voitures sont actuellement louées.", 
                    "Aucune voiture disponible", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        } else {
            System.out.println("Aucune voiture n'a été récupérée");
            JOptionPane.showMessageDialog(parent, 
                "Impossible de récupérer la liste des voitures.", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Pré-sélectionner la voiture si elle est fournie
        if (preSelectedCar != null) {
            for (int i = 0; i < carComboBox.getItemCount(); i++) {
                CarItem item = carComboBox.getItemAt(i);
                if (item.getCar().getIdCar().equals(preSelectedCar.getIdCar())) {
                    carComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        // Ajouter un écouteur pour mettre à jour le prix quand la voiture change
        carComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTotalPrice();
            }
        });
        
        panel.add(carLabel);
        panel.add(carComboBox);

        JLabel clientLabel = new JLabel("Client :");
        clientLabel.setFont(labelFont);
        clientComboBox = new JComboBox<>();
        clientComboBox.setFont(fieldFont);
        
        // Remplir la liste déroulante des clients
        if (availableClients != null) {
            for (Client client : availableClients) {
                clientComboBox.addItem(new ClientItem(client));
            }
            
            // Vérifier s'il y a des clients disponibles
            if (clientComboBox.getItemCount() == 0) {
                JOptionPane.showMessageDialog(parent, 
                    "Aucun client disponible pour la réservation.\n" +
                    "Veuillez d'abord ajouter des clients.", 
                    "Aucun client disponible", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        } else {
            JOptionPane.showMessageDialog(parent, 
                "Impossible de récupérer la liste des clients.", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        panel.add(clientLabel);
        panel.add(clientComboBox);

        JLabel startDateLabel = new JLabel("Date début (AAAA-MM-JJ) :");
        startDateLabel.setFont(labelFont);
        startDateField = new JTextField();
        startDateField.setFont(fieldFont);
        startDateField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateTotalPrice();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateTotalPrice();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateTotalPrice();
            }
        });
        
        startDateButton = new JButton("...");
        startDateButton.setFont(fieldFont);
        startDateButton.setPreferredSize(new Dimension(30, 25));
        startDateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDatePicker(startDateField);
            }
        });
        
        JPanel startDatePanel = new JPanel(new BorderLayout(5, 0));
        startDatePanel.setBackground(new Color(245, 247, 250));
        startDatePanel.add(startDateField, BorderLayout.CENTER);
        startDatePanel.add(startDateButton, BorderLayout.EAST);
        
        panel.add(startDateLabel);
        panel.add(startDatePanel);

        JLabel endDateLabel = new JLabel("Date fin (AAAA-MM-JJ) :");
        endDateLabel.setFont(labelFont);
        endDateField = new JTextField();
        endDateField.setFont(fieldFont);
        endDateField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateTotalPrice();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateTotalPrice();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateTotalPrice();
            }
        });
        
        endDateButton = new JButton("...");
        endDateButton.setFont(fieldFont);
        endDateButton.setPreferredSize(new Dimension(30, 25));
        endDateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDatePicker(endDateField);
            }
        });
        
        JPanel endDatePanel = new JPanel(new BorderLayout(5, 0));
        endDatePanel.setBackground(new Color(245, 247, 250));
        endDatePanel.add(endDateField, BorderLayout.CENTER);
        endDatePanel.add(endDateButton, BorderLayout.EAST);
        
        panel.add(endDateLabel);
        panel.add(endDatePanel);

        JLabel responsibleLabel = new JLabel("Responsable :");
        responsibleLabel.setFont(labelFont);
        responsibleField = new JTextField();
        responsibleField.setFont(fieldFont);
        panel.add(responsibleLabel);
        panel.add(responsibleField);

        JLabel priceLabel = new JLabel("Prix total :");
        priceLabel.setFont(labelFont);
        priceField = new JTextField();
        priceField.setFont(fieldFont);
        panel.add(priceLabel);
        panel.add(priceField);

        JLabel notesLabel = new JLabel("Notes :");
        notesLabel.setFont(labelFont);
        notesArea = new JTextArea(3, 20);
        notesArea.setFont(fieldFont);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        panel.add(notesLabel);
        panel.add(notesScroll);

        JButton addButton = new JButton("Ajouter");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.setOpaque(true);
        
        // Modification ici : le bouton ajouter valide le formulaire et le ferme
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validateAndSave();
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.add(addButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog = new JDialog(parent, "Ajouter une location", true);
        dialog.setContentPane(mainPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
    }
    
    /**
     * Valide les données du formulaire, crée l'objet Reservation et ferme le dialogue
     */
    private void validateAndSave() {
        result = createReservationFromForm();
        if (result != null) {
            try {
                // Sauvegarder la réservation via le controller
                if (controller != null) {
                    controller.addReservation(result);
                    
                    // Mettre à jour le statut de la voiture (disponible -> indisponible)
                    Car selectedCar = ((CarItem)carComboBox.getSelectedItem()).getCar();
                    selectedCar.setAvailable(false);
                    controller.updateCar(selectedCar);
                    
                    // Mettre à jour toutes les tables
                    controller.updateAllTables();
                    
                    JOptionPane.showMessageDialog(parent, 
                        "Réservation créée avec succès !\n" +
                        "Véhicule: " + selectedCar.getBrand() + " " + selectedCar.getModel() + "\n" +
                        "Prix total: " + result.getPrice() + " €", 
                        "Réservation confirmée", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
                
                // Si la réservation a été créée avec succès, on ferme le formulaire
                dialog.dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(parent, 
                    "Erreur lors de la sauvegarde de la réservation: " + e.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Affiche le formulaire et attend que l'utilisateur le ferme
     */
    public void setVisible(boolean visible) {
        if (visible) {
            dialog.setVisible(true);
        } else {
            dialog.setVisible(false);
        }
    }

    /**
     * Ferme le formulaire
     */
    public void dispose() {
        if (dialog != null) {
            dialog.dispose();
        }
    }

    /**
     * Crée et retourne un objet Reservation à partir des valeurs saisies dans le formulaire
     * @return un nouvel objet Reservation ou null si les données sont invalides
     */
    private Reservation createReservationFromForm() {
        try {
            // Vérification que les champs obligatoires sont remplis
            if (carComboBox.getSelectedItem() == null ||
                clientComboBox.getSelectedItem() == null ||
                startDateField.getText().isEmpty() ||
                endDateField.getText().isEmpty()) {
                
                JOptionPane.showMessageDialog(parent, 
                    "Veuillez remplir tous les champs obligatoires (véhicule, client, date début, date fin)", 
                    "Champs incomplets", 
                    JOptionPane.WARNING_MESSAGE);
                return null;
            }
            
            // Récupérer les objets Car et Client sélectionnés
            Car selectedCar = ((CarItem)carComboBox.getSelectedItem()).getCar();
            Client selectedClient = ((ClientItem)clientComboBox.getSelectedItem()).getClient();
            
            // Conversion des dates
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(startDateField.getText(), dateFormatter);
            LocalDate endDate = LocalDate.parse(endDateField.getText(), dateFormatter);
            
            // Vérifier que la date de fin est après la date de début
            if (endDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(parent, 
                    "La date de fin doit être après la date de début", 
                    "Erreur de saisie", 
                    JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            String responsable = responsibleField.getText().trim();
            
            float price = 0;
            if (!priceField.getText().trim().isEmpty()) {
                try {
                    // Remplacer la virgule par un point pour assurer la compatibilité
                    String priceText = priceField.getText().trim().replace(',', '.');
                    price = Float.parseFloat(priceText);
                    
                    // Vérifier que le prix est positif
                    if (price <= 0) {
                        JOptionPane.showMessageDialog(parent, 
                            "Le prix doit être positif", 
                            "Erreur de saisie", 
                            JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(parent, 
                        "Le prix doit être un nombre valide", 
                        "Erreur de saisie", 
                        JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            } else {
                // Calculer automatiquement le prix en fonction du prix journalier de la voiture
                // et du nombre de jours de location
                long days = endDate.toEpochDay() - startDate.toEpochDay() + 1;
                price = selectedCar.getPriceday() * days;
                priceField.setText(String.valueOf(price)); // Mettre à jour le champ de prix
            }
            
            String notes = notesArea.getText();
            
            // Créer un objet Reservation avec les données du formulaire
            Reservation reservation = new Reservation();
            reservation.setCar(selectedCar);
            reservation.setClient(selectedClient);
            reservation.setStartDate(startDate);
            reservation.setEndDate(endDate);
            reservation.setResponsable(responsable);
            reservation.setPrice(price);
            reservation.setNotes(notes);
            
            return reservation;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, 
                "Une erreur est survenue lors de la création de la réservation: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Affiche un sélecteur de date pour la saisie de la date
     * @param textField Le champ de texte où la date sera affichée
     */
    private void showDatePicker(JTextField textField) {
        // Créer un calendrier avec la date actuelle ou la date du champ si elle existe
        Calendar calendar = Calendar.getInstance();
        try {
            if (!textField.getText().isEmpty()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = dateFormat.parse(textField.getText());
                calendar.setTime(date);
            }
        } catch (ParseException e) {
            // Ignorer et utiliser la date actuelle
        }

        // Créer un dialogue personnalisé pour le calendrier
        JDialog dateDialog = new JDialog(dialog, "Sélectionner une date", true);
        dateDialog.setLayout(new BorderLayout());
        dateDialog.setResizable(false);

        // Panneau pour l'année et le mois
        JPanel headerPanel = new JPanel(new GridLayout(1, 3));
        
        // Année
        JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(calendar.get(Calendar.YEAR), 2000, 2100, 1));
        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "####"));
        
        // Mois
        String[] months = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};
        JComboBox<String> monthCombo = new JComboBox<>(months);
        monthCombo.setSelectedIndex(calendar.get(Calendar.MONTH));
        
        headerPanel.add(new JLabel("Année:", JLabel.CENTER));
        headerPanel.add(yearSpinner);
        headerPanel.add(monthCombo);
        
        // Panneau pour les jours de la semaine
        JPanel weekdaysPanel = new JPanel(new GridLayout(1, 7));
        String[] weekdays = {"Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam"};
        for (String weekday : weekdays) {
            JLabel label = new JLabel(weekday, JLabel.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            weekdaysPanel.add(label);
        }
        
        // Panneau pour les jours du mois
        JPanel daysPanel = new JPanel(new GridLayout(6, 7));
        JButton[][] dayButtons = new JButton[6][7];
        
        // Fonction pour mettre à jour le calendrier
        ActionListener updateCalendar = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Mettre à jour le calendrier avec l'année et le mois sélectionnés
                int year = (int) yearSpinner.getValue();
                int month = monthCombo.getSelectedIndex();
                calendar.set(year, month, 1);
                
                // Effacer tous les boutons
                daysPanel.removeAll();
                
                // Déterminer le premier jour du mois
                int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                
                // Créer les boutons pour chaque jour
                for (int i = 0; i < 42; i++) {
                    int row = i / 7;
                    int col = i % 7;
                    
                    if (i >= firstDayOfWeek && i < firstDayOfWeek + daysInMonth) {
                        final int day = i - firstDayOfWeek + 1;
                        JButton dayButton = new JButton(String.valueOf(day));
                        dayButton.setFocusPainted(false);
                        
                        // Sélectionner la date actuelle
                        if (calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR) &&
                            calendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH) &&
                            day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                            dayButton.setBackground(new Color(200, 220, 250));
                        }
                        
                        dayButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // Mettre à jour le champ de texte avec la date sélectionnée
                                calendar.set(Calendar.DAY_OF_MONTH, day);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                textField.setText(dateFormat.format(calendar.getTime()));
                                dateDialog.dispose();
                                
                                // Mettre à jour le prix total
                                updateTotalPrice();
                            }
                        });
                        
                        daysPanel.add(dayButton);
                    } else {
                        daysPanel.add(new JLabel(""));
                    }
                }
                
                daysPanel.revalidate();
                daysPanel.repaint();
            }
        };
        
        // Ajouter des écouteurs pour mettre à jour le calendrier quand l'année ou le mois change
        yearSpinner.addChangeListener(e -> updateCalendar.actionPerformed(null));
        monthCombo.addActionListener(updateCalendar);
        
        // Initialiser le calendrier
        updateCalendar.actionPerformed(null);
        
        // Ajouter les panneaux au dialogue
        JPanel calendarPanel = new JPanel(new BorderLayout());
        calendarPanel.add(headerPanel, BorderLayout.NORTH);
        calendarPanel.add(weekdaysPanel, BorderLayout.CENTER);
        calendarPanel.add(daysPanel, BorderLayout.SOUTH);
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        dateDialog.add(calendarPanel, BorderLayout.CENTER);
        dateDialog.pack();
        dateDialog.setLocationRelativeTo(dialog);
        dateDialog.setVisible(true);
    }
    
    /**
     * Met à jour le prix total en fonction des dates de début et de fin
     */
    private void updateTotalPrice() {
        try {
            // Vérifier que tous les champs nécessaires sont remplis
            if (carComboBox.getSelectedItem() == null || 
                startDateField.getText().isEmpty() || 
                endDateField.getText().isEmpty()) {
                return; // Ne rien faire si les données nécessaires ne sont pas disponibles
            }
            
            // Récupérer les dates de début et de fin
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(startDateField.getText(), dateFormatter);
            LocalDate endDate = LocalDate.parse(endDateField.getText(), dateFormatter);
            
            // Vérifier que la date de fin est après la date de début
            if (endDate.isBefore(startDate)) {
                return; // Ne pas mettre à jour le prix si les dates sont invalides
            }
            
            // Récupérer l'objet Car sélectionné
            Car selectedCar = ((CarItem)carComboBox.getSelectedItem()).getCar();
            
            // Calculer le prix total en fonction du prix journalier de la voiture
            // et du nombre de jours de location
            long days = endDate.toEpochDay() - startDate.toEpochDay() + 1;
            float price = selectedCar.getPriceday() * days;
            
            // Mettre à jour le champ de prix avec deux décimales
            priceField.setText(String.format("%.2f", price));
            
            // Afficher un message de débogage dans la console
            System.out.println("Prix total calculé: " + String.format("%.2f", price) + "€ pour " + days + " jour(s) à " + selectedCar.getPriceday() + "€/jour");
        } catch (Exception e) {
            System.out.println("Erreur lors du calcul du prix: " + e.getMessage());
        }
    }
    
    /**
     * Retourne la réservation créée par le formulaire
     * @return l'objet Reservation créé ou null si aucune réservation n'a été créée
     */
    public Reservation getReservation() {
        return result;
    }
    
    /**
     * Remplit le formulaire avec les données d'une réservation existante
     * @param reservation La réservation dont les données doivent être affichées dans le formulaire
     */
    public void fillFormWithReservation(Reservation reservation) {
        if (reservation != null) {
            // Sélectionner la voiture dans la liste déroulante
            if (reservation.getCar() != null) {
                for (int i = 0; i < carComboBox.getItemCount(); i++) {
                    CarItem item = carComboBox.getItemAt(i);
                    if (item.getCar().getIdCar().equals(reservation.getCar().getIdCar())) {
                        carComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            // Sélectionner le client dans la liste déroulante
            if (reservation.getClient() != null) {
                for (int i = 0; i < clientComboBox.getItemCount(); i++) {
                    ClientItem item = clientComboBox.getItemAt(i);
                    if (item.getClient().getIdClient() == reservation.getClient().getIdClient()) {
                        clientComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            // Formatter les dates pour l'affichage
            if (reservation.getStartDate() != null) {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                startDateField.setText(dateFormatter.format(reservation.getStartDate()));
            }
            if (reservation.getEndDate() != null) {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                endDateField.setText(dateFormatter.format(reservation.getEndDate()));
            }
            
            responsibleField.setText(reservation.getResponsable());
            priceField.setText(String.valueOf(reservation.getPrice()));
            notesArea.setText(reservation.getNotes());
        }
    }
    
    /**
     * Classe interne pour représenter une voiture dans la liste déroulante
     */
    private class CarItem {
        private Car car;
        
        public CarItem(Car car) {
            this.car = car;
        }
        
        public Car getCar() {
            return car;
        }
        
        @Override
        public String toString() {
            return car.getIdCar() + " - " + car.getBrand() + " " + car.getModel() + " (" + car.getYear() + ") - " + car.getPriceday() + "€/jour";
        }
    }
    
    /**
     * Classe interne pour représenter un client dans la liste déroulante
     */
    private class ClientItem {
        private Client client;
        
        public ClientItem(Client client) {
            this.client = client;
        }
        
        public Client getClient() {
            return client;
        }
        
        @Override
        public String toString() {
            return client.getIdClient() + " - " + client.getName() + " " + client.getSurname();
        }
    }
}
