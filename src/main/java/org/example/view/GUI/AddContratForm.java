package org.example.view.GUI;

import org.example.controller.Controller;
import org.example.model.entity.Contrat;
import org.example.model.entity.Reservation;
import org.example.model.entity.StatutContrat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AddContratForm {
    private JFrame parent;
    private Controller controller;
    private JTextField cautionField;
    private JComboBox<String> typeAssuranceComboBox;
    private JCheckBox estSigneCheckBox;
    private JComboBox<StatutContrat> statutContratComboBox;
    private JList<String> optionsList;
    private DefaultListModel<String> optionsListModel;
    private JTextField newOptionField;
    private JDialog dialog;
    private Contrat result;
    private JComboBox<String> reservationComboBox;
    private List<Reservation> availableReservations;
    private Reservation selectedReservation;
    private JTextField prixAssuranceField;
    private JTextField prixTotalField;
    private JLabel clientInfoLabel;
    private JLabel carInfoLabel;

    public AddContratForm(JFrame parent, Controller controller) {
        this.parent = parent;
        this.controller = controller;
    }

    public void showForm() {
        // Création du panel principal avec un espacement entre les composants
        JPanel panel = new JPanel(new GridLayout(10, 2, 16, 16));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        Font labelFont = new Font("Segoe UI", Font.BOLD, 15);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 15);

        // Champ Réservation
        JLabel reservationLabel = new JLabel("Réservation :");
        reservationLabel.setFont(labelFont);
        reservationComboBox = new JComboBox<>();
        reservationComboBox.setFont(fieldFont);
        panel.add(reservationLabel);
        panel.add(reservationComboBox);

        // Informations du client
        JLabel clientLabel = new JLabel("Client :");
        clientLabel.setFont(labelFont);
        clientInfoLabel = new JLabel("Aucun client sélectionné");
        clientInfoLabel.setFont(fieldFont);
        panel.add(clientLabel);
        panel.add(clientInfoLabel);

        // Informations de la voiture
        JLabel carLabel = new JLabel("Véhicule :");
        carLabel.setFont(labelFont);
        carInfoLabel = new JLabel("Aucun véhicule sélectionné");
        carInfoLabel.setFont(fieldFont);
        panel.add(carLabel);
        panel.add(carInfoLabel);

        // Champ Caution
        JLabel cautionLabel = new JLabel("Caution (€) :");
        cautionLabel.setFont(labelFont);
        cautionField = new JTextField();
        cautionField.setFont(fieldFont);
        panel.add(cautionLabel);
        panel.add(cautionField);

        // ComboBox Type Assurance
        JLabel typeAssuranceLabel = new JLabel("Type d'assurance :");
        typeAssuranceLabel.setFont(labelFont);
        String[] typeAssuranceOptions = {"Tous risques", "Tiers", "Tiers étendu", "Autre"};
        typeAssuranceComboBox = new JComboBox<>(typeAssuranceOptions);
        typeAssuranceComboBox.setFont(fieldFont);
        panel.add(typeAssuranceLabel);
        panel.add(typeAssuranceComboBox);

        // Champ Prix Assurance (en lecture seule)
        JLabel prixAssuranceLabel = new JLabel("Prix Assurance (€) :");
        prixAssuranceLabel.setFont(labelFont);
        prixAssuranceField = new JTextField();
        prixAssuranceField.setFont(fieldFont);
        prixAssuranceField.setEditable(false);
        prixAssuranceField.setText("150.00"); // Valeur par défaut pour "Tous risques"
        panel.add(prixAssuranceLabel);
        panel.add(prixAssuranceField);

        // Champ Prix Total (en lecture seule)
        JLabel prixTotalLabel = new JLabel("Prix Total (€) :");
        prixTotalLabel.setFont(labelFont);
        prixTotalField = new JTextField();
        prixTotalField.setFont(fieldFont);
        prixTotalField.setEditable(false);
        prixTotalField.setText("0.00");
        panel.add(prixTotalLabel);
        panel.add(prixTotalField);

        // CheckBox Est Signé
        JLabel estSigneLabel = new JLabel("Contrat signé :");
        estSigneLabel.setFont(labelFont);
        estSigneCheckBox = new JCheckBox();
        estSigneCheckBox.setBackground(new Color(245, 247, 250));
        panel.add(estSigneLabel);
        panel.add(estSigneCheckBox);

        // ComboBox Statut Contrat
        JLabel statutContratLabel = new JLabel("Statut du contrat :");
        statutContratLabel.setFont(labelFont);
        statutContratComboBox = new JComboBox<>(StatutContrat.values());
        statutContratComboBox.setFont(fieldFont);
        statutContratComboBox.setSelectedItem(StatutContrat.EN_ATTENTE);
        panel.add(statutContratLabel);
        panel.add(statutContratComboBox);

        // Synchronisation entre estSigne et statutContrat
        estSigneCheckBox.addActionListener(e -> {
            if (estSigneCheckBox.isSelected()) {
                statutContratComboBox.setSelectedItem(StatutContrat.SIGNE);
            } else if (statutContratComboBox.getSelectedItem() == StatutContrat.SIGNE) {
                statutContratComboBox.setSelectedItem(StatutContrat.EN_ATTENTE);
            }
        });

        statutContratComboBox.addActionListener(e -> {
            StatutContrat selectedStatus = (StatutContrat) statutContratComboBox.getSelectedItem();
            if (selectedStatus == StatutContrat.SIGNE) {
                estSigneCheckBox.setSelected(true);
            } else if (selectedStatus == StatutContrat.ANNULE || selectedStatus == StatutContrat.EXPIRE) {
                estSigneCheckBox.setSelected(false);
            }
        });

        // Gestionnaire d'événements pour la sélection de réservation
        reservationComboBox.addActionListener(e -> {
            int selectedIndex = reservationComboBox.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < availableReservations.size()) {
                selectedReservation = availableReservations.get(selectedIndex);
                updatePrixTotal();
                updateClientAndCarInfo();
            }
        });

        // Gestionnaire d'événements pour le type d'assurance
        typeAssuranceComboBox.addActionListener(e -> {
            String typeAssurance = (String) typeAssuranceComboBox.getSelectedItem();
            double prixAssurance = calculerPrixAssurance(typeAssurance);
            prixAssuranceField.setText(String.format("%.2f", prixAssurance));
            updatePrixTotal();
        });

        // Gestionnaire d'événements pour la caution
        cautionField.addActionListener(e -> updatePrixTotal());
        cautionField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                updatePrixTotal();
            }
        });

        // Panel pour les options
        JPanel optionsPanel = new JPanel(new BorderLayout(10, 10));
        optionsPanel.setBackground(new Color(245, 247, 250));
        JLabel optionsLabel = new JLabel("Options :");
        optionsLabel.setFont(labelFont);

        // Liste des options
        optionsListModel = new DefaultListModel<>();
        optionsList = new JList<>(optionsListModel);
        optionsList.setFont(fieldFont);
        JScrollPane optionsScrollPane = new JScrollPane(optionsList);
        optionsScrollPane.setPreferredSize(new Dimension(200, 100));

        // Panel pour ajouter/supprimer des options
        JPanel optionsButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        optionsButtonPanel.setBackground(new Color(245, 247, 250));
        newOptionField = new JTextField(15);
        newOptionField.setFont(fieldFont);
        JButton addOptionButton = new JButton("+");
        addOptionButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        addOptionButton.setBackground(new Color(46, 204, 113));
        addOptionButton.setForeground(Color.WHITE);
        addOptionButton.setFocusPainted(false);
        addOptionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addOptionButton.setOpaque(true);

        JButton removeOptionButton = new JButton("-");
        removeOptionButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        removeOptionButton.setBackground(new Color(231, 76, 60));
        removeOptionButton.setForeground(Color.WHITE);
        removeOptionButton.setFocusPainted(false);
        removeOptionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeOptionButton.setOpaque(true);

        optionsButtonPanel.add(newOptionField);
        optionsButtonPanel.add(addOptionButton);
        optionsButtonPanel.add(removeOptionButton);

        // Ajouter une option
        addOptionButton.addActionListener(e -> {
            String newOption = newOptionField.getText().trim();
            if (!newOption.isEmpty() && !optionsListModel.contains(newOption)) {
                optionsListModel.addElement(newOption);
                newOptionField.setText("");
            }
        });

        // Supprimer une option
        removeOptionButton.addActionListener(e -> {
            int selectedIndex = optionsList.getSelectedIndex();
            if (selectedIndex != -1) {
                optionsListModel.remove(selectedIndex);
            }
        });

        optionsPanel.add(optionsLabel, BorderLayout.NORTH);
        optionsPanel.add(optionsScrollPane, BorderLayout.CENTER);
        optionsPanel.add(optionsButtonPanel, BorderLayout.SOUTH);

        // Bouton d'ajout
        JButton addButton = new JButton("Ajouter");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.setOpaque(true);

        // Action du bouton ajouter
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validateAndSave();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.add(addButton);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.add(panel, BorderLayout.NORTH);
        mainPanel.add(optionsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Création de la boîte de dialogue
        dialog = new JDialog(parent, "Ajouter un contrat", true);
        dialog.setContentPane(mainPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
    }

    /**
     * Calcule le prix de l'assurance en fonction du type d'assurance.
     *
     * @param typeAssurance Le type d'assurance
     * @return Le prix de l'assurance
     */
    private double calculerPrixAssurance(String typeAssurance) {
        if (typeAssurance == null) {
            return 0.0;
        }

        switch (typeAssurance) {
            case "Tous risques":
                return 150.0;
            case "Tiers étendu":
                return 100.0;
            case "Tiers":
                return 50.0;
            default:
                return 75.0; // Prix par défaut pour "Autre"
        }
    }

    /**
     * Met à jour le champ de prix total en fonction des valeurs actuelles.
     */
    private void updatePrixTotal() {
        try {
            double prixAssurance = Double.parseDouble(prixAssuranceField.getText().replace(',', '.'));
            double caution;
            try {
                caution = Double.parseDouble(cautionField.getText().trim().replace(',', '.'));
                if (caution < 0) {
                    caution = 0.0;
                }
            } catch (NumberFormatException | NullPointerException e) {
                // Ignorer l'erreur, utiliser 0 comme valeur par défaut
                caution = 0.0;
            }

            double prixReservation = 0.0;
            if (selectedReservation != null) {
                prixReservation = selectedReservation.getPrice();
            }

            double prixTotal = prixAssurance + caution + prixReservation;
            prixTotalField.setText(String.format("%.2f", prixTotal));
        } catch (NumberFormatException | NullPointerException e) {
            prixTotalField.setText("0.00");
        }
    }

    /**
     * Charge les réservations disponibles dans le ComboBox
     *
     * @param reservations Liste des réservations disponibles
     */
    public void loadReservations(List<Reservation> reservations) {
        this.availableReservations = reservations;
        reservationComboBox.removeAllItems();

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Reservation reservation : reservations) {
            // Afficher les informations pertinentes de la réservation dans le ComboBox
            String clientName = reservation.getClient() != null ?
                    reservation.getClient().getName() + " " + reservation.getClient().getSurname() : "Client inconnu";
            String carModel = reservation.getCar() != null ?
                    reservation.getCar().getBrand() + " " + reservation.getCar().getModel() : "Véhicule inconnu";
            String dates = "";
            if (reservation.getStartDate() != null && reservation.getEndDate() != null) {
                dates = reservation.getStartDate().format(dateFormat) + " - " + reservation.getEndDate().format(dateFormat);
            }

            String displayText = "#" + reservation.getIdReservation() + ": " + clientName + " - " + carModel + " (" + dates + ") - " + reservation.getPrice() + "€";
            reservationComboBox.addItem(displayText);
        }

        // Sélectionner la première réservation si disponible
        if (!reservations.isEmpty()) {
            reservationComboBox.setSelectedIndex(0);
            selectedReservation = reservations.get(0);
            updatePrixTotal();
            updateClientAndCarInfo();
        }
    }

    /**
     * Valide les données du formulaire, crée l'objet Contrat et ferme le dialogue
     */
    private void validateAndSave() {
        result = createContratFromForm();
        if (result != null) {
            // Si le contrat a été créé avec succès, on ferme le formulaire
            dialog.dispose();
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
     * Crée et retourne un objet Contrat à partir des valeurs saisies dans le formulaire
     *
     * @return un nouvel objet Contrat ou null si les données sont invalides
     */
    private Contrat createContratFromForm() {
        try {
            double caution;
            try {
                caution = Double.parseDouble(cautionField.getText().trim().replace(',', '.'));
                if (caution < 0) {
                    throw new NumberFormatException("La caution doit être positive");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(dialog, "La caution doit être un nombre positif", "Erreur de validation", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            String typeAssurance = (String) typeAssuranceComboBox.getSelectedItem();
            boolean estSigne = estSigneCheckBox.isSelected();
            StatutContrat statutContrat = (StatutContrat) statutContratComboBox.getSelectedItem();

            // Création du contrat
            Contrat contrat = new Contrat(caution, typeAssurance, estSigne, statutContrat);

            // Ajout des options
            List<String> options = new ArrayList<>();
            for (int i = 0; i < optionsListModel.size(); i++) {
                options.add(optionsListModel.getElementAt(i));
            }
            contrat.setOptions(options);

            // Association du contrat à la réservation sélectionnée
            if (selectedReservation != null) {
                selectedReservation.setContrat(contrat);
                contrat.setReservation(selectedReservation);
            }

            // Mise à jour du prix total
            contrat.calculerPrixTotal();

            return contrat;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog, "Erreur lors de la création du contrat: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Retourne le contrat créé par le formulaire
     *
     * @return l'objet Contrat créé ou null si aucun contrat n'a été créé
     */
    public Contrat getContrat() {
        return result;
    }

    /**
     * Retourne la réservation sélectionnée
     *
     * @return l'objet Reservation sélectionné ou null si aucune réservation n'a été sélectionnée
     */
    public Reservation getSelectedReservation() {
        return selectedReservation;
    }

    /**
     * Remplit le formulaire avec les données d'un contrat existant
     *
     * @param contrat Le contrat dont les données doivent être affichées dans le formulaire
     */
    public void fillFormWithContrat(Contrat contrat) {
        if (contrat != null) {
            cautionField.setText(String.valueOf(contrat.getCaution()));
            typeAssuranceComboBox.setSelectedItem(contrat.getTypeAssurance());
            estSigneCheckBox.setSelected(contrat.isEstSigne());
            statutContratComboBox.setSelectedItem(contrat.getStatutContrat());
            prixAssuranceField.setText(String.format("%.2f", contrat.getPrixAssurance()));
            prixTotalField.setText(String.format("%.2f", contrat.getPrixTotal()));

            // Remplir la liste des options
            optionsListModel.clear();
            for (String option : contrat.getOptions()) {
                optionsListModel.addElement(option);
            }

            // Sélectionner la réservation associée si elle existe
            if (contrat.getReservation() != null) {
                selectReservation(contrat.getReservation());
            }
        }
    }

    /**
     * Sélectionne une réservation spécifique dans le ComboBox
     *
     * @param reservation La réservation à sélectionner
     */
    public void selectReservation(Reservation reservation) {
        if (reservation != null && availableReservations != null) {
            for (int i = 0; i < availableReservations.size(); i++) {
                if (availableReservations.get(i).getIdReservation() == reservation.getIdReservation()) {
                    reservationComboBox.setSelectedIndex(i);
                    selectedReservation = reservation;
                    updatePrixTotal();
                    updateClientAndCarInfo();
                    break;
                }
            }
        }
    }

    private void updateClientAndCarInfo() {
        if (selectedReservation != null) {
            String clientName = selectedReservation.getClient() != null ?
                    selectedReservation.getClient().getName() + " " + selectedReservation.getClient().getSurname() : "Client inconnu";
            String carModel = selectedReservation.getCar() != null ?
                    selectedReservation.getCar().getBrand() + " " + selectedReservation.getCar().getModel() : "Véhicule inconnu";

            clientInfoLabel.setText(clientName);
            carInfoLabel.setText(carModel);
        } else {
            clientInfoLabel.setText("Aucun client sélectionné");
            carInfoLabel.setText("Aucun véhicule sélectionné");
        }
    }
}
