package com.materiel.client.view.planning;

import com.materiel.client.model.Intervention;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for overlap lane assignment.
 */
class OverlapLayoutTest {

    private Intervention make(int sh, int sm, int eh, int em) {
        Intervention i = new Intervention();
        i.setDateDebut(LocalDateTime.of(2024, 1, 1, sh, sm));
        i.setDateFin(LocalDateTime.of(2024, 1, 1, eh, em));
        return i;
        }

    @Test
    void assignColumnsForOverlaps() {
        Intervention a = make(8, 0, 9, 0);
        Intervention b = make(8, 30, 9, 30);
        Intervention c = make(9, 30, 10, 0);
        List<OverlapLayout.Lane> lanes = OverlapLayout.layoutLanes(List.of(a, b, c));
        OverlapLayout.Lane la = lanes.stream().filter(l -> l.getIntervention() == a).findFirst().orElseThrow();
        OverlapLayout.Lane lb = lanes.stream().filter(l -> l.getIntervention() == b).findFirst().orElseThrow();
        OverlapLayout.Lane lc = lanes.stream().filter(l -> l.getIntervention() == c).findFirst().orElseThrow();
        assertEquals(0, la.getCol());
        assertEquals(1, lb.getCol());
        assertEquals(2, la.getColCount());
        assertEquals(2, lb.getColCount());
        assertEquals(0, lc.getCol());
        assertEquals(1, lc.getColCount());
    }
}
