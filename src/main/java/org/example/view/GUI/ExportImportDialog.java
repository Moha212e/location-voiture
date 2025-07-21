package org.example.view.GUI;

import org.example.utils.PDFExporter;
import org.example.utils.ExcelExporter;
import org.example.utils.AutoBackupManager;
import org.example.model.entity.*;
import org.example.model.dao.DAOLocation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

/**
 * Dialogue pour l'export et l'import de données
 */
public class ExportImportDialog extends JDialog {
    
    private final DAOLocation dao;
    private final AutoBackupManager backupManager;
    
    // Composants UI
    private JTabbedPane tabbedPane;
    private JButton exportPdfButton;
    private JButton exportExcelButton;
    private JButton manualBackupButton;
    private JButton restoreBackupButton;
    private JCheckBox autoBackupCheckBox;
    private JSpinner backupIntervalSpinner;
    private JTextArea logArea;
    
    public ExportImportDialog(JFrame parent, DAOLocation dao) {
        super(parent, "Export/Import Avancé", true);
        this.dao = dao;
        this.backupManager = new AutoBackupManager(dao);
        
        initComponents();
        setupLayout();
        setupEventListeners();
        
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private void initComponents() {
        tabbedPane = new JTabbedPane();
        
        // Onglet Export
        JPanel exportPanel = createExportPanel();
        tabbedPane.addTab("Export", new ImageIcon("src/main/resources/icons/export.png"), exportPanel);
        
        // Onglet Sauvegarde
        JPanel backupPanel = createBackupPanel();
        tabbedPane.addTab("Sauvegarde", new ImageIcon("src/main/resources/icons/backup.png"), backupPanel);
        
        // Onglet Logs
        JPanel logPanel = createLogPanel();
        tabbedPane.addTab("Logs", new ImageIcon("src/main/resources/icons/log.png"), logPanel);
    }
    
    private JPanel createExportPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Section PDF
        JPanel pdfSection = createSection("Export PDF", "Générer des contrats en format PDF");
        exportPdfButton = createModernButton("Exporter Contrat PDF", new Color(52, 152, 219));
        exportPdfButton.addActionListener(e -> exportContractPDF());
        pdfSection.add(exportPdfButton, BorderLayout.CENTER);
        
        // Section Excel
        JPanel excelSection = createSection("Export Excel", "Générer des rapports détaillés en Excel");
        exportExcelButton = createModernButton("Exporter Rapport Excel", new Color(46, 204, 113));
        exportExcelButton.addActionListener(e -> exportExcelReport());
        excelSection.add(exportExcelButton, BorderLayout.CENTER);
        
        // Assemblage
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        contentPanel.add(pdfSection);
        contentPanel.add(excelSection);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBackupPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Section sauvegarde automatique
        JPanel autoBackupSection = createSection("Sauvegarde Automatique", "Configurer la sauvegarde automatique");
        
        autoBackupCheckBox = new JCheckBox("Activer la sauvegarde automatique");
        autoBackupCheckBox.setSelected(true);
        
        JPanel intervalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        intervalPanel.add(new JLabel("Intervalle:"));
        backupIntervalSpinner = new JSpinner(new SpinnerNumberModel(24, 1, 168, 1));
        intervalPanel.add(backupIntervalSpinner);
        intervalPanel.add(new JLabel("heures"));
        
        autoBackupSection.setLayout(new BorderLayout(10, 10));
        autoBackupSection.add(autoBackupCheckBox, BorderLayout.NORTH);
        autoBackupSection.add(intervalPanel, BorderLayout.CENTER);
        
        // Section sauvegarde manuelle
        JPanel manualBackupSection = createSection("Sauvegarde Manuelle", "Effectuer une sauvegarde manuelle");
        manualBackupButton = createModernButton("Sauvegarde Manuelle", new Color(155, 89, 182));
        manualBackupButton.addActionListener(e -> performManualBackup());
        manualBackupSection.add(manualBackupButton, BorderLayout.CENTER);
        
        // Section restauration
        JPanel restoreSection = createSection("Restauration", "Restaurer une sauvegarde");
        restoreBackupButton = createModernButton("Restaurer Sauvegarde", new Color(231, 76, 60));
        restoreSection.add(restoreBackupButton, BorderLayout.CENTER);
        
        // Assemblage
        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        contentPanel.add(autoBackupSection);
        contentPanel.add(manualBackupSection);
        contentPanel.add(restoreSection);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        JButton clearLogButton = createModernButton("Effacer Logs", new Color(149, 165, 166));
        clearLogButton.addActionListener(e -> logArea.setText(""));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(clearLogButton, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createSection(String title, String description) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            title, 
            TitledBorder.LEFT, 
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            new Color(52, 73, 94)
        ));
        
        JLabel descLabel = new JLabel(description);
        descLabel.setForeground(new Color(127, 140, 141));
        panel.add(descLabel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JButton createModernButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 40));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.brighter());
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        
        // Bouton fermer
        JButton closeButton = createModernButton("Fermer", new Color(149, 165, 166));
        closeButton.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        autoBackupCheckBox.addActionListener(e -> {
            backupManager.setEnabled(autoBackupCheckBox.isSelected());
            logMessage("Sauvegarde automatique " + (autoBackupCheckBox.isSelected() ? "activée" : "désactivée"));
        });
        
        backupIntervalSpinner.addChangeListener(e -> {
            int hours = (Integer) backupIntervalSpinner.getValue();
            backupManager.setBackupInterval(hours);
            logMessage("Intervalle de sauvegarde modifié: " + hours + " heures");
        });
    }
    
    private void exportContractPDF() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Sauvegarder le contrat PDF");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Fichiers PDF", "pdf"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    filePath += ".pdf";
                }
                
                // Pour cet exemple, on prend le premier contrat disponible
                List<Contrat> contrats = dao.getAllContracts();
                if (!contrats.isEmpty()) {
                    PDFExporter.exportContratToPDF(contrats.get(0), filePath);
                    logMessage("Contrat PDF exporté avec succès: " + filePath);
                    JOptionPane.showMessageDialog(this, 
                        "Contrat PDF exporté avec succès!", 
                        "Succès", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Aucun contrat disponible pour l'export.", 
                        "Information", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            logMessage("Erreur lors de l'export PDF: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'export PDF: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportExcelReport() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Sauvegarder le rapport Excel");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Fichiers Excel", "xlsx"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".xlsx")) {
                    filePath += ".xlsx";
                }
                
                List<Car> cars = dao.getAllCars();
                List<Client> clients = dao.getAllClients();
                List<Reservation> reservations = dao.getAllReservations();
                List<Contrat> contrats = dao.getAllContracts();
                
                ExcelExporter.exportCompleteReportToExcel(cars, clients, reservations, contrats, filePath);
                
                logMessage("Rapport Excel exporté avec succès: " + filePath);
                JOptionPane.showMessageDialog(this, 
                    "Rapport Excel exporté avec succès!", 
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            logMessage("Erreur lors de l'export Excel: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'export Excel: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performManualBackup() {
        try {
            backupManager.performManualBackup();
            logMessage("Sauvegarde manuelle effectuée avec succès");
            JOptionPane.showMessageDialog(this, 
                "Sauvegarde manuelle effectuée avec succès!", 
                "Succès", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            logMessage("Erreur lors de la sauvegarde manuelle: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la sauvegarde manuelle: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void logMessage(String message) {
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        logArea.append("[" + timestamp + "] " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    
    @Override
    public void dispose() {
        backupManager.shutdown();
        super.dispose();
    }
} 