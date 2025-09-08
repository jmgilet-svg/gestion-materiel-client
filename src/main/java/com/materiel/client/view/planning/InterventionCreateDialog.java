package com.materiel.client.view.planning;

import com.materiel.client.model.Intervention;
import com.materiel.client.model.Resource;
import com.materiel.client.model.Client;
import com.materiel.client.mock.MockDataManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialogue de création d'intervention après drop d'une ressource
 */
public class InterventionCreateDialog extends JDialog {
    
    private Intervention intervention;
    private boolean confirmed = false;
    private boolean hasConflict;
    
    // Composants UI
    private JTextField titreField;
    private JTextArea descriptionArea;
    private JComboBox<Client> clientComboBox;
    private JSpinner dateDebutSpinner;
    private JSpinner dateFinSpinner;
    private JTextField adresseField;
    private JTextArea notesArea;
    private JLabel conflitLabel;
    private JLabel resourceLabel;
    
    public InterventionCreateDialog(Frame parent, Resource resource, 
                                   LocalDateTime dateDebut, LocalDateTime dateFin, 
                                   boolean hasConflict) {
        super(parent, "Nouvelle Intervention", true);
        this.hasConflict = hasConflict;
        
        // Créer l'intervention avec la ressource
        this.intervention = new Intervention();
        this.intervention.setDateDebut(dateDebut);
        this.intervention.setDateFin(dateFin);
        this.intervention.setStatut(Intervention.StatutIntervention.PLANIFIEE);
        
        List<Resource> resources = new ArrayList<>();
        resources.add(resource);
        this.intervention.setRessources(resources);
        
        initComponents();
        populateFields();
        setupDialog();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel principal
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);
        
        // Panel de boutons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        // Header avec alerte conflit si nécessaire
        JPanel headerPanel = createHeaderPanel();
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Formulaire
        JPanel formPanel = createFormPanel();
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        // Titre
        JLabel titleLabel = new JLabel("🗓️ Nouvelle Intervention");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Ressource affectée
        resourceLabel = new JLabel();
        resourceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resourceLabel.setForeground(Color.decode("#64748B"));
        resourceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(resourceLabel);
        
        // Alerte conflit si nécessaire
        if (hasConflict) {
            panel.add(Box.createVerticalStrut(10));
            conflitLabel = new JLabel("⚠️ CONFLIT DÉTECTÉ - Cette ressource est déjà utilisée sur la même période");
            conflitLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            conflitLabel.setForeground(Color.decode("#EF4444"));
            conflitLabel.setOpaque(true);
            conflitLabel.setBackground(Color.decode("#FEF2F2"));
            conflitLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#FECACA"), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            conflitLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(conflitLabel);
        }
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Titre de l'intervention
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Titre:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        titreField = new JTextField(25);
        panel.add(titreField, gbc);
        row++;
        
        // Client
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Client:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        clientComboBox = new JComboBox<>();
        loadClients();
        panel.add(clientComboBox, gbc);
        row++;
        
        // Date et heure de début
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Début:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        dateDebutSpinner = createDateTimeSpinner();
        panel.add(dateDebutSpinner, gbc);
        row++;
        
        // Date et heure de fin
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Fin:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        dateFinSpinner = createDateTimeSpinner();
        panel.add(dateFinSpinner, gbc);
        row++;
        
        // Adresse d'intervention
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Adresse:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        adresseField = new JTextField(25);
        panel.add(adresseField, gbc);
        row++;
        
        // Description
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        descriptionArea = new JTextArea(3, 25);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        panel.add(descScrollPane, gbc);
        row++;
        
        // Notes
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        notesArea = new JTextArea(2, 25);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        panel.add(notesScrollPane, gbc);
        
        return panel;
    }
    
    private JSpinner createDateTimeSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy HH:mm");
        spinner.setEditor(editor);
        return spinner;
    }
    
    private void loadClients() {
        try {
            List<Client> clients = MockDataManager.getInstance().getClients();
            for (Client client : clients) {
                clientComboBox.addItem(client);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des clients: " + e.getMessage());
        }
    }
    
    private void populateFields() {
        // Titre par défaut
        if (intervention.getRessources() != null && !intervention.getRessources().isEmpty()) {
            Resource resource = intervention.getRessources().get(0);
            titreField.setText("Intervention " + resource.getNom());
            resourceLabel.setText("Ressource: " + resource.getNom() + " (" + resource.getType().getDisplayName() + ")");
        }
        
        // Dates
        if (intervention.getDateDebut() != null) {
            dateDebutSpinner.setValue(java.sql.Timestamp.valueOf(intervention.getDateDebut()));
        }
        
        if (intervention.getDateFin() != null) {
            dateFinSpinner.setValue(java.sql.Timestamp.valueOf(intervention.getDateFin()));
        }
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        JButton cancelButton = new JButton("Annuler");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.addActionListener(this::cancelAction);
        
        JButton saveButton = new JButton(hasConflict ? "Créer malgré le conflit" : "Créer l'intervention");
        saveButton.setBackground(hasConflict ? Color.decode("#F97316") : Color.decode("#3B82F6"));
        saveButton.setForeground(Color.WHITE);
        saveButton.setPreferredSize(new Dimension(hasConflict ? 180 : 150, 35));
        saveButton.addActionListener(this::saveAction);
        
        panel.add(cancelButton);
        panel.add(saveButton);
        
        return panel;
    }
    
    private void saveAction(ActionEvent e) {
        if (validateAndSave()) {
            confirmed = true;
            dispose();
        }
    }
    
    private void cancelAction(ActionEvent e) {
        confirmed = false;
        dispose();
    }
    
    private boolean validateAndSave() {
        try {
            // Validation des champs obligatoires
            if (titreField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Le titre est obligatoire.", 
                                            "Erreur de validation", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            if (clientComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Vous devez sélectionner un client.", 
                                            "Erreur de validation", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Récupération des dates
            java.util.Date dateDebut = (java.util.Date) dateDebutSpinner.getValue();
            java.util.Date dateFin = (java.util.Date) dateFinSpinner.getValue();
            
            if (dateFin.before(dateDebut)) {
                JOptionPane.showMessageDialog(this, "La date de fin doit être après la date de début.", 
                                            "Erreur de validation", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Mise à jour de l'intervention
            intervention.setTitre(titreField.getText().trim());
            intervention.setClient((Client) clientComboBox.getSelectedItem());
            intervention.setDateDebut(LocalDateTime.ofInstant(dateDebut.toInstant(), java.time.ZoneId.systemDefault()));
            intervention.setDateFin(LocalDateTime.ofInstant(dateFin.toInstant(), java.time.ZoneId.systemDefault()));
            intervention.setAdresseIntervention(adresseField.getText().trim());
            intervention.setDescription(descriptionArea.getText().trim());
            intervention.setNotes(notesArea.getText().trim());
            
            return true;
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la validation: " + ex.getMessage(), 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private void setupDialog() {
        pack();
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(true);
        
        // Focus sur le titre
        SwingUtilities.invokeLater(() -> titreField.requestFocus());
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public Intervention getIntervention() {
        return intervention;
    }
}