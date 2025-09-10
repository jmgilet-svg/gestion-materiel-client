package com.materiel.client.view.planning;

import com.materiel.client.view.planning.layout.DefaultTimeGridModel;
import com.materiel.client.view.planning.layout.TimeGridModel;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class HeaderAlignmentTest {
    @Test
    void headerAndBoardShareSameModel() {
        TimeGridModel model = new DefaultTimeGridModel(LocalDate.now(), 100);
        TimelineHeader header = new TimelineHeader(model);
        PlanningBoard board = new PlanningBoard();
        board.setTimeGridModel(model);
        assertSame(model, header.getModel());
        assertSame(model, board.getTimeGridModel());
    }
}
