package org.example.view.GUI;

import org.example.controller.Controller;
import org.example.model.entity.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AddClientForm {
    private JFrame parent;
    private Controller controller;
    private JTextField nameField;
    private JTextField surnameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField birthDateField;
    private JTextField licenseField;
    private JDialog dialog;
    private Client result;

    public AddClientForm(JFrame parent, Controller controller) {
        this.parent = parent;
        this.controller = controller;
    }

    public void showForm() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 16, 16));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        Font labelFont = new Font("Segoe UI", Font.BOLD, 15);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 15);

        JLabel nameLabel = new JLabel("Nom :");
        nameLabel.setFont(labelFont);
        nameField = new JTextField();
        nameField.setFont(fieldFont);
        panel.add(nameLabel);
        panel.add(nameField);

        JLabel surnameLabel = new JLabel("Prénom :");
        surnameLabel.setFont(labelFont);
        surnameField = new JTextField();
        surnameField.setFont(fieldFont);
        panel.add(surnameLabel);
        panel.add(surnameField);

        JLabel emailLabel = new JLabel("Email :");
        emailLabel.setFont(labelFont);
        emailField = new JTextField();
        emailField.setFont(fieldFont);
        panel.add(emailLabel);
        panel.add(emailField);

        JLabel licenseNumeber = new JLabel("Numéro de licence :");
        licenseNumeber.setFont(labelFont);
        licenseField = new JTextField();
        licenseField.setFont(fieldFont);
        panel.add(licenseNumeber);
        panel.add(licenseField);


        JLabel birthdateLabel = new JLabel("Date de naissance (AAAA-MM-JJ) :");
        birthdateLabel.setFont(labelFont);
        birthDateField = new JTextField();
        birthDateField.setFont(fieldFont);
        panel.add(birthdateLabel);
        panel.add(birthDateField);

        JLabel phoneLabel = new JLabel("Téléphone :");
        phoneLabel.setFont(labelFont);
        phoneField = new JTextField();
        phoneField.setFont(fieldFont);
        panel.add(phoneLabel);
        panel.add(phoneField);

        JLabel addressLabel = new JLabel("Adresse :");
        addressLabel.setFont(labelFont);
        addressField = new JTextField();
        addressField.setFont(fieldFont);
        panel.add(addressLabel);
        panel.add(addressField);

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

        dialog = new JDialog(parent, "Ajouter un client", true);
        dialog.setContentPane(mainPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
    }
    
    /**
     * Valide les données du formulaire, crée l'objet Client et ferme le dialogue
     */
    private void validateAndSave() {
        result = createClientFromForm();
        if (result != null) {
            // Si le client a été créé avec succès, on ferme le formulaire
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
     * Crée et retourne un objet Client à partir des valeurs saisies dans le formulaire
     * @return un nouvel objet Client ou null si les données sont invalides
     */
    private Client createClientFromForm() {
        try {
            // Vérification que les champs obligatoires sont remplis
            if (nameField.getText().trim().isEmpty() ||
                surnameField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty()) {
                
                JOptionPane.showMessageDialog(parent, 
                    "Veuillez remplir tous les champs obligatoires (nom, prénom, email, mot de passe)", 
                    "Champs incomplets", 
                    JOptionPane.WARNING_MESSAGE);
                return null;
            }
            
            String name = nameField.getText().trim();
            String surname = surnameField.getText().trim();
            String email = emailField.getText().trim();
            String licenseNumber = licenseField.getText().trim();
            String birthDateStr = birthDateField.getText().trim();
            String phoneNumber = phoneField.getText().trim();
            String address = addressField.getText().trim();
            
            // Créer un objet Client avec les données du formulaire
            Client client = new Client();
            client.setName(name);
            client.setSurname(surname);
            client.setEmail(email);
            client.setLicenseNumber(licenseNumber);
            client.setPhoneNumber(phoneNumber);
            client.setAddress(address);
            
            // Conversion de la date de naissance en LocalDate si elle est fournie
            if (!birthDateStr.isEmpty()) {
                try {
                    LocalDate birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                    client.setBirthDate(birthDate);
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(parent, 
                        "Format de date invalide. Utilisez le format AAAA-MM-JJ", 
                        "Erreur de saisie", 
                        JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            }
            
            return client;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, 
                "Une erreur est survenue lors de la création du client: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Retourne le client créé par le formulaire
     * @return l'objet Client créé ou null si aucun client n'a été créé
     */
    public Client getClient() {
        return result;
    }
    
    // Getters pour accéder aux valeurs des champs
    public String getName() {
        return nameField.getText();
    }
    
    public String getSurname() {
        return surnameField.getText();
    }
    
    public String getEmail() {
        return emailField.getText();
    }

    
    public String getBirthDate() {
        return birthDateField.getText();
    }
    
    public String getPhone() {
        return phoneField.getText();
    }
    
    public String getAddress() {
        return addressField.getText();
    }
    
    /**
     * Remplit le formulaire avec les données d'un client existant
     * @param client Le client dont les données doivent être affichées dans le formulaire
     */
    public void fillFormWithClient(Client client) {
        if (client != null) {
            nameField.setText(client.getName());
            surnameField.setText(client.getSurname());
            emailField.setText(client.getEmail());
            licenseField.setText(client.getLicenseNumber());
            
            if (client.getBirthDate() != null) {
                birthDateField.setText(client.getBirthDate().toString());
            }
            
            phoneField.setText(client.getPhoneNumber());
            addressField.setText(client.getAddress());
        }
    }
}
