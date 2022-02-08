package de.precision.analysis.repetitions;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.generated.Kopemedata.Testcases;

public class PrecisionPlotHandler {
   
   private static final Logger LOG = LogManager.getLogger(PrecisionPlotHandler.class);
   
   private final Map<String, Testcases> testcasesV1;
   private final Map<String, Testcases> testcasesV2;
   
   private final ExecutorService pool;
   private final long repetitions;
   private final PrecisionConfig precisionConfig;
   private WritingData writingData;

   
   public PrecisionPlotHandler(final Map<String, Testcases> testcasesV1, final Map<String, Testcases> testcasesV2, final ExecutorService pool, final long repetitions, final PrecisionConfig precisionConfig,
         final WritingData writingData) {
      this.testcasesV1 = testcasesV1;
      this.testcasesV2 = testcasesV2;
      this.pool = pool;
      this.repetitions = repetitions;
      this.precisionConfig = precisionConfig;
      this.writingData = writingData;
   }

   public void handleAllParameters(final int maxVMs, final int maxIterations) throws JAXBException, IOException {
      // final int maxWarmup = maxExecutions / 2;
      final int iterationStepSize = Math.max(maxIterations / 50, 1);
      LOG.debug("Step size: {}", iterationStepSize);
      // for (int warmup = 0; warmup <= maxWarmup; warmup += executionStepSize) {
      // this.warmup = warmup;
      // this.executions = maxExecutions - warmup;
      for (int iterations = iterationStepSize; iterations <= maxIterations / 2; iterations += iterationStepSize) {
         final int vmStepSize = maxVMs / 20;
         for (int vms = vmStepSize; vms <= maxVMs; vms += vmStepSize) {
            LOG.info("Warmup: {} Executions: {} VMs: {}", iterations, iterations, vms);
            executeVersionHandling(new ExecutionData(vms, iterations, iterations, repetitions));
         }
         // }
      }
   }
   
   public void handleOnlyVMs(final int maxVMs, final int maxExecutions) throws JAXBException, IOException {
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
   
   private void executeVersionHandling(final ExecutionData config) throws JAXBException, IOException {
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