package com.materiel.client.view.planning;

import com.materiel.client.model.Intervention;
import com.materiel.client.view.planning.layout.LaneLayout;
import com.materiel.client.view.planning.layout.TimeScaleModel;
import com.materiel.client.view.planning.UIConstants;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/** Tests that board applies track vertical offsets when laying out tiles. */
public class PlanningBoardTrackOffsetTest {
    @Test
    void tilesAreOffsetByTrack() {
        PlanningBoard board = new PlanningBoard();
        TimeScaleModel scale = new TimeScaleModel(100);
        Intervention in = new Intervention();
        in.setId(1L);
        in.setDateDebut(LocalDateTime.of(2024,1,1,0,0));
        in.setDateFin(LocalDateTime.of(2024,1,1,1,0));

        LaneLayout.Lane lane = new LaneLayout.Lane();
        lane.index = 0;
        lane.count = 1;
        lane.track = 1; // second track
        lane.tracks = 2;

        Map<Intervention, LaneLayout.Lane> lanes = new LinkedHashMap<>();
        lanes.put(in, lane);

        board.layoutTiles(lanes, scale);

        int x = scale.getLeftGutterWidth() + 10;
        // y within track 0 should miss
        Optional<Intervention> miss = board.pickTileAt(new Point(x, 10));
        assertTrue(miss.isEmpty());

        int trackOffsetY = UIConstants.ROW_BASE_HEIGHT + UIConstants.TRACK_V_GUTTER + 10;
        Optional<Intervention> hit = board.pickTileAt(new Point(x, trackOffsetY));
        assertTrue(hit.isPresent());
        assertEquals(in, hit.get());
    }
}
