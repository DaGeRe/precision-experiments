package de.precision.analysis.graalvm;

import java.lang.reflect.Executable;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.peass.config.StatisticsConfig;
import de.dagere.peass.measurement.statistics.Relation;
import de.dagere.peass.measurement.statistics.bimodal.CompareData;
import de.precision.analysis.graalvm.resultingData.ComparisonCounts;
import de.precision.analysis.graalvm.resultingData.GraalConfiguration;
import de.precision.analysis.graalvm.resultingData.SimpleModel;
import de.precision.analysis.graalvm.resultingData.TrainingMetadata;
import de.precision.analysis.heatmap.Configuration;
import de.precision.analysis.repetitions.PrecisionComparer;
import de.precision.analysis.repetitions.PrecisionConfig;
import de.precision.analysis.repetitions.StatisticalTestResult;
import de.precision.analysis.repetitions.StatisticalTests;
import de.precision.processing.repetitions.sampling.SamplingConfig;
import de.precision.processing.repetitions.sampling.SamplingExecutor;

public class GraalVMPrecisionThread {

   private static final Logger LOG = LogManager.getLogger(GraalVMPrecisionDeterminer.class);

   private final boolean cleaned;
   private final SimpleModel model;
   private final PrecisionConfig precisionConfig;
   private final ComparisonFinder finder;
   private final PrecisionFileManager manager;
   private final double type2error;
   private final int samplingExecutions = 10000;
   private final PlottableHistogramWriter histogramWriter;

   public GraalVMPrecisionThread(boolean cleaned, SimpleModel model, PrecisionConfig precisionConfig, ComparisonFinder finder, PrecisionFileManager manager, double type2error, PlottableHistogramWriter histogramWriter) {
      this.cleaned = cleaned;
      this.model = model;
      this.precisionConfig = precisionConfig;
      this.finder = finder;
      this.manager = manager;
      this.type2error = type2error;
      this.histogramWriter = histogramWriter;
   }

   public void getConfigurationAndTest() {
      ConfigurationDeterminer configurationDeterminer = new ConfigurationDeterminer(cleaned, type2error, precisionConfig, manager, samplingExecutions);
      determineCounts(configurationDeterminer);
      
      if (model.getCountTesting().getUnequal() > 0) {
         
         Configuration configuration = configurationDeterminer.determineConfiguration(finder, histogramWriter);
         
         buildModelDebugData(configurationDeterminer);
         
         GraalConfiguration graalConfig = buildConfig(configurationDeterminer, configuration);
         
         test(configuration, graalConfig, StatisticalTests.TTEST);
      }
   }

   private void determineCounts(ConfigurationDeterminer configurationDeterminer) {
      ComparisonCounts trainingCounts = configurationDeterminer.determineComparisonCounts(finder);
      model.setCountTraining(trainingCounts);
      
      ComparisonCounts testCounts = getTestComparisonCounts();
      model.setCountTesting(testCounts);
   }

   private GraalConfiguration buildConfig(ConfigurationDeterminer configurationDeterminer, Configuration configuration) {
      GraalConfiguration graalConfig = new GraalConfiguration();
      graalConfig.setRuns(configuration.getVMs());
      graalConfig.setIterations(configuration.getIterations());
      graalConfig.setWarmup(0);
      model.getRuns_iterations().put(type2error, graalConfig);
      
      LOG.info("Runs: {} Iterations: {}", configuration.getVMs(), configuration.getIterations());
      
      return graalConfig;
   }

   private void buildModelDebugData(ConfigurationDeterminer configurationDeterminer) {
      
      for (Comparison comparison : finder.getComparisonsTraining().values()) {
         TrainingMetadata metadata = new TrainingMetadata(comparison.getPValue(), comparison.getRunsOld(), comparison.getRunsNew());
         model.getTrainingComparisons().put(comparison.getName(), metadata);
      }
      for (Comparison comparison : finder.getComparisonsTest().values()) {
         TrainingMetadata metadata = new TrainingMetadata(comparison.getPValue(), comparison.getRunsOld(), comparison.getRunsNew());
         model.getTestComparisons().put(comparison.getName(), metadata);
      }
   }

