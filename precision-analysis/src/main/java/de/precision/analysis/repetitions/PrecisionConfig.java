package de.precision.analysis.repetitions;

public class PrecisionConfig {
   private final boolean removeOutliers;
   private final boolean printPicks;
   private final int threads;
   private final StatisticalTests[] types;
   private final int iterationResolution;
   private final int vmResolution;
   private final int minVMs, maxVMs;

   public PrecisionConfig(final boolean removeOutliers, final boolean printPicks, final int threads, final StatisticalTests[] types, 
         final int iterationResolution, final int vmResolution, final int minVMs, final int maxVMs) {
      this.removeOutliers = removeOutliers;
      this.printPicks = printPicks;
      this.threads = threads;
      this.types = types;
      this.iterationResolution = iterationResolution;
      this.vmResolution = vmResolution;
      this.minVMs = minVMs;
      this.maxVMs = maxVMs;
   }

   public boolean isRemoveOutliers() {
      return removeOutliers;
   }

   public boolean isPrintPicks() {
      return printPicks;
   }
   
   public int getThreads() {
      return threads;
   }
   
   public StatisticalTests[] getTypes() {
      return types;
   }
   
   public int getIterationResolution() {
      return iterationResolution;
   }
   
   public int getVmResolution() {
      return vmResolution;
   }
   
   public int getMinVMs() {
      return minVMs;
   }
   
   public int getMaxVMs() {
      return maxVMs;
   }
}