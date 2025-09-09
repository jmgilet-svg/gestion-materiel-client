package com.materiel.client.view.planning;

import com.materiel.client.model.Intervention;
import com.materiel.client.service.InterventionService;
import com.materiel.client.service.ServiceFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Interactive planning board handling zoom, selection and duplication logic.
 */
public class PlanningBoard extends JPanel {

    private final ZoomModel zoom = new ZoomModel();
    private boolean multiSelectionEnabled = false;
    private final Set<Long> selectedIds = new HashSet<>();
    private Rectangle selectionRect;

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

    /** Clear current selection. */
    public void clearSelection() {
        selectedIds.clear();
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
            if (multiSelectionEnabled && SwingUtilities.isLeftMouseButton(e)) {
                anchor = e.getPoint();
                selectionRect = new Rectangle(anchor);
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (anchor != null) {
                selectionRect.setBounds(Math.min(anchor.x, e.getX()), Math.min(anchor.y, e.getY()),
                        Math.abs(anchor.x - e.getX()), Math.abs(anchor.y - e.getY()));
                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            anchor = null;
            selectionRect = null;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (selectionRect != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(59, 130, 246, 80));
            g2.fill(selectionRect);
            g2.setColor(new Color(59, 130, 246));
            g2.draw(selectionRect);
            g2.dispose();
        }
    }
}
