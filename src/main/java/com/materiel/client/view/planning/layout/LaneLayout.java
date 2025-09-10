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

    /** Compute lanes for interventions. */
    public static Map<Intervention, Lane> computeLanes(List<Intervention> byResource, int rowUsableWidth) {
        Map<Intervention, Lane> result = new LinkedHashMap<>();
        int laneCount = byResource.size();
        int tracks = (int) Math.ceil((laneCount * (double) UIConstants.MIN_TILE_WIDTH) / rowUsableWidth);
        tracks = Math.max(tracks, 1);
        int lanesPerTrack = (int) Math.ceil(laneCount / (double) tracks);
        for (int i = 0; i < byResource.size(); i++) {
            Intervention in = byResource.get(i);
            Lane lane = new Lane();
            lane.track = i / lanesPerTrack;
            lane.tracks = tracks;
            lane.index = i % lanesPerTrack;
            lane.count = lanesPerTrack;
            result.put(in, lane);
        }
        return result;
    }

    /** Compute pixel bounds of a tile. */
    public static Rectangle computeTileBounds(Intervention i, Lane lane, TimeScaleModel scale) {
        int y1 = scale.timeToY(i.getDateDebut());
        int y2 = scale.timeToY(i.getDateFin());
        int height = Math.max(UIConstants.ROW_BASE_HEIGHT, y2 - y1);
        int trackOffset = lane.track * (UIConstants.ROW_BASE_HEIGHT + UIConstants.TRACK_V_GUTTER);
        y1 += trackOffset;

        int[] xs = scale.getColumnXs(i.getDateDebut().toLocalDate());
        int rowUsableWidth = xs[xs.length - 1] - scale.getLeftGutterWidth();
        int laneWidth = lane.count == 0 ? rowUsableWidth : rowUsableWidth / lane.count;
        int x = scale.getLeftGutterWidth() + lane.index * laneWidth;
        return new Rectangle(x, y1, laneWidth, height);
    }
}
