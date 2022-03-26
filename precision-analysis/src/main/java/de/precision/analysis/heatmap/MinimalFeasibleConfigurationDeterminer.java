package de.precision.analysis.heatmap;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

class Configuration {
   private final int repetitions;
   private final int VMs;
   private final int iterations;

   public Configuration(final int repetitions, final int vMs, final int iterations) {
      this.repetitions = repetitions;
      VMs = vMs;
      this.iterations = iterations;
   }

   public int getRepetitions() {
      return repetitions;
   }

   public int getVMs() {
      return VMs;
   }

   public int getIterations() {
      return iterations;
   }

}

public class MinimalFeasibleConfigurationDeterminer {
   public static Configuration getMinimalFeasibleConfiguration(final PrecisionData data) {
      Configuration minimal = new Configuration(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);

      for (Map.Entry<Integer, WorkloadHeatmap> repetitionHeatmap : data.getPrecisionData().entrySet()) {
         int repetitions = repetitionHeatmap.getKey();

         SortedMap<Integer, SortedMap<Integer, Double>> reverseOrderMap = new TreeMap<>(Collections.reverseOrder());
         reverseOrderMap.putAll(repetitionHeatmap.getValue().getOneHeatmap());

         Configuration repetitionCandidate = null;
         
         for (Entry<Integer, SortedMap<Integer, Double>> vmCount : reverseOrderMap.entrySet()) {

            SortedMap<Integer, Double> iterationReverseMap = new TreeMap<>(Collections.reverseOrder());
            iterationReverseMap.putAll(vmCount.getValue());

            Configuration candidate = getLowestIterationCandidate(repetitions, vmCount, iterationReverseMap);
            if (candidate == null) {
               break;
            }
            repetitionCandidate = candidate;
         }
         
         if (repetitionCandidate != null) {
            minimal = repetitionCandidate;
         }
      }

      return minimal;
   }

   private static Configuration getLowestIterationCandidate(final int repetitions, final Entry<Integer, SortedMap<Integer, Double>> vmCount, final SortedMap<Integer, Double> iterationReverseMap) {
      Configuration candidate = null;
      for (Entry<Integer, Double> iterationCount : iterationReverseMap.entrySet()) {
         if (iterationCount.getValue() > 99.0) {
            candidate = new Configuration(repetitions, vmCount.getKey(), iterationCount.getKey());
         } else {
            break;
         }
      }
      return candidate;
   }
}
