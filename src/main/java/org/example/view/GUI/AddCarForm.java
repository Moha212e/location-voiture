package org.example.view.GUI;

import org.example.controller.Controller;
import org.example.model.entity.Car;
import org.example.utils.LicensePlateGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class AddCarForm {
    private JFrame parent;
    private Controller controller;
    private JTextField idCarField;
    private JTextField brandField;
    private JTextField modelField;
    private JTextField yearField;
    private JTextField pricedayField;
    private JTextField mileageField;
    private JComboBox<String> fuelTypeComboBox;
    private JComboBox<String> transmissionComboBox;
    private JTextField seatsField;
    private JCheckBox availableCheckBox;
    private JTextField imageField;
    private JDialog dialog;
    private Car result;

    public AddCarForm(JFrame parent, Controller controller) {
        this.parent = parent;
        this.controller = controller;
    }

    public void showForm() {
        JPanel panel = new JPanel(new GridLayout(11, 2, 16, 16));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        Font labelFont = new Font("Segoe UI", Font.BOLD, 15);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 15);

        JLabel idCarLabel = new JLabel("Immatriculation :");
        idCarLabel.setFont(labelFont);
        idCarField = new JTextField();
        idCarField.setFont(fieldFont);
        idCarField.setText(LicensePlateGenerator.generateBelgianLicensePlate());
        idCarField.setEditable(false);
        idCarField.setBackground(new Color(240, 240, 240));
        panel.add(idCarLabel);
        panel.add(idCarField);

        JLabel brandLabel = new JLabel("Marque :");
        brandLabel.setFont(labelFont);
        brandField = new JTextField();
        brandField.setFont(fieldFont);
        panel.add(brandLabel);
        panel.add(brandField);

        JLabel modelLabel = new JLabel("Modèle :");
        modelLabel.setFont(labelFont);
        modelField = new JTextField();
        modelField.setFont(fieldFont);
        panel.add(modelLabel);
        panel.add(modelField);

        JLabel yearLabel = new JLabel("Année :");
        yearLabel.setFont(labelFont);
        yearField = new JTextField();
        yearField.setFont(fieldFont);
        panel.add(yearLabel);
        panel.add(yearField);

        JLabel pricedayLabel = new JLabel("Prix par jour (€) :");
        pricedayLabel.setFont(labelFont);
        pricedayField = new JTextField();
        pricedayField.setFont(fieldFont);
        panel.add(pricedayLabel);
        panel.add(pricedayField);

        JLabel mileageLabel = new JLabel("Kilométrage :");
        mileageLabel.setFont(labelFont);
        mileageField = new JTextField();
        mileageField.setFont(fieldFont);
        panel.add(mileageLabel);
        panel.add(mileageField);

        JLabel fuelTypeLabel = new JLabel("Type de carburant :");
        fuelTypeLabel.setFont(labelFont);
        String[] fuelTypes = {"Essence", "Diesel", "Électrique", "Hybride"};
        fuelTypeComboBox = new JComboBox<>(fuelTypes);
        fuelTypeComboBox.setFont(fieldFont);
        panel.add(fuelTypeLabel);
        panel.add(fuelTypeComboBox);

        JLabel transmissionLabel = new JLabel("Transmission :");
        transmissionLabel.setFont(labelFont);
        String[] transmissionTypes = {"Manuelle", "Automatique"};
        transmissionComboBox = new JComboBox<>(transmissionTypes);
        transmissionComboBox.setFont(fieldFont);
        panel.add(transmissionLabel);
        panel.add(transmissionComboBox);

        JLabel seatsLabel = new JLabel("Nombre de places :");
        seatsLabel.setFont(labelFont);
        seatsField = new JTextField();
        seatsField.setFont(fieldFont);
        panel.add(seatsLabel);
        panel.add(seatsField);

        JLabel availableLabel = new JLabel("Disponible :");
        availableLabel.setFont(labelFont);
        availableCheckBox = new JCheckBox();
        availableCheckBox.setSelected(true);
        availableCheckBox.setBackground(new Color(245, 247, 250));
        panel.add(availableLabel);
        panel.add(availableCheckBox);

        JLabel imageLabel = new JLabel("Image :");
        imageLabel.setFont(labelFont);
        JPanel imagePanel = new JPanel(new BorderLayout(5, 0));
        imagePanel.setBackground(new Color(245, 247, 250));

        imageField = new JTextField();
        imageField.setFont(fieldFont);
        imageField.setEditable(false);

        JButton browseButton = new JButton("Parcourir...");
        browseButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        browseButton.setBackground(new Color(52, 152, 219));
        browseButton.setForeground(Color.WHITE);
        browseButton.setFocusPainted(false);
        browseButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        browseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        browseButton.setOpaque(true);

        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectImage();
            }
        });

        imagePanel.add(imageField, BorderLayout.CENTER);
        imagePanel.add(browseButton, BorderLayout.EAST);

        panel.add(imageLabel);
        panel.add(imagePanel);

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

        dialog = new JDialog(parent, "Ajouter une voiture", true);
        dialog.setContentPane(mainPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
    }

    /**
     * Valide les données du formulaire, crée l'objet Car et ferme le dialogue
     */
    private void validateAndSave() {
        result = createCarFromForm();
        if (result != null) {
            // Si la voiture a été créée avec succès, on ferme le formulaire
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
     * Crée et retourne un objet Car à partir des valeurs saisies dans le formulaire
     *
     * @return un nouvel objet Car ou null si les données sont invalides
     */
    private Car createCarFromForm() {
        try {
            // Vérification que les champs obligatoires sont remplis
            if (idCarField.getText().trim().isEmpty() ||
                    brandField.getText().trim().isEmpty() ||
                    modelField.getText().trim().isEmpty() ||
                    yearField.getText().trim().isEmpty() ||
                    pricedayField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(parent,
                        "Veuillez remplir tous les champs obligatoires (immatriculation, marque, modèle, année, prix)",
                        "Champs incomplets",
                        JOptionPane.WARNING_MESSAGE);
                return null;
            }

            String idCar = idCarField.getText().trim();
            String brand = brandField.getText().trim();
            String model = modelField.getText().trim();
            int year = Integer.parseInt(yearField.getText().trim());
            float priceday = Float.parseFloat(pricedayField.getText().trim());

            // Vérifier que le prix est positif
            if (priceday <= 0) {
                JOptionPane.showMessageDialog(parent,
                        "Le prix par jour doit être positif",
                        "Erreur de saisie",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }

            // Valeurs par défaut pour les champs optionnels
            int mileage = 0;
            if (!mileageField.getText().trim().isEmpty()) {
                mileage = Integer.parseInt(mileageField.getText().trim());
                // Vérifier que le kilométrage est positif
                if (mileage < 0) {
                    JOptionPane.showMessageDialog(parent,
                            "Le kilométrage doit être positif ou nul",
                            "Erreur de saisie",
                            JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            }

            String fuelType = (String) fuelTypeComboBox.getSelectedItem();
            String transmission = (String) transmissionComboBox.getSelectedItem();

            int seats = 5; // Valeur par défaut
            if (!seatsField.getText().trim().isEmpty()) {
                seats = Integer.parseInt(seatsField.getText().trim());
                // Vérifier que le nombre de places est positif
                if (seats <= 0) {
                    JOptionPane.showMessageDialog(parent,
                            "Le nombre de places doit être positif",
                            "Erreur de saisie",
                            JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            }

            boolean available = availableCheckBox.isSelected();
            String image = imageField.getText().trim();

            Car car = new Car(idCar, brand, model, year, priceday,
                    mileage, fuelType, transmission, seats,
                    available, image);

            return car;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent,
                    "Veuillez vérifier les valeurs numériques (année, prix, kilométrage, places)",
                    "Erreur de saisie",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Retourne la voiture créée par le formulaire
     *
     * @return l'objet Car créé ou null si aucune voiture n'a été créée
     */
    public Car getCar() {
        return result;
    }

    // Getters pour accéder aux valeurs des champs
    public String getIdCar() {
        return idCarField.getText().trim();
    }

    public String getBrand() {
        return brandField.getText().trim();
    }

    public String getModel() {
        return modelField.getText().trim();
    }


    /**
     * Ouvre un sélecteur de fichier pour choisir une image et la copie dans le dossier des images
     */
    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sélectionner une image");

        // Filtre pour n'afficher que les images
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                        name.endsWith(".png") || name.endsWith(".gif") ||
                        name.endsWith(".bmp");
            }

            @Override
            public String getDescription() {
                return "Fichiers image (*.jpg, *.jpeg, *.png, *.gif, *.bmp)";
            }
        });

        int result = fileChooser.showOpenDialog(dialog);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                // Créer le dossier images s'il n'existe pas
                String imagesDir = "images";
                Path imagesDirPath = Paths.get(imagesDir);
                if (!Files.exists(imagesDirPath)) {
                    Files.createDirectories(imagesDirPath);
                }

                // Générer un nom unique pour l'image
                String originalFileName = selectedFile.getName();
                String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
                String newFileName = System.currentTimeMillis() + extension;
                Path destinationPath = Paths.get(imagesDir, newFileName);

                // Copier le fichier vers le dossier images
                Files.copy(selectedFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

                // Mettre à jour le champ avec le chemin relatif
                imageField.setText(destinationPath.toString());

                JOptionPane.showMessageDialog(dialog,
                        "Image importée avec succès !",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Erreur lors de l'importation de l'image : " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

}