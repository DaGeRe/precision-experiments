package de.precision.analysis.graalvm.resultingData;

public class GraalConfiguration {
   int warmup, runs, iterations;

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

}
