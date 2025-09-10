package com.materiel.client.view.planning.layout;

import java.awt.Rectangle;
import java.time.LocalDateTime;
import java.util.*;

import static com.materiel.client.view.ui.UIConstants.*;

/**
 * Utility computing lane assignments and tile bounds with vertical wrapping
 * when horizontal space is limited.
 */
public final class LaneLayout {
    private LaneLayout() {}

    /** Metadata describing a lane within tracks. */
    public static final class Lane {
        public final int index;
        public final int count;
        public final int track;
        public final int tracks;
        public Lane(int index, int count, int track, int tracks) {
            this.index = index;
            this.count = count;
            this.track = track;
            this.tracks = tracks;
        }
    }

    /** Strategy interface to extract start/end from arbitrary items. */
    public interface StartEnd<T> {
        LocalDateTime start(T t);
        LocalDateTime end(T t);
    }

    /**
     * Compute lane assignment for the given items. Items overlapping in time
     * are placed in separate columns; when the total width would make columns
     * narrower than {@link UIConstants#MIN_TILE_WIDTH}, lanes wrap vertically
     * into multiple tracks.
     */
    public static <T> Map<T, Lane> computeLanes(List<T> items, StartEnd<T> se, int rowUsableWidth) {
        items.sort(Comparator.comparing(se::start));
        List<T> open = new ArrayList<>();
        Map<T, Integer> col = new HashMap<>();
        int maxCols = 0;
        for (T it : items) {
            LocalDateTime s = se.start(it);
            open.removeIf(o -> !se.end(o).isAfter(s));
            boolean[] used = new boolean[open.size() + 1];
            for (T o : open) {
                used[col.get(o)] = true;
            }
            int idx = 0;
            while (idx < used.length && used[idx]) {
                idx++;
            }
            col.put(it, idx);
            open.add(it);
            maxCols = Math.max(maxCols, idx + 1);
        }
        int tracks = Math.max(1, (int) Math.ceil((maxCols * 1.0 * MIN_TILE_WIDTH) / Math.max(1, rowUsableWidth)));
        Map<T, Lane> out = new LinkedHashMap<>();
        int colsPerTrack = (int) Math.ceil(maxCols * 1.0 / tracks);
        for (T it : items) {
            int k = col.get(it);
            int track = k % tracks;
            int indexWithinTrack = k / tracks;
            out.put(it, new Lane(indexWithinTrack, colsPerTrack, track, tracks));
        }
        return out;
    }

    /** Compute total row height for the given lane count and available width. */
    public static int computeRowHeight(int laneCount, int rowUsableWidth) {
        int tracks = Math.max(1, (int) Math.ceil((laneCount * 1.0 * MIN_TILE_WIDTH) / Math.max(1, rowUsableWidth)));
        return ROW_BASE_HEIGHT * tracks + TRACK_V_GUTTER * (tracks - 1);
    }

    /** Compute pixel bounds of a tile inside its row and track. */
    public static Rectangle computeTileBounds(LocalDateTime start, LocalDateTime end, Lane lane,
                                             TimeGridModel grid, int rowY) {
        int x1 = grid.timeToX(start);
        int x2 = grid.timeToX(end);
        int y = rowY + lane.track * (ROW_BASE_HEIGHT + TRACK_V_GUTTER);
        int h = Math.max(MIN_TILE_HEIGHT, ROW_BASE_HEIGHT - 1);
        int w = Math.max(1, Math.abs(x2 - x1));
        int x = Math.min(x1, x2);
        return new Rectangle(x, y, w, h);
    }
}
