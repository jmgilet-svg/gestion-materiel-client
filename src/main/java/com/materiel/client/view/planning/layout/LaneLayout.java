package com.materiel.client.view.planning.layout;

import java.awt.Rectangle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.materiel.client.model.Intervention;
import com.materiel.client.view.planning.UIConstants;

/**
 * Layout helper computing lanes and tracks with wrapping when space is limited.
 */
public final class LaneLayout {
    private LaneLayout() {
    }

    /** Data describing lane position within tracks. */
    public static class Lane {
        public int index;
        public int count;
        public int track;
        public int tracks;
    }

    /**
     * Assign lanes and wrap into vertical tracks when the available width is
     * insufficient to display every lane at the minimum tile width.
     *
     * @param byResource    interventions for a resource
     * @param rowUsableWidth width available for tiles excluding the left gutter
     * @return lane metadata for each intervention preserving the iteration order
     */
    public static Map<Intervention, Lane> computeLanes(List<Intervention> byResource, int rowUsableWidth) {
        Map<Intervention, Lane> result = new LinkedHashMap<>();
        int laneCount = byResource.size();
        int tracks = (int) Math.ceil((laneCount * (double) UIConstants.MIN_TILE_WIDTH) / rowUsableWidth);
        tracks = Math.max(tracks, 1);

        int base = laneCount / tracks;
        int extra = laneCount % tracks;
        int laneIndex = 0;
        for (int t = 0; t < tracks; t++) {
            int count = base + (t < extra ? 1 : 0);
            for (int i = 0; i < count; i++) {
                Intervention in = byResource.get(laneIndex++);
                Lane lane = new Lane();
                lane.track = t;
                lane.tracks = tracks;
                lane.index = i;
                lane.count = count;
                result.put(in, lane);
            }
        }
        return result;
    }

    /**
     * Compute pixel bounds of a tile inside its track. The returned rectangle is
     * relative to the first track; callers must apply the track vertical offset.
     */
    public static Rectangle computeTileBounds(Intervention i, Lane lane, TimeScaleModel scale) {
        int y1 = scale.timeToY(i.getDateDebut());
        int y2 = scale.timeToY(i.getDateFin());
        int height = Math.max(UIConstants.ROW_BASE_HEIGHT, y2 - y1);

        int[] xs = scale.getColumnXs(i.getDateDebut().toLocalDate());
        int rowUsableWidth = xs[xs.length - 1] - scale.getLeftGutterWidth();
        int laneWidth = lane.count == 0 ? rowUsableWidth : rowUsableWidth / lane.count;
        int x = scale.getLeftGutterWidth() + lane.index * laneWidth;
        return new Rectangle(x, y1, laneWidth, height);
    }

    /**
     * Compute total row height depending on the number of lanes and available
     * width.
     */
    public static int computeRowHeight(int laneCount, int rowUsableWidth) {
        if (laneCount <= 0) {
            return UIConstants.ROW_BASE_HEIGHT;
        }
        int tracks = (int) Math.ceil((laneCount * (double) UIConstants.MIN_TILE_WIDTH) / rowUsableWidth);
        tracks = Math.max(tracks, 1);
        return UIConstants.ROW_BASE_HEIGHT * tracks + UIConstants.TRACK_V_GUTTER * (tracks - 1);
    }
}
