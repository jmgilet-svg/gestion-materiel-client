package com.materiel.client;

import com.materiel.client.config.AppConfig;
import com.materiel.client.config.DataMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de démarrage de l'application
 */
@DisplayName("Tests Application")
class GestionMaterielAppTest {
    
    @BeforeEach
    void setUp() {
        // Réinitialiser la configuration avant chaque test
        AppConfig.getInstance().setDataMode(DataMode.MOCK_JSON);
    }
    
    @Test
    @DisplayName("L'application devrait avoir une configuration par défaut")
    void shouldHaveDefaultConfiguration() {
        // When
        AppConfig config = AppConfig.getInstance();
        
        // Then
        assertNotNull(config);
        assertNotNull(config.getDataMode());
        assertNotNull(config.getApiBaseUrl());
        
        // Vérifier les valeurs par défaut
        assertEquals("http://localhost:8080", config.getApiBaseUrl());
    }
    
    @Test
    @DisplayName("Devrait pouvoir changer le mode de données")
    void shouldBeAbleToChangeDataMode() {
        // Given
        AppConfig config = AppConfig.getInstance();
        DataMode originalMode = config.getDataMode();
        
        // When
        DataMode newMode = originalMode == DataMode.MOCK_JSON ? DataMode.BACKEND_API : DataMode.MOCK_JSON;
        config.setDataMode(newMode);
        
        // Then
        assertEquals(newMode, config.getDataMode());
        assertNotEquals(originalMode, config.getDataMode());
    }
    
    @Test
    @DisplayName("Devrait pouvoir changer l'URL de l'API")
    void shouldBeAbleToChangeApiUrl() {
        // Given
        AppConfig config = AppConfig.getInstance();
        String originalUrl = config.getApiBaseUrl();
        String newUrl = "http://test.example.com:9090";
        
        // When
        config.setApiBaseUrl(newUrl);
        
        // Then
        assertEquals(newUrl, config.getApiBaseUrl());
        assertNotEquals(originalUrl, config.getApiBaseUrl());
    }
    
    @Test
    @DisplayName("Devrait identifier correctement le mode Backend")
    void shouldIdentifyBackendModeCorrectly() {
        // Given
        AppConfig config = AppConfig.getInstance();
        
        // When
        config.setDataMode(DataMode.BACKEND_API);
        
        // Then
        assertTrue(config.isBackendMode());
        assertFalse(config.isMockMode());
    }
    
    @Test
    @DisplayName("Devrait identifier correctement le mode Mock")
    void shouldIdentifyMockModeCorrectly() {
        // Given
        AppConfig config = AppConfig.getInstance();
        
        // When
        config.setDataMode(DataMode.MOCK_JSON);
        
        // Then
        assertTrue(config.isMockMode());
        assertFalse(config.isBackendMode());
    }
    
    @Test
    @DisplayName("Les modes de données devraient avoir des noms d'affichage")
    void dataModeShouldHaveDisplayNames() {
        // When & Then
        assertEquals("Backend API", DataMode.BACKEND_API.getDisplayName());
        assertEquals("Mock JSON", DataMode.MOCK_JSON.getDisplayName());
    }
} 
