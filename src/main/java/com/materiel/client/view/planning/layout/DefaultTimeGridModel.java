package com.materiel.client.view.planning.layout;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.materiel.client.view.ui.UIConstants;

/** Implémentation simple : 1 jour = 24h * pxPerHour. */
public final class DefaultTimeGridModel implements TimeGridModel {
    private LocalDate weekStart;
    private int pxPerHour;

    public DefaultTimeGridModel(LocalDate weekStart, int pxPerHour) {
        this.weekStart = weekStart.with(DayOfWeek.MONDAY);
        this.pxPerHour = Math.max(8, pxPerHour);
    }

    public void setWeek(LocalDate ws)      { this.weekStart = ws.with(DayOfWeek.MONDAY); }
    public void setPxPerHour(int pxHour)   { this.pxPerHour = Math.max(8, pxHour); }

    @Override public int getLeftGutterWidth() { return UIConstants.LEFT_GUTTER_WIDTH; }

    @Override public int[] getDayColumnXs(LocalDate ws) {
        LocalDate base = (ws != null) ? ws.with(DayOfWeek.MONDAY) : weekStart;
        // base non utilisée ici car le pas est régulier : 7 jours => 8 bornes
        int[] xs = new int[8];
        xs[0] = UIConstants.LEFT_GUTTER_WIDTH;
        int dayW = 24 * pxPerHour;
        for (int d = 1; d <= 7; d++) xs[d] = UIConstants.LEFT_GUTTER_WIDTH + d * dayW;
        return xs;
    }

    @Override public int timeToX(LocalDateTime t) {
        long days = Duration.between(weekStart.atStartOfDay(), t.withSecond(0).withNano(0)).toDays();
        int minutesInDay = t.getHour() * 60 + t.getMinute();
        return UIConstants.LEFT_GUTTER_WIDTH + (int)days * (24 * pxPerHour) + Math.round((minutesInDay / 60f) * pxPerHour);
    }

    @Override public LocalDateTime xToTime(int x) {
        int rel = Math.max(0, x - UIConstants.LEFT_GUTTER_WIDTH);
        int dayW = 24 * pxPerHour;
        int d = rel / dayW;
        int rem = rel % dayW;
        int h = rem / pxPerHour;
        int m = Math.round(((rem % pxPerHour) * 60f) / pxPerHour);
        LocalDate day = weekStart.plusDays(Math.min(6, Math.max(0, d)));
        return day.atTime(Math.min(23, h), Math.min(59, m));
    }

    @Override public int getContentWidth() { return 7 * 24 * pxPerHour; }
}