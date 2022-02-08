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

   public int getExecutions() {
      return iterations;
   }

   public long getRepetitions() {
      return repetitions;
   }

}