package de.precision.analysis.repetitions;

class PrecisionConfig{
   final boolean useConfidence, only100k, removeOutliers;

   public PrecisionConfig(boolean useConfidence, boolean only100k, boolean removeOutliers) {
      this.useConfidence = useConfidence;
      this.only100k = only100k;
      this.removeOutliers = removeOutliers;
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
   
}