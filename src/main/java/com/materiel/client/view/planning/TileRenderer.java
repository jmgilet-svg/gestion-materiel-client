package com.materiel.client.view.planning;

import com.materiel.client.view.ui.ColorUtils;
import com.materiel.client.view.ui.ColorUtils.TileColors;
import com.materiel.client.view.ui.UIConstants;

import java.awt.*;

/** Painter used to render intervention tiles. */
public final class TileRenderer {
    private TileRenderer() {
    }

    public static void paint(Graphics2D g2, Rectangle bounds, Color baseColor, String text) {
        TileColors colors = ColorUtils.deriveTileColors(baseColor);
        g2.setColor(colors.background());
        g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g2.setColor(colors.border());
        g2.setStroke(new BasicStroke(UIConstants.TILE_BORDER));
        g2.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g2.setColor(colors.text());
        FontMetrics fm = g2.getFontMetrics();
        int handle = 8; // top/bottom resize handle height
        int tx = bounds.x + UIConstants.TILE_PADDING;
        int ty = bounds.y + handle + fm.getAscent() + UIConstants.TILE_PADDING;
        int maxWidth = bounds.width - 2 * UIConstants.TILE_PADDING;
        String clipped = ellipsize(text, fm, maxWidth);
        g2.drawString(clipped, tx, ty);
    }

    private static String ellipsize(String text, FontMetrics fm, int maxWidth) {
        if (fm.stringWidth(text) <= maxWidth) {
            return text;
        }
        String ellipsis = "\u2026"; // single character ellipsis
        int ellipsisWidth = fm.stringWidth(ellipsis);
        int end = text.length();
        while (end > 0 && fm.stringWidth(text.substring(0, end)) + ellipsisWidth > maxWidth) {
            end--;
        }
        return text.substring(0, Math.max(end, 0)) + ellipsis;
    }
}
