package com.materiel.client.view.planning.layout;
import java.time.*;
public interface TimeGridModel {
  int getLeftGutterWidth();
  int[] getDayColumnXs(LocalDate weekStart);
  int timeToX(LocalDateTime t);
  LocalDateTime xToTime(int x);
  int getContentWidth(); // largeur sans gouttière, pour le calcul de wrap
}
