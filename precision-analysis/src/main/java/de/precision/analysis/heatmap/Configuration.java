package de.precision.analysis.heatmap;

public class Configuration {
   private final int repetitions;
   private final int VMs;
   private final int iterations;

   public Configuration(final int repetitions, final int vMs, final int iterations) {
      this.repetitions = repetitions;
      VMs = vMs;
      this.iterations = iterations;
   }

   public int getRepetitions() {
      return repetitions;
   }

   public int getVMs() {
      return VMs;
   }

   public int getIterations() {
      return iterations;
   }

   @Override
   public String toString() {
      return "Repetitions: " + repetitions + " VMs: " + VMs + " iterations: " + iterations;
   }

}