// StartupDialog.java
package com.materiel.client.view;

import com.materiel.client.config.DataMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Dialogue de démarrage pour choisir le mode de fonctionnement
 */
public class StartupDialog extends JDialog {
    
    private DataMode selectedMode = DataMode.MOCK_JSON;
    private boolean confirmed = false;
    private ButtonGroup modeButtonGroup;
    private JRadioButton mockRadioButton;
    private JRadioButton apiRadioButton;
    private JPanel mockCard;
    private JPanel apiCard;
    
    public StartupDialog() {
        initComponents();
        setupDialog();
    }
    
    private void initComponents() {
        setTitle("Gestion Matériel - Configuration");
        setModal(true);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(Color.WHITE);
        
        // Logo et titre
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Options de configuration
        JPanel optionsPanel = createOptionsPanel();
        mainPanel.add(optionsPanel, BorderLayout.CENTER);
        
        // Boutons d'action
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        JLabel logoLabel = new JLabel("🏗️");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Gestion de Matériel");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.decode("#1E293B"));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Choisissez le mode de fonctionnement");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.decode("#64748B"));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(logoLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitleLabel);
        
        return panel;
    }
    
    private JPanel createOptionsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 15));
        panel.setOpaque(false);
        
        // Groupe de boutons radio
        modeButtonGroup = new ButtonGroup();
        
        // Option Mock JSON
        mockCard = createOptionCard(
            "📁", "Mode Mock JSON", 
            "Données locales simulées",
            "Fonctionne hors ligne • Données persistées • Idéal pour démonstration",
            DataMode.MOCK_JSON,
            true
        );
        
        // Option Backend API
        apiCard = createOptionCard(
            "🌐", "Mode Backend API", 
            "Connexion au serveur Spring Boot",
            "Données temps réel • Synchronisation • Nécessite serveur actif",
            DataMode.BACKEND_API,
            false
        );
        
        panel.add(mockCard);
        panel.add(apiCard);
        
        return panel;
    }
    
    private JPanel createOptionCard(String icon, String title, String subtitle, 
                                   String description, DataMode mode, boolean selected) {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(selected ? Color.decode("#3B82F6") : Color.decode("#D1D5DB"), 2, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setBackground(selected ? Color.decode("#EBF4FF") : Color.WHITE);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Panneau gauche avec icône et radio
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JRadioButton radioButton = new JRadioButton();
        radioButton.setSelected(selected);
        radioButton.setOpaque(false);
        radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        radioButton.addActionListener(e -> selectMode(mode));
        
        if (mode == DataMode.MOCK_JSON) {
            mockRadioButton = radioButton;
        } else {
            apiRadioButton = radioButton;
        }
        
        modeButtonGroup.add(radioButton);
        
        leftPanel.add(iconLabel);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(radioButton);
        
        // Panneau central avec contenu
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.decode("#1E293B"));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(Color.decode("#64748B"));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel("<html><div style='margin-top: 8px;'>" + description + "</div></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLabel.setForeground(Color.decode("#9CA3AF"));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(descLabel);
        
        // Panneau droite avec indicateur
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rightPanel.setOpaque(false);
        
        JLabel statusLabel = new JLabel(selected ? "✓" : "");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        statusLabel.setForeground(Color.decode("#10B981"));
        rightPanel.add(statusLabel);
        
        card.add(leftPanel, BorderLayout.WEST);
        card.add(contentPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);
        
        // Rendre toute la carte cliquable
        addClickHandler(card, radioButton, mode);
        
        return card;
    }
    
    private void addClickHandler(JPanel card, JRadioButton radioButton, DataMode mode) {
        MouseAdapter clickHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                radioButton.setSelected(true);
                selectMode(mode);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!radioButton.isSelected()) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.decode("#93C5FD"), 2, true),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                    ));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!radioButton.isSelected()) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.decode("#D1D5DB"), 2, true),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                    ));
                }
            }
        };
        
        card.addMouseListener(clickHandler);
        
        // Ajouter le handler à tous les composants enfants aussi
        addMouseListenerToAllComponents(card, clickHandler);
    }
    
    private void addMouseListenerToAllComponents(Container container, MouseAdapter listener) {
        for (Component component : container.getComponents()) {
            component.addMouseListener(listener);
            if (component instanceof Container) {
                addMouseListenerToAllComponents((Container) component, listener);
            }
        }
    }
    
    private void selectMode(DataMode mode) {
        selectedMode = mode;
        updateCardSelection();
    }
    
    private void updateCardSelection() {
        // Mettre à jour l'apparence des cartes
        boolean mockSelected = (selectedMode == DataMode.MOCK_JSON);
        boolean apiSelected = (selectedMode == DataMode.BACKEND_API);
        
        // Carte Mock
        mockCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(mockSelected ? Color.decode("#3B82F6") : Color.decode("#D1D5DB"), 2, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        mockCard.setBackground(mockSelected ? Color.decode("#EBF4FF") : Color.WHITE);
        
        // Carte API
        apiCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(apiSelected ? Color.decode("#3B82F6") : Color.decode("#D1D5DB"), 2, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        apiCard.setBackground(apiSelected ? Color.decode("#EBF4FF") : Color.WHITE);
        
        // Mettre à jour les indicateurs de statut
        updateStatusIndicators();
        
        repaint();
    }
    
    private void updateStatusIndicators() {
        // Trouver et mettre à jour les labels de statut
        updateStatusLabel(mockCard, selectedMode == DataMode.MOCK_JSON);
        updateStatusLabel(apiCard, selectedMode == DataMode.BACKEND_API);
    }
    
    private void updateStatusLabel(JPanel card, boolean selected) {
        Component rightPanel = ((BorderLayout) card.getLayout()).getLayoutComponent(BorderLayout.EAST);
        if (rightPanel instanceof JPanel) {
            Component[] components = ((JPanel) rightPanel).getComponents();
            for (Component comp : components) {
                if (comp instanceof JLabel) {
                    ((JLabel) comp).setText(selected ? "✓" : "");
                    break;
                }
            }
        }
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panel.setOpaque(false);
        
        JButton cancelButton = new JButton("Annuler");
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#D1D5DB"), 1, true),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        cancelButton.setBackground(Color.WHITE);
        cancelButton.addActionListener(this::cancelAction);
        
        JButton confirmButton = new JButton("Continuer");
        confirmButton.setPreferredSize(new Dimension(120, 40));
        confirmButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        confirmButton.setBackground(Color.decode("#3B82F6"));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        confirmButton.addActionListener(this::confirmAction);
        
        // Effet hover pour le bouton confirmer
        confirmButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                confirmButton.setBackground(Color.decode("#2563EB"));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                confirmButton.setBackground(Color.decode("#3B82F6"));
            }
        });
        
        panel.add(cancelButton);
        panel.add(confirmButton);
        
        return panel;
    }
    
    private void confirmAction(ActionEvent e) {
        confirmed = true;
        dispose();
    }
    
    private void cancelAction(ActionEvent e) {
        confirmed = false;
        dispose();
    }
    
    private void setupDialog() {
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Gérer la fermeture avec Escape
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelAction(e);
            }
        });
        
        // Gérer la confirmation avec Entrée
        KeyStroke enterKeyStroke = KeyStroke.getKeyStroke("ENTER");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enterKeyStroke, "ENTER");
        getRootPane().getActionMap().put("ENTER", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmAction(e);
            }
        });
        
        // Définir le bouton par défaut
        JButton confirmButton = new JButton("Continuer");
     // ... configuration du bouton ...
     getRootPane().setDefaultButton(confirmButton);
    }
    
    public DataMode getSelectedMode() {
        return selectedMode;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}