package com.materiel.client.view.clients;

import com.materiel.client.model.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.regex.Pattern;

/**
 * Dialogue d'édition/création de client
 */
public class ClientEditDialog extends JDialog {
    
    private Client client;
    private boolean confirmed = false;
    
    // Composants UI
    private JTextField nomField;
    private JTextArea adresseArea;
    private JTextField telephoneField;
    private JTextField emailField;
    private JTextField siretField;
    
    public ClientEditDialog(Frame parent, Client client) {
        super(parent, client == null ? "Nouveau Client" : "Modifier Client", true);
        this.client = client != null ? client : new Client();
        
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
        
        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        
        JLabel titleLabel = new JLabel(client.getId() == null ? "Création d'un nouveau client" : "Modification du client");
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
        
        // Adresse
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Adresse:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        adresseArea = new JTextArea(3, 25);
        adresseArea.setLineWrap(true);
        adresseArea.setWrapStyleWord(true);
        adresseArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#D1D5DB"), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        JScrollPane adresseScrollPane = new JScrollPane(adresseArea);
        adresseScrollPane.setPreferredSize(new Dimension(300, 80));
        panel.add(adresseScrollPane, gbc);
        row++;
        
        // Téléphone
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Téléphone:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        telephoneField = new JTextField(25);
        telephoneField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#D1D5DB"), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        panel.add(telephoneField, gbc);
        row++;
        
        // Email
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        emailField = new JTextField(25);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#D1D5DB"), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        panel.add(emailField, gbc);
        row++;
        
        // SIRET
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("SIRET:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        siretField = new JTextField(25);
        siretField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#D1D5DB"), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        panel.add(siretField, gbc);
        row++;
        
        // Note sur les champs obligatoires
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel noteLabel = new JLabel("* Champs obligatoires");
        noteLabel.setFont(noteLabel.getFont().deriveFont(Font.ITALIC, 11f));
        noteLabel.setForeground(Color.decode("#6B7280"));
        panel.add(noteLabel, gbc);
        
        return panel;
    }
    
    private void populateFields() {
        if (client.getNom() != null) {
            nomField.setText(client.getNom());
        }
        
        if (client.getAdresse() != null) {
            adresseArea.setText(client.getAdresse());
        }
        
        if (client.getTelephone() != null) {
            telephoneField.setText(client.getTelephone());
        }
        
        if (client.getEmail() != null) {
            emailField.setText(client.getEmail());
        }
        
        if (client.getSiret() != null) {
            siretField.setText(client.getSiret());
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
        saveButton.setBackground(Color.decode("#10B981"));
        saveButton.setForeground(Color.WHITE);
        saveButton.setPreferredSize(new Dimension(120, 35));
        saveButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        saveButton.addActionListener(this::saveAction);
        
        // Effet hover pour les boutons
        addHoverEffect(cancelButton, Color.decode("#F3F4F6"), Color.WHITE);
        addHoverEffect(saveButton, Color.decode("#059669"), Color.decode("#10B981"));
        
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
                showValidationError("Le nom du client est obligatoire.", nomField);
                return false;
            }
            
            // Validation de l'email si renseigné
            String email = emailField.getText().trim();
            if (!email.isEmpty() && !isValidEmail(email)) {
                showValidationError("L'adresse email n'est pas valide.", emailField);
                return false;
            }
            
            // Validation du SIRET si renseigné
            String siret = siretField.getText().trim();
            if (!siret.isEmpty() && !isValidSiret(siret)) {
                showValidationError("Le numéro SIRET doit contenir 14 chiffres.", siretField);
                return false;
            }
            
            // Mise à jour de l'objet client
            client.setNom(nomField.getText().trim());
            client.setAdresse(adresseArea.getText().trim());
            client.setTelephone(telephoneField.getText().trim());
            client.setEmail(email.isEmpty() ? null : email);
            client.setSiret(siret.isEmpty() ? null : siret);
            
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
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    
    private boolean isValidSiret(String siret) {
        // Supprimer les espaces et vérifier que c'est 14 chiffres
        String cleanSiret = siret.replaceAll("\\s", "");
        return cleanSiret.matches("\\d{14}");
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
        
        // Gérer la confirmation avec Entrée sur le dernier champ
        siretField.addActionListener(this::saveAction);
        
        // Focus sur le nom
        SwingUtilities.invokeLater(() -> nomField.requestFocus());
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public Client getClient() {
        return client;
    }
}