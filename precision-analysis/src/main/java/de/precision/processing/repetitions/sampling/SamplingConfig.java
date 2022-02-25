package de.precision.processing.repetitions.sampling;

public class SamplingConfig{
   private final int vms;
   private final String testclazz;
   private final int samplingExecutions;
   
   public SamplingConfig(final int vms, final String testclazz, final int samplingExecutions) {
      this.vms = vms;
      this.testclazz = testclazz;
      this.samplingExecutions = samplingExecutions;
      if (vms == 0) {
         throw new RuntimeException("VMs must not be 0!");
      }
   }

   public SamplingConfig(final int vms, final String testclazz) {
      this.vms = vms;
      this.testclazz = testclazz;
      samplingExecutions = 10000;
      if (vms == 0) {
         throw new RuntimeException("VMs must not be 0!");
      }
   }

   public int getVms() {
      return vms;
   }

   public String getTestclazz() {
      return testclazz;
   }

   public int getSamplingExecutions() {
      return samplingExecutions;
   }
   
}