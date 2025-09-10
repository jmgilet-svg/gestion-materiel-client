package com.materiel.client.view.ui;

import java.awt.Color;

/** Utility class for color operations used in the UI. */
public final class ColorUtils {
    private ColorUtils() {}

    /** Holder for derived tile colors. */
    public record TileColors(Color background, Color border, Color text) {}

    /**
     * Derive a readable palette from the base color ensuring contrast for text
     * and a visible border.
     */
    public static TileColors deriveTileColors(Color base) {
        float[] hsb = Color.RGBtoHSB(base.getRed(), base.getGreen(), base.getBlue(), null);
        Color background = Color.getHSBColor(hsb[0], Math.max(0f, hsb[1] * 0.1f), Math.min(1f, hsb[2] * 1.2f));
        Color border = Color.getHSBColor(hsb[0], hsb[1], Math.max(0f, hsb[2] * 0.8f));
        int luminance = (int) (0.299 * background.getRed() + 0.587 * background.getGreen() + 0.114 * background.getBlue());
        Color text = luminance > 140 ? Color.BLACK : Color.WHITE;
        return new TileColors(background, border, text);
    }

    /** Create a color with the given alpha from a hex string. */
    public static Color createColorWithAlpha(String hexColor, int alpha) {
        try {
            Color base = Color.decode(hexColor);
            return new Color(base.getRed(), base.getGreen(), base.getBlue(), Math.max(0, Math.min(255, alpha)));
        } catch (Exception e) {
            return new Color(128, 128, 128, Math.max(0, Math.min(255, alpha)));
        }
    }

    /** Create a light transparent color (~20% opacity) from a hex string. */
    public static Color createLightTransparentColor(String hexColor) {
        return createColorWithAlpha(hexColor, 51);
    }

    /** Create a medium transparent color (~50% opacity) from a hex string. */
    public static Color createMediumTransparentColor(String hexColor) {
        return createColorWithAlpha(hexColor, 128);
    }

    /** Lighten a color by the given factor (0-1). */
    public static Color lighten(Color color, float factor) {
        int r = Math.min(255, (int) (color.getRed() + (255 - color.getRed()) * factor));
        int g = Math.min(255, (int) (color.getGreen() + (255 - color.getGreen()) * factor));
        int b = Math.min(255, (int) (color.getBlue() + (255 - color.getBlue()) * factor));
        return new Color(r, g, b, color.getAlpha());
    }

    /** Darken a color by the given factor (0-1). */
    public static Color darken(Color color, float factor) {
        int r = Math.max(0, (int) (color.getRed() * (1 - factor)));
        int g = Math.max(0, (int) (color.getGreen() * (1 - factor)));
        int b = Math.max(0, (int) (color.getBlue() * (1 - factor)));
        return new Color(r, g, b, color.getAlpha());
    }

    /** Validate a hex color string. */
    public static boolean isValidHexColor(String hexColor) {
        try {
            Color.decode(hexColor);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Convert a color to its hex string representation. */
    public static String toHexString(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    /** Determine if a color is light or dark. */
    public static boolean isLightColor(Color color) {
        double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
        return luminance > 0.5;
    }

    /** Return black or white depending on background luminance. */
    public static Color getContrastingTextColor(Color background) {
        return isLightColor(background) ? Color.BLACK : Color.WHITE;
    }
}
