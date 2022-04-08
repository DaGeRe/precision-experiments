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

   @Override
   public String toString() {
      return "Repetitions: " + repetitions + " VMs: " + VMs + " iterations: " + iterations;
   }

}

public class MinimalFeasibleConfigurationDeterminer {

   private double minimalF1Score = 99;

   public MinimalFeasibleConfigurationDeterminer(final double minimalF1Score) {
      this.minimalF1Score = minimalF1Score;
   }

   public Configuration getMinimalFeasibleConfiguration(final PrecisionData data) {
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

            if (repetitionCandidate != null) {
               int legalIterations = Math.max(candidate.getIterations(), repetitionCandidate.getIterations());
               Configuration candidate2 = new Configuration(repetitions, candidate.getVMs(), legalIterations);
               repetitionCandidate = candidate2;
            } else {
               repetitionCandidate = candidate;
            }
         }

         if (repetitionCandidate != null) {
            if (minimal != null) {
               int candidateIterations = repetitionCandidate.getIterations() * repetitionCandidate.getRepetitions();
               int newMinimalIterations = minimal.getIterations() * minimal.getRepetitions();
               
               if (repetitionCandidate.getVMs() < minimal.getVMs() || 
                     (repetitionCandidate.getVMs() == minimal.getVMs() && candidateIterations < newMinimalIterations) ||
                     (repetitionCandidate.getVMs() == minimal.getVMs() && candidateIterations == newMinimalIterations && repetitionCandidate.getRepetitions() > minimal.getRepetitions())) {
                  minimal = repetitionCandidate;
               }
            } else {
               minimal = repetitionCandidate;
            }
         }
      }

      return minimal;
   }

   private Configuration getLowestIterationCandidate(final int repetitions, final Entry<Integer, SortedMap<Integer, Double>> vmCount,
         final SortedMap<Integer, Double> iterationReverseMap) {
      Configuration candidate = null;
      for (Entry<Integer, Double> iterationCount : iterationReverseMap.entrySet()) {
         if (iterationCount.getValue() > minimalF1Score) {
            candidate = new Configuration(repetitions, vmCount.getKey(), iterationCount.getKey());
         } else {
            break;
         }
      }
      return candidate;
   }
}
