package de.precision.analysis.repetitions;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.kopemedata.Kopemedata;

public class PrecisionPlotHandler {

   private static final Logger LOG = LogManager.getLogger(PrecisionPlotHandler.class);

   private final Map<String, Kopemedata> testcasesV1;
   private final Map<String, Kopemedata> testcasesV2;

   private final ExecutorService pool;
   private final long repetitions;
   private final PrecisionConfig precisionConfig;
   private WritingData writingData;

   public PrecisionPlotHandler(final Map<String, Kopemedata> testcasesV1, final Map<String, Kopemedata> testcasesV2, final ExecutorService pool, final long repetitions,
         final PrecisionConfig precisionConfig,
         final WritingData writingData) {
      this.testcasesV1 = testcasesV1;
      this.testcasesV2 = testcasesV2;
      this.pool = pool;
      this.repetitions = repetitions;
      this.precisionConfig = precisionConfig;
      this.writingData = writingData;
   }

   public void handleAllParameters(final int maxVMs, final int maxIterations, boolean useWarmup) throws IOException {
      final int iterationStepSize = Math.max(maxIterations / precisionConfig.getIterationResolution(), 1);
      LOG.debug("Step size: {}", iterationStepSize);
      int usedMax = useWarmup ? maxIterations / 2 : maxIterations;
      for (int iterations = iterationStepSize; iterations <= usedMax; iterations += iterationStepSize) {

         final int vmStepSize = Math.max(1, maxVMs / precisionConfig.getVmResolution());
         
         int minVmCount = Math.max(vmStepSize, precisionConfig.getMinVMs());
         if (minVmCount == 1) {
            minVmCount = 2;
         }
         int usedMaxVMs = getUsedMaxVMs(maxVMs);

         for (int vms = minVmCount; vms <= usedMaxVMs; vms += vmStepSize) {
            LOG.info("Warmup: {} Executions: {} VMs: {}", iterations, iterations, vms);
            int warmup = useWarmup ? iterations : 0;
            executeVersionHandling(new ExecutionData(vms, warmup, iterations, repetitions));
         }
         // }
      }
   }

   private int getUsedMaxVMs(final int maxVMs) {
      int usedMaxVMs;
      if (precisionConfig.getMaxVMs() != -1 && precisionConfig.getMaxVMs() < maxVMs) {
         usedMaxVMs = precisionConfig.getMaxVMs();
      } else {
         usedMaxVMs = maxVMs;
      }
      return usedMaxVMs;
   }

   public void handleOnlyVMs(final int maxVMs, final int maxExecutions) throws IOException {
      int warmup = maxExecutions / 2;
      // this.executions = maxExecutions - warmup;
      int executions = maxExecutions - warmup;
      // maxVMs = 100;
      LOG.info("Max VMs: " + maxVMs);
      for (int vms = 5; vms <= 50; vms += 5) {
         LOG.info("Warmup: {} Executions: {} VMs: {}", warmup, executions, vms);
         executeVersionHandling(new ExecutionData(vms, warmup, executions, repetitions));
      }
      if (maxVMs > 50) {
         for (int vms = 60; vms <= Math.min(500, maxVMs); vms += 10) {
            LOG.info("Warmup: {} Executions: {} VMs: {}", warmup, executions, vms);
            executeVersionHandling(new ExecutionData(vms, warmup, executions, repetitions));
         }
         if (maxVMs > 500) {
            for (int vms = 600; vms <= Math.min(1000, maxVMs); vms += 50) {
               LOG.info("Warmup: {} Executions: {} VMs: {}", warmup, executions, vms);
               executeVersionHandling(new ExecutionData(vms, warmup, executions, repetitions));
            }
         }
      }
   }

   private void executeVersionHandling(final ExecutionData config) throws IOException {
      pool.submit(() -> {
         try {
            LOG.info("Starting processing: {}", repetitions);
            final PrecisionPlotThread thread = new PrecisionPlotThread(config, writingData, precisionConfig, testcasesV1, testcasesV2);
            thread.execute();
            LOG.debug("Leaving thread");
         } catch (Throwable e) {
            e.printStackTrace();
         }
      });

      LOG.info("Submitting " + repetitions + " " + config.getVms());

      if (pool instanceof ThreadPoolExecutor) {
         final ThreadPoolExecutor threadPool = (ThreadPoolExecutor) pool;
         LOG.info("Active Threads: " + threadPool.getActiveCount() + " Overall size: " + threadPool.getQueue().size());
      } else {
         LOG.info("Pool type: {}", pool.getClass());
      }
   }
}
