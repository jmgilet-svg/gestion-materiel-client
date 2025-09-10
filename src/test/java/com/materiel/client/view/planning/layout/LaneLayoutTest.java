package com.materiel.client.view.planning.layout;

import com.materiel.client.model.Intervention;
import com.materiel.client.view.ui.UIConstants;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LaneLayoutTest {
    @Test
    void wrapOccursWhenWidthTooSmall() {
        List<Intervention> interventions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            interventions.add(new Intervention());
        }
        int rowUsableWidth = UIConstants.MIN_TILE_WIDTH * 2; // force wrap
        Map<Intervention, LaneLayout.Lane> lanes = LaneLayout.computeLanes(interventions, rowUsableWidth);
        LaneLayout.Lane lane = lanes.values().iterator().next();
        assertTrue(lane.tracks > 1, "should wrap to multiple tracks");
    }

    @Test
    void rowHeightGrowsWithTracks() {
        int laneCount = 5;
        int rowUsableWidth = UIConstants.MIN_TILE_WIDTH * 2; // forces 3 tracks
        int expected = UIConstants.ROW_BASE_HEIGHT * 3 + UIConstants.TRACK_V_GUTTER * 2;
        assertEquals(expected, LaneLayout.computeRowHeight(laneCount, rowUsableWidth));
    }
}
