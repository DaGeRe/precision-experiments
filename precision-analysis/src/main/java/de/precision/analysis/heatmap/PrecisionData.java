package de.precision.analysis.heatmap;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Saves the map from repetition count to the heatmap data of the respective repetition count
 *
 */
public class PrecisionData {
   private final Map<Integer, WorkloadHeatmap> precisionData = new LinkedHashMap<>();
 
   public Map<Integer, WorkloadHeatmap> getPrecisionData() {
      return precisionData;
   }

   public void addData(final int repetitions, final int vMs, final int iterations, final double value) {
      
      WorkloadHeatmap heatmap = precisionData.get(repetitions);
      if (heatmap == null) {
         heatmap = new WorkloadHeatmap();
         precisionData.put(repetitions, heatmap);
      }
      
      heatmap.add(vMs, iterations, value);
   }
   
}
