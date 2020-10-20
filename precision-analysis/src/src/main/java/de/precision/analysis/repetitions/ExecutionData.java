package de.precision.analysis.repetitions;

public class ExecutionData {
   private final int vms, warmup, executions, repetitions;

   private long overhead = 0;
   private long duration = 0;

   public ExecutionData(int vms, int warmup, int executions, int repetitions) {
      this.vms = vms;
      this.warmup = warmup;
      this.executions = executions;
      this.repetitions = repetitions;
   }

   public long getOverhead() {
      return overhead;
   }

   public void setOverhead(long overhead) {
      this.overhead = overhead;
   }

   public long getDuration() {
      return duration;
   }

   public void setDuration(long duration) {
      this.duration = duration;
   }

   public int getVms() {
      return vms;
   }

   public int getWarmup() {
      return warmup;
   }

   public int getExecutions() {
      return executions;
   }

   public int getRepetitions() {
      return repetitions;
   }

}