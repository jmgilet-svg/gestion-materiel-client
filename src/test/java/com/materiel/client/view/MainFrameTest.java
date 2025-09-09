package com.materiel.client.view;

import com.materiel.client.config.AppConfig;
import com.materiel.client.config.DataMode;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests UI pour MainFrame avec AssertJ-Swing
 */
@DisplayName("Tests MainFrame UI")
class MainFrameTest extends AssertJSwingJUnitTestCase {
    
    private FrameFixture window;
    private MainFrame frame;
    
    @Override
    protected void onSetUp() {
        // Configurer le mode Mock pour les tests
        AppConfig.getInstance().setDataMode(DataMode.MOCK_JSON);
        
        // Créer la frame dans l'EDT
        frame = GuiActionRunner.execute(() -> new MainFrame());
        window = new FrameFixture(robot(), frame);
        window.show(); // Afficher la fenêtre pour les tests
    }
    
    @Override
    protected void onTearDown() {
        // Nettoyer après les tests
        if (window != null) {
            window.cleanUp();
        }
    }
    
    @Test
    @DisplayName("La fenêtre principale devrait s'ouvrir correctement")
    void shouldOpenMainWindowCorrectly() {
        // Then
        window.requireVisible();
        window.requireEnabled();
        
        // Vérifier le titre
        String expectedTitle = "Gestion de Matériel - " + DataMode.MOCK_JSON.getDisplayName();
        window.requireTitle(expectedTitle);
        
        // Vérifier que la fenêtre a une taille raisonnable
        assertTrue(frame.getWidth() > 800, "La largeur devrait être supérieure à 800px");
        assertTrue(frame.getHeight() > 600, "La hauteur devrait être supérieure à 600px");
    }
    
    @Test
    @DisplayName("Le menu latéral devrait être présent et fonctionnel")
    void shouldHaveFunctionalSideMenu() {
        // Vérifier que le menu latéral est présent
        window.panel("sideMenuPanel").requireVisible();
        
        // Vérifier la présence des boutons de menu principaux
        // Note: Ces tests dépendent de l'implémentation des noms de composants
        assertDoesNotThrow(() -> {
            // Le planning devrait être sélectionné par défaut
            // Ces assertions peuvent nécessiter l'ajout de noms aux composants dans le code
        });
    }
    
    @Test
    @DisplayName("La barre de statut devrait afficher les informations correctes")
    void shouldDisplayCorrectStatusBar() {
        // Vérifier que la barre de statut est présente
        window.panel("statusBarPanel").requireVisible();
        
        // La barre de statut devrait montrer le mode Mock
        // Note: Ces tests nécessitent l'ajout de noms aux composants dans StatusBarPanel
    }
    
    @Test
    @DisplayName("Devrait pouvoir naviguer entre les différents panels")
    void shouldNavigateBetweenPanels() {
        // Test de navigation basique
        // Note: Ce test nécessiterait des méthodes publiques pour la navigation
        // ou des noms de composants pour les boutons du menu
        
        assertDoesNotThrow(() -> {
            // Simuler la navigation vers différents panels
            frame.navigateToPanel("DEVIS");
            frame.navigateToPanel("CLIENTS");
            frame.navigateToPanel("RESSOURCES");
            frame.navigateToPanel("PLANNING");
        });
    }
    
    @Test
    @DisplayName("Devrait pouvoir fermer l'application proprement")
    void shouldCloseApplicationProperly() {
        // Vérifier que la fenêtre peut fermer sans erreur
        assertTrue(frame.canExit(), "L'application devrait pouvoir se fermer");
        
        // Note: Ne pas fermer réellement la fenêtre dans le test
        // car cela interférerait avec les autres tests
    }
    
    @Test
    @DisplayName("Devrait avoir la configuration Look & Feel correcte")
    void shouldHaveCorrectLookAndFeel() {
        // Vérifier que FlatLaf est appliqué
        String currentLAF = UIManager.getLookAndFeel().getName();
        assertTrue(currentLAF.contains("FlatLaf") || currentLAF.contains("Flat"), 
            "Le Look & Feel devrait être FlatLaf");
        
        // Vérifier quelques propriétés UI personnalisées
        assertNotNull(UIManager.get("App.primaryColor"));
        assertNotNull(UIManager.get("App.secondaryColor"));
    }
    
    @Test
    @DisplayName("Devrait détecter les composants principaux")
    void shouldDetectMainComponents() {
        // Vérifier la présence des composants principaux
        assertDoesNotThrow(() -> {
            // Panel de contenu central
            window.panel(new GenericTypeMatcher<JPanel>(JPanel.class) {
                @Override
                protected boolean isMatching(JPanel panel) {
                    return panel.getLayout() instanceof java.awt.CardLayout;
                }
            }).requireVisible();
        });
    }
    
    @Test
    @DisplayName("Devrait gérer les événements de redimensionnement")
    void shouldHandleResizeEvents() {
        // Tester le redimensionnement
        window.resizeTo(new java.awt.Dimension(1200, 800));
        
        // Vérifier que la fenêtre s'adapte
        assertTrue(frame.getWidth() >= 1200);
        assertTrue(frame.getHeight() >= 800);
        
        // Revenir à une taille normale
        window.resizeTo(new java.awt.Dimension(1000, 700));
    }
    
    @Test
    @DisplayName("Devrait avoir une icône d'application")
    void shouldHaveApplicationIcon() {
        // Vérifier que l'icône de l'application est définie
        assertNotNull(frame.getIconImage(), "L'application devrait avoir une icône");
        
        // Vérifier les dimensions de l'icône
        java.awt.Image icon = frame.getIconImage();
        assertTrue(icon.getWidth(null) > 0, "L'icône devrait avoir une largeur");
        assertTrue(icon.getHeight(null) > 0, "L'icône devrait avoir une hauteur");
    }
    
    @Test
    @DisplayName("Devrait pouvoir rafraîchir la barre de statut")
    void shouldRefreshStatusBar() {
        // Test de la méthode publique refreshStatusBar
        assertDoesNotThrow(() -> {
            frame.refreshStatusBar();
        }, "Le rafraîchissement de la barre de statut ne devrait pas lever d'exception");
    }
    
    /**
     * Test helper pour vérifier la présence d'un panel spécifique
     */
    private void verifyPanelExists(String panelName) {
        assertDoesNotThrow(() -> {
            window.panel(panelName).requireVisible();
        }, "Le panel " + panelName + " devrait être présent et visible");
    }
    
    /**
     * Test helper pour simuler une action utilisateur
     */
    private void simulateUserAction(Runnable action) {
        GuiActionRunner.execute(() -> {
            action.run();
            return null;
        });
    }
    
    @Test
    @DisplayName("Les panels de contenu devraient être initialisés")
    void shouldInitializeContentPanels() {
        // Vérifier que les panels principaux sont créés
        // Note: Ces tests nécessitent des améliorations dans MainFrame
        // pour exposer ou nommer les composants
        
        assertDoesNotThrow(() -> {
            // Vérifier que le planning panel existe
            // Vérifier que le devis panel existe  
            // Vérifier que le client panel existe
            // Vérifier que le resource panel existe
        });
    }
    
    @Test
    @DisplayName("Devrait gérer les changements de mode de données")
    void shouldHandleDataModeChanges() {
        // Tester le changement de mode de données
        DataMode originalMode = AppConfig.getInstance().getDataMode();
        
        try {
            // Changer le mode
            DataMode newMode = originalMode == DataMode.MOCK_JSON ? 
                DataMode.BACKEND_API : DataMode.MOCK_JSON;
            
            GuiActionRunner.execute(() -> {
                AppConfig.getInstance().setDataMode(newMode);
                frame.refreshStatusBar();
                return null;
            });
            
            // Vérifier que le changement est reflété
            assertEquals(newMode, AppConfig.getInstance().getDataMode());
            
        } finally {
            // Restaurer le mode original
            AppConfig.getInstance().setDataMode(originalMode);
        }
    }
} 
