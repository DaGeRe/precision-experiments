package de.precision.analysis.repetitions;

public class ExecutionData {
   private final int vms, iterations, warmup;
   private final long repetitions;
   
   private long overhead = 0;
   private long duration = 0;

   public ExecutionData(final int vms, final int warmup, final int iterations, final long repetitions) {
      this.vms = vms;
      this.warmup = warmup;
      this.iterations = iterations;
      this.repetitions = repetitions;
      if (vms == 0) {
         throw new RuntimeException("VMs must not be 0!");
      }
      if (repetitions == 0) {
         throw new RuntimeException("Iterations must not be 0!");
      }
   }

   public long getOverhead() {
      return overhead;
   }

   public void setOverhead(final long overhead) {
      this.overhead = overhead;
   }

   public long getDuration() {
      return duration;
   }

   public void setDuration(final long duration) {
      this.duration = duration;
   }

   public int getVms() {
      return vms;
   }

   public int getWarmup() {
      return warmup;
   }

   public int getIterations() {
      return iterations;
   }

   public long getRepetitions() {
      return repetitions;
   }

}