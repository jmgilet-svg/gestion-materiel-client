package com.materiel.client.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.materiel.client.config.AppConfig;
import com.materiel.client.controller.EventBus;
import com.materiel.client.controller.events.MenuSelectionEvent;
import com.materiel.client.view.components.SideMenuPanel;
import com.materiel.client.view.components.StatusBarPanel;
import com.materiel.client.view.devis.DevisListPanel;
import com.materiel.client.view.planning.PlanningPanel;

/**
 * Fenêtre principale de l'application
 */
public class MainFrame extends JFrame {
    
    private SideMenuPanel sideMenuPanel;
    private JPanel contentPanel;
    private StatusBarPanel statusBarPanel;
    
    // Panels de contenu
    private PlanningPanel planningPanel;
    private DevisListPanel devisListPanel;
    // TODO: Ajouter autres panels (Commandes, BL, Factures, etc.)
    
    public MainFrame() {
        initComponents();
        setupFrame();
        setupEventListeners();
        
        // Afficher le planning par défaut
        showPlanningPanel();
    }
    
    private void initComponents() {
        setTitle("Gestion de Matériel - " + AppConfig.getInstance().getDataMode().getDisplayName());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // Layout principal
        setLayout(new BorderLayout());
        
        // Menu latéral
        sideMenuPanel = new SideMenuPanel();
        add(sideMenuPanel, BorderLayout.WEST);
        
        // Zone de contenu central
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(Color.decode("#F8FAFC"));
        add(contentPanel, BorderLayout.CENTER);
        
        // Barre de statut
        statusBarPanel = new StatusBarPanel();
        add(statusBarPanel, BorderLayout.SOUTH);
        
        // Initialiser les panels de contenu
        initContentPanels();
    }
    
    private void initContentPanels() {
        planningPanel = new PlanningPanel();
        devisListPanel = new DevisListPanel();
        
        contentPanel.add(planningPanel, "PLANNING");
        contentPanel.add(devisListPanel, "DEVIS");
        // TODO: Ajouter autres panels
    }
    
    private void setupFrame() {
        // Taille et position
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Icône de l'application
        setIconImage(createAppIcon());
        
        // Fermeture propre
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }
    
    private void setupEventListeners() {
        // Écouter les événements de sélection du menu
        EventBus.getInstance().subscribe(MenuSelectionEvent.class, event -> {
            SwingUtilities.invokeLater(() -> showPanel(event.getSelectedMenu()));
        });
    }
    
    private void showPanel(String panelName) {
        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        
        switch (panelName) {
            case "PLANNING":
                showPlanningPanel();
                break;
            case "DEVIS":
                cardLayout.show(contentPanel, "DEVIS");
                devisListPanel.refreshData();
                break;
            case "COMMANDES":
                // TODO: Implémenter
                break;
            case "BONS_LIVRAISON":
                // TODO: Implémenter
                break;
            case "FACTURES":
                // TODO: Implémenter
                break;
            case "CLIENTS":
                // TODO: Implémenter
                break;
            case "RESSOURCES":
                // TODO: Implémenter
                break;
            default:
                showPlanningPanel();
        }
    }
    
    private void showPlanningPanel() {
        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        cardLayout.show(contentPanel, "PLANNING");
        planningPanel.refreshPlanning();
    }
    
    private Image createAppIcon() {
        // Créer une icône simple pour l'application
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = icon.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fond bleu
        g2.setColor(Color.decode("#3B82F6"));
        g2.fillRoundRect(2, 2, 28, 28, 8, 8);
        
        // Icône grue (simplifié)
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(8, 24, 8, 8);
        g2.drawLine(8, 8, 24, 8);
        g2.drawLine(20, 8, 20, 16);
        
        g2.dispose();
        return icon;
    }
    
    private void exitApplication() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Voulez-vous vraiment quitter l'application ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // Sauvegarder la configuration
            AppConfig.getInstance().saveConfiguration();
            
            // Nettoyer les ressources
            dispose();
            System.exit(0);
        }
    }
}
 
