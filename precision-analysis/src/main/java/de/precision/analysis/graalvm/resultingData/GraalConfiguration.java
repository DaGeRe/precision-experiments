package de.precision.analysis.graalvm.resultingData;

public class GraalConfiguration {
   int warmup, runs, iterations;
   double type2error;
   double type2error_above1percent;

   public int getWarmup() {
      return warmup;
   }

   public void setWarmup(int warmup) {
      this.warmup = warmup;
   }

   public int getRuns() {
      return runs;
   }

   public void setRuns(int runs) {
      this.runs = runs;
   }

   public int getIterations() {
      return iterations;
   }

   public void setIterations(int iterations) {
      this.iterations = iterations;
   }

   public double getType2error() {
      return type2error;
   }

   public void setType2error(double type2error) {
      this.type2error = type2error;
   }

   public double getType2error_above1percent() {
      return type2error_above1percent;
   }

   public void setType2error_above1percent(double type2error_above1percent) {
      this.type2error_above1percent = type2error_above1percent;
   }
}
