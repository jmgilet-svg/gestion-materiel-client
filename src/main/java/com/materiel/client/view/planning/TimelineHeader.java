package com.materiel.client.view.planning;

import com.materiel.client.view.planning.layout.TimeGridModel;
import com.materiel.client.view.ui.UIConstants;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.time.LocalDate;

/** Header displaying day columns aligned with the planning grid. */
public class TimelineHeader extends JComponent implements ChangeListener {
    private final TimeGridModel model;
    private JViewport viewport;

    public TimelineHeader(TimeGridModel model) {
        this.model = model;
        setPreferredSize(new Dimension(0, UIConstants.ROW_BASE_HEIGHT));
    }

    /** Attach this header to the same viewport as the planning board for sync. */
    public void attachToViewport(JViewport vp) {
        if (this.viewport != null) {
            this.viewport.removeChangeListener(this);
        }
        this.viewport = vp;
        if (vp != null) {
            vp.addChangeListener(this);
        }
    }

    /** Access to underlying grid model, mainly for testing. */
    public TimeGridModel getModel() {
        return model;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.GRAY);
        int[] xs = model.getDayColumnXs(LocalDate.now());
        for (int x : xs) {
            g2.drawLine(x, 0, x, getHeight());
        }
        g2.dispose();
    }
}
