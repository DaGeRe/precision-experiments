package de.precision.analysis.graalvm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.inference.TTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.VMResult;
import de.dagere.peass.config.StatisticsConfig;
import de.dagere.peass.measurement.statistics.Relation;
import de.dagere.peass.measurement.statistics.StatisticUtil;
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

   private final int vmCount;
   private final double type2error;
   private final File folder;
   private final PrecisionConfig precisionConfig;

   public ConfigurationDeterminer(int vmCount, double type2error, File folder, PrecisionConfig precisionConfig) {
      this.vmCount = vmCount;
      this.type2error = type2error;
      this.folder = folder;
      this.precisionConfig = precisionConfig;
   }

   public Configuration executeComparisons(ComparisonFinder finder) throws IOException, FileNotFoundException {
      Configuration configuration = null;
      DiffPairLoader loader = new DiffPairLoader(folder);
      for (Comparison comparison : finder.getComparisonsTraining().values()) {
         loader.loadDiffPair(comparison);
         LOG.info("Expected relation: {}", loader.getExpected());

         Configuration currentConfiguration = executeOneComparison(comparison, loader);
         if (configuration == null) {
            configuration = currentConfiguration;
         } else if (currentConfiguration != null) {
            configuration = GetMinimalFeasibleConfiguration.mergeConfigurations(1, configuration, currentConfiguration);
         }
      }
      System.out.println("Final configuration: VMs: " + configuration.getVMs() + " Iterations: " + configuration.getIterations());
      return configuration;
   }

   private Configuration executeOneComparison(Comparison comparison, DiffPairLoader loader) throws IOException {
      String fileName = (Relation.isUnequal(loader.getExpected()) ? "unequal_" : "equal_") + comparison.getIdNew() + ".csv";
      File resultFile = new File("results/" + fileName);
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile))) {
         PrecisionData data = executeComparisons(loader, writer);
         MinimalFeasibleConfigurationDeterminer determiner = new MinimalFeasibleConfigurationDeterminer(100 - type2error);
         Map<Integer, Configuration> minimalFeasibleConfiguration = determiner.getMinimalFeasibleConfiguration(data);
         Configuration currentConfig = minimalFeasibleConfiguration.get(1);
         if (currentConfig != null) {
            System.out.println("VMs: " + currentConfig.getVMs() + " Iterations: " + currentConfig.getIterations());
            return currentConfig;
         } else {
            System.out.println("Did not find a suitable configuration!");
            return null;
         }
      }
   }

   private PrecisionData executeComparisons(DiffPairLoader loader, BufferedWriter writer) throws IOException {
      PrecisionData data = new PrecisionData();
      SamplingConfig samplingConfig = new SamplingConfig(vmCount, "GraalVMBenchmark");
      int maxRuns = getMaximumPossibleRuns(loader.getDataOld(), loader.getDataNew());
      for (int iterations = 1; iterations < maxRuns; iterations++) {
         ExecutionData executionData = new ExecutionData(vmCount, 0, iterations, 1);

         CompareData shortenedData = loader.getShortenedCompareData(iterations);

         StatisticsConfig config = new StatisticsConfig();

         PrecisionComparer comparer = new PrecisionComparer(config, precisionConfig);
         for (int i = 0; i < samplingConfig.getSamplingExecutions(); i++) {
            SamplingExecutor samplingExecutor = new SamplingExecutor(samplingConfig, shortenedData, comparer);
            samplingExecutor.executeComparisons(loader.getExpected());
         }

         PrecisionWriter precisionWriter = new PrecisionWriter(comparer, executionData);
         precisionWriter.writeTestcase(writer, comparer.getOverallResults().getResults());

         data.addData(1, vmCount, iterations,
               Relation.isUnequal(loader.getExpected()) ? comparer.getFScore(StatisticalTests.TTEST) : comparer.getTrueNegativeRate(StatisticalTests.TTEST));
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

}
