package de.precision.analysis.repetitions;

public class PrecisionConfig {
   private final boolean only100k, removeOutliers;
   private final boolean printPicks;
   private final int threads;
   private final String[] types;

   public PrecisionConfig(boolean only100k, boolean removeOutliers, boolean printPicks, int threads, String[] types) {
      this.only100k = only100k;
      this.removeOutliers = removeOutliers;
      this.printPicks = printPicks;
      this.threads = threads;
      this.types = types;
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

}