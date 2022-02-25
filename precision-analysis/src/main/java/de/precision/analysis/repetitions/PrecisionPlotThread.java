package de.precision.analysis.repetitions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.peass.config.StatisticsConfig;
import de.dagere.peass.measurement.statistics.StatisticUtil;
import de.precision.processing.repetitions.misc.DetermineAverageTime;
import de.precision.processing.repetitions.sampling.SamplingConfig;
import de.precision.processing.repetitions.sampling.VMCombinationSampler;

public class PrecisionPlotThread {

   private static final Logger LOG = LogManager.getLogger(PrecisionPlotThread.class);

   private final ExecutionData executionData;
   private WritingData writingData;
   private SamplingConfig config;
   private PrecisionComparer comparer;
   private final PrecisionConfig precisionConfig;
   private long overhead = 0, duration = 0;

   protected Map<String, Testcases> testcasesV1 = null;
   protected Map<String, Testcases> testcasesV2 = null;

   public PrecisionPlotThread(final ExecutionData executionData, final WritingData writinData, final PrecisionConfig precisionConfig, final Map<String, Testcases> testcasesV1,
         final Map<String, Testcases> testcasesV2) {
      this.executionData = executionData;
      this.writingData = writinData;
      this.precisionConfig = precisionConfig;
      this.testcasesV1 = testcasesV1;
      this.testcasesV2 = testcasesV2;
   }

   public void execute() throws IOException {
      for (String testcase : testcasesV1.keySet()) {
         processTestcases(testcasesV1.get(testcase), testcasesV2.get(testcase));
      }

      LOG.info("Processing finished {}");

      writeOverallPrecision();

      writePerTestcasePrecision();
   }

   private void writePerTestcasePrecision() {
      for (final Map.Entry<String, MethodResult> entry : comparer.getTestcaseResults().entrySet()) {
         try {
            BufferedWriter testcaseWriter = writingData.getTestcaseWriters().get(entry.getKey());
            if (testcaseWriter == null) {
               testcaseWriter = new BufferedWriter(new FileWriter(new File(writingData.getResultFolder(), entry.getKey() + ".csv")));
               writingData.getTestcaseWriters().put(entry.getKey(), testcaseWriter);
               PrecisionWriter.writeHeader(testcaseWriter, precisionConfig.getTypes());
            }
            new PrecisionWriter(comparer, executionData).writeTestcase(testcaseWriter, entry.getValue().getResults());
         } catch (final IOException e) {
            e.printStackTrace();
         }
      }
   }

   private void writeOverallPrecision() throws IOException {
      new PrecisionWriter(comparer, executionData).writeTestcase(writingData.getPrecisionRecallWriter(), comparer.getOverallResults().getResults());
   }

   protected void processTestcases(final Testcases testclazz, final Testcases otherPackageTestcase) {
      config = new SamplingConfig(executionData.getVms(), testclazz.getClazz());
      StatisticsConfig statisticsConfig = new StatisticsConfig();
      if (precisionConfig.isRemoveOutliers()) {
         statisticsConfig.setOutlierFactor(StatisticsConfig.DEFAULT_OUTLIER_FACTOR);
      } else {
         statisticsConfig.setOutlierFactor(0.0);
      }
      comparer = new PrecisionComparer(statisticsConfig, precisionConfig);

      final TestcaseType before = testclazz.getTestcase().get(0);
      final TestcaseType after = otherPackageTestcase.getTestcase().get(0);

      final long averageOverheadInMS = DetermineAverageTime.getOverhead(after.getDatacollector().get(0).getResult(), before.getDatacollector().get(0).getResult());
      LOG.debug("Overhead in ms: {}", averageOverheadInMS);
      overhead += ((double) averageOverheadInMS) / 1000;

      final int allExecutions = executionData.getWarmup() + executionData.getExecutions();

      LOG.debug("VMs: {}", executionData.getVms());

      final List<Result> fastShortened = StatisticUtil.shortenValues(before.getDatacollector().get(0).getResult(), executionData.getWarmup(), allExecutions);
      final List<Result> slowShortened = StatisticUtil.shortenValues(after.getDatacollector().get(0).getResult(), executionData.getWarmup(), allExecutions);

      writeValues(fastShortened, new File(writingData.getResultFolder(), "fast_" + executionData.getRepetitions() + ".csv"));
      writeValues(slowShortened, new File(writingData.getResultFolder(), "slow_" + executionData.getRepetitions() + ".csv"));

      final VMCombinationSampler vmCombinationSampler = new VMCombinationSampler(executionData.getWarmup(), allExecutions, comparer, config, statisticsConfig);
      final double durationInS = (vmCombinationSampler.sampleArtificialVMCombinations(fastShortened, slowShortened)) / 1000;
      duration += durationInS;
      LOG.debug("Duration in s: {}", durationInS);

      executionData.setDuration(duration);
      executionData.setOverhead(overhead);
   }

   private void writeValues(final List<Result> values, final File destination) {
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(destination))) {
         for (Result r : values) {
            writer.write(r.getValue() + "\n");
         }
         writer.flush();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
