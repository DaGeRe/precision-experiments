package de.precision.analysis.graalvm;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.VMResult;
import de.dagere.peass.config.StatisticsConfig;
import de.dagere.peass.measurement.dataloading.MultipleVMTestUtil;
import de.dagere.peass.measurement.statistics.Relation;
import de.dagere.peass.measurement.statistics.bimodal.CompareData;
import de.precision.analysis.heatmap.Configuration;
import de.precision.analysis.heatmap.GetMinimalFeasibleConfiguration;
import de.precision.analysis.heatmap.MinimalFeasibleConfigurationDeterminer;
import de.precision.analysis.heatmap.PrecisionData;
import de.precision.analysis.repetitions.ExecutionData;
import de.precision.analysis.repetitions.PrecisionComparer;
import de.precision.analysis.repetitions.PrecisionConfig;
import de.precision.analysis.repetitions.PrecisionWriter;
import de.precision.analysis.repetitions.StatisticalTests;
import de.precision.processing.repetitions.sampling.SamplingConfig;
import de.precision.processing.repetitions.sampling.SamplingExecutor;

public class ConfigurationDeterminer {

   private static final Logger LOG = LogManager.getLogger(ConfigurationDeterminer.class);

   private final double type2error;
   private final boolean cleaned;
   private final PrecisionConfig precisionConfig;
   private final PrecisionFileManager precisionFileManager;
   private int equal = 0, unequal = 0;
   private final int samplingExecutions;
   private final StatisticalTests statisticalTest = StatisticalTests.MANNWHITNEY;

   public ConfigurationDeterminer(boolean cleaned, double type2error, PrecisionConfig precisionConfig, PrecisionFileManager precisionFileManager,
         int samplingExecutions) {
      this.type2error = type2error;
      this.cleaned = cleaned;
      this.precisionConfig = precisionConfig;
      this.precisionFileManager = precisionFileManager;
      this.samplingExecutions = samplingExecutions;
   }

   public Configuration determineConfiguration(ComparisonFinder finder) {
      Configuration configuration = null;
      DiffPairLoader loader = new DiffPairLoader(cleaned);
      for (Comparison comparison : finder.getComparisonsTraining().values()) {
         LOG.debug("Folder existing");
         loader.loadDiffPair(comparison);

         LOG.info("Expected relation: {}", loader.getExpected());
         if (loader.getExpected() == Relation.EQUAL) {
            equal++;
         } else {
            unequal++;
         }

         Configuration currentConfiguration = executeOneComparison(comparison, loader);
         if (configuration == null) {
            configuration = currentConfiguration;
         } else if (currentConfiguration != null) {
            configuration = GetMinimalFeasibleConfiguration.mergeConfigurations(1, configuration, currentConfiguration);
         }

      }
      LOG.info("Final configuration: VMs: {} Iterations: {}", configuration.getVMs(), configuration.getIterations());
      return configuration;
   }

   private Configuration executeOneComparison(Comparison comparison, DiffPairLoader loader) {
      try {
         BufferedWriter writer = precisionFileManager.getFile(comparison.getVersionIdNew(), loader.getExpected());
         PrecisionData data = executeComparisons(loader, writer);
         MinimalFeasibleConfigurationDeterminer determiner = new MinimalFeasibleConfigurationDeterminer(100 - type2error);
         Map<Integer, Configuration> minimalFeasibleConfiguration = determiner.getMinimalFeasibleConfiguration(data);
         Configuration currentConfig = minimalFeasibleConfiguration.get(1);
         if (currentConfig != null) {
            LOG.info("Found configuration, Runs: {} Iterations: {}", currentConfig.getVMs(), currentConfig.getIterations());
            return currentConfig;
         } else {
            LOG.info("Did not find a suitable configuration with type 2 error {}, setting to maximum", type2error);
            List<VMResult> vmResults = loader.getDataOld().getFirstDatacollectorContent();
            List<VMResult> measuredData = vmResults;
            int iterations = measuredData.get(0).getFulldata().getValues().size();
            int VMs = measuredData.size();
            LOG.info("Maximum here: VMS: {} Iterations: {}", VMs, iterations);
            
            printDebug(MultipleVMTestUtil.getAverages(loader.getDataOld().getFirstDatacollectorContent()), "old");
            printDebug(MultipleVMTestUtil.getAverages(loader.getDataNew().getFirstDatacollectorContent()), "new");
            
            return new Configuration(1, VMs, iterations);
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private void printDebug(List<Double> meanOld, String classifier) {
      final double[] dataOld = ArrayUtils.toPrimitive(meanOld.toArray(new Double[0]));
      DescriptiveStatistics statistics = new DescriptiveStatistics(dataOld);
      LOG.info("Mean {}: {}", classifier, statistics.getMean());
   }

   private PrecisionData executeComparisons(DiffPairLoader loader, BufferedWriter writer) throws IOException {
      PrecisionData data = new PrecisionData();
      
      int sizeOld = loader.getDataOld().getFirstDatacollectorContent().size();
      int sizeNew = loader.getDataNew().getFirstDatacollectorContent().size();
      int maxVms = Math.min(sizeOld, sizeNew);
      
      int vmStepSize = Math.max(5, maxVms / 100);
      LOG.info("VMs step size: {} to {}", vmStepSize, maxVms);
      
      for (int vmCount = 5; vmCount < maxVms; vmCount += vmStepSize) {
         SamplingConfig samplingConfig = new SamplingConfig(vmCount, "GraalVMBenchmark", samplingExecutions);
         int maxRuns = getMaximumPossibleRuns(loader.getDataOld(), loader.getDataNew());
         
         int stepsize = Math.max(1, maxRuns / 100);
         LOG.debug("Stepsize for iterations 1 to {} is {} (VMs: {})", maxRuns, stepsize, vmCount);
         
         double previous = Double.MAX_VALUE;
         for (int iterations = 1; iterations < maxRuns; iterations+=stepsize) {
            ExecutionData executionData = new ExecutionData(vmCount, 0, iterations, 1);

            CompareData shortenedData = loader.getShortenedCompareData(iterations);

            StatisticsConfig statisticsConfig = new StatisticsConfig();
            if (!precisionConfig.isRemoveOutliers()) {
               statisticsConfig.setOutlierFactor(0.0);
            }

            PrecisionComparer comparer = new PrecisionComparer(statisticsConfig, precisionConfig);
            for (int i = 0; i < samplingConfig.getSamplingExecutions(); i++) {
               SamplingExecutor samplingExecutor = new SamplingExecutor(samplingConfig, shortenedData, comparer);
               samplingExecutor.executeComparisons(loader.getExpected());
            }

            PrecisionWriter precisionWriter = new PrecisionWriter(comparer, executionData);
            precisionWriter.writeTestcase(writer, comparer.getOverallResults().getResults());

            double value = Relation.isUnequal(loader.getExpected()) ? comparer.getFScore(statisticalTest) : comparer.getTrueNegativeRate(statisticalTest);
            data.addData(1, vmCount, iterations, value);
            if (value < type2error && previous < type2error) {
               break;
            }
            previous = value;
         }
      }
      return data;
   }

   private int getMaximumPossibleRuns(Kopemedata dataOld, Kopemedata dataNew) {
      int maxRuns = Integer.MAX_VALUE;
      for (VMResult result : dataOld.getFirstDatacollectorContent()) {
         maxRuns = Math.min(maxRuns, result.getFulldata().getValues().size());
      }
      for (VMResult result : dataNew.getFirstDatacollectorContent()) {
         maxRuns = Math.min(maxRuns, result.getFulldata().getValues().size());
      }
      return maxRuns;
   }

   public int getEqual() {
      return equal;
   }

   public int getUnequal() {
      return unequal;
   }

}
