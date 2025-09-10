package com.materiel.client.view.planning;

import com.materiel.client.model.Intervention;
import com.materiel.client.service.InterventionService;
import com.materiel.client.service.ServiceFactory;
import com.materiel.client.view.planning.layout.LaneLayout;
import com.materiel.client.view.planning.layout.TimeScaleModel;
import com.materiel.client.view.planning.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Interactive planning board handling zoom, selection and duplication logic.
 */
public class PlanningBoard extends JPanel {

    private final ZoomModel zoom = new ZoomModel();
    private boolean multiSelectionEnabled = false;
    private final Set<Long> selectedIds = new HashSet<>();
    private Rectangle selectionRect;
    private TimeGridModel gridModel;
    private final List<Map.Entry<Intervention, Rectangle>> tileRects = new ArrayList<>();
    private final List<Integer> rowLaneCounts = new ArrayList<>();

    public PlanningBoard() {
        setBackground(Color.WHITE);
        addMouseWheelListener(this::handleWheel);
        SelectionHandler handler = new SelectionHandler();
        addMouseListener(handler);
        addMouseMotionListener(handler);
    }

    private void handleWheel(MouseWheelEvent e) {
        if (e.isControlDown()) {
            if (e.getWheelRotation() < 0) {
                zoom.zoomIn();
            } else {
                zoom.zoomOut();
            }
            revalidate();
            repaint();
            e.consume();
        }
    }

    /**
     * Change time scale of the board.
     * @param minutesPerCell increment in minutes
     */
    public void setTimeScale(int minutesPerCell) {
        zoom.setMinutesPerCell(minutesPerCell);
        revalidate();
        repaint();
    }

    /** Enable or disable multi selection. */
    public void enableMultiSelection(boolean enable) {
        this.multiSelectionEnabled = enable;
    }

    /**
     * Duplicate an intervention at the provided slot.
     */
    public void duplicateIntervention(Intervention src, LocalDateTime start, LocalDateTime end) {
        Intervention copy = new Intervention();
        copy.setTitre(src.getTitre());
        copy.setClient(src.getClient());
        copy.setStatut(src.getStatut());
        copy.setAdresseIntervention(src.getAdresseIntervention());
        copy.setDateDebut(start);
        copy.setDateFin(end);
        copy.setDescription(src.getDescription());
        copy.setRessources(src.getRessources());
        InterventionService svc = ServiceFactory.getInterventionService();
        svc.saveIntervention(copy);
        revalidate();
        repaint();
    }

    /**
     * Snap the given time to current increment.
     * @param t time to round
     * @return rounded time
     */
    public LocalDateTime applySnap(LocalDateTime t) {
        int inc = zoom.getMinutesPerCell();
        int minute = t.getMinute();
        int snapped = (minute / inc) * inc;
        if (minute % inc >= inc / 2) {
            snapped += inc;
        }
        return t.withMinute(0).withSecond(0).withNano(0).plusMinutes(snapped);
    }

    /**
     * @return current zoom model
     */
    public ZoomModel getZoomModel() {
        return zoom;
    }

    /** Set shared time scale model. */
    public void setTimeScaleModel(TimeScaleModel model) {
        this.scale = model;
        revalidate();
        repaint();
    }

    /** Access to current grid model, mainly for tests. */
    public TimeGridModel getTimeGridModel() {
        return gridModel;
    }

    /** Update cached tile bounds and z-order. */
    public void setTileBounds(Map<Intervention, Rectangle> bounds) {
        tileRects.clear();
        tileRects.addAll(bounds.entrySet());
        revalidate();
        repaint();
    }

    /**
     * Compute rectangles from lane metadata and update internal cache.
     */
    public void layoutTiles(Map<Intervention, LaneLayout.Lane> lanes, TimeScaleModel scale) {
        this.scale = scale;
        tileRects.clear();
        for (Map.Entry<Intervention, LaneLayout.Lane> e : lanes.entrySet()) {
            Rectangle r = LaneLayout.computeTileBounds(e.getKey(), e.getValue(), scale);
            int trackOffset = e.getValue().track * (UIConstants.ROW_BASE_HEIGHT + UIConstants.TRACK_V_GUTTER);
            r.y += trackOffset;
            tileRects.add(new AbstractMap.SimpleEntry<>(e.getKey(), r));
        }
        // keep height in sync with current tiles without requiring an external call
        rowLaneCounts.clear();
        rowLaneCounts.add(lanes.size());
        revalidate();
        repaint();
    }

    /**
     * Update lane counts for each resource to compute preferred height.
     */
    public void setLaneCounts(List<Integer> counts) {
        rowLaneCounts.clear();
        if (counts != null) {
            rowLaneCounts.addAll(counts);
        }
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        if (scale == null) {
            return super.getPreferredSize();
        }
        int[] xs = scale.getColumnXs(LocalDate.now());
        int width = xs.length > 0 ? xs[xs.length - 1] : 0;
        int rowUsableWidth = width - scale.getLeftGutterWidth();
        int height = 0;
        for (int laneCount : rowLaneCounts) {
            height += LaneLayout.computeRowHeight(laneCount, rowUsableWidth);
        }
        return new Dimension(width, height);
    }

    /**
     * Update lane counts for each resource to compute preferred height.
     */
    public void setLaneCounts(List<Integer> counts) {
        rowLaneCounts.clear();
        if (counts != null) {
            rowLaneCounts.addAll(counts);
        }
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        if (scale == null) {
            return super.getPreferredSize();
        }
        int[] xs = scale.getColumnXs(LocalDate.now());
        int width = xs.length > 0 ? xs[xs.length - 1] : 0;
        int rowUsableWidth = width - scale.getLeftGutterWidth();
        int height = 0;
        for (int laneCount : rowLaneCounts) {
            height += LaneLayout.computeRowHeight(laneCount, rowUsableWidth);
        }
        return new Dimension(width, height);
    }

    /** Hit test tiles from top-most to bottom using z-order. */
    public Optional<Intervention> pickTileAt(Point p) {
        ListIterator<Intervention> it = zOrder.listIterator(zOrder.size());
        while (it.hasPrevious()) {
            Intervention i = it.previous();
            Rectangle r = tileBounds.get(i);
            if (r != null && r.contains(p)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    /** Test if point is on top resize handle. */
    public boolean hitHandleTop(Rectangle r, Point p) {
        Rectangle top = new Rectangle(r.x, r.y, r.width, 8);
        return top.contains(p);
    }

    /** Test if point is on bottom resize handle. */
    public boolean hitHandleBottom(Rectangle r, Point p) {
        Rectangle bottom = new Rectangle(r.x, r.y + r.height - 8, r.width, 8);
        return bottom.contains(p);
    }

    /** Clear current selection. */
    public void clearSelection() {
        selectedIds.clear();
        revalidate();
        repaint();
    }

    /** Test utility: check if id is selected. */
    public boolean isSelected(Long id) {
        return selectedIds.contains(id);
    }

    private class SelectionHandler extends MouseAdapter {
        private Point anchor;

        @Override
        public void mousePressed(MouseEvent e) {
            pickTileAt(e.getPoint());
            if (multiSelectionEnabled && SwingUtilities.isLeftMouseButton(e)) {
                anchor = e.getPoint();
                selectionRect = new Rectangle(anchor);
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            pickTileAt(e.getPoint());
            if (anchor != null) {
                selectionRect.setBounds(Math.min(anchor.x, e.getX()), Math.min(anchor.y, e.getY()),
                        Math.abs(anchor.x - e.getX()), Math.abs(anchor.y - e.getY()));
                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            pickTileAt(e.getPoint());
            anchor = null;
            selectionRect = null;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        if (gridModel != null) {
            g2.setColor(Color.LIGHT_GRAY);
            int[] xs = gridModel.getDayColumnXs(LocalDate.now());
            for (int x : xs) {
                g2.drawLine(x, 0, x, getHeight());
            }
        }
        if (selectionRect != null) {
            g2.setColor(new Color(59, 130, 246, 80));
            g2.fill(selectionRect);
            g2.setColor(new Color(59, 130, 246));
            g2.draw(selectionRect);
        }
        g2.dispose();
    }
}
