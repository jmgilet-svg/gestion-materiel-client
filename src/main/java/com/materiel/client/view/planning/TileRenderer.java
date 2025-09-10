package com.materiel.client.view.planning;

import com.materiel.client.view.ui.ColorUtils;
import com.materiel.client.view.ui.ColorUtils.TileColors;

import java.awt.*;

/** Painter used to render intervention tiles. */
public final class TileRenderer {
    private TileRenderer() {
    }

    public static void paint(Graphics2D g2, Rectangle bounds, Color baseColor, String text) {
        TileColors colors = ColorUtils.deriveTileColors(baseColor);
        g2.setColor(colors.background());
        g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height,
                UIConstants.TILE_RADIUS, UIConstants.TILE_RADIUS);
        g2.setColor(colors.border());
        g2.setStroke(new BasicStroke(UIConstants.TILE_BORDER_WIDTH));
        g2.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height,
                UIConstants.TILE_RADIUS, UIConstants.TILE_RADIUS);
        g2.setColor(colors.text());
        FontMetrics fm = g2.getFontMetrics();
        int tx = bounds.x + UIConstants.TILE_PADDING;
        int ty = bounds.y + fm.getAscent() + UIConstants.TILE_PADDING;
        g2.drawString(text, tx, ty);
    }
}
