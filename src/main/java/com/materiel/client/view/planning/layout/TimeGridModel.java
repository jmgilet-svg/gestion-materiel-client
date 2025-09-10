package com.materiel.client.view.planning.layout;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Shared model to translate between time and pixel positions for the planning grid.
 * All rounding of X positions happens inside this model.
 */
public interface TimeGridModel {

    /** @return width in pixels of the frozen left gutter. */
    int getLeftGutterWidth();

    /**
     * Compute X coordinates of week day column boundaries including the left gutter offset.
     *
     * @param weekStart first day of the week (typically Monday)
     * @return array of x positions for each day boundary
     */
    int[] getDayColumnXs(LocalDate weekStart);

    /** Convert a time value to an X pixel coordinate. */
    int timeToX(LocalDateTime t);

    /** Convert an X pixel coordinate back to a time rounded to the nearest minute. */
    LocalDateTime xToTime(int x);
}

