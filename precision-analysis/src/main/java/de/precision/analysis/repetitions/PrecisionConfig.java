package de.precision.analysis.repetitions;

public class PrecisionConfig {
   private final boolean removeOutliers;
   private final boolean printPicks;
   private final int threads;
   private final String[] types;
   private final int iterationResolution;
   private final int vmResolution;
   private final int maxVMs;

   public PrecisionConfig(final boolean removeOutliers, final boolean printPicks, final int threads, final String[] types, 
         final int iterationResolution, final int vmResolution, final int maxVMs) {
      this.removeOutliers = removeOutliers;
      this.printPicks = printPicks;
      this.threads = threads;
      this.types = types;
      this.iterationResolution = iterationResolution;
      this.vmResolution = vmResolution;
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
   
   public String[] getTypes() {
      return types;
   }
   
   public int getIterationResolution() {
      return iterationResolution;
   }
   
   public int getVmResolution() {
      return vmResolution;
   }
   
   public int getMaxVMs() {
      return maxVMs;
   }
}