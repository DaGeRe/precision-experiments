package de.precision.analysis.graalvm.resultingData;

import java.util.LinkedHashMap;
import java.util.Map;

import de.precision.analysis.heatmap.Configuration;

public class RegressionDetectionModel {
   private String first;
   private String last;
   private Counts countTraining;
   private Counts countTesting;
   
   private PositiveDetections positive = new PositiveDetections();

   public String getFirst() {
      return first;
   }

   public void setFirst(String first) {
      this.first = first;
   }

   public String getLast() {
      return last;
   }

   public void setLast(String last) {
      this.last = last;
   }

   public PositiveDetections getPositive() {
      return positive;
   }

   public void setPositive(PositiveDetections positive) {
      this.positive = positive;
   }

   public Counts getCountTraining() {
      return countTraining;
   }

   public void setCountTraining(Counts countTraining) {
      this.countTraining = countTraining;
   }

   public Counts getCountTesting() {
      return countTesting;
   }

   public void setCountTesting(Counts countTesting) {
      this.countTesting = countTesting;
   }

   public synchronized void addDetection(int vmsOld, int vmsNew, double type2error, double realError, 
         ConfigurationResult result) {
      String key = vmsOld + "-" + vmsNew;
      LinkedHashMap<String, Double> quantile = positive.getQuantiles().get(key);
      if (quantile == null) {
         quantile = new LinkedHashMap<>();
         positive.getQuantiles().put(key, quantile);
      }
      quantile.put("" + type2error, realError);

      LinkedHashMap<String, ConfigurationResult> iterations = positive.getIterations().get(key);
      if (iterations == null) {
         iterations = new LinkedHashMap<>();
         positive.getIterations().put(key, iterations);
      }
      iterations.put("" + type2error, result);
   }
}
