package de.precision.analysis.heatmap;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class TestMerging {

   @Test
   public void testOneMerging() {
      List<WorkloadHeatmap> mappings = new LinkedList<>();

      mappings.add(buildSimpleHeatmap());

      WorkloadHeatmap merged = MergeHeatmaps.mergeHeatmaps(mappings);
      
      Assert.assertEquals(10, merged.getOneHeatmap().get(10).get(10), 0.01);
      Assert.assertEquals(20, merged.getOneHeatmap().get(10).get(20), 0.01);
      Assert.assertEquals(35, merged.getOneHeatmap().get(20).get(30), 0.01);
   }

   @Test
   public void testTwoMerging() {
      List<WorkloadHeatmap> mappings = new LinkedList<>();

      mappings.add(buildSimpleHeatmap());
      mappings.add(buildBiggerHeatmap());

      WorkloadHeatmap merged = MergeHeatmaps.mergeHeatmaps(mappings);
      
      Assert.assertEquals(12.5, merged.getOneHeatmap().get(10).get(10), 0.01);
      Assert.assertEquals(22.5, merged.getOneHeatmap().get(10).get(20), 0.01);
      Assert.assertEquals(37.5, merged.getOneHeatmap().get(20).get(30), 0.01);
   }
   
   private WorkloadHeatmap buildBiggerHeatmap() {
      WorkloadHeatmap map = new WorkloadHeatmap();

      map.getOneHeatmap().put(10, createSimpleMap(5.0));
      map.getOneHeatmap().put(20, createSimpleMap(10.0));
      map.getOneHeatmap().put(30, createSimpleMap(15.0));
      return map;
   }

   private WorkloadHeatmap buildSimpleHeatmap() {
      WorkloadHeatmap map = new WorkloadHeatmap();

      map.getOneHeatmap().put(10, createSimpleMap(0.0));
      map.getOneHeatmap().put(20, createSimpleMap(5.0));
      map.getOneHeatmap().put(30, createSimpleMap(10.0));
      return map;
   }
   
   public static SortedMap<Integer, Double> createSimpleMap(final double base) {
      SortedMap<Integer, Double> iterationMap = new TreeMap<Integer, Double>();
      iterationMap.put(10, base + 10.0);
      iterationMap.put(20, base + 20.0);
      iterationMap.put(30, base + 30.0);
      return iterationMap;
   }
}
