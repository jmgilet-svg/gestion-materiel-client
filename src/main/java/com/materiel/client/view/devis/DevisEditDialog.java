// DevisEditDialog.java
package com.materiel.client.view.devis;

import com.materiel.client.model.Devis;
import com.materiel.client.model.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * Dialogue d'édition/création de devis
 */
public class DevisEditDialog extends JDialog {
    
    private Devis devis;
    private boolean confirmed = false;
    
    // Composants UI
    private JTextField numeroField;
    private JComboBox<Client> clientComboBox;
    private JSpinner dateCreationSpinner;
    private JSpinner dateValiditeSpinner;
    private JComboBox<Devis.StatutDevis> statutComboBox;
    private JTextField montantHTField;
    private JTextField montantTVAField;
    private JTextField montantTTCField;
    private JSpinner versionSpinner;
    
    public DevisEditDialog(Frame parent, Devis devis) {
        super(parent, devis == null ? "Nouveau Devis" : "Modifier Devis", true);
        this.devis = devis != null ? devis : new Devis();
        
        initComponents();
        populateFields();
        setupDialog();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel principal avec formulaire
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);
        
        // Panel de boutons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header avec icône et titre
        JPanel headerPanel = createHeaderPanel();
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Formulaire principal
        JPanel formPanel = createFormPanel();
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JLabel iconLabel = new JLabel("📋");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        
        JLabel titleLabel = new JLabel(devis.getId() == null ? "Création d'un nouveau devis" : "Modification du devis");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        
        panel.add(iconLabel);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(titleLabel);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Numéro
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Numéro:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        numeroField = new JTextField(20);
        panel.add(numeroField, gbc);
        row++;
        
        // Client
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Client:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        clientComboBox = new JComboBox<>();
        loadClients();
        panel.add(clientComboBox, gbc);
        row++;
        
        // Date de création
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Date de création:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        dateCreationSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateCreationEditor = new JSpinner.DateEditor(dateCreationSpinner, "dd/MM/yyyy");
        dateCreationSpinner.setEditor(dateCreationEditor);
        panel.add(dateCreationSpinner, gbc);
        row++;
        
        // Date de validité
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Date de validité:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        dateValiditeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateValiditeEditor = new JSpinner.DateEditor(dateValiditeSpinner, "dd/MM/yyyy");
        dateValiditeSpinner.setEditor(dateValiditeEditor);
        panel.add(dateValiditeSpinner, gbc);
        row++;
        
        // Statut
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Statut:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        statutComboBox = new JComboBox<>(Devis.StatutDevis.values());
        statutComboBox.setRenderer(new StatutComboBoxRenderer());
        panel.add(statutComboBox, gbc);
        row++;
        
        // Version
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Version:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        versionSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        panel.add(versionSpinner, gbc);
        row++;
        
        // Séparateur
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JSeparator separator = new JSeparator();
        panel.add(separator, gbc);
        gbc.gridwidth = 1;
        row++;
        
        // Montants
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Montant HT:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        montantHTField = new JTextField(15);
        montantHTField.setHorizontalAlignment(JTextField.RIGHT);
        panel.add(montantHTField, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Montant TVA:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        montantTVAField = new JTextField(15);
        montantTVAField.setHorizontalAlignment(JTextField.RIGHT);
        panel.add(montantTVAField, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Montant TTC:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        montantTTCField = new JTextField(15);
        montantTTCField.setHorizontalAlignment(JTextField.RIGHT);
        montantTTCField.setFont(montantTTCField.getFont().deriveFont(Font.BOLD));
        panel.add(montantTTCField, gbc);
        
        return panel;
    }
    
    private void loadClients() {
        // TODO: Charger les clients depuis le service
        // Pour l'instant, ajouter quelques clients de démonstration
        clientComboBox.addItem(new Client(1L, "BTP Construction SARL"));
        clientComboBox.addItem(new Client(2L, "Entreprise Durand"));
        clientComboBox.addItem(new Client(3L, "Travaux Publics Lyon"));
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        JButton cancelButton = new JButton("Annuler");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.addActionListener(this::cancelAction);
        
        JButton saveButton = new JButton("Enregistrer");
        saveButton.setBackground(Color.decode("#3B82F6"));
        saveButton.setForeground(Color.WHITE);
        saveButton.setPreferredSize(new Dimension(120, 35));
        saveButton.addActionListener(this::saveAction);
        
        panel.add(cancelButton);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(saveButton);
        
        return panel;
    }
    
    private void populateFields() {
        if (devis.getNumero() != null) {
            numeroField.setText(devis.getNumero());
        }
        
        if (devis.getClient() != null) {
            clientComboBox.setSelectedItem(devis.getClient());
        }
        
        if (devis.getDateCreation() != null) {
            dateCreationSpinner.setValue(java.sql.Date.valueOf(devis.getDateCreation()));
        }
        
        if (devis.getDateValidite() != null) {
            dateValiditeSpinner.setValue(java.sql.Date.valueOf(devis.getDateValidite()));
        }
        
        statutComboBox.setSelectedItem(devis.getStatut());
        versionSpinner.setValue(devis.getVersion());
        
        if (devis.getMontantHT() != null) {
            montantHTField.setText(devis.getMontantHT().toString());
        }
        
        if (devis.getMontantTVA() != null) {
            montantTVAField.setText(devis.getMontantTVA().toString());
        }
        
        if (devis.getMontantTTC() != null) {
            montantTTCField.setText(devis.getMontantTTC().toString());
        }
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
            if (numeroField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Le numéro de devis est obligatoire.", 
                                            "Erreur de validation", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            if (clientComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Vous devez sélectionner un client.", 
                                            "Erreur de validation", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Mise à jour de l'objet devis
            devis.setNumero(numeroField.getText().trim());
            devis.setClient((Client) clientComboBox.getSelectedItem());
            
            java.util.Date dateCreation = (java.util.Date) dateCreationSpinner.getValue();
            devis.setDateCreation(dateCreation.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            
            java.util.Date dateValidite = (java.util.Date) dateValiditeSpinner.getValue();
            devis.setDateValidite(dateValidite.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            
            devis.setStatut((Devis.StatutDevis) statutComboBox.getSelectedItem());
            devis.setVersion((Integer) versionSpinner.getValue());
            
            // Montants
            if (!montantHTField.getText().trim().isEmpty()) {
                devis.setMontantHT(new BigDecimal(montantHTField.getText().trim()));
            }
            
            if (!montantTVAField.getText().trim().isEmpty()) {
                devis.setMontantTVA(new BigDecimal(montantTVAField.getText().trim()));
            }
            
            if (!montantTTCField.getText().trim().isEmpty()) {
                devis.setMontantTTC(new BigDecimal(montantTTCField.getText().trim()));
            }
            
            return true;
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format de nombre invalide dans les montants.", 
                                        "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde: " + ex.getMessage(), 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private void setupDialog() {
        pack();
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public Devis getDevis() {
        return devis;
    }
    
    /**
     * Renderer personnalisé pour la ComboBox des statuts
     */
    private static class StatutComboBoxRenderer extends DefaultListCellRenderer {
        
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Devis.StatutDevis) {
                Devis.StatutDevis statut = (Devis.StatutDevis) value;
                setText("● " + statut.getDisplayName());
                
                if (!isSelected) {
                    setForeground(Color.decode(statut.getColor()));
                }
            }
            
            return this;
        }
    }
}
