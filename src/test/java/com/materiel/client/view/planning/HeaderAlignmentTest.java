package com.materiel.client.view.planning;

import com.materiel.client.view.planning.layout.TimeScaleModel;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class HeaderAlignmentTest {
    @Test
    void headerAndBoardShareSameColumns() {
        TimeScaleModel model = new TimeScaleModel(100);
        TimelineHeader header = new TimelineHeader(model);
        PlanningBoard board = new PlanningBoard();
        board.setTimeScaleModel(model);
        int[] hx = header.getColumnXs(LocalDate.now());
        int[] bx = board.getColumnXs(LocalDate.now());
        assertArrayEquals(hx, bx, "header and board must align");
    }
}
