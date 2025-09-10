package com.materiel.client.view.planning.layout;

import java.time.*;

import static com.materiel.client.view.ui.UIConstants.*;

/**
 * Default implementation of {@link TimeGridModel} converting between time and
 * pixel positions for a weekly grid. All rounding logic is centralized here.
 */
public final class DefaultTimeGridModel implements TimeGridModel {
    private final LocalDate weekStart;
    private int pxPerHour;

    public DefaultTimeGridModel(LocalDate weekStart, int pxPerHour) {
        this.weekStart = weekStart;
        this.pxPerHour = Math.max(8, pxPerHour);
    }

    /** Adjust horizontal zoom in pixels per hour. */
    public void setPxPerHour(int v) {
        this.pxPerHour = Math.max(8, v);
    }

    @Override
    public int getLeftGutterWidth() {
        return LEFT_GUTTER_WIDTH;
    }

    @Override
    public int[] getDayColumnXs(LocalDate ws) {
        LocalDate base = (ws != null) ? ws : weekStart;
        int[] xs = new int[8];
        xs[0] = LEFT_GUTTER_WIDTH;
        int dayW = 24 * pxPerHour;
        for (int d = 1; d <= 7; d++) {
            xs[d] = LEFT_GUTTER_WIDTH + d * dayW;
        }
        return xs;
    }

    @Override
    public int timeToX(LocalDateTime t) {
        long days = Duration.between(weekStart.atStartOfDay(), t.withSecond(0).withNano(0)).toDays();
        long minutes = Duration.between(t.toLocalDate().atStartOfDay(), t).toMinutes();
        int x = LEFT_GUTTER_WIDTH + (int) days * (24 * pxPerHour)
                + (int) ((minutes / 60.0) * pxPerHour);
        return x;
    }

    @Override
    public LocalDateTime xToTime(int x) {
        int rel = Math.max(0, x - LEFT_GUTTER_WIDTH);
        int dayW = 24 * pxPerHour;
        int d = rel / dayW;
        int rem = rel % dayW;
        int h = rem / pxPerHour;
        int m = (int) Math.round(((rem % pxPerHour) * 60.0) / pxPerHour);
        return weekStart.plusDays(d).atTime(Math.min(23, h), Math.min(59, m));
    }

    @Override
    public int getContentWidth() {
        return 7 * 24 * pxPerHour;
    }
}
