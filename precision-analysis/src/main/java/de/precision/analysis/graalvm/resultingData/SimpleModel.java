package de.precision.analysis.graalvm.resultingData;

import java.util.Map;
import java.util.TreeMap;

import de.precision.analysis.heatmap.Configuration;

public class SimpleModel {
   private String first;
   private String last;
   
   private Counts countTraining;
   private Counts countTesting;
   
   private Map<String, Double> trainingComparisons = new TreeMap<>();
   private Map<Double, Map<String, Integer>> testComparisonFNR = new TreeMap<>();
   
   Map<Double, GraalConfiguration> runs_iterations = new TreeMap<>();

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

   public Map<Double, GraalConfiguration> getRuns_iterations() {
      return runs_iterations;
   }

   public void setRuns_iterations(Map<Double, GraalConfiguration> runs_iterations) {
      this.runs_iterations = runs_iterations;
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

   public Map<String, Double> getTrainingComparisons() {
      return trainingComparisons;
   }

   public void setTrainingComparisons(Map<String, Double> trainingComparisons) {
      this.trainingComparisons = trainingComparisons;
   }

   public Map<Double, Map<String, Integer>> getTestComparisonFNR() {
      return testComparisonFNR;
   }

   public void setTestComparisonFNR(Map<Double, Map<String, Integer>> testComparisonFNR) {
      this.testComparisonFNR = testComparisonFNR;
   }
   
   public void addComparison(double type2error, Map<String, Integer> fnr) {
      testComparisonFNR.put(type2error, fnr);
   }
}
