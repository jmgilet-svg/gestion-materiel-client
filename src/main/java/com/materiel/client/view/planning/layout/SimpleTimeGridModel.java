package com.materiel.client.view.planning.layout;

import com.materiel.client.view.ui.UIConstants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;

/**
 * Basic implementation of {@link TimeGridModel} using a uniform hour width.
 * The week starts on Monday and spans seven days.
 */
public class SimpleTimeGridModel implements TimeGridModel {
    private final int hourWidth;
    private final int leftGutter;

    public SimpleTimeGridModel(int hourWidth) {
        this.hourWidth = hourWidth;
        this.leftGutter = UIConstants.LEFT_GUTTER_WIDTH;
    }

    @Override
    public int getLeftGutterWidth() {
        return leftGutter;
    }

    @Override
    public int[] getDayColumnXs(LocalDate weekStart) {
        int dayWidth = hourWidth * 24;
        int[] xs = new int[8];
        xs[0] = leftGutter;
        for (int i = 1; i <= 7; i++) {
            xs[i] = leftGutter + i * dayWidth;
        }
        return xs;
    }

    @Override
    public int timeToX(LocalDateTime t) {
        int dayWidth = hourWidth * 24;
        int dayIndex = t.getDayOfWeek().getValue() - 1; // Monday = 0
        int minutes = t.getHour() * 60 + t.getMinute();
        int offset = (int) Math.round(minutes * (hourWidth / 60.0));
        return leftGutter + dayIndex * dayWidth + offset;
    }

    @Override
    public LocalDateTime xToTime(int x) {
        int dayWidth = hourWidth * 24;
        int offset = Math.max(0, x - leftGutter);
        int dayIndex = offset / dayWidth;
        int withinDay = offset % dayWidth;
        int minutes = (int) Math.round(withinDay * 60.0 / hourWidth);
        int hour = minutes / 60;
        int minute = minutes % 60;
        LocalDate base = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .plusDays(dayIndex);
        return LocalDateTime.of(base, LocalTime.of(hour, minute));
    }
}

