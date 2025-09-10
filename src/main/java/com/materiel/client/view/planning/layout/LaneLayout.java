package com.materiel.client.view.planning.layout;

import com.materiel.client.model.Intervention;
import com.materiel.client.view.ui.UIConstants;

import java.awt.Rectangle;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Layout helper computing lanes and tracks with wrapping when space is limited.
 */
public final class LaneLayout {
    private LaneLayout() {
    }

    /** Data describing lane position within tracks. */
    public static final class Lane {
        public final int index;   // column index within the track group
        public final int count;   // number of columns in the track group
        public final int track;   // vertical track index
        public final int tracks;  // total number of tracks

        public Lane(int index, int count, int track, int tracks) {
            this.index = index;
            this.count = count;
            this.track = track;
            this.tracks = tracks;
        }
    }

    /**
     * Compute lane assignment using an interval graph. Interventions are first
     * assigned to lanes so that overlapping ones get different indices. The
     * lanes are then wrapped into vertical tracks when the available width is
     * insufficient to display all of them at the minimum tile width.
     *
     * @param interventionsForResource interventions for a resource
     * @param rowUsableWidth           width available for tiles excluding the left gutter
     * @return lane metadata for each intervention preserving iteration order
     */
    public static Map<Intervention, Lane> computeLanes(
            List<Intervention> interventionsForResource, int rowUsableWidth) {
        List<Intervention> sorted = new ArrayList<>(interventionsForResource);
        sorted.sort(Comparator.comparing(Intervention::getDateDebut));

        // First pass: assign lane indices based on overlaps
        List<LocalDateTime> laneEnd = new ArrayList<>();
        Map<Intervention, Integer> laneIndex = new LinkedHashMap<>();
        for (Intervention in : sorted) {
            LocalDateTime start = in.getDateDebut();
            LocalDateTime end = in.getDateFin();
            int lane = 0;
            while (lane < laneEnd.size() && laneEnd.get(lane).isAfter(start)) {
                lane++;
            }
            if (lane == laneEnd.size()) {
                laneEnd.add(end);
            } else {
                laneEnd.set(lane, end);
            }
            laneIndex.put(in, lane);
        }

        int laneCount = laneEnd.size();
        int tracks = Math.max(1,
                (int) Math.ceil((laneCount * 1.0 * UIConstants.MIN_TILE_WIDTH) / rowUsableWidth));
        int colsPerTrack = (int) Math.ceil(laneCount / (double) tracks);

        Map<Intervention, Lane> result = new LinkedHashMap<>();
        for (Map.Entry<Intervention, Integer> e : laneIndex.entrySet()) {
            int lane = e.getValue();
            int track = lane / colsPerTrack;
            int index = lane % colsPerTrack;
            int count = Math.min(colsPerTrack, laneCount - track * colsPerTrack);
            result.put(e.getKey(), new Lane(index, count, track, tracks));
        }

    /**
     * Compute pixel bounds of a tile using the provided grid model and lane info.
     */
    public static Rectangle computeTileBounds(
            Intervention intervention, Lane lane, TimeGridModel grid,
            int rowY, LocalDateTime start, LocalDateTime end) {
        int x1 = grid.timeToX(start);
        int x2 = grid.timeToX(end);
        int y = rowY + lane.track * (UIConstants.ROW_BASE_HEIGHT + UIConstants.TRACK_V_GUTTER);
        int h = Math.max(UIConstants.MIN_TILE_HEIGHT, UIConstants.ROW_BASE_HEIGHT - 1);
        return new Rectangle(Math.min(x1, x2), y, Math.max(1, Math.abs(x2 - x1)), h);
    }

    /**
     * Compute total row height depending on the number of lanes and available
     * width.
     */
    public static int computeRowHeight(int laneCount, int rowUsableWidth) {
        int tracks = Math.max(1,
                (int) Math.ceil((laneCount * 1.0 * UIConstants.MIN_TILE_WIDTH) /
                        Math.max(1, rowUsableWidth)));
        return UIConstants.ROW_BASE_HEIGHT * tracks + UIConstants.TRACK_V_GUTTER * (tracks - 1);
    }
}

