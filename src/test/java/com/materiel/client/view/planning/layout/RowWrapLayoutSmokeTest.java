package com.materiel.client.view.planning.layout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RowWrapLayoutSmokeTest {
  @Test void computeRowHeightIncreasesWithLanes() {
    int w = 300;
    int h1 = LaneLayout.computeRowHeight(1, w);
    int h5 = LaneLayout.computeRowHeight(5, w);
    assertTrue(h5 >= h1, "Row height must grow with more lanes when width limited");
  }
}
