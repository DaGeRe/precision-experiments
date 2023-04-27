package de.precision.analysis.heatmap;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.precision.analysis.graalvm.ConfigurationDeterminer;

import java.util.SortedMap;
import java.util.TreeMap;

public class MinimalFeasibleConfigurationDeterminer {

   private static final Logger LOG = LogManager.getLogger(MinimalFeasibleConfigurationDeterminer.class);
   
   private final double minimalF1Score;

   public MinimalFeasibleConfigurationDeterminer(final double minimalF1Score) {
      this.minimalF1Score = minimalF1Score;
   }

   public Map<Integer, Configuration> getMinimalFeasibleConfiguration(final PrecisionData data) {
//      Configuration minimal = new Configuration(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
      
      Map<Integer, Configuration> minimalConfigurations = new TreeMap<>();

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
            minimalConfigurations.put(repetitionCandidate.getRepetitions(), repetitionCandidate);
         }
      }

      return minimalConfigurations;
   }

   private Configuration getLowestIterationCandidate(final int repetitions, final Entry<Integer, SortedMap<Integer, Double>> vmCount,
         final SortedMap<Integer, Double> iterationReverseMap) {
      Configuration candidate = null;
      for (Entry<Integer, Double> iterationCount : iterationReverseMap.entrySet()) {
         
         LOG.trace("Checking " + iterationCount.getValue() + " against " + minimalF1Score);
         
         if (iterationCount.getValue() > minimalF1Score) {
            Integer iterations = iterationCount.getKey();
            candidate = new Configuration(repetitions, vmCount.getKey(), iterations);
         } else {
            break;
         }
      }
      return candidate;
   }
}
