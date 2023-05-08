package de.precision.analysis.graalvm.resultingData;

import java.util.Map;

public class ConfigurationResult {
   private int iterations;
   private Map<String, Integer> falsePositives;
   private Map<String, Integer> falseNegatives;
   
   public ConfigurationResult(int iterations, Map<String, Integer> falsePositives, Map<String, Integer> falseNegatives) {
      this.iterations = iterations;
      this.falsePositives = falsePositives;
      this.falseNegatives = falseNegatives;
   }

   public int getIterations() {
      return iterations;
   }

   public void setIterations(int iterations) {
      this.iterations = iterations;
   }

   public Map<String, Integer> getFalsePositives() {
      return falsePositives;
   }

   public void setFalsePositives(Map<String, Integer> falsePositives) {
      this.falsePositives = falsePositives;
   }

   public Map<String, Integer> getFalseNegatives() {
      return falseNegatives;
   }

   public void setFalseNegatives(Map<String, Integer> falseNegatives) {
      this.falseNegatives = falseNegatives;
   }
}