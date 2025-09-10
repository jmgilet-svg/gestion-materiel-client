package com.materiel.client.view.ui;

/** Common UI constants shared by UI components. */
public final class UIConstants {
    private UIConstants() {}

    // Layout général
    public static final int LEFT_GUTTER_WIDTH = 180;
    public static final int ROW_BASE_HEIGHT = 88;
    public static final int TRACK_V_GUTTER = 6;

    // Tuiles
    public static final int MIN_TILE_WIDTH = 120;
    public static final int MIN_TILE_HEIGHT = 20;
    public static final int TILE_PADDING = 6;
    public static final int TILE_BORDER = 1;
    public static final int TILE_RADIUS = 8;

    // Grille/temps
    public static final int MIN_TIME_SLICE_MINUTES = 15;

    // Compatibilité anciennes constantes
    public static final int TILE_BORDER_WIDTH = TILE_BORDER;
}