   private ComparisonCounts getTestComparisonCounts() {
      ComparisonCounts counts = new ComparisonCounts();
      for (Comparison comparison : finder.getComparisonsTest().values()) {
         DiffPairLoader loader = new DiffPairLoader(cleaned);
         loader.loadDiffPair(comparison);
         if (loader.getExpected() == Relation.EQUAL) {
            counts.setEqual(counts.getEqual() + 1);
         } else {
            counts.setUnequal(counts.getUnequal() + 1);
         }
      }
      return counts;
   }

   private void test(Configuration configuration, GraalConfiguration graalConfig, StatisticalTests statisticalTest) {
      Map<String, Integer> falseNegativeDetections = new HashMap<>();
      Map<String, Integer> falsePositiveDetections = new HashMap<>();
      StatisticsConfig statisticsConfig = new StatisticsConfig();
//      PrecisionConfig config2 = new PrecisionConfig(cleaned, true, 4, new StatisticalTests[] { StatisticalTests.TTEST}, 0, 0, 0, 0);
      PrecisionComparer comparer = new PrecisionComparer(statisticsConfig, precisionConfig);
      
      for (Comparison comparison : finder.getComparisonsTest().values()) {
         testOneComparison(configuration, statisticalTest, falseNegativeDetections, falsePositiveDetections, comparer, comparison);
         LOG.info("Done: {}, FNR: {}", comparison.getPValue(), comparer.getFalseNegativeRate(statisticalTest));
      }
      double falseNegativeRate = comparer.getFalseNegativeRate(statisticalTest);
      double fScore = comparer.getFScore(statisticalTest);
      LOG.info("Goal type 2 error: {}", type2error);
      LOG.info("Simulated false negative rate: {} F_1-score: {}", falseNegativeRate, fScore);
      LOG.info("Iterations: {} Runs: {}", graalConfig.getIterations(), graalConfig.getRuns());

      graalConfig.setFalsenegative(comparer.getFalseNegatives(statisticalTest));
      graalConfig.setTruepositive(comparer.getTruePositives(statisticalTest));
      graalConfig.setType2error(falseNegativeRate);
      graalConfig.setType2error_above5percent(comparer.getFalseNegativeRateAbove5Percent(statisticalTest));
      graalConfig.setType2error_above10percent(comparer.getFalseNegativeRateAbove10Percent(statisticalTest));
      
      model.addComparison(type2error, falseNegativeDetections);
   }

   private void testOneComparison(Configuration configuration, StatisticalTests statisticalTest, Map<String, Integer> falseNegativeDetections,
         Map<String, Integer> falsePositiveDetections, PrecisionComparer comparer, Comparison comparison) {
      DiffPairLoader loader = new DiffPairLoader(cleaned);
      loader.loadDiffPair(comparison);
      
      Relation expected;
      if (loader.isConsideredRelevant()) {
         expected = loader.getExpected();
      } else {
         expected = Relation.EQUAL;
      }
      
      
      histogramWriter.plotTesting(comparison.getName(), loader.getDataOld(), loader.getDataNew());

      Map<StatisticalTestResult, Integer> oldResults = comparer.getOverallResults().getResults().get(statisticalTest);
      int falseNegatives = oldResults.get(StatisticalTestResult.FALSENEGATIVE);
      int falsePositives = oldResults.get(StatisticalTestResult.SELECTED) - oldResults.get(StatisticalTestResult.TRUEPOSITIVE);
      
      for (int i = 0; i < samplingExecutions; i++) {
         CompareData data = loader.getShortenedCompareData(configuration.getIterations());
         SamplingExecutor executor = new SamplingExecutor(new SamplingConfig(configuration.getVMs(), "graalVM"), data, comparer);
         executor.executeComparisons(expected);
      }
      if (expected == Relation.EQUAL) {
         int falsePositiveNew = oldResults.get(StatisticalTestResult.SELECTED) - oldResults.get(StatisticalTestResult.TRUEPOSITIVE);
         int falsePositivesThisRun = falsePositiveNew - falsePositives;
         falsePositiveDetections.put(comparison.getName(), falsePositivesThisRun);
      } else {
         int falseNegativesThisRun = oldResults.get(StatisticalTestResult.FALSENEGATIVE) - falseNegatives;
         falseNegativeDetections.put(comparison.getName(), falseNegativesThisRun);
         LOG.debug("False negative: {} Count: {}", comparison.getName(), falseNegativesThisRun);
      }
   }

}
