package de.precision.analysis.graalvm.resultingData;

import java.util.Map;
import java.util.TreeMap;

import de.precision.analysis.heatmap.Configuration;

public class SimpleModel {
   private String first;
   private String last;
   
   private Counts countTraining;
   
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
}
