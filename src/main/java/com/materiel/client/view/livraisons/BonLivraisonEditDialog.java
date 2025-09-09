package com.materiel.client.view.livraisons;

import com.materiel.client.model.BonLivraison;
import com.materiel.client.model.Client;
import com.materiel.client.mock.MockDataManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

/**
 * Dialogue d'édition/création de bon de livraison
 */
public class BonLivraisonEditDialog extends JDialog {
    
    private BonLivraison bonLivraison;
    private boolean confirmed = false;
    
    // Composants UI
    private JTextField numeroField;
    private JComboBox<Client> clientComboBox;
    private JSpinner dateCreationSpinner;
    private JSpinner dateLivraisonSpinner;
    private JComboBox<BonLivraison.StatutBonLivraison> statutComboBox;
    private JTextArea adresseLivraisonArea;
    private JTextField chauffeurField;
    private JTextField vehiculeField;
    private JTextField immatriculationField;
    private JTextField poidsField;
    private JTextArea commentairesArea;
    
    public BonLivraisonEditDialog(Frame parent, BonLivraison bonLivraison) {
        super(parent, bonLivraison == null ? "Nouveau Bon de Livraison" : "Modifier Bon de Livraison", true);
        this.bonLivraison = bonLivraison != null ? bonLivraison : new BonLivraison();
        
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
        panel.setBackground(Color.WHITE);
        
        // Header avec icône et titre
        JPanel headerPanel = createHeaderPanel();
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Formulaire principal
        JScrollPane scrollPane = new JScrollPane(createFormPanel());
        scrollPane.setPreferredSize(new Dimension(600, 500));
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        
        JLabel iconLabel = new JLabel("🚚");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        
        JLabel titleLabel = new JLabel(bonLivraison.getId() == null ? "Création d'un nouveau bon de livraison" : "Modification du bon de livraison");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        
        panel.add(iconLabel);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(titleLabel);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Numéro de BL
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Numéro:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        numeroField = new JTextField(20);
        numeroField.setBorder(createFieldBorder());
        panel.add(numeroField, gbc);
        row++;
        
        // Client
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Client *:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        clientComboBox = new JComboBox<>();
        loadClients();
        panel.add(clientComboBox, gbc);
        row++;
        
        // Date de création
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Date création:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        dateCreationSpinner = createDateSpinner();
        panel.add(dateCreationSpinner, gbc);
        row++;
        
        // Date de livraison
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Date livraison:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        dateLivraisonSpinner = createDateTimeSpinner();
        panel.add(dateLivraisonSpinner, gbc);
        row++;
        
        // Statut
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Statut:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        statutComboBox = new JComboBox<>(BonLivraison.StatutBonLivraison.values());
        statutComboBox.setRenderer(new StatutComboBoxRenderer());
        panel.add(statutComboBox, gbc);
        row++;
        
        // Séparateur Transport
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel transportSeparator = new JPanel(new FlowLayout(FlowLayout.LEFT));
        transportSeparator.setOpaque(false);
        transportSeparator.add(new JLabel("🚛 Transport"));
        ((JLabel) transportSeparator.getComponent(0)).setFont(((JLabel) transportSeparator.getComponent(0)).getFont().deriveFont(Font.BOLD, 14f));
        panel.add(transportSeparator, gbc);
        gbc.gridwidth = 1;
        row++;
        
        // Chauffeur
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Chauffeur:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        chauffeurField = new JTextField(20);
        chauffeurField.setBorder(createFieldBorder());
        panel.add(chauffeurField, gbc);
        row++;
        
        // Véhicule
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Véhicule:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        vehiculeField = new JTextField(20);
        vehiculeField.setBorder(createFieldBorder());
        panel.add(vehiculeField, gbc);
        row++;
        
        // Immatriculation
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Immatriculation:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        immatriculationField = new JTextField(20);
        immatriculationField.setBorder(createFieldBorder());
        panel.add(immatriculationField, gbc);
        row++;
        
        // Poids total
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Poids total (T):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        poidsField = new JTextField(20);
        poidsField.setHorizontalAlignment(JTextField.RIGHT);
        poidsField.setBorder(createFieldBorder());
        panel.add(poidsField, gbc);
        row++;
        
        // Séparateur Livraison
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel livraisonSeparator = new JPanel(new FlowLayout(FlowLayout.LEFT));
        livraisonSeparator.setOpaque(false);
        livraisonSeparator.add(new JLabel("📍 Livraison"));
        ((JLabel) livraisonSeparator.getComponent(0)).setFont(((JLabel) livraisonSeparator.getComponent(0)).getFont().deriveFont(Font.BOLD, 14f));
        panel.add(livraisonSeparator, gbc);
        gbc.gridwidth = 1;
        row++;
        
        // Adresse de livraison
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Adresse livraison:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.3;
        adresseLivraisonArea = new JTextArea(3, 20);
        adresseLivraisonArea.setLineWrap(true);
        adresseLivraisonArea.setWrapStyleWord(true);
        adresseLivraisonArea.setBorder(createFieldBorder());
        JScrollPane adresseScrollPane = new JScrollPane(adresseLivraisonArea);
        adresseScrollPane.setPreferredSize(new Dimension(300, 80));
        panel.add(adresseScrollPane, gbc);
        row++;
        
        // Commentaires
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Commentaires:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.3;
        commentairesArea = new JTextArea(3, 20);
        commentairesArea.setLineWrap(true);
        commentairesArea.setWrapStyleWord(true);
        commentairesArea.setBorder(createFieldBorder());
        JScrollPane commentairesScrollPane = new JScrollPane(commentairesArea);
        commentairesScrollPane.setPreferredSize(new Dimension(300, 80));
        panel.add(commentairesScrollPane, gbc);
        
        return panel;
    }
    
    private javax.swing.border.Border createFieldBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#D1D5DB"), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
    }
    
    private JSpinner createDateSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy");
        spinner.setEditor(editor);
        return spinner;
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
        if (bonLivraison.getNumero() != null) {
            numeroField.setText(bonLivraison.getNumero());
        }
        
        if (bonLivraison.getClient() != null) {
            clientComboBox.setSelectedItem(bonLivraison.getClient());
        }
        
        if (bonLivraison.getDateCreation() != null) {
            dateCreationSpinner.setValue(java.sql.Date.valueOf(bonLivraison.getDateCreation()));
        }
        
        if (bonLivraison.getDateLivraison() != null) {
            dateLivraisonSpinner.setValue(java.sql.Timestamp.valueOf(bonLivraison.getDateLivraison()));
        }
        
        statutComboBox.setSelectedItem(bonLivraison.getStatut());
        
        if (bonLivraison.getChauffeur() != null) {
            chauffeurField.setText(bonLivraison.getChauffeur());
        }
        
        if (bonLivraison.getVehicule() != null) {
            vehiculeField.setText(bonLivraison.getVehicule());
        }
        
        if (bonLivraison.getNumeroImmatriculation() != null) {
            immatriculationField.setText(bonLivraison.getNumeroImmatriculation());
        }
        
        if (bonLivraison.getPoidsTotal() != null) {
            poidsField.setText(bonLivraison.getPoidsTotal().toString());
        }
        
        if (bonLivraison.getAdresseLivraison() != null) {
            adresseLivraisonArea.setText(bonLivraison.getAdresseLivraison());
        }
        
        if (bonLivraison.getCommentairesLivraison() != null) {
            commentairesArea.setText(bonLivraison.getCommentairesLivraison());
        }
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        JButton cancelButton = new JButton("Annuler");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#D1D5DB"), 1, true),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        cancelButton.setBackground(Color.WHITE);
        cancelButton.addActionListener(this::cancelAction);
        
        JButton saveButton = new JButton("Enregistrer");
        saveButton.setBackground(Color.decode("#3B82F6"));
        saveButton.setForeground(Color.WHITE);
        saveButton.setPreferredSize(new Dimension(120, 35));
        saveButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        saveButton.addActionListener(this::saveAction);
        
        // Effet hover pour les boutons
        addHoverEffect(cancelButton, Color.decode("#F3F4F6"), Color.WHITE);
        addHoverEffect(saveButton, Color.decode("#2563EB"), Color.decode("#3B82F6"));
        
        panel.add(cancelButton);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(saveButton);
        
        return panel;
    }
    
    private void addHoverEffect(JButton button, Color hoverColor, Color normalColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hoverColor);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(normalColor);
            }
        });
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
            if (clientComboBox.getSelectedItem() == null) {
                showValidationError("Vous devez sélectionner un client.", clientComboBox);
                return false;
            }
            
            // Validation des dates
            java.util.Date dateCreation = (java.util.Date) dateCreationSpinner.getValue();
            java.util.Date dateLivraison = (java.util.Date) dateLivraisonSpinner.getValue();
            
            if (dateLivraison.before(dateCreation)) {
                showValidationError("La date de livraison doit être après la date de création.", dateLivraisonSpinner);
                return false;
            }
            
            // Validation du poids
            BigDecimal poids = null;
            if (!poidsField.getText().trim().isEmpty()) {
                try {
                    poids = new BigDecimal(poidsField.getText().trim());
                    if (poids.compareTo(BigDecimal.ZERO) < 0) {
                        showValidationError("Le poids doit être positif.", poidsField);
                        return false;
                    }
                } catch (NumberFormatException ex) {
                    showValidationError("Format de poids invalide.", poidsField);
                    return false;
                }
            }
            
            // Mise à jour de l'objet bon de livraison
            bonLivraison.setNumero(numeroField.getText().trim());
            bonLivraison.setClient((Client) clientComboBox.getSelectedItem());
            
            bonLivraison.setDateCreation(dateCreation.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            bonLivraison.setDateLivraison(LocalDateTime.ofInstant(dateLivraison.toInstant(), java.time.ZoneId.systemDefault()));
            
            bonLivraison.setStatut((BonLivraison.StatutBonLivraison) statutComboBox.getSelectedItem());
            bonLivraison.setChauffeur(chauffeurField.getText().trim());
            bonLivraison.setVehicule(vehiculeField.getText().trim());
            bonLivraison.setNumeroImmatriculation(immatriculationField.getText().trim());
            bonLivraison.setPoidsTotal(poids);
            bonLivraison.setAdresseLivraison(adresseLivraisonArea.getText().trim());
            bonLivraison.setCommentairesLivraison(commentairesArea.getText().trim());
            
            return true;
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la validation: " + ex.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private void showValidationError(String message, JComponent component) {
        JOptionPane.showMessageDialog(this, message, "Erreur de validation", JOptionPane.ERROR_MESSAGE);
        component.requestFocus();
    }
    
    private void setupDialog() {
        pack();
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(true);
        
        // Gérer la fermeture avec Escape
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelAction(e);
            }
        });
        
        // Focus sur le numéro si création
        SwingUtilities.invokeLater(() -> {
            if (bonLivraison.getId() == null) {
                numeroField.requestFocus();
            }
        });
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public BonLivraison getBonLivraison() {
        return bonLivraison;
    }
    
    /**
     * Renderer personnalisé pour la ComboBox des statuts
     */
    private static class StatutComboBoxRenderer extends DefaultListCellRenderer {
        
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof BonLivraison.StatutBonLivraison) {
                BonLivraison.StatutBonLivraison statut = (BonLivraison.StatutBonLivraison) value;
                setText("● " + statut.getDisplayName());
                
                if (!isSelected) {
                    setForeground(Color.decode(statut.getColor()));
                }
            }
            
            return this;
        }
    }
}