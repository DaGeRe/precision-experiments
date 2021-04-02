package de.precision.processing.repetitions.sampling;

public class SamplingConfig{
   private final int vms;
   private final String testclazz;
   private final boolean printPicks, useConfidenceInterval;
   private final int samplingExecutions;
   
   public SamplingConfig(final int vms, final String testclazz, final boolean printPicks, final boolean useConfidenceInterval, final int samplingExecutions) {
      this.vms = vms;
      this.testclazz = testclazz;
      this.printPicks = printPicks;
      this.useConfidenceInterval = useConfidenceInterval;
      this.samplingExecutions = samplingExecutions;
   }

   public SamplingConfig(final int vms, final String testclazz, final boolean printPicks, final boolean useConfidenceInterval) {
      this.vms = vms;
      this.testclazz = testclazz;
      this.printPicks = printPicks;
      this.useConfidenceInterval = useConfidenceInterval;
      samplingExecutions = 10000;
   }

   public int getVms() {
      return vms;
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