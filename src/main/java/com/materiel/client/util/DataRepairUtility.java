package com.materiel.client.util;

import com.materiel.client.mock.MockDataManager;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utilitaire pour réparer les données corrompues
 */
public class DataRepairUtility {
    
    /**
     * Répare les données JSON corrompues
     */
    public static void repairCorruptedData() {
        MockDataManager dataManager = MockDataManager.getInstance();
        Path dataDirectory = dataManager.getDataDirectory();
        
        try {
            // Vérifier si le fichier interventions.json existe et est corrompu
            Path interventionsFile = dataDirectory.resolve("interventions.json");
            
            if (Files.exists(interventionsFile)) {
                // Lire le contenu pour vérifier s'il contient les champs problématiques
                String content = Files.readString(interventionsFile);
                
                if (content.contains("\"heureDebut\"") || content.contains("\"heureFin\"")) {
                    // Le fichier est corrompu, le supprimer
                    Files.delete(interventionsFile);
                    System.out.println("Fichier interventions.json corrompu supprimé");
                }
            }
            
            // Vérifier et nettoyer les autres fichiers si nécessaire
            cleanOtherFiles(dataDirectory);
            
            System.out.println("Nettoyage des données terminé");
            
        } catch (IOException e) {
            System.err.println("Erreur lors de la réparation des données: " + e.getMessage());
        }
    }
    
    private static void cleanOtherFiles(Path dataDirectory) throws IOException {
        // Nettoyer d'autres fichiers potentiellement corrompus
        String[] filesToCheck = {"resources.json", "clients.json", "devis.json"};
        
        for (String filename : filesToCheck) {
            Path file = dataDirectory.resolve(filename);
            if (Files.exists(file)) {
                String content = Files.readString(file);
                
                // Vérifier des patterns de corruption spécifiques
                if (content.trim().isEmpty() || content.equals("null") || content.startsWith("{\"error\"")) {
                    Files.delete(file);
                    System.out.println("Fichier corrompu supprimé: " + filename);
                }
            }
        }
    }
    
    /**
     * Dialogue pour proposer la réparation à l'utilisateur
     */
    public static boolean askUserForRepair() {
        int result = JOptionPane.showConfirmDialog(
            null,
            "Des données corrompues ont été détectées.\n\n" +
            "Voulez-vous les nettoyer et redémarrer avec des données par défaut ?\n" +
            "(Cela supprimera les données actuelles mais conservera la structure)",
            "Réparation des données",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Réinitialise complètement les données
     */
    public static void resetAllData() {
        try {
            MockDataManager dataManager = MockDataManager.getInstance();
            dataManager.resetAllData();
            
            JOptionPane.showMessageDialog(
                null,
                "✅ Données réinitialisées avec succès !\n\n" +
                "L'application va redémarrer avec des données par défaut.",
                "Réparation terminée",
                JOptionPane.INFORMATION_MESSAGE
            );
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                "❌ Erreur lors de la réinitialisation:\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Crée une sauvegarde des données avant réparation
     */
    public static void backupDataBeforeRepair() {
        try {
            MockDataManager dataManager = MockDataManager.getInstance();
            dataManager.backupAllData();
            System.out.println("Sauvegarde créée avant réparation");
        } catch (Exception e) {
            System.err.println("Impossible de créer une sauvegarde: " + e.getMessage());
        }
    }
}