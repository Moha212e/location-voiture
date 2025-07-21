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

public class ModifyContratForm {
    private JFrame parent;
    private Controller controller;
    private JTextField idContratField;
    private JTextField cautionField;
    private JComboBox<String> typeAssuranceComboBox;
    private JCheckBox estSigneCheckBox;
    private JComboBox<StatutContrat> statutContratComboBox;
    private JList<String> optionsList;
    private DefaultListModel<String> optionsListModel;
    private JTextField newOptionField;
    private JDialog dialog;
    private Contrat result;
    private Contrat originalContrat;
    private JComboBox<String> reservationComboBox;
    private List<Reservation> availableReservations;
    private Reservation selectedReservation;
    private JTextField prixAssuranceField;
    private JTextField prixTotalField;
    private JLabel clientInfoLabel;
    private JLabel carInfoLabel;

    public ModifyContratForm(JFrame parent, Controller controller, Contrat contrat) {
        this.parent = parent;
        this.controller = controller;
        this.originalContrat = contrat;
    }

    public void showForm() {
        // Création du panel principal avec un espacement réduit entre les composants
        JPanel panel = new JPanel(new GridLayout(11, 2, 8, 8));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Champ ID Contrat (en lecture seule car c'est une modification)
        JLabel idContratLabel = new JLabel("ID Contrat :");
        idContratLabel.setFont(labelFont);
        idContratField = new JTextField();
        idContratField.setFont(fieldFont);
        idContratField.setEditable(false); // ID non modifiable
        panel.add(idContratLabel);
        panel.add(idContratField);

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
        panel.add(prixAssuranceLabel);
        panel.add(prixAssuranceField);

        // Champ Prix Total (en lecture seule)
        JLabel prixTotalLabel = new JLabel("Prix Total (€) :");
        prixTotalLabel.setFont(labelFont);
        prixTotalField = new JTextField();
        prixTotalField.setFont(fieldFont);
        prixTotalField.setEditable(false);
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
        optionsScrollPane.setPreferredSize(new Dimension(200, 80));

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

        // Bouton de modification
        JButton modifyButton = new JButton("Modifier");
        modifyButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        modifyButton.setBackground(new Color(241, 196, 15));
        modifyButton.setForeground(Color.WHITE);
        modifyButton.setFocusPainted(false);
        modifyButton.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        modifyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        modifyButton.setOpaque(true);

        // Action du bouton modifier
        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validateAndSave();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.add(modifyButton);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.add(panel, BorderLayout.NORTH);
        mainPanel.add(optionsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Création de la boîte de dialogue
        dialog = new JDialog(parent, "Modifier un contrat", true);
        dialog.setContentPane(mainPanel);
        
        // Définir une taille fixe plus petite au lieu d'utiliser pack()
        dialog.setSize(600, 650);
        dialog.setLocationRelativeTo(parent);
        
        // Remplir le formulaire avec les données du contrat existant
        fillFormWithContrat(originalContrat);
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

        // Si le contrat a déjà une réservation associée, la sélectionner
        if (originalContrat != null && originalContrat.getReservation() != null) {
            selectReservation(originalContrat.getReservation());
        } else if (!reservations.isEmpty()) {
            // Sinon, sélectionner la première réservation si disponible
            reservationComboBox.setSelectedIndex(0);
            selectedReservation = reservations.get(0);
            updatePrixTotal();
            updateClientAndCarInfo();
        }
    }

    /**
     * Valide les données du formulaire, met à jour l'objet Contrat et ferme le dialogue
     */
    private void validateAndSave() {
        result = updateContratFromForm();
        if (result != null) {
            // Appel au contrôleur pour mettre à jour le contrat dans le modèle
            controller.updateContrat(result);
            // Si le contrat a été mis à jour avec succès, on ferme le formulaire
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
     * Met à jour l'objet Contrat existant à partir des valeurs saisies dans le formulaire
     *
     * @return l'objet Contrat mis à jour ou null si les données sont invalides
     */
    private Contrat updateContratFromForm() {
        try {
            // L'ID du contrat n'est pas modifiable
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

            // Mise à jour du contrat existant
            originalContrat.setCaution(caution);
            originalContrat.setTypeAssurance(typeAssurance);
            originalContrat.setEstSigne(estSigne);
            originalContrat.setStatutContrat(statutContrat);

            // Mise à jour des options
            List<String> options = new ArrayList<>();
            for (int i = 0; i < optionsListModel.size(); i++) {
                options.add(optionsListModel.getElementAt(i));
            }
            originalContrat.setOptions(options);

            // Mise à jour de l'association avec la réservation
            if (selectedReservation != null) {
                // Si la réservation a changé
                if (originalContrat.getReservation() == null || 
                    originalContrat.getReservation().getIdReservation() != selectedReservation.getIdReservation()) {
                    
                    // Détacher de l'ancienne réservation si nécessaire
                    if (originalContrat.getReservation() != null) {
                        originalContrat.getReservation().setContrat(null);
                    }
                    
                    // Attacher à la nouvelle réservation
                    selectedReservation.setContrat(originalContrat);
                    originalContrat.setReservation(selectedReservation);
                }
            }

            // Recalculer le prix total
            originalContrat.calculerPrixTotal();

            return originalContrat;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog, "Erreur lors de la modification du contrat: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Retourne le contrat modifié par le formulaire
     *
     * @return l'objet Contrat modifié ou null si aucune modification n'a été effectuée
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
            idContratField.setText(contrat.getIdContrat());
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
                selectedReservation = contrat.getReservation();
                updateClientAndCarInfo();
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
