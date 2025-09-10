package com.materiel.client.view.planning;

import com.materiel.client.model.Intervention;

import java.awt.Rectangle;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility to assign overlapping interventions to columns (lanes).
 */
public final class OverlapLayout {

    private OverlapLayout() {
        // utility
    }

    /** Representation of a column assignment for an intervention. */
    public static class Lane {
        private final Intervention intervention;
        private final int col;
        private int colCount;

        Lane(Intervention intervention, int col) {
            this.intervention = intervention;
            this.col = col;
        }

        public Intervention getIntervention() {
            return intervention;
        }

        public int getCol() {
            return col;
        }

        public int getColCount() {
            return colCount;
        }

        void setColCount(int colCount) {
            this.colCount = colCount;
        }
    }

    /**
     * Assign columns to interventions for a given resource.
     * @param byResource interventions for a single resource
     * @return lanes describing the column and total column count per overlap group
     */
    public static List<Lane> layoutLanes(List<Intervention> byResource) {
        List<Lane> result = new ArrayList<>();
        if (byResource == null || byResource.isEmpty()) {
            return result;
        }

        List<Intervention> sorted = new ArrayList<>(byResource);
        sorted.sort(Comparator.comparing(Intervention::getDateDebut,
                Comparator.nullsFirst(Comparator.naturalOrder())));

        List<Lane> active = new ArrayList<>();
        List<Lane> group = new ArrayList<>();
        int maxCol = 0;

        for (Intervention i : sorted) {
            LocalDateTime start = i.getDateDebut();
            LocalDateTime end = i.getDateFin();
            if (start == null || end == null) {
                Lane lane = new Lane(i, 0);
                lane.setColCount(1);
                result.add(lane);
                continue;
            }

            active.removeIf(l -> !l.getIntervention().getDateFin().isAfter(start));

            if (active.isEmpty() && !group.isEmpty()) {
                int cols = maxCol + 1;
                for (Lane l : group) {
                    l.setColCount(cols);
                }
                group.clear();
                maxCol = 0;
            }

            Set<Integer> used = new HashSet<>();
            for (Lane l : active) {
                used.add(l.getCol());
            }

            int col = 0;
            while (used.contains(col)) {
                col++;
            }

            Lane lane = new Lane(i, col);
            active.add(lane);
            group.add(lane);
            result.add(lane);
            if (col > maxCol) {
                maxCol = col;
            }
        }

        if (!group.isEmpty()) {
            int cols = maxCol + 1;
            for (Lane l : group) {
                l.setColCount(cols);
            }
        }

        return result;
    }

    /**
     * Compute tile bounds in cell units using the provided scale for vertical metrics.
     * @param i intervention
     * @param lane lane assignment
     * @param scale zoom/time scale
     * @return rectangle representing column/row units
     */
    public static Rectangle computeTileBounds(Intervention i, Lane lane, ZoomModel scale) {
        LocalDateTime start = i.getDateDebut();
        LocalDateTime end = i.getDateFin();
        if (start == null || end == null) {
            return new Rectangle();
        }
        int startMinutes = start.getHour() * 60 + start.getMinute();
        int endMinutes = end.getHour() * 60 + end.getMinute();
        int y = scale.minuteToPixel(startMinutes);
        int h = scale.minuteToPixel(endMinutes - startMinutes);
        return new Rectangle(lane.getCol(), y, lane.getColCount(), h);
    }
}
