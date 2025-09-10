package com.materiel.client.view.planning;

import com.materiel.client.model.Intervention;
import com.materiel.client.view.planning.layout.TimeScaleModel;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HitTestTopMostTileTest {
    @Test
    void pickReturnsTopMostTile() {
        PlanningBoard board = new PlanningBoard();
        board.setTimeScaleModel(new TimeScaleModel(100));
        Intervention bottom = new Intervention();
        bottom.setId(1L);
        Intervention top = new Intervention();
        top.setId(2L);
        Rectangle r1 = new Rectangle(10, 10, 100, 50);
        Rectangle r2 = new Rectangle(10, 10, 100, 50);
        Map<Intervention, Rectangle> bounds = new LinkedHashMap<>();
        bounds.put(bottom, r1);
        bounds.put(top, r2); // top drawn last
        board.setTileBounds(bounds);
        Optional<Intervention> pick = board.pickTileAt(new Point(20, 20));
        assertTrue(pick.isPresent());
        assertEquals(top, pick.get());
    }
}
