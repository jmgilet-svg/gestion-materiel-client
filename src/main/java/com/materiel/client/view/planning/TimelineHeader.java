package com.materiel.client.view.planning;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import javax.swing.JComponent;

import com.materiel.client.view.planning.layout.TimeGridModel;
import com.materiel.client.view.ui.UIConstants;

/** Header displaying day columns aligned with the planning grid. */
public final class TimelineHeader extends JComponent {
    private final TimeGridModel grid;
    private LocalDate weekStart;
    private static final int HEADER_HEIGHT = 32;
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM");

    public TimelineHeader(TimeGridModel grid, LocalDate weekStart) {
        this.grid = grid;
        this.weekStart = weekStart.with(DayOfWeek.MONDAY);
        setPreferredSize(new Dimension(grid.getLeftGutterWidth()+grid.getContentWidth(), HEADER_HEIGHT));
        setFont(new Font("Inter", Font.BOLD, 12));
        setOpaque(true);
    }

    public void setWeek(LocalDate ws) {
        this.weekStart = ws.with(DayOfWeek.MONDAY);
        revalidate(); repaint();
    }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // fond
        g.setColor(new Color(0xF3F6F9));
        g.fillRect(0, 0, getWidth(), getHeight());

        // cellule vide au-dessus de la gouttière "Ressources"
        g.setColor(new Color(0xE0E6EB));
        g.drawRect(0, 0, UIConstants.LEFT_GUTTER_WIDTH - 1, getHeight() - 1);

        // colonnes (mêmes X que la grille)
        int[] xs = grid.getDayColumnXs(weekStart);
        g.setColor(new Color(0xE0E6EB));
        for (int x : xs) g.drawLine(x, 0, x, getHeight());

        // libellés
        g.setColor(new Color(0x1A1F36));
        for (int d = 0; d < 7; d++) {
            int x1 = xs[d];
            int x2 = xs[d + 1];
            LocalDate day = weekStart.plusDays(d);
            String label = day.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRANCE) + " " + DF.format(day);
            drawCentered(g, label, x1, x2, getHeight());
        }
        g.dispose();
    }

    private static void drawCentered(Graphics2D g, String text, int x1, int x2, int h) {
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(text);
        int x = x1 + Math.max(0, (x2 - x1 - w) / 2);
        int y = (h + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(text, x, y);
    }
}
