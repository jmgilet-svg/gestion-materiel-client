package com.materiel.client.view.planning;

import com.materiel.client.model.Intervention;
import com.materiel.client.view.components.InterventionCard;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Vue calendrier jour basique avec timeline verticale des interventions.
 */
public class DayTimelinePanel extends JPanel {

    private static final int HOUR_HEIGHT = 60;
    private LocalDate date;
    private List<Intervention> interventions;

    public DayTimelinePanel(LocalDate date, List<Intervention> interventions) {
        this.date = date;
        this.interventions = interventions;
        setLayout(null);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 24 * HOUR_HEIGHT));
        drawHourLines();
        displayInterventions();
    }

    /**
     * Met à jour la date affichée et recharge les interventions.
     */
    public void setDate(LocalDate date, List<Intervention> interventions) {
        this.date = date;
        this.interventions = interventions;
        removeAll();
        drawHourLines();
        displayInterventions();
        revalidate();
        repaint();
    }

    private void drawHourLines() {
        for (int h = 0; h < 24; h++) {
            int y = h * HOUR_HEIGHT;
            JLabel hourLabel = new JLabel(String.format("%02d:00", h));
            hourLabel.setBounds(5, y, 50, 20);
            add(hourLabel);

            JPanel line = new JPanel();
            line.setBackground(new Color(230, 230, 230));
            line.setBounds(60, y, 700, 1);
            add(line);
        }
    }

    private void displayInterventions() {
        if (interventions == null) {
            return;
        }
        for (Intervention iv : interventions) {
            if (iv.getDateDebut() == null || iv.getDateFin() == null) {
                continue;
            }
            if (!iv.getDateDebut().toLocalDate().equals(date)) {
                continue;
            }
            LocalTime start = iv.getDateDebut().toLocalTime();
            LocalTime end = iv.getDateFin().toLocalTime();
            int y = start.getHour() * HOUR_HEIGHT + start.getMinute() * HOUR_HEIGHT / 60;
            int height = (int) Duration.between(start, end).toMinutes() * HOUR_HEIGHT / 60;
            InterventionCard card = new InterventionCard(iv);
            card.setBounds(70, y, 200, Math.max(height, 20));
            add(card);
        }
    }
}
