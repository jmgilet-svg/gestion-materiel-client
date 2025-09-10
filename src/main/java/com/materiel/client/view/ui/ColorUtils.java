package com.materiel.client.view.ui;

import java.awt.Color;

/** Utility class for color operations used in planning UI. */
public final class ColorUtils {
    private ColorUtils() {
    }

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
}
