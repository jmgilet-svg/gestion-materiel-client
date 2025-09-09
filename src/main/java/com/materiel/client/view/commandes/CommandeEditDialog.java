package com.materiel.client.view.commandes;

import com.materiel.client.model.Commande;
import com.materiel.client.model.Client;
import com.materiel.client.mock.MockDataManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

/**
 * Dialogue d'édition/création de commande
 */
public class CommandeEditDialog extends JDialog {
    
    private Commande commande;
    private boolean confirmed = false;
    
    // Composants UI
    private JTextField numeroField;
    private JComboBox<Client> clientComboBox;
    private JSpinner dateCreationSpinner;
    private JSpinner dateLivraisonPrevueSpinner;
    private JComboBox<Commande.StatutCommande> statutComboBox;
    private JTextField montantHTField;
    private JTextField montantTVAField;
    private JTextField montantTTCField;
    private JTextArea adresseLivraisonArea;
    private JTextField responsableField;
    private JTextArea commentairesArea;
    
    public CommandeEditDialog(Frame parent, Commande commande) {
        super(parent, commande == null ? "Nouvelle Commande" : "Modifier Commande", true);
        this.commande = commande != null ? commande : new Commande();
        
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
        JPanel formPanel = createFormPanel();
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        
        JLabel iconLabel = new JLabel("📦");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        
        JLabel titleLabel = new JLabel(commande.getId() == null ? "Création d'une nouvelle commande" : "Modification de la commande");
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
        
        // Numéro de commande
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
        
        // Date de livraison prévue
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Livraison prévue:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        dateLivraisonPrevueSpinner = createDateSpinner();
        panel.add(dateLivraisonPrevueSpinner, gbc);
        row++;
        
        // Statut
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Statut:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        statutComboBox = new JComboBox<>(Commande.StatutCommande.values());
        statutComboBox.setRenderer(new StatutComboBoxRenderer());
        panel.add(statutComboBox, gbc);
        row++;
        
        // Responsable
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Responsable:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        responsableField = new JTextField(20);
        responsableField.setBorder(createFieldBorder());
        panel.add(responsableField, gbc);
        row++;
        
        // Séparateur
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JSeparator separator = new JSeparator();
        panel.add(separator, gbc);
        gbc.gridwidth = 1;
        row++;
        
        // Montants
        JPanel montantsPanel = createMontantsPanel();
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(montantsPanel, gbc);
        gbc.gridwidth = 1;
        row++;
        
        // Adresse de livraison
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Adresse livraison:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.5;
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
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.5;
        commentairesArea = new JTextArea(3, 20);
        commentairesArea.setLineWrap(true);
        commentairesArea.setWrapStyleWord(true);
        commentairesArea.setBorder(createFieldBorder());
        JScrollPane commentairesScrollPane = new JScrollPane(commentairesArea);
        commentairesScrollPane.setPreferredSize(new Dimension(300, 80));
        panel.add(commentairesScrollPane, gbc);
        
        return panel;
    }
    
    private JPanel createMontantsPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 5));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Montants"));
        
        panel.add(new JLabel("Montant HT:"));
        montantHTField = new JTextField(10);
        montantHTField.setHorizontalAlignment(JTextField.RIGHT);
        montantHTField.setBorder(createFieldBorder());
        panel.add(montantHTField);
        
        panel.add(new JLabel("Montant TVA:"));
        montantTVAField = new JTextField(10);
        montantTVAField.setHorizontalAlignment(JTextField.RIGHT);
        montantTVAField.setBorder(createFieldBorder());
        panel.add(montantTVAField);
        
        panel.add(new JLabel("Montant TTC:"));
        montantTTCField = new JTextField(10);
        montantTTCField.setHorizontalAlignment(JTextField.RIGHT);
        montantTTCField.setFont(montantTTCField.getFont().deriveFont(Font.BOLD));
        montantTTCField.setBorder(createFieldBorder());
        panel.add(montantTTCField);
        
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
        if (commande.getNumero() != null) {
            numeroField.setText(commande.getNumero());
        }
        
        if (commande.getClient() != null) {
            clientComboBox.setSelectedItem(commande.getClient());
        }
        
        if (commande.getDateCreation() != null) {
            dateCreationSpinner.setValue(java.sql.Date.valueOf(commande.getDateCreation()));
        }
        
        if (commande.getDateLivraisonPrevue() != null) {
            dateLivraisonPrevueSpinner.setValue(java.sql.Date.valueOf(commande.getDateLivraisonPrevue()));
        }
        
        statutComboBox.setSelectedItem(commande.getStatut());
        
        if (commande.getResponsablePreparation() != null) {
            responsableField.setText(commande.getResponsablePreparation());
        }
        
        if (commande.getMontantHT() != null) {
            montantHTField.setText(commande.getMontantHT().toString());
        }
        
        if (commande.getMontantTVA() != null) {
            montantTVAField.setText(commande.getMontantTVA().toString());
        }
        
        if (commande.getMontantTTC() != null) {
            montantTTCField.setText(commande.getMontantTTC().toString());
        }
        
        if (commande.getAdresseLivraison() != null) {
            adresseLivraisonArea.setText(commande.getAdresseLivraison());
        }
        
        if (commande.getCommentaires() != null) {
            commentairesArea.setText(commande.getCommentaires());
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
            java.util.Date dateLivraisonPrevue = (java.util.Date) dateLivraisonPrevueSpinner.getValue();
            
            if (dateLivraisonPrevue.before(dateCreation)) {
                showValidationError("La date de livraison prévue doit être après la date de création.", dateLivraisonPrevueSpinner);
                return false;
            }
            
            // Mise à jour de l'objet commande
            commande.setNumero(numeroField.getText().trim());
            commande.setClient((Client) clientComboBox.getSelectedItem());
            
            commande.setDateCreation(dateCreation.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            commande.setDateLivraisonPrevue(dateLivraisonPrevue.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            
            commande.setStatut((Commande.StatutCommande) statutComboBox.getSelectedItem());
            commande.setResponsablePreparation(responsableField.getText().trim());
            commande.setAdresseLivraison(adresseLivraisonArea.getText().trim());
            commande.setCommentaires(commentairesArea.getText().trim());
            
            // Montants
            if (!montantHTField.getText().trim().isEmpty()) {
                commande.setMontantHT(new BigDecimal(montantHTField.getText().trim()));
            }
            
            if (!montantTVAField.getText().trim().isEmpty()) {
                commande.setMontantTVA(new BigDecimal(montantTVAField.getText().trim()));
            }
            
            if (!montantTTCField.getText().trim().isEmpty()) {
                commande.setMontantTTC(new BigDecimal(montantTTCField.getText().trim()));
            }
            
            return true;
            
        } catch (NumberFormatException ex) {
            showValidationError("Format de nombre invalide dans les montants.", montantHTField);
            return false;
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
            if (commande.getId() == null) {
                numeroField.requestFocus();
            }
        });
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public Commande getCommande() {
        return commande;
    }
    
    /**
     * Renderer personnalisé pour la ComboBox des statuts
     */
    private static class StatutComboBoxRenderer extends DefaultListCellRenderer {
        
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Commande.StatutCommande) {
                Commande.StatutCommande statut = (Commande.StatutCommande) value;
                setText("● " + statut.getDisplayName());
                
                if (!isSelected) {
                    setForeground(Color.decode(statut.getColor()));
                }
            }
            
            return this;
        }
    }
}