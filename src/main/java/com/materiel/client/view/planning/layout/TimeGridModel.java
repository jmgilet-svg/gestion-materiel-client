package com.materiel.client.view.planning.layout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.materiel.client.view.planning.UIConstants;

/** Shared model to translate between time and pixel positions for the planning grid. */
public class TimeGridModel {
    private final int hourWidth;
    private final int leftGutter;

    public TimeGridModel(int hourWidth) {
        this.hourWidth = hourWidth;
        this.leftGutter = UIConstants.LEFT_GUTTER_WIDTH;
    }

    /** Width in pixels of the frozen left gutter. */
    public int getLeftGutterWidth() {
        return leftGutter;
    }

    /**
     * Compute x coordinates of hour boundaries for the given day.
     * The provided monday is currently ignored but kept for future week layouts.
     */
    public int[] getDayColumnXs(LocalDate monday) {
        int[] xs = new int[25];
        xs[0] = leftGutter;
        for (int h = 1; h <= 24; h++) {
            xs[h] = leftGutter + h * hourWidth;
        }
        return xs;
    }

    /** Convert a time value to a y pixel coordinate. All rounding happens here. */
    public int timeToY(LocalDateTime t) {
        int minutes = t.getHour() * 60 + t.getMinute();
        return Math.round(minutes * (UIConstants.ROW_BASE_HEIGHT / 60f));
    }

    /** Convert a y pixel coordinate back to a time rounded to the nearest minute. */
    public LocalDateTime yToTime(int y) {
        int minutes = Math.round(y * 60f / UIConstants.ROW_BASE_HEIGHT);
        int hour = minutes / 60;
        int minute = minutes % 60;
        return LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minute));
    }
}
