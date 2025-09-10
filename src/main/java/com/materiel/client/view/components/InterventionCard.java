package com.materiel.client.view.components;

import com.materiel.client.model.Client;
import com.materiel.client.model.Intervention;
import com.materiel.client.view.util.TextUtils;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Lightweight intervention tile drawn with custom painting to avoid text overlap.
 */
public class InterventionCard extends JComponent {

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font SUB_FONT = new Font("Segoe UI", Font.PLAIN, 11);

    private final Intervention intervention;
    private boolean selected;

    public InterventionCard(Intervention intervention) {
        this.intervention = intervention;
        setOpaque(false);
        setToolTipText(buildTooltip());
    }

    public Intervention getIntervention() {
        return intervention;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();
        g2.setColor(selected ? new Color(0xEBF4FF) : Color.WHITE);
        g2.fillRect(0, 0, w, h);
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawRect(0, 0, w - 1, h - 1);

        int padding = 6;
        int handle = 6;
        int textWidth = w - padding * 2;
        int y = padding + handle;

        g2.setClip(padding, padding, textWidth, h - padding * 2);

        if (h < 3 * g2.getFontMetrics(TITLE_FONT).getHeight() + handle * 2 + padding * 2) {
            // chip mode
            g2.setFont(TITLE_FONT);
            String label = formatTimeRange() + " " + safe(intervention.getTitre());
            label = TextUtils.elide(g2, label, textWidth);
            g2.drawString(label, padding, y + g2.getFontMetrics().getAscent());
        } else {
            g2.setFont(TITLE_FONT);
            String title = TextUtils.elide(g2, safe(intervention.getTitre()), textWidth);
            g2.drawString(title, padding, y + g2.getFontMetrics().getAscent());
            y += g2.getFontMetrics().getHeight();

            g2.setFont(SUB_FONT);
            Client client = intervention.getClient();
            String clientName = client != null ? client.getNom() : "";
            clientName = TextUtils.elide(g2, clientName, textWidth);
            g2.drawString(clientName, padding, y + g2.getFontMetrics().getAscent());
            y += g2.getFontMetrics().getHeight();

            String time = formatTimeRange();
            time = TextUtils.elide(g2, time, textWidth);
            g2.drawString(time, padding, y + g2.getFontMetrics().getAscent());
        }

        g2.dispose();
    }

    private String formatTimeRange() {
        LocalDateTime start = intervention.getDateDebut();
        LocalDateTime end = intervention.getDateFin();
        if (start == null || end == null) {
            return "";
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        long minutes = Duration.between(start, end).toMinutes();
        long hours = minutes / 60;
        long mins = minutes % 60;
        String dur = hours > 0 ? hours + "h" + (mins > 0 ? mins : "") : mins + "m";
        return start.format(fmt) + "-​" + end.format(fmt) + " (" + dur + ")";
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String buildTooltip() {
        StringBuilder sb = new StringBuilder();
        sb.append(safe(intervention.getTitre()));
        if (intervention.getClient() != null) {
            sb.append("\n").append(intervention.getClient().getNom());
        }
        sb.append("\n").append(formatTimeRange());
        return sb.toString();
    }
}
