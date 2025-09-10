package com.materiel.client.view.planning.layout;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Modèle de grille temporelle partagé (header + grille). */
public interface TimeGridModel {
    int getLeftGutterWidth();
    /** 8 bornes X pour 7 jours (inclut la gouttière). */
    int[] getDayColumnXs(LocalDate weekStart);
    int timeToX(LocalDateTime t);
    LocalDateTime xToTime(int x);
    /** Largeur totale des 7 jours (sans la gouttière). */
    int getContentWidth();
}