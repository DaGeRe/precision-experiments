package de.precision.analysis.heatmap;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class TestResolutionChangeMerging {
   
   @Test
   public void testResolutionChangeMerging() {
      List<WorkloadHeatmap> mappings = new LinkedList<>();

      mappings.add(buildResolutionChangeHeatmap());

      WorkloadHeatmap merged = MergeHeatmaps.mergeHeatmaps(mappings);
      
      Assert.assertEquals(10, merged.getOneHeatmap().get(1).get(10), 0.01);
      Assert.assertEquals(10, merged.getOneHeatmap().get(10).get(10), 0.01);
      Assert.assertEquals(20, merged.getOneHeatmap().get(10).get(20), 0.01);
      Assert.assertEquals(20, merged.getOneHeatmap().get(15).get(20), 0.01);
      Assert.assertEquals(30, merged.getOneHeatmap().get(25).get(30), 0.01);
   }
   
   private WorkloadHeatmap buildResolutionChangeHeatmap() {
      WorkloadHeatmap map = new WorkloadHeatmap();

      for (int i = 0; i < 10; i++) {
         map.getOneHeatmap().put(i, TestMerging.createSimpleMap(0.0));
      }
      for (int i = 10; i <= 100; i+=10) {
         map.getOneHeatmap().put(i, TestMerging.createSimpleMap(0.0));
      }
      return map;
   }
   
}
