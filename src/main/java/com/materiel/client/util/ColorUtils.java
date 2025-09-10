package com.materiel.client.util;

import java.awt.Color;

/**
 * Utilitaires pour la gestion des couleurs
 */
public class ColorUtils {
    
    /**
     * Crée une couleur avec transparence à partir d'un code hex
     * @param hexColor Code couleur hex (ex: "#3B82F6")
     * @param alpha Valeur alpha entre 0 et 255 (0 = transparent, 255 = opaque)
     * @return Couleur avec transparence
     */
    public static Color createColorWithAlpha(String hexColor, int alpha) {
        try {
            Color baseColor = Color.decode(hexColor);
            return new Color(
                baseColor.getRed(),
                baseColor.getGreen(), 
                baseColor.getBlue(),
                Math.max(0, Math.min(255, alpha))
            );
        } catch (Exception e) {
            // Couleur par défaut en cas d'erreur
            return new Color(128, 128, 128, alpha);
        }
    }
    
    /**
     * Crée une couleur avec transparence faible (20%)
     * @param hexColor Code couleur hex
     * @return Couleur avec 20% d'opacité
     */
    public static Color createLightTransparentColor(String hexColor) {
        return createColorWithAlpha(hexColor, 51); // 20% de 255 ≈ 51
    }
    
    /**
     * Crée une couleur avec transparence moyenne (50%)
     * @param hexColor Code couleur hex
     * @return Couleur avec 50% d'opacité
     */
    public static Color createMediumTransparentColor(String hexColor) {
        return createColorWithAlpha(hexColor, 128); // 50% de 255 = 128
    }
    
    /**
     * Éclaircit une couleur
     * @param color Couleur de base
     * @param factor Facteur d'éclaircissement (0.0 à 1.0)
     * @return Couleur éclaircie
     */
    public static Color lighten(Color color, float factor) {
        int r = Math.min(255, (int) (color.getRed() + (255 - color.getRed()) * factor));
        int g = Math.min(255, (int) (color.getGreen() + (255 - color.getGreen()) * factor));
        int b = Math.min(255, (int) (color.getBlue() + (255 - color.getBlue()) * factor));
        return new Color(r, g, b, color.getAlpha());
    }
    
    /**
     * Assombrit une couleur
     * @param color Couleur de base
     * @param factor Facteur d'assombrissement (0.0 à 1.0)
     * @return Couleur assombrie
     */
    public static Color darken(Color color, float factor) {
        int r = Math.max(0, (int) (color.getRed() * (1 - factor)));
        int g = Math.max(0, (int) (color.getGreen() * (1 - factor)));
        int b = Math.max(0, (int) (color.getBlue() * (1 - factor)));
        return new Color(r, g, b, color.getAlpha());
    }
    
    /**
     * Vérifie si un code couleur hex est valide
     * @param hexColor Code couleur à vérifier
     * @return true si valide, false sinon
     */
    public static boolean isValidHexColor(String hexColor) {
        try {
            Color.decode(hexColor);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Convertit une couleur en code hex
     * @param color Couleur à convertir
     * @return Code hex (ex: "#3B82F6")
     */
    public static String toHexString(Color color) {
        return String.format("#%02X%02X%02X", 
            color.getRed(), 
            color.getGreen(), 
            color.getBlue());
    }
    
    /**
     * Détermine si une couleur est claire ou sombre
     * @param color Couleur à analyser
     * @return true si claire, false si sombre
     */
    public static boolean isLightColor(Color color) {
        // Calcul de la luminance selon la formule standard
        double luminance = (0.299 * color.getRed() + 
                           0.587 * color.getGreen() + 
                           0.114 * color.getBlue()) / 255;
        return luminance > 0.5;
    }
    
    /**
     * Retourne une couleur de texte contrastée (noir ou blanc)
     * @param backgroundColor Couleur de fond
     * @return Couleur de texte optimale
     */
    public static Color getContrastingTextColor(Color backgroundColor) {
        return isLightColor(backgroundColor) ? Color.BLACK : Color.WHITE;
    }
}