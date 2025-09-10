package com.materiel.client.view.planning.layout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.materiel.client.view.planning.UIConstants;

/** Shared model to translate time and columns into pixel positions. */
public class TimeScaleModel {
    private final int hourWidth;
    private final int leftGutter;

    public TimeScaleModel(int hourWidth) {
        this.hourWidth = hourWidth;
        this.leftGutter = UIConstants.LEFT_GUTTER_WIDTH;
    }

    /**
     * @param day ignored for now but kept for future multi-day layouts
     * @return x positions of column boundaries for the given day
     */
    public int[] getColumnXs(LocalDate day) {
        int[] xs = new int[25];
        xs[0] = leftGutter;
        for (int h = 1; h <= 24; h++) {
            xs[h] = leftGutter + h * hourWidth;
        }
        return xs;
    }

    /** Convert time to y position. */
    public int timeToY(LocalDateTime t) {
        return t.getHour() * UIConstants.ROW_BASE_HEIGHT;
    }

    /** Convert y position back to nearest hour. */
    public LocalDateTime yToTime(int y) {
        int hour = y / UIConstants.ROW_BASE_HEIGHT;
        return LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, 0));
    }

    public int getLeftGutterWidth() {
        return leftGutter;
    }
}
