package de.precision.analysis.graalvm;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.peass.config.StatisticsConfig;
import de.dagere.peass.measurement.statistics.Relation;
import de.dagere.peass.measurement.statistics.bimodal.CompareData;
import de.precision.analysis.graalvm.resultingData.Counts;
import de.precision.analysis.graalvm.resultingData.GraalConfiguration;
import de.precision.analysis.graalvm.resultingData.SimpleModel;
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

   public GraalVMPrecisionThread(boolean cleaned, SimpleModel model, PrecisionConfig precisionConfig, ComparisonFinder finder, PrecisionFileManager manager, double type2error) {
      this.cleaned = cleaned;
      this.model = model;
      this.precisionConfig = precisionConfig;
      this.finder = finder;
      this.manager = manager;
      this.type2error = type2error;
   }

   public void getConfigurationAndTest() {
      ConfigurationDeterminer configurationDeterminer = new ConfigurationDeterminer(cleaned, type2error, precisionConfig, manager, samplingExecutions);
      
      Configuration configuration = configurationDeterminer.determineConfiguration(finder);
      
      buildModelDebugData(configurationDeterminer);
      
      GraalConfiguration graalConfig = buildConfig(configurationDeterminer, configuration);

      executeTesting(configuration, graalConfig, StatisticalTests.TTEST);
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
      Counts trainingCounts = new Counts(configurationDeterminer.getEqual(), configurationDeterminer.getUnequal());
      model.setCountTraining(trainingCounts);
      
      for (Comparison comparison : finder.getComparisonsTraining().values()) {
         model.getTrainingComparisons().put(comparison.getName(), comparison.getPValue());
      }
   }

   private void executeTesting(Configuration configuration, GraalConfiguration graalConfig, StatisticalTests statisticalTest) {
      Map<String, Integer> falseNegativeDetections = new HashMap<>();
      Map<String, Integer> falsePositiveDetections = new HashMap<>();

      Counts counts = new Counts();

      StatisticsConfig statisticsConfig = new StatisticsConfig();
      PrecisionComparer comparer = new PrecisionComparer(statisticsConfig, precisionConfig);
      for (Comparison comparison : finder.getComparisonsTest().values()) {
         DiffPairLoader loader = new DiffPairLoader(cleaned);
         loader.loadDiffPair(comparison);
         if (loader.getExpected() == Relation.EQUAL) {
            counts.setEqual(counts.getEqual() + 1);
         } else {
            counts.setUnequal(counts.getUnequal() + 1);
         }

         Map<StatisticalTestResult, Integer> oldResults = comparer.getOverallResults().getResults().get(statisticalTest);
         int falseNegatives = oldResults.get(StatisticalTestResult.FALSENEGATIVE);
         int falsePositives = oldResults.get(StatisticalTestResult.SELECTED) - oldResults.get(StatisticalTestResult.TRUEPOSITIVE);
         for (int i = 0; i < samplingExecutions; i++) {
            CompareData data = loader.getShortenedCompareData(configuration.getIterations());
            SamplingExecutor executor = new SamplingExecutor(new SamplingConfig(configuration.getVMs(), "graalVM"), data, comparer);
            executor.executeComparisons(loader.getExpected());
         }
         if (loader.getExpected() == Relation.EQUAL) {
            int falsePositiveNew = oldResults.get(StatisticalTestResult.SELECTED) - oldResults.get(StatisticalTestResult.TRUEPOSITIVE);
            int falsePositivesThisRun = falsePositiveNew - falsePositives;
            falsePositiveDetections.put(comparison.getName(), falsePositivesThisRun);
         } else {
            int falseNegativesThisRun = oldResults.get(StatisticalTestResult.FALSENEGATIVE) - falseNegatives;
            falseNegativeDetections.put(comparison.getName(), falseNegativesThisRun);
            LOG.debug("False negative: {} Count: {}", comparison.getName(), falseNegativesThisRun);
         }

      }
      double falseNegativeRate = comparer.getFalseNegativeRate(statisticalTest);
      double fScore = comparer.getFScore(statisticalTest);
      LOG.info("Goal type 2 error: {}", type2error);
      LOG.info("Simulated false negative rate: {} F_1-score: {}", falseNegativeRate, fScore);
      LOG.info("Iterations: {} Runs: {}", graalConfig.getIterations(), graalConfig.getRuns());

      graalConfig.setType2error(falseNegativeRate);
      graalConfig.setType2error_above1percent(comparer.getFalseNegativeRateAbove1Percent(statisticalTest));
      
      model.setCountTesting(counts);
      model.getTestComparisonFNR().putAll(falseNegativeDetections);
      
//      model.setCountTesting(counts);
//      ConfigurationResult configurationResult = new ConfigurationResult(configuration.getRepetitions(), falsePositiveDetections, falseNegativeDetections);
//      model.addDetection(vmCount, vmCount, type2error, fScore, configurationResult);
   }

}
