package de.precision.analysis.repetitions;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.TestcaseType;
import de.precision.processing.util.RepetitionFolderHandler;

public class PrecisionPlotGenerator extends RepetitionFolderHandler {

   private static final Logger LOG = LogManager.getLogger(PrecisionPlotGenerator.class);

   private final boolean handleOnlyVMS;
   private final ExecutorService pool;
   private WritingData writingData;
   private boolean finished = false;
   private final PrecisionConfig precisionConfig;

   public PrecisionPlotGenerator(final File sequenceFolder, PrecisionConfig precisionConfig, WritingData writingData, ExecutorService pool) {
      super(sequenceFolder);
      this.handleOnlyVMS = false;
      this.precisionConfig = precisionConfig;
      this.writingData = writingData;
      this.pool = pool;
      if (handleOnlyVMS) {
         /**
          * If only VMs are handled, start dates can be deleted; otherwise, start dates of all iterations are needed because the iteration count is changed
          */
         clearStartDates = true;
      }
   }

   /**
    * Calculates the precision of the given sequence-folder
    * 
    * @param sequencefolder
    * @param repetitions
    * @param precision
    * @throws JAXBException XML Exceptions should not occur at any time - if this happens, they should be thrown to the top level and handled by the user
    * @throws IOException
    */
   @Override
   public void handleVersion() throws JAXBException, IOException {
      if (finished) {
         throw new RuntimeException("Call only once!");
      }
      super.clearCache();
      finished = true;
      List<TestcaseType> testcase = testcasesV1.values().iterator().next().getTestcase();
      List<Result> results = testcase.get(0).getDatacollector().get(0).getResult();
      int maxVMs = results.size();
      int maxExecutions = results.get(0).getFulldata().getValue().size();
      LOG.debug("Max Executions: {} Max VMs: {}", maxExecutions, maxVMs);

      if (handleOnlyVMS) {
         handleOnlyVMs(maxVMs, maxExecutions);
      } else {
         handleAllParameters(maxVMs, maxExecutions);
      }
   }

   private void handleAllParameters(int maxVMs, int maxExecutions) throws JAXBException, IOException {
      // final int maxWarmup = maxExecutions / 2;
      final int executionStepSize = Math.max(maxExecutions / 50, 1);
      LOG.debug("Step size: {}", executionStepSize);
      // for (int warmup = 0; warmup <= maxWarmup; warmup += executionStepSize) {
      // this.warmup = warmup;
      // this.executions = maxExecutions - warmup;
      for (int executions = executionStepSize; executions <= maxExecutions / 2; executions += executionStepSize) {
         final int vmStepSize = maxVMs / 20;
         for (int vms = vmStepSize; vms <= maxVMs; vms += vmStepSize) {
            LOG.info("Warmup: {} Executions: {} VMs: {}", executions, executions, vms);
            executeVersionHandling(new ExecutionData(vms, executions, executions, repetitions));
         }
         // }
      }
   }

   private void handleOnlyVMs(int maxVMs, int maxExecutions) throws JAXBException, IOException {
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

   @Override
   protected void processTestcases(Testcases versionFast, Testcases versionSlow) {
      throw new RuntimeException("Old interface; should never be run");
   }
   
}
