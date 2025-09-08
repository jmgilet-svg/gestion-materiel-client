package com.materiel.client.view.components;

import com.materiel.client.config.AppConfig;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Barre de statut en bas de l'application
 */
public class StatusBarPanel extends JPanel {
    
    private JLabel modeLabel;
    private JLabel connectionLabel;
    private JLabel timeLabel;
    private Timer timeTimer;
    
    public StatusBarPanel() {
        initComponents();
        setupPanel();
        startTimeUpdater();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 25));
        setBackground(Color.decode("#F1F5F9"));
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        
        // Panel gauche - Mode et connexion
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 3));
        leftPanel.setOpaque(false);
        
        modeLabel = new JLabel();
        updateModeLabel();
        
        connectionLabel = new JLabel();
        updateConnectionStatus();
        
        leftPanel.add(modeLabel);
        leftPanel.add(new JSeparator(SwingConstants.VERTICAL));
        leftPanel.add(connectionLabel);
        
        // Panel droite - Heure
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 3));
        rightPanel.setOpaque(false);
        
        timeLabel = new JLabel();
        rightPanel.add(timeLabel);
        
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }
    
    private void setupPanel() {
        // Configuration additionnelle si nécessaire
    }
    
    private void updateModeLabel() {
        AppConfig config = AppConfig.getInstance();
        String mode = config.getDataMode().getDisplayName();
        String icon = config.isBackendMode() ? "🌐" : "📁";
        
        modeLabel.setText(icon + " " + mode);
        modeLabel.setForeground(config.isBackendMode() ? 
            Color.decode("#10B981") : Color.decode("#F97316"));
    }
    
    private void updateConnectionStatus() {
        AppConfig config = AppConfig.getInstance();
        if (config.isBackendMode()) {
            // TODO: Vérifier vraiment la connexion au backend
            connectionLabel.setText("🟢 Connecté");
            connectionLabel.setForeground(Color.decode("#10B981"));
        } else {
            connectionLabel.setText("🟡 Mode hors ligne");
            connectionLabel.setForeground(Color.decode("#F97316"));
        }
    }
    
    private void startTimeUpdater() {
        timeTimer = new Timer(1000, e -> updateTime());
        timeTimer.start();
        updateTime(); // Mise à jour immédiate
    }
    
    private void updateTime() {
        LocalDateTime now = LocalDateTime.now();
        String timeText = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        timeLabel.setText(timeText);
    }
    
    public void updateStatus() {
        updateModeLabel();
        updateConnectionStatus();
    }
} 
