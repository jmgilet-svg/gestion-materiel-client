package com.materiel.client.view.planning;

import static com.materiel.client.util.UIConstants.*;

import java.awt.Rectangle;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Outils de calcul des "lanes" : colonnes et pistes verticales pour
 * l'agencement des tuiles de planning.
 */
public final class LaneLayout {
    private LaneLayout() {}

    /** Informations d'une tuile dans une ligne. */
    public static final class Lane {
        public final int index;   // index dans le track (0..count-1)
        public final int count;   // nb de colonnes dans le track
        public final int track;   // piste verticale (0..tracks-1)
        public final int tracks;  // nb total de pistes de la ligne

        public Lane(int index, int count, int track, int tracks) {
            this.index = index; this.count = count; this.track = track; this.tracks = tracks;
        }
    }

    /** Interface bi-fonctionnelle pour extraire start/end. */
    public interface StartEnd<T> { LocalDateTime start(T t); LocalDateTime end(T t); }

    /** Surcharge pratique (évite l'erreur "target type must be a functional interface"). */
    public static <T> Map<T, Lane> computeLanes(List<T> items,
            Function<T, LocalDateTime> startFn,
            Function<T, LocalDateTime> endFn,
            int rowUsableWidth) {
        return computeLanes(items, new StartEnd<>() {
            @Override public LocalDateTime start(T t) { return startFn.apply(t); }
            @Override public LocalDateTime end(T t) { return endFn.apply(t); }
        }, rowUsableWidth);
    }

    /** Calcule colonnes + tracks à partir d'une liste d'items chevauchants. */
    public static <T> Map<T, Lane> computeLanes(List<T> items, StartEnd<T> se, int rowUsableWidth) {
        if (items == null || items.isEmpty()) return Collections.emptyMap();
        items.sort(Comparator.comparing(se::start));

        // Sweep-line : 1ère colonne libre
        List<T> open = new ArrayList<>();
        Map<T,Integer> col = new LinkedHashMap<>();
        int maxCols = 0;
        for (T it : items) {
            LocalDateTime s = se.start(it);
            open.removeIf(o -> !se.end(o).isAfter(s));
            boolean[] used = new boolean[open.size()+1];
            for (T o : open) used[col.get(o)] = true;
            int idx = 0; while (idx < used.length && used[idx]) idx++;
            col.put(it, idx);
            open.add(it);
            maxCols = Math.max(maxCols, idx+1);
        }

        int tracks = Math.max(1,
                (int) Math.ceil((maxCols * 1.0 * MIN_TILE_WIDTH) / Math.max(1, rowUsableWidth)));
        int colsPerTrack = (int) Math.ceil(maxCols * 1.0 / tracks);

        Map<T, Lane> out = new LinkedHashMap<>();
        for (T it : items) {
            int k = col.get(it);
            int track = k % tracks;
            int indexWithinTrack = k / tracks;
            out.put(it, new Lane(indexWithinTrack, colsPerTrack, track, tracks));
        }
        return out;
    }

    /** Hauteur réelle d'une ligne selon le nombre de colonnes nécessaires. */
    public static int computeRowHeight(int laneCount, int rowUsableWidth) {
        int tracks = Math.max(1,
                (int) Math.ceil((laneCount * 1.0 * MIN_TILE_WIDTH) / Math.max(1, rowUsableWidth)));
        return ROW_BASE_HEIGHT * tracks + TRACK_V_GUTTER * (tracks - 1);
    }

    /** Rectangle d'une tuile (dans le track). */
    public static Rectangle computeTileBounds(
            LocalDateTime start, LocalDateTime end,
            Lane lane, TimeGridModel grid, int rowY) {
        int x1 = grid.timeToX(start);
        int x2 = grid.timeToX(end);
        int y = rowY + lane.track * (ROW_BASE_HEIGHT + TRACK_V_GUTTER);
        int h = Math.max(MIN_TILE_HEIGHT, ROW_BASE_HEIGHT - 1);
        int w = Math.max(1, Math.abs(x2 - x1));
        int x = Math.min(x1, x2);
        return new Rectangle(x, y, w, h);
    }
}

