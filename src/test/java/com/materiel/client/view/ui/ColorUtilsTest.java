package com.materiel.client.view.ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.awt.Color;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour ColorUtils et correction des erreurs de couleur
 */
@DisplayName("Tests ColorUtils - Correction NumberFormatException")
class ColorUtilsTest {
    
    @Test
    @DisplayName("Devrait créer une couleur avec transparence sans erreur")
    void shouldCreateColorWithAlphaWithoutError() {
        // Given - Couleurs utilisées dans l'application
        String[] colors = {
            "#3B82F6", // Bleu principal
            "#F97316", // Orange secondaire
            "#10B981", // Vert succès
            "#EF4444", // Rouge erreur
            "#8B5CF6", // Violet accent
            "#64748B"  // Gris neutre
        };
        
        // When & Then - Aucune exception ne doit être levée
        for (String color : colors) {
            assertDoesNotThrow(() -> {
                Color result = ColorUtils.createColorWithAlpha(color, 51);
                assertNotNull(result);
                assertEquals(51, result.getAlpha());
            }, "Erreur pour la couleur: " + color);
        }
    }
    
    @Test
    @DisplayName("Devrait créer une couleur transparente légère")
    void shouldCreateLightTransparentColor() {
        // Given
        String blueColor = "#3B82F6";
        
        // When
        Color result = ColorUtils.createLightTransparentColor(blueColor);
        
        // Then
        assertNotNull(result);
        assertEquals(51, result.getAlpha()); // 20% de 255
        assertEquals(59, result.getRed());   // 0x3B = 59
        assertEquals(130, result.getGreen()); // 0x82 = 130  
        assertEquals(246, result.getBlue());  // 0xF6 = 246
    }
    
    @Test
    @DisplayName("Devrait gérer les couleurs invalides sans crash")
    void shouldHandleInvalidColorsGracefully() {
        // Given - Couleurs invalides
        String[] invalidColors = {
            "#F9731620", // Problématique - 8 caractères
            "#GGGGGG",   // Caractères non hex
            "",          // Vide
            null,        // null
            "#12345",    // Trop court
            "#1234567890" // Trop long
        };
        
        // When & Then - Aucune exception, couleur par défaut retournée
        for (String invalidColor : invalidColors) {
            assertDoesNotThrow(() -> {
                Color result = ColorUtils.createLightTransparentColor(invalidColor);
                assertNotNull(result);
                assertEquals(51, result.getAlpha());
                // Couleur par défaut grise attendue
                assertEquals(128, result.getRed());
                assertEquals(128, result.getGreen());
                assertEquals(128, result.getBlue());
            }, "Erreur pour la couleur invalide: " + invalidColor);
        }
    }
    
    @Test
    @DisplayName("Devrait valider correctement les codes hex")
    void shouldValidateHexColorsCorrectly() {
        // Valid colors
        assertTrue(ColorUtils.isValidHexColor("#3B82F6"));
        assertTrue(ColorUtils.isValidHexColor("#000000"));
        assertTrue(ColorUtils.isValidHexColor("#FFFFFF"));
        
        // Invalid colors  
        assertFalse(ColorUtils.isValidHexColor("#F9731620")); // Trop long
        assertFalse(ColorUtils.isValidHexColor("#GGGGGG"));   // Caractères invalides
        assertFalse(ColorUtils.isValidHexColor(""));          // Vide
        assertFalse(ColorUtils.isValidHexColor(null));        // null
    }
    
    @Test
    @DisplayName("Devrait éclaircir et assombrir les couleurs")
    void shouldLightenAndDarkenColors() {
        // Given
        Color red = Color.RED; // RGB(255, 0, 0)
        
        // When
        Color lighter = ColorUtils.lighten(red, 0.5f);
        Color darker = ColorUtils.darken(red, 0.5f);
        
        // Then
        assertTrue(lighter.getRed() > red.getRed());
        assertTrue(darker.getRed() < red.getRed());
        assertEquals(255, lighter.getAlpha());
        assertEquals(255, darker.getAlpha());
    }
    
    @Test
    @DisplayName("Devrait déterminer la luminance correctement")
    void shouldDetermineLuminanceCorrectly() {
        // Given
        Color white = Color.WHITE;
        Color black = Color.BLACK;
        Color blue = Color.BLUE;
        
        // When & Then
        assertTrue(ColorUtils.isLightColor(white));
        assertFalse(ColorUtils.isLightColor(black));
        assertFalse(ColorUtils.isLightColor(blue)); // Bleu est considéré comme sombre
    }
    
    @Test
    @DisplayName("Devrait retourner des couleurs de texte contrastées")
    void shouldReturnContrastingTextColors() {
        // Given
        Color lightBackground = Color.WHITE;
        Color darkBackground = Color.BLACK;
        
        // When
        Color textForLight = ColorUtils.getContrastingTextColor(lightBackground);
        Color textForDark = ColorUtils.getContrastingTextColor(darkBackground);
        
        // Then
        assertEquals(Color.BLACK, textForLight);
        assertEquals(Color.WHITE, textForDark);
    }
    
    @Test
    @DisplayName("Devrait convertir les couleurs en hex")
    void shouldConvertColorsToHex() {
        // Given
        Color red = Color.RED;
        Color blue = new Color(59, 130, 246); // #3B82F6
        
        // When
        String redHex = ColorUtils.toHexString(red);
        String blueHex = ColorUtils.toHexString(blue);
        
        // Then
        assertEquals("#FF0000", redHex);
        assertEquals("#3B82F6", blueHex);
    }
    
    /**
     * Test spécifique pour reproduire l'erreur originale
     */
    @Test
    @DisplayName("Ne devrait plus lever NumberFormatException avec l'ancien code")
    void shouldNotThrowNumberFormatExceptionWithOldCode() {
        // Given - Simule l'ancien code problématique
        String colorWithAlpha = "#F97316" + "20"; // Problématique
        
        // When & Then - L'ancienne méthode aurait levé une exception
        assertThrows(NumberFormatException.class, () -> {
            Color.decode(colorWithAlpha); // Ancienne méthode problématique
        });
        
        // Mais la nouvelle méthode fonctionne
        assertDoesNotThrow(() -> {
            Color result = ColorUtils.createLightTransparentColor("#F97316");
            assertNotNull(result);
        });
    }
}