package com.materiel.client.view.planning;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for PlanningBoard utility methods.
 */
class PlanningBoardTest {

    @Test
    void applySnapRoundsToIncrement() {
        PlanningBoard board = new PlanningBoard();
        board.setTimeScale(15);
        LocalDateTime t1 = LocalDateTime.of(2024, 1, 1, 10, 7);
        assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), board.applySnap(t1));
        LocalDateTime t2 = LocalDateTime.of(2024, 1, 1, 10, 53);
        assertEquals(LocalDateTime.of(2024, 1, 1, 11, 0), board.applySnap(t2));
    }
}
