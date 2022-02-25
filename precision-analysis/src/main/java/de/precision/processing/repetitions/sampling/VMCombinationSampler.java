package de.precision.processing.repetitions.sampling;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.peass.config.StatisticsConfig;
import de.dagere.peass.measurement.statistics.Relation;
import de.dagere.peass.measurement.statistics.StatisticUtil;
import de.dagere.peass.measurement.statistics.bimodal.CompareData;
import de.precision.analysis.repetitions.PrecisionComparer;
import de.precision.processing.repetitions.misc.DetermineAverageTime;

public class VMCombinationSampler {

   private static final Logger LOG = LogManager.getLogger(VMCombinationSampler.class);

   private final int warmup, allExecutions;
   private final PrecisionComparer comparer;
   private final SamplingConfig config;
   private final StatisticsConfig statisticsConfig;

   public VMCombinationSampler(final int warmup, final int allExecutions, final PrecisionComparer comparer, final SamplingConfig config, final StatisticsConfig statisticConfig) {
      this.warmup = warmup;
      this.allExecutions = allExecutions;
      this.comparer = comparer;
      this.config = config;
      this.statisticsConfig = statisticConfig;
   }

   /**
    * 
    * @param testclazz
    * @param versionFast
    * @param versionSlow
    * @return average VM-duration in seconds
    */
   public double sampleArtificialVMCombinations(final TestcaseType versionFast, final TestcaseType versionSlow) {
      final List<Result> fastShortened = StatisticUtil.shortenValues(versionFast.getDatacollector().get(0).getResult(), warmup, allExecutions);
      final List<Result> slowShortened = StatisticUtil.shortenValues(versionSlow.getDatacollector().get(0).getResult(), warmup, allExecutions);

      return sampleArtificialVMCombinations(fastShortened, slowShortened);
   }

   /**
    * 
    * @param fastShortened
    * @param slowShortened
    * @return average duration in seconds
    */
   public double sampleArtificialVMCombinations(final List<Result> fastShortened, final List<Result> slowShortened) {
      final double overallDuration = DetermineAverageTime.getDurationInMS(fastShortened, slowShortened);
      final double calculatedDuration = overallDuration / fastShortened.size() * config.getVms();
      
      CompareData data = new CompareData(fastShortened, slowShortened);
      for (int i = 0; i < config.getSamplingExecutions(); i++) {
         executeComparisons(config, data);
      }
      return calculatedDuration / 1000;
   }

   private void executeComparisons(final SamplingConfig config, final CompareData data) {
      final SamplingExecutor samplingExecutor = new SamplingExecutor(config, statisticsConfig, data, comparer);
      samplingExecutor.executeComparisons(Relation.LESS_THAN);
      final SamplingExecutor samplingExecutor2 = new SamplingExecutor(config, statisticsConfig, data.getBefore(), data.getBefore(), comparer);
      samplingExecutor2.executeComparisons(Relation.EQUAL);
   }

}
