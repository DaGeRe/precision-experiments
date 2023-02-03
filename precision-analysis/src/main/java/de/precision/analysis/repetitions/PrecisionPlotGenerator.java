package de.precision.analysis.repetitions;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.TestMethod;
import de.dagere.kopeme.kopemedata.VMResult;
import de.precision.processing.util.RepetitionFolderHandler;

public class PrecisionPlotGenerator extends RepetitionFolderHandler {

   private static final Logger LOG = LogManager.getLogger(PrecisionPlotGenerator.class);

   private final boolean handleOnlyVMS;
   private final ExecutorService pool;
   private WritingData writingData;
   private boolean finished = false;
   private final PrecisionConfig precisionConfig;
   
   public PrecisionPlotGenerator(final File sequenceFolder, final PrecisionConfig precisionConfig, final WritingData writingData, final ExecutorService pool) {
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
    * @ XML Exceptions should not occur at any time - if this happens, they should be thrown to the top level and handled by the user
    * @throws IOException
    */
   @Override
   public void handleVersion() throws IOException {
      if (finished) {
         throw new RuntimeException("Call only once!");
      }
      super.clearCache();
      finished = true;
      List<TestMethod> testcase = testcasesV1.values().iterator().next().getMethods();
      List<VMResult> results = testcase.get(0).getDatacollectorResults().get(0).getResults();
      int maxVMs = results.size();
      int maxExecutions = results.get(0).getFulldata().getValues().size();
      LOG.debug("Max Executions: {} Max VMs: {}", maxExecutions, maxVMs);

      PrecisionPlotHandler handler = new PrecisionPlotHandler(testcasesV1, testcasesV2, pool, repetitions, precisionConfig, writingData);
      
      if (handleOnlyVMS) {
         handler.handleOnlyVMs(maxVMs, maxExecutions);
      } else {
         handler.handleAllParameters(maxVMs, maxExecutions, true);
      }
   }

   @Override
   protected void processTestcases(final Kopemedata versionFast, final Kopemedata versionSlow) {
      throw new RuntimeException("Old interface; should never be run");
   }
   
}
