package com.materiel.client;

import com.formdev.flatlaf.FlatLightLaf;
import com.materiel.client.config.AppConfig;
import com.materiel.client.config.DataMode;
import com.materiel.client.view.MainFrame;
import com.materiel.client.view.StartupDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Application principale de gestion de matériel
 * Architecture MVC avec support API Backend / Mock JSON
 */
public class GestionMaterielApp {
    
    public static void main(String[] args) {
        System.out.println("FIX_WRAP_AND_ALIGN_APPLIED");
        // Configuration du Look & Feel FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            configureUIDefaults();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        SwingUtilities.invokeLater(() -> {
            // Affichage du dialogue de démarrage pour choisir le mode
            StartupDialog startupDialog = new StartupDialog();
            startupDialog.setVisible(true);
            
            if (!startupDialog.isConfirmed()) {
                System.exit(0);
            }
            
            // Configuration de l'application selon le choix utilisateur
            DataMode selectedMode = startupDialog.getSelectedMode();
            AppConfig.getInstance().setDataMode(selectedMode);
            
            // Lancement de l'application principale
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
    
    /**
     * Configuration des propriétés UI globales
     */
    private static void configureUIDefaults() {
        // Couleurs personnalisées
        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.alternateRowColor", Color.decode("#F8FAFC"));
        
        // Police système
        Font systemFont = new Font("Segoe UI", Font.PLAIN, 12);
        UIManager.put("defaultFont", systemFont);
        
        // Couleurs de l'application
        UIManager.put("App.primaryColor", Color.decode("#3B82F6"));
        UIManager.put("App.secondaryColor", Color.decode("#F97316"));
        UIManager.put("App.successColor", Color.decode("#10B981"));
        UIManager.put("App.accentColor", Color.decode("#8B5CF6"));
    }
}

