package de.precision.analysis.graalvm;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ComparisonCollection {
   private final Map<String, Map<String, Comparison>> comparisons = new LinkedHashMap<>();
   
   
   
   public void addComparison(String benchmark, String comparisonId, Comparison comparison) {
      Map<String, Comparison> benchmarkComparisons = comparisons.get(benchmark);
      if (benchmarkComparisons == null) {
         benchmarkComparisons = new HashMap<>();
         comparisons.put(benchmark, benchmarkComparisons);
      }
      benchmarkComparisons.put(comparisonId, comparison);
   }


   public Map<String, Map<String, Comparison>> getComparisons() {
      return comparisons;
   }
}
