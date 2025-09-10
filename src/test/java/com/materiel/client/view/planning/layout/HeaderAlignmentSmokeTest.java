package com.materiel.client.view.planning.layout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HeaderAlignmentSmokeTest {
  @Test void gridAndHeaderShareModel() {
    TimeGridModel model = new DefaultTimeGridModel(100);
    assertTrue(model.getLeftGutterWidth() > 0, "Left gutter must be > 0");
    int[] xs = model.getDayColumnXs(java.time.LocalDate.now());
    assertNotNull(xs);
    assertTrue(xs.length > 1, "At least two X boundaries expected");
  }
}
