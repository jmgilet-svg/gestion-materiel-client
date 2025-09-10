package com.materiel.client.view.util;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

/** Utility text helpers for UI components. */
public final class TextUtils {
    private TextUtils() {
    }

    /**
     * Ellipse the given text so that its rendered width does not exceed the provided maximum.
     *
     * @param g        graphics context used for measuring
     * @param text     original text
     * @param maxWidth maximum width in pixels
     * @return possibly shortened text with an ellipsis
     */
    public static String elide(Graphics2D g, String text, int maxWidth) {
        if (text == null) {
            return "";
        }
        FontMetrics fm = g.getFontMetrics();
        if (fm.stringWidth(text) <= maxWidth) {
            return text;
        }
        String ellipsis = "...";
        int ellipsisWidth = fm.stringWidth(ellipsis);
        int end = text.length();
        while (end > 0 && fm.stringWidth(text.substring(0, end)) + ellipsisWidth > maxWidth) {
            end--;
        }
        return text.substring(0, Math.max(0, end)) + ellipsis;
    }
}
