package com.materiel.client.view.planning;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import com.materiel.client.model.Intervention;
import com.materiel.client.util.UIConstants;

import com.materiel.client.view.planning.DefaultTimeGridModel;
import com.materiel.client.view.planning.LaneLayout;
import com.materiel.client.view.planning.TimeGridModel;

/**
 * Interactive planning board handling zoom, selection and duplication logic.
 */
public final class PlanningBoard extends JComponent implements Scrollable {

    // ----------------- Modèle de grille (partagé avec le header) -----------------
    private final DefaultTimeGridModel gridModel;
    private LocalDate currentWeek;
    public TimeGridModel getTimeGridModel() { return gridModel; }
    public void setWeek(LocalDate week) { this.currentWeek = week.with(DayOfWeek.MONDAY); gridModel.setWeek(currentWeek); relayoutAll(); }
    public void setPxPerHour(int v) { gridModel.setPxPerHour(v); relayoutAll(); }

    // ----------------- Données d'affichage -----------------
    /** Ligne "ressource" affichée à gauche (nom + couleur). */
    public static final class RessourceRow {
        public final UUID id;
        public final String displayName;
        public final Color color;
        public RessourceRow(UUID id, String name, Color color) {
            this.id = id; this.displayName = name; this.color = (color != null ? color : new Color(0x6882C7));
        }
    }

    private List<RessourceRow> ressources = new ArrayList<>();
    private final Map<UUID, List<Intervention>> byResource = new LinkedHashMap<>();

    // layout calculé
    private static final class RowLayout {
        int y;
        int height;
        Map<Intervention, Rectangle> bounds = new LinkedHashMap<>();
        List<Intervention> z = new ArrayList<>();
    }
    private final Map<UUID, RowLayout> rowLayouts = new LinkedHashMap<>();
    private final Map<Intervention, Rectangle> tileBounds = new LinkedHashMap<>();
    private final List<Intervention> zOrder = new ArrayList<>();
    private int totalHeight = 400;

    // interaction
    private Intervention active = null;

