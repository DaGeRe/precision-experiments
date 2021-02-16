package de.precision.processing.repetitions.sampling;

public class SamplingConfig{
   private final int vms;
   private final boolean removeOutliers;
   private final String testclazz;
   private final boolean printPicks, useConfidenceInterval;
   private final int samplingExecutions;
   
   public SamplingConfig(int vms, boolean removeOutliers, String testclazz, boolean printPicks, boolean useConfidenceInterval, int samplingExecutions) {
      this.vms = vms;
      this.removeOutliers = removeOutliers;
      this.testclazz = testclazz;
      this.printPicks = printPicks;
      this.useConfidenceInterval = useConfidenceInterval;
      this.samplingExecutions = samplingExecutions;
   }

   public SamplingConfig(int vms, boolean removeOutliers, String testclazz, boolean printPicks, boolean useConfidenceInterval) {
      this.vms = vms;
      this.removeOutliers = removeOutliers;
      this.testclazz = testclazz;
      this.printPicks = printPicks;
      this.useConfidenceInterval = useConfidenceInterval;
      samplingExecutions = 10000;
   }

   public int getVms() {
      return vms;
   }

   public boolean isRemoveOutliers() {
      return removeOutliers;
   }

   public String getTestclazz() {
      return testclazz;
   }

   public boolean isPrintPicks() {
      return printPicks;
   }

   public boolean isUseConfidenceInterval() {
      return useConfidenceInterval;
   }

   public int getSamplingExecutions() {
      return samplingExecutions;
   }
   
}