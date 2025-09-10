package com.materiel.client.view.planning.layout;
import java.awt.Rectangle;
import java.time.LocalDateTime;
import java.util.*;
import static com.materiel.client.view.ui.UIConstants.*;

public final class LaneLayout {
  public static final class Lane {
    public final int index; public final int count; public final int track; public final int tracks;
    public Lane(int index,int count,int track,int tracks){ this.index=index; this.count=count; this.track=track; this.tracks=tracks; }
  }
  public interface StartEnd<T>{ LocalDateTime start(T t); LocalDateTime end(T t); }

  public static <T> Map<T, Lane> computeLanes(List<T> items, StartEnd<T> se, int rowUsableWidth){
    items.sort(Comparator.comparing(se::start));
    // allocation de colonnes (sweep-line)
    List<T> open = new ArrayList<>();
    Map<T,Integer> col = new HashMap<>();
    int maxCols = 0;
    for (T it : items){
      LocalDateTime s = se.start(it);
      LocalDateTime e = se.end(it);
      open.removeIf(o -> !se.end(o).isAfter(s)); // garde les overlaps stricts
      // trouve 1ère colonne libre
      boolean[] used = new boolean[open.size()+1];
      for (T o: open){ used[col.get(o)] = true; }
      int idx=0; while(idx < used.length && used[idx]) idx++;
      col.put(it, idx);
      open.add(it);
      maxCols = Math.max(maxCols, idx+1);
    }
    int tracks = Math.max(1, (int)Math.ceil((maxCols * 1.0 * MIN_TILE_WIDTH) / Math.max(1,rowUsableWidth)));
    // dispatch en tracks basique: colonne k -> track = k % tracks, indexWithinTrack = k / tracks
    Map<T, Lane> out = new LinkedHashMap<>();
    for (T it: items){
      int k = col.get(it);
      int track = k % tracks;
      int indexWithinTrack = k / tracks;
      int countWithinTrack = (int)Math.ceil(maxCols * 1.0 / tracks);
      out.put(it, new Lane(indexWithinTrack, countWithinTrack, track, tracks));
    }
    return out;
  }

  public static int computeRowHeight(int laneCount, int rowUsableWidth){
    int tracks = Math.max(1, (int)Math.ceil((laneCount*1.0*MIN_TILE_WIDTH)/Math.max(1,rowUsableWidth)));
    return ROW_BASE_HEIGHT*tracks + TRACK_V_GUTTER*(tracks-1);
  }

  public static Rectangle computeTileBounds(LocalDateTime start, LocalDateTime end, Lane lane, TimeGridModel grid, int rowY){
    int x1 = grid.timeToX(start);
    int x2 = grid.timeToX(end);
    int y  = rowY + lane.track * (ROW_BASE_HEIGHT + TRACK_V_GUTTER);
    int h  = Math.max(MIN_TILE_HEIGHT, ROW_BASE_HEIGHT-1);
    int w  = Math.max(1, Math.abs(x2-x1));
    int x  = Math.min(x1,x2);
    return new Rectangle(x, y, w, h);
  }
}
