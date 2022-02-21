package de.precision.analysis.repetitions;

public class PrecisionConfig {
   private final boolean useConfidence, only100k, removeOutliers;
   private final boolean printPicks;
   private final String[] types;

   public PrecisionConfig(boolean useConfidence, boolean only100k, boolean removeOutliers, boolean printPicks, String[] types) {
      this.useConfidence = useConfidence;
      this.only100k = only100k;
      this.removeOutliers = removeOutliers;
      this.printPicks = printPicks;
      this.types = types;
   }

   public boolean isUseConfidence() {
      return useConfidence;
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
   
   public String[] getTypes() {
      return types;
   }

}