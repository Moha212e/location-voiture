package org.example.view.GUI;

import org.example.controller.Controller;
import org.example.model.entity.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Formulaire de modification d'un client
 */
public class ModifyClientForm extends JDialog {
    private final Controller controller;
    private final Client originalClient;
    private boolean clientModified = false;

    // Composants du formulaire
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField licenseNumberField;
    private JTextField telephoneField;
    private JTextField emailField;

    /**
     * Constructeur du formulaire de modification d'un client
     * @param parent La fenêtre parente
     * @param controller Le contrôleur
     * @param client Le client à modifier
     */
    public ModifyClientForm(JFrame parent, Controller controller, Client client) {
        super(parent, "Modifier un client", true);
        this.controller = controller;
        this.originalClient = client;
        
        // Configuration de la fenêtre
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        // Initialisation des composants
        initComponents();
        
        // Remplir le formulaire avec les données du client
        fillFormWithClient(client);
    }
    
    /**
     * Initialise les composants du formulaire
     */
    private void initComponents() {
        // Création du panneau principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panneau pour les champs de saisie
        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        
        // Création des champs de saisie
        nomField = new JTextField(20);
        prenomField = new JTextField(20);
        licenseNumberField = new JTextField(20);
        telephoneField = new JTextField(20);
        emailField = new JTextField(20);
        
        // Ajout des champs au panneau
        fieldsPanel.add(new JLabel("Nom :"));
        fieldsPanel.add(nomField);
        fieldsPanel.add(new JLabel("Prénom :"));
        fieldsPanel.add(prenomField);
        fieldsPanel.add(new JLabel("Numéro de licence :"));
        fieldsPanel.add(licenseNumberField);
        fieldsPanel.add(new JLabel("Téléphone :"));
        fieldsPanel.add(telephoneField);
        fieldsPanel.add(new JLabel("Email :"));
        fieldsPanel.add(emailField);
        
        // Panneau pour les boutons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Création des boutons
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");
        
        // Ajout des boutons au panneau
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);
        
        // Ajout des panneaux au panneau principal
        mainPanel.add(fieldsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Ajout du panneau principal à la fenêtre
        setContentPane(mainPanel);
        
        // Gestion des événements
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveClient();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    /**
     * Remplit le formulaire avec les données du client
     * @param client Le client dont on veut afficher les données
     */
    private void fillFormWithClient(Client client) {
        if (client != null) {
            nomField.setText(client.getName());
            prenomField.setText(client.getSurname());
            licenseNumberField.setText(client.getLicenseNumber());
            telephoneField.setText(client.getPhoneNumber());
            emailField.setText(client.getEmail());
        }
    }
    
    /**
     * Enregistre les modifications du client
     */
    private void saveClient() {
        // Vérification des champs obligatoires
        if (nomField.getText().trim().isEmpty() || 
            prenomField.getText().trim().isEmpty() || 
            licenseNumberField.getText().trim().isEmpty() || 
            telephoneField.getText().trim().isEmpty() || 
            emailField.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, 
                "Tous les champs sont obligatoires.", 
                "Erreur de saisie", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Création d'un nouveau client avec les données modifiées
            Client modifiedClient = new Client(
                originalClient.getIdClient(),
                nomField.getText().trim(),
                prenomField.getText().trim(),
                emailField.getText().trim(),
                originalClient.getBirthDate(),
                telephoneField.getText().trim(),
                originalClient.getAddress()
            );
            
            // Définir le numéro de licence
            modifiedClient.setLicenseNumber(licenseNumberField.getText().trim());
            
            // Mise à jour du client dans le modèle
            controller.updateClient(modifiedClient);
            
            // Marquer le client comme modifié
            clientModified = true;
            
            // Fermer la fenêtre
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la modification du client : " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Affiche le formulaire
     * @return true si le client a été modifié, false sinon
     */
    public boolean showForm() {
        setVisible(true);
        return clientModified;
    }
}
