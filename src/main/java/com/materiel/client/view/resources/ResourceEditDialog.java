package com.materiel.client.view.resources;

import com.materiel.client.model.Resource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Dialogue d'édition/création de ressource
 */
public class ResourceEditDialog extends JDialog {
    
    private Resource resource;
    private boolean confirmed = false;
    
    // Composants UI
    private JTextField nomField;
    private JComboBox<Resource.ResourceType> typeComboBox;
    private JTextArea descriptionArea;
    private JCheckBox disponibleCheckBox;
    private JTextArea specificationsArea;
    
    public ResourceEditDialog(Frame parent, Resource resource) {
        super(parent, resource == null ? "Nouvelle Ressource" : "Modifier Ressource", true);
        this.resource = resource != null ? resource : new Resource();
        
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
        
        JLabel iconLabel = new JLabel("⚙️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        
        JLabel titleLabel = new JLabel(resource.getId() == null ? "Création d'une nouvelle ressource" : "Modification de la ressource");
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
        
        // Nom (obligatoire)
        gbc.gridx = 0; gbc.gridy = row;
        JLabel nomLabel = new JLabel("Nom *:");
        nomLabel.setForeground(Color.decode("#DC2626")); // Rouge pour obligatoire
        panel.add(nomLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        nomField = new JTextField(25);
        nomField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#D1D5DB"), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        panel.add(nomField, gbc);
        row++;
        
        // Type (obligatoire)
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel typeLabel = new JLabel("Type *:");
        typeLabel.setForeground(Color.decode("#DC2626"));
        panel.add(typeLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        typeComboBox = new JComboBox<>(Resource.ResourceType.values());
        typeComboBox.setRenderer(new TypeComboBoxRenderer());
        panel.add(typeComboBox, gbc);
        row++;
        
        // Disponibilité
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Statut:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        disponibleCheckBox = new JCheckBox("Ressource disponible");
        disponibleCheckBox.setSelected(true);
        panel.add(disponibleCheckBox, gbc);
        row++;
        
        // Description
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.5;
        descriptionArea = new JTextArea(3, 25);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#D1D5DB"), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setPreferredSize(new Dimension(300, 80));
        panel.add(descScrollPane, gbc);
        row++;
        
        // Spécifications techniques
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Spécifications:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.5;
        specificationsArea = new JTextArea(3, 25);
        specificationsArea.setLineWrap(true);
        specificationsArea.setWrapStyleWord(true);
        specificationsArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#D1D5DB"), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        JScrollPane specsScrollPane = new JScrollPane(specificationsArea);
        specsScrollPane.setPreferredSize(new Dimension(300, 80));
        panel.add(specsScrollPane, gbc);
        row++;
        
        // Note sur les champs obligatoires
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        JLabel noteLabel = new JLabel("* Champs obligatoires");
        noteLabel.setFont(noteLabel.getFont().deriveFont(Font.ITALIC, 11f));
        noteLabel.setForeground(Color.decode("#6B7280"));
        panel.add(noteLabel, gbc);
        
        return panel;
    }
    
    private void populateFields() {
        if (resource.getNom() != null) {
            nomField.setText(resource.getNom());
        }
        
        if (resource.getType() != null) {
            typeComboBox.setSelectedItem(resource.getType());
        } else {
            typeComboBox.setSelectedIndex(0); // Premier type par défaut
        }
        
        disponibleCheckBox.setSelected(resource.isDisponible());
        
        if (resource.getDescription() != null) {
            descriptionArea.setText(resource.getDescription());
        }
        
        if (resource.getSpecifications() != null) {
            specificationsArea.setText(resource.getSpecifications());
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
        saveButton.setBackground(Color.decode("#8B5CF6"));
        saveButton.setForeground(Color.WHITE);
        saveButton.setPreferredSize(new Dimension(120, 35));
        saveButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        saveButton.addActionListener(this::saveAction);
        
        // Effet hover pour les boutons
        addHoverEffect(cancelButton, Color.decode("#F3F4F6"), Color.WHITE);
        addHoverEffect(saveButton, Color.decode("#7C3AED"), Color.decode("#8B5CF6"));
        
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
            if (nomField.getText().trim().isEmpty()) {
                showValidationError("Le nom de la ressource est obligatoire.", nomField);
                return false;
            }
            
            if (typeComboBox.getSelectedItem() == null) {
                showValidationError("Vous devez sélectionner un type de ressource.", typeComboBox);
                return false;
            }
            
            // Mise à jour de l'objet ressource
            resource.setNom(nomField.getText().trim());
            resource.setType((Resource.ResourceType) typeComboBox.getSelectedItem());
            resource.setDisponible(disponibleCheckBox.isSelected());
            
            String description = descriptionArea.getText().trim();
            resource.setDescription(description.isEmpty() ? null : description);
            
            String specifications = specificationsArea.getText().trim();
            resource.setSpecifications(specifications.isEmpty() ? null : specifications);
            
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
        
        // Mettre en évidence le champ en erreur
        if (component instanceof JTextField) {
            ((JTextField) component).selectAll();
        }
    }
    
    private void setupDialog() {
        pack();
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // Gérer la fermeture avec Escape
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelAction(e);
            }
        });
        
        // Focus sur le nom
        SwingUtilities.invokeLater(() -> nomField.requestFocus());
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public Resource getResource() {
        return resource;
    }
    
    /**
     * Renderer personnalisé pour la ComboBox des types
     */
    private static class TypeComboBoxRenderer extends DefaultListCellRenderer {
        
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Resource.ResourceType) {
                Resource.ResourceType type = (Resource.ResourceType) value;
                setText(type.getDisplayName());
                
                if (!isSelected) {
                    setForeground(Color.decode(type.getColor()));
                }
            }
            
            return this;
        }
    }
}