package de.precision.analysis.repetitions;

public class PrecisionConfig {
   private final boolean only100k, removeOutliers;
   private final boolean printPicks;
   private final int threads;
   private final String[] types;
   private final int iterationResolution;
   private final int vmResolution;

   public PrecisionConfig(boolean only100k, boolean removeOutliers, boolean printPicks, int threads, String[] types, int iterationResolution, int vmResolution) {
      this.only100k = only100k;
      this.removeOutliers = removeOutliers;
      this.printPicks = printPicks;
      this.threads = threads;
      this.types = types;
      this.iterationResolution = iterationResolution;
      this.vmResolution = vmResolution;
   }

   public boolean isOnly100k() {
      return only100k;
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
}