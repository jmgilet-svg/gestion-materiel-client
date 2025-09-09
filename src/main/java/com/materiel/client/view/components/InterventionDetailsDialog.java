package com.materiel.client.view.components;

import com.materiel.client.model.Intervention;
import com.materiel.client.model.Resource;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Dialogue pour afficher les détails complets d'une intervention
 */
public class InterventionDetailsDialog extends JDialog {
    
    private final Intervention intervention;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public InterventionDetailsDialog(java.awt.Window parent, Intervention intervention) {
        super(parent, "Détails de l'intervention", ModalityType.APPLICATION_MODAL);
        this.intervention = intervention;
        
        initComponents();
        setupDialog();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel principal avec détails
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
        
        // Header avec statut et titre
        JPanel headerPanel = createHeaderPanel();
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Contenu avec les détails
        JScrollPane scrollPane = new JScrollPane(createDetailsPanel());
        scrollPane.setPreferredSize(new Dimension(500, 400));
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Titre principal
        JLabel titleLabel = new JLabel(intervention.getTitre());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.decode("#1F2937"));
        
        // Statut avec couleur
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setOpaque(false);
        
        JLabel statusLabel = new JLabel("● " + intervention.getStatut().getDisplayName());
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(Color.decode(intervention.getStatut().getColor()));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(createLightTransparentColor(intervention.getStatut().getColor()));
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode(intervention.getStatut().getColor()), 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        
        statusPanel.add(statusLabel);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(statusPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        // Section Client
        if (intervention.getClient() != null) {
            panel.add(createSection("👤 Client", intervention.getClient().getNom()));
            panel.add(Box.createVerticalStrut(15));
        }
        
        // Section Horaires
        if (intervention.getDateDebut() != null || intervention.getDateFin() != null) {
            String horaires = "";
            if (intervention.getDateDebut() != null) {
                horaires += "Début: " + intervention.getDateDebut().format(DATE_TIME_FORMATTER);
            }
            if (intervention.getDateFin() != null) {
                if (!horaires.isEmpty()) horaires += "\n";
                horaires += "Fin: " + intervention.getDateFin().format(DATE_TIME_FORMATTER);
            }
            if (intervention.getDateDebut() != null && intervention.getDateFin() != null) {
                long duration = java.time.Duration.between(intervention.getDateDebut(), intervention.getDateFin()).toHours();
                horaires += "\nDurée: " + duration + " heures";
            }
            
            panel.add(createSection("🕐 Horaires", horaires));
            panel.add(Box.createVerticalStrut(15));
        }
        
        // Section Ressources
        if (intervention.getRessources() != null && !intervention.getRessources().isEmpty()) {
            String resourcesText = intervention.getRessources().stream()
                .map(r -> "• " + r.getNom() + " (" + r.getType().getDisplayName() + ")")
                .collect(Collectors.joining("\n"));
            
            panel.add(createSection("🏗️ Ressources (" + intervention.getRessources().size() + ")", resourcesText));
            panel.add(Box.createVerticalStrut(15));
        }
        
        // Section Adresse
        if (intervention.getAdresseIntervention() != null && !intervention.getAdresseIntervention().trim().isEmpty()) {
            panel.add(createSection("📍 Adresse d'intervention", intervention.getAdresseIntervention()));
            panel.add(Box.createVerticalStrut(15));
        }
        
        // Section Description
        if (intervention.getDescription() != null && !intervention.getDescription().trim().isEmpty()) {
            panel.add(createSection("📋 Description", intervention.getDescription()));
            panel.add(Box.createVerticalStrut(15));
        }
        
        // Section Notes
        if (intervention.getNotes() != null && !intervention.getNotes().trim().isEmpty()) {
            panel.add(createSection("📝 Notes", intervention.getNotes()));
            panel.add(Box.createVerticalStrut(15));
        }
        
        // Section Informations système
        panel.add(createSystemInfoSection());
        
        return panel;
    }
    
    private JPanel createSection(String title, String content) {
        JPanel section = new JPanel(new BorderLayout(10, 8));
        section.setOpaque(false);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#E5E7EB"), 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        section.setBackground(Color.decode("#F9FAFB"));
        section.setOpaque(true);
        
        // Titre de la section
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(Color.decode("#374151"));
        
        // Contenu de la section
        JTextArea contentArea = new JTextArea(content);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        contentArea.setForeground(Color.decode("#6B7280"));
        contentArea.setOpaque(false);
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        
        section.add(titleLabel, BorderLayout.NORTH);
        section.add(contentArea, BorderLayout.CENTER);
        
        return section;
    }
    
    private JPanel createSystemInfoSection() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 5));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.decode("#D1D5DB"), 1),
            "Informations système",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 11),
            Color.decode("#6B7280")
        ));
        
        // ID de l'intervention
        panel.add(createInfoLabel("ID:", intervention.getId() != null ? intervention.getId().toString() : "N/A"));
        
        // Nombre de ressources
        int resourceCount = intervention.getRessources() != null ? intervention.getRessources().size() : 0;
        panel.add(createInfoLabel("Ressources:", resourceCount + " affectée(s)"));
        
        // Durée calculée
        String duration = "N/A";
        if (intervention.getDateDebut() != null && intervention.getDateFin() != null) {
            long hours = java.time.Duration.between(intervention.getDateDebut(), intervention.getDateFin()).toHours();
            long minutes = java.time.Duration.between(intervention.getDateDebut(), intervention.getDateFin()).toMinutes() % 60;
            duration = hours + "h" + (minutes > 0 ? " " + minutes + "min" : "");
        }
        panel.add(createInfoLabel("Durée:", duration));
        
        // Type d'intervention (basé sur les ressources)
        String type = "Mixte";
        if (intervention.getRessources() != null && !intervention.getRessources().isEmpty()) {
            boolean allSameType = intervention.getRessources().stream()
                .map(Resource::getType)
                .distinct()
                .count() == 1;
            
            if (allSameType) {
                type = intervention.getRessources().get(0).getType().getDisplayName();
            }
        }
        panel.add(createInfoLabel("Type:", type));
        
        return panel;
    }
    
    private JLabel createInfoLabel(String label, String value) {
        JLabel infoLabel = new JLabel("<html><b>" + label + "</b> " + value + "</html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        infoLabel.setForeground(Color.decode("#6B7280"));
        return infoLabel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        JButton editButton = new JButton("✏️ Modifier");
        editButton.setBackground(Color.decode("#3B82F6"));
        editButton.setForeground(Color.WHITE);
        editButton.setPreferredSize(new Dimension(100, 35));
        editButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        editButton.addActionListener(e -> editIntervention());
        
        JButton closeButton = new JButton("Fermer");
        closeButton.setPreferredSize(new Dimension(80, 35));
        closeButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#D1D5DB"), 1, true),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        closeButton.setBackground(Color.WHITE);
        closeButton.addActionListener(e -> dispose());
        
        // Effet hover pour les boutons
        addHoverEffect(editButton, Color.decode("#2563EB"), Color.decode("#3B82F6"));
        addHoverEffect(closeButton, Color.decode("#F3F4F6"), Color.WHITE);
        
        panel.add(editButton);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(closeButton);
        
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
    
    private void editIntervention() {
        // TODO: Ouvrir le dialogue d'édition
        JOptionPane.showMessageDialog(this, 
            "Dialogue d'édition à implémenter", 
            "Édition", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private Color createLightTransparentColor(String hexColor) {
        try {
            Color baseColor = Color.decode(hexColor);
            return new Color(
                baseColor.getRed(),
                baseColor.getGreen(),
                baseColor.getBlue(),
                51 // 20% d'opacité
            );
        } catch (Exception e) {
            return new Color(128, 128, 128, 51);
        }
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
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
    }
}