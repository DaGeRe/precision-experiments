package de.precision.processing.repetitions.sampling;

import java.util.List;
import java.util.Random;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.TestcaseType;
import de.peass.measurement.analysis.Relation;
import de.peass.measurement.analysis.StatisticUtil;
import de.precision.analysis.repetitions.PrecisionComparer;

public class VMCombinationSampler {

   private static final Logger LOG = LogManager.getLogger(VMCombinationSampler.class);

   private final int warmup, allExecutions;
   private final PrecisionComparer comparer;
   private final SamplingConfig config;

   public VMCombinationSampler(int warmup, int allExecutions, PrecisionComparer comparer, SamplingConfig config) {
      this.warmup = warmup;
      this.allExecutions = allExecutions;
      this.comparer = comparer;
      this.config = config;
   }

   /**
    * 
    * @param testclazz
    * @param versionFast
    * @param versionSlow
    * @return average VM-duration in seconds
    */
   public double sampleArtificialVMCombinations(final Testcases testclazz, final TestcaseType versionFast, final TestcaseType versionSlow) {
      final List<Result> fastShortened = StatisticUtil.shortenValues(versionFast.getDatacollector().get(0).getResult(), warmup, allExecutions);
      final List<Result> slowShortened = StatisticUtil.shortenValues(versionSlow.getDatacollector().get(0).getResult(), warmup, allExecutions);

      return sampleArtificialVMCombinations(testclazz, fastShortened, slowShortened);
   }

   public double sampleArtificialVMCombinations(final Testcases testclazz, final List<Result> fastShortened, final List<Result> slowShortened) {
      final SummaryStatistics averageDuration = new SummaryStatistics();

      for (int i = 0; i < config.getSamplingExecutions(); i++) {
         executeComparisons(config, fastShortened, slowShortened, averageDuration);
      }
      return averageDuration.getMean() / 1000;
   }

   private void executeComparisons(final SamplingConfig config, final List<Result> fastShortened, final List<Result> slowShortened, final SummaryStatistics averageDuration) {
      final SamplingExecutor samplingExecutor = new SamplingExecutor(config, fastShortened, slowShortened, averageDuration, comparer);
      samplingExecutor.executeComparisons(Relation.LESS_THAN);
      final SamplingExecutor samplingExecutor2 = new SamplingExecutor(config, fastShortened, fastShortened, averageDuration, comparer);
      samplingExecutor2.executeComparisons(Relation.EQUAL);
   }

}
