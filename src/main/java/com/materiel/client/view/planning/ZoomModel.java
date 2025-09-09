package com.materiel.client.view.planning;

/**
 * Simple model to handle vertical time zoom in the planning view.
 */
public class ZoomModel {
    private static final int[] LEVELS = {5, 15, 30, 60};
    private static final int CELL_HEIGHT = 20; // pixels per cell

    private int index = 1; // default 15 min
    private double pixelsPerMinute;

    public ZoomModel() {
        updatePixelsPerMinute();
    }

    /**
     * @return current grid increment in minutes.
     */
    public int getMinutesPerCell() {
        return LEVELS[index];
    }

    /**
     * Change current grid increment.
     * @param minutes minutes represented by a cell
     */
    public void setMinutesPerCell(int minutes) {
        for (int i = 0; i < LEVELS.length; i++) {
            if (LEVELS[i] == minutes) {
                index = i;
                updatePixelsPerMinute();
                return;
            }
        }
        throw new IllegalArgumentException("Unsupported scale: " + minutes);
    }

    /** Zoom in to the next finer scale. */
    public void zoomIn() {
        if (index > 0) {
            index--;
            updatePixelsPerMinute();
        }
    }

    /** Zoom out to the next coarser scale. */
    public void zoomOut() {
        if (index < LEVELS.length - 1) {
            index++;
            updatePixelsPerMinute();
        }
    }

    /**
     * Convert minutes to pixels according to current zoom.
     */
    public int minuteToPixel(int minutes) {
        return (int) Math.round(minutes * pixelsPerMinute);
    }

    /**
     * Convert pixels to minutes according to current zoom.
     */
    public int pixelToMinute(int pixels) {
        return (int) Math.round(pixels / pixelsPerMinute);
    }

    /**
     * Pixels representing one hour at current zoom.
     */
    public int getPixelsPerHour() {
        return minuteToPixel(60);
    }

    private void updatePixelsPerMinute() {
        pixelsPerMinute = (double) CELL_HEIGHT / getMinutesPerCell();
    }
}