    public PlanningBoard(LocalDate weekStart) {
        setOpaque(true);
        setBackground(Color.white);
        setFont(new Font("Inter", Font.PLAIN, 12));
        this.currentWeek = weekStart.with(DayOfWeek.MONDAY);
        this.gridModel = new DefaultTimeGridModel(currentWeek, 48);
        System.out.println("PLANNING_WRAP_ENABLED");

        // hit test simple pour sélection
        MouseAdapter ma = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                active = pickAt(e.getPoint()).orElse(null);
                repaint();
            }
        };
        addMouseListener(ma);
    }

    // ----------------- API de données -----------------
    public void setDonnees(List<RessourceRow> rows, Map<UUID, List<Intervention>> map) {
        this.ressources = rows != null ? new ArrayList<>(rows) : new ArrayList<>();
        this.byResource.clear();
        if (map != null) map.forEach((k,v) -> this.byResource.put(k, new ArrayList<>(v)));
        relayoutAll();
    }

    // ----------------- Layout global -----------------
    private void relayoutAll() {
        tileBounds.clear(); zOrder.clear(); rowLayouts.clear();

        int y = 0;
        // NB: pour un wrap "fort" dès 2 overlaps, mesure le wrap à l'échelle d'une ligne complète.
        // Si tu préfères le wrap par jour, adapte rowUsableWidth à 24h*pxPerHour.
        int rowUsableWidth = gridModel.getContentWidth();

        for (RessourceRow r : ressources) {
            List<Intervention> list = byResource.getOrDefault(r.id, Collections.emptyList());
            RowLayout rl = layoutOneRowWithWrap(list, rowUsableWidth, y);
            rowLayouts.put(r.id, rl);
            y += rl.height + 1;
            tileBounds.putAll(rl.bounds);
            zOrder.addAll(rl.z);
        }
        totalHeight = Math.max(400, y);
        revalidate(); repaint();
    }

    /** Mise en page d'une ligne : colonnes (overlaps) + wrap vertical (tracks). */
    private RowLayout layoutOneRowWithWrap(List<Intervention> items, int rowUsableWidth, int rowY) {
        RowLayout out = new RowLayout();
        out.y = rowY;
        if (items == null || items.isEmpty()) {
            out.height = UIConstants.ROW_BASE_HEIGHT;
            return out;
        }

        // copie triée pour un z-order stable
        List<Intervention> sorted = new ArrayList<>(items);
        sorted.sort(Comparator.comparing(Intervention::getDateDebut));

        Map<Intervention, LaneLayout.Lane> lanes = LaneLayout.computeLanes(
                sorted, Intervention::getDateDebut, Intervention::getDateFin, rowUsableWidth);

        int laneCount = lanes.values().stream().mapToInt(l -> l.index + 1).max().orElse(1);
        out.height = LaneLayout.computeRowHeight(laneCount, rowUsableWidth);

        for (Intervention it : sorted) {
            LaneLayout.Lane lane = lanes.get(it);

            int totalGutter = (lane.count - 1) * 2;
            int tileAreaW = Math.max(1, rowUsableWidth - totalGutter);
            int colW = Math.max(UIConstants.MIN_TILE_WIDTH, tileAreaW / lane.count);
            int colX = UIConstants.LEFT_GUTTER_WIDTH + lane.index * (colW + 2);

            Rectangle r = LaneLayout.computeTileBounds(it.getDateDebut(), it.getDateFin(), lane, gridModel, rowY);

            int x = Math.max(colX, Math.min(r.x, colX + colW - 1));
            int w = Math.min(r.width, colW - (x - colX));
            Rectangle rr = new Rectangle(x, r.y, Math.max(1, w), r.height);

            out.bounds.put(it, rr);
            out.z.add(it);
        }
        return out;
    }

    // ----------------- Peinture -----------------
    @Override protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // fond
        g.setColor(Color.white);
        g.fillRect(0,0,getWidth(),getHeight());

        // gouttière gauche
        paintLeftGutter(g);

        // colonnes (mêmes X que le header)
        paintDayGrid(g);

        // séparateurs de lignes
        g.setColor(new Color(0xE0E0E0));
        for (RessourceRow r : ressources) {
            RowLayout rl = rowLayouts.get(r.id);
            if (rl == null) continue;
            int y = rl.y + rl.height;
            g.drawLine(0, y, getWidth(), y);
        }

        // tuiles
        for (Intervention it : zOrder) {
            Rectangle r = tileBounds.get(it);
            if (r == null) continue;
            paintTile(g, it, r, it == active);
        }
        g.dispose();
    }

    private void paintLeftGutter(Graphics2D g) {
        g.setColor(new Color(0xF7F7F9));
        g.fillRect(0,0,UIConstants.LEFT_GUTTER_WIDTH,getHeight());
        g.setColor(new Color(0xDDDDDD));
        g.drawLine(UIConstants.LEFT_GUTTER_WIDTH-1, 0, UIConstants.LEFT_GUTTER_WIDTH-1, getHeight());

        Font bold = getFont().deriveFont(Font.BOLD, 12f);
        g.setFont(bold);
        g.setColor(new Color(0x333333));

        for (RessourceRow row : ressources) {
            RowLayout rl = rowLayouts.get(row.id);
            if (rl == null) continue;
            // puce couleur + nom
            g.setColor(row.color);
            g.fillOval(12, rl.y + 10, 10,10);
            g.setColor(new Color(0x333333));
            drawElided(g, row.displayName, 30, rl.y + 20, UIConstants.LEFT_GUTTER_WIDTH - 40);
        }
    }

    private void paintDayGrid(Graphics2D g) {
        int[] xs = gridModel.getDayColumnXs(currentWeek);
        g.setColor(new Color(0xECEFF1));
        for (int x : xs) g.drawLine(x, 0, x, getHeight());
    }

    private void paintTile(Graphics2D g, Intervention it, Rectangle r, boolean selected) {
        Color base = new Color(0x88AACC);
        Color bg   = lighten(base, 0.55f);
        Color brd  = darken(base, 0.25f);
        Color txt  = luminance(bg) > 140 ? Color.BLACK : Color.WHITE;
        if (selected) bg = new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 220);

        g.setColor(bg);
        g.fillRoundRect(r.x, r.y, r.width, r.height, 8, 8);
        g.setStroke(new BasicStroke(UIConstants.TILE_BORDER));
        g.setColor(brd);
        g.drawRoundRect(r.x, r.y, r.width, r.height, 8, 8);

        // handles
        g.setColor(new Color(0,0,0,30));
        g.fillRect(r.x+2, r.y, r.width-4, 4);
        g.fillRect(r.x+2, r.y+r.height-4, r.width-4, 4);

        // texte (heure seule par défaut)
        g.setColor(txt);
        String s = fmt(it.getDateDebut()) + " - " + fmt(it.getDateFin());
        drawElided(g, s, r.x + UIConstants.TILE_PADDING, r.y + UIConstants.TILE_PADDING + g.getFontMetrics().getAscent(), r.width - 2*UIConstants.TILE_PADDING);
    }

    // ----------------- Utils -----------------
    private Optional<Intervention> pickAt(Point p) {
        for (int i=zOrder.size()-1; i>=0; i--) {
            Intervention it = zOrder.get(i);
            Rectangle r = tileBounds.get(it);
            if (r != null && r.contains(p)) return Optional.of(it);
        }
        return Optional.empty();
    }

    private static void drawElided(Graphics2D g, String text, int x, int baselineY, int maxWidth) {
        if (text == null) return;
        FontMetrics fm = g.getFontMetrics();
        if (fm.stringWidth(text) <= maxWidth) { g.drawString(text, x, baselineY); return; }
        String ell = "...";
        int ellW = fm.stringWidth(ell);
        StringBuilder b = new StringBuilder();
        for (int i=0; i<text.length(); i++) {
            String sub = text.substring(0, i+1);
            if (fm.stringWidth(sub)+ellW > maxWidth) break;
            b.append(text.charAt(i));
        }
        g.drawString(b.append(ell).toString(), x, baselineY);
    }

    private static String fmt(LocalDateTime t) { return String.format("%02d:%02d", t.getHour(), t.getMinute()); }
    private static int luminance(Color c) { return (int)(0.2126*c.getRed()+0.7152*c.getGreen()+0.0722*c.getBlue()); }
    private static Color lighten(Color c, float f){ float[] h=Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(),null);
        return Color.getHSBColor(h[0], Math.max(0,h[1]*0.35f), Math.min(1,h[2]*(0.8f+f*0.2f))); }
    private static Color darken(Color c, float f){ float[] h=Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(),null);
        return Color.getHSBColor(h[0], Math.min(1,h[1]*(0.8f+f*0.2f)), Math.max(0,h[2]*(0.7f-f*0.2f))); }

    // ----------------- Scrollable -----------------
    @Override public Dimension getPreferredSize() {
        return new Dimension(UIConstants.LEFT_GUTTER_WIDTH + gridModel.getContentWidth(), totalHeight);
    }
    @Override public Dimension getPreferredScrollableViewportSize(){ return new Dimension(1200,600); }
    @Override public int getScrollableUnitIncrement(Rectangle vr, int orientation, int direction){ return orientation==SwingConstants.VERTICAL?24:24; }
    @Override public int getScrollableBlockIncrement(Rectangle vr, int orientation, int direction){ return orientation==SwingConstants.VERTICAL?Math.max(UIConstants.ROW_BASE_HEIGHT, vr.height-40):24*48; }
    @Override public boolean getScrollableTracksViewportWidth(){ return false; }
    @Override public boolean getScrollableTracksViewportHeight(){ return false; }
}
