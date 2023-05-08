package de.precision.analysis.graalvm.resultingData;

import java.util.LinkedHashMap;

public class PositiveDetections {
   private LinkedHashMap<String, LinkedHashMap<String, Double>> quantiles = new LinkedHashMap<>();
   private LinkedHashMap<String, LinkedHashMap<String, ConfigurationResult>> iterations = new LinkedHashMap<>();

   public LinkedHashMap<String, LinkedHashMap<String, Double>> getQuantiles() {
      return quantiles;
   }

   public void setQuantiles(LinkedHashMap<String, LinkedHashMap<String, Double>> quantiles) {
      this.quantiles = quantiles;
   }

   public LinkedHashMap<String, LinkedHashMap<String, ConfigurationResult>> getIterations() {
      return iterations;
   }

   public void setIterations(LinkedHashMap<String, LinkedHashMap<String, ConfigurationResult>> iterations) {
      this.iterations = iterations;
   }
}
