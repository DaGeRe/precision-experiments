package de.precision.analysis.repetitions;

import picocli.CommandLine.Option;

public class PrecisionConfigMixin {
   @Option(names = { "-printPicks", "--printPicks" }, description = "Print the picked values summaries (for debugging)")
   private boolean printPicks;

   @Option(names = { "-threads", "--threads" }, description = "Count of threads for analysis")
   private int threads = 2;

   @Option(names = { "-iterationResolution", "--iterationResolution" }, description = "Resolution for iteration count analysis (by default: 50 steps for iteration count)")
   private int iterationResolution = 50;

   @Option(names = { "-vmResolution", "--vmResolution" }, description = "Resolution for VM count analysis (by default: 50 steps for VM count)")
   private int vmResolution = 20;
   
   @Option(names = { "-minVMs", "--minVMs" }, description = "Minimum amount of VMs that should be analyzed for detailed analysis (default 0, so all VMs are analyzed)")
   private int minVMs = -1;
   
   @Option(names = { "-maxVMs", "--maxVMs" }, description = "Maximum amount of VMs that should be analyzed for detailed analysis (default -1, so all VMs are analyzed)")
   private int maxVMs = -1;

   @Option(names = { "-statisticalTests", "--statisticalTests" }, description = "Statistical tests that should be used (either ALL or ALL_NO_BIMODAL)")
   private StatisticalTestList statisticalTestList = StatisticalTestList.ALL_NO_BIMODAL_NO_CONFIDENCE;
   
   @Option(names = { "-outlierRemoval", "--outlierRemoval" }, description = "Whether to remove outliers (default: false)")
   private boolean outlierRemoval = false;

   public boolean isPrintPicks() {
      return printPicks;
   }

   public void setPrintPicks(final boolean printPicks) {
      this.printPicks = printPicks;
   }

   public int getThreads() {
      return threads;
   }

   public void setThreads(final int threads) {
      this.threads = threads;
   }

   public int getIterationResolution() {
      return iterationResolution;
   }

   public void setIterationResolution(final int iterationResolution) {
      this.iterationResolution = iterationResolution;
   }

   public int getVmResolution() {
      return vmResolution;
   }

   public void setVmResolution(final int vmResolution) {
      this.vmResolution = vmResolution;
   }
   
   public void setMinVMs(final int minVMs) {
      this.minVMs = minVMs;
   }
   
   public int getMinVMs() {
      return minVMs;
   }

   public int getMaxVMs() {
      return maxVMs;
   }

   public void setMaxVMs(final int maxVMs) {
      this.maxVMs = maxVMs;
   }

   public StatisticalTestList getStatisticalTestList() {
      return statisticalTestList;
   }

   public void setStatisticalTestList(final StatisticalTestList statisticalTestList) {
      this.statisticalTestList = statisticalTestList;
   }
   
   public boolean isOutlierRemoval() {
      return outlierRemoval;
   }
   
   public void setOutlierRemoval(final boolean outlierRemoval) {
      this.outlierRemoval = outlierRemoval;
   }
}
