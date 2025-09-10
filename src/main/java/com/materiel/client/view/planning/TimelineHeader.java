package com.materiel.client.view.planning;

import com.materiel.client.view.planning.layout.TimeGridModel;
import com.materiel.client.view.ui.UIConstants;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

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

        int h = getHeight();
        // Empty header cell over the resource column
        g2.setColor(getBackground());
        g2.fillRect(0, 0, UIConstants.LEFT_GUTTER_WIDTH, h);
        g2.setColor(Color.GRAY);
        g2.drawRect(0, 0, UIConstants.LEFT_GUTTER_WIDTH, h - 1);

        int[] xs = model.getDayColumnXs(LocalDate.now());
        LocalDate d = LocalDate.now();
        FontMetrics fm = g2.getFontMetrics();
        for (int i = 0; i < xs.length; i++) {
            int x = xs[i];
            g2.drawLine(x, 0, x, h);
            if (i + 1 < xs.length) {
                int next = xs[i + 1];
                String label = d.plusDays(i).getDayOfWeek()
                        .getDisplayName(TextStyle.SHORT, Locale.getDefault());
                int textX = x + (next - x - fm.stringWidth(label)) / 2;
                int textY = (h + fm.getAscent()) / 2 - 2;
                g2.drawString(label, textX, textY);
            }
        }

        g2.dispose();
    }
}
