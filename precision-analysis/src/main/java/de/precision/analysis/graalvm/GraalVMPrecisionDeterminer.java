package de.precision.analysis.graalvm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.inference.TTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.VMResult;
import de.dagere.peass.config.StatisticsConfig;
import de.dagere.peass.measurement.statistics.Relation;
import de.dagere.peass.measurement.statistics.StatisticUtil;
import de.dagere.peass.measurement.statistics.bimodal.CompareData;
import de.dagere.peass.utils.Constants;
import de.precision.analysis.graalvm.resultingData.RegressionDetectionModel;
import de.precision.analysis.heatmap.Configuration;
import de.precision.analysis.heatmap.GetMinimalFeasibleConfiguration;
import de.precision.analysis.heatmap.MinimalFeasibleConfigurationDeterminer;
import de.precision.analysis.heatmap.PrecisionData;
import de.precision.analysis.repetitions.ExecutionData;
import de.precision.analysis.repetitions.PrecisionComparer;
import de.precision.analysis.repetitions.PrecisionConfigMixin;
import de.precision.analysis.repetitions.PrecisionWriter;
import de.precision.analysis.repetitions.StatisticalTestResult;
import de.precision.analysis.repetitions.StatisticalTests;
import de.precision.processing.repetitions.sampling.SamplingConfig;
import de.precision.processing.repetitions.sampling.SamplingExecutor;
import picocli.CommandLine;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

public class GraalVMPrecisionDeterminer implements Runnable {

   private static final Logger LOG = LogManager.getLogger(GraalVMPrecisionDeterminer.class);

   @Option(names = { "-folder", "--folder" }, description = "Folder, that contains *all* data folders for the analysis", required = true)
   private File folder;

   @Option(names = { "-first", "--first" }, description = "Start date for the training")
   private String first;
   
   @Option(names = { "-endDate", "--endDate" }, description = "End date for the training (subsequent data will be used for testing)", required = true)
   private String endDate;

   @Mixin
   private PrecisionConfigMixin precisionConfigMixin;

   public static void main(String[] args) {
      GraalVMPrecisionDeterminer plot = new GraalVMPrecisionDeterminer();
      CommandLine cli = new CommandLine(plot);
      cli.execute(args);
   }

   @Override
   public void run() {
      RegressionDetectionModel model = new RegressionDetectionModel();
      
      Date date;
      try {
         date = DateFormat.getInstance().parse(endDate);
         System.out.println("End date: " + date);

         model.setLast(endDate);
         
         ComparisonFinder finder = first == null ? new ComparisonFinder(folder, date) : new ComparisonFinder(folder, DateFormat.getInstance().parse(first), date);
         System.out.println("Start date: " + finder.getStartDate().toString());
         model.setFirst(finder.getStartDate().toString());
         
         System.out.println("Training comparisons: " + finder.getComparisonsTraining().size());
         System.out.println("Test comparisons: " + finder.getComparisonsTest().size());

         for (int vmCount = 5; vmCount < 30; vmCount +=5) {
            for (double type2error : new double[] {0.01, 0.1, 0.2}) {
               ConfigurationDeterminer configurationDeterminer = new ConfigurationDeterminer(vmCount, type2error, folder, precisionConfigMixin.getConfig());
               Configuration configuration = configurationDeterminer.executeComparisons(finder);

               double falseNegativeRate = executeTesting(finder, configuration);
               
               model.addDetection(vmCount, vmCount, type2error, falseNegativeRate, configuration);
            }
         }
         
         Constants.OBJECTMAPPER.writeValue(new File("model.json"), model);
         
      } catch (ParseException | IOException e1) {
         e1.printStackTrace();
      }
   }

   private double executeTesting(ComparisonFinder finder, Configuration configuration) throws FileNotFoundException, IOException {
      PrecisionComparer comparer = new PrecisionComparer(new StatisticsConfig(), precisionConfigMixin.getConfig());
      for (Comparison comparison : finder.getComparisonsTest().values()) {
         DiffPairLoader loader = new DiffPairLoader(folder);
         loader.loadDiffPair(comparison);
         CompareData data = loader.getShortenedCompareData(configuration.getIterations());
         SamplingExecutor executor = new SamplingExecutor(new SamplingConfig(configuration.getVMs(), "graalVM"), data, comparer);
         executor.executeComparisons(loader.getExpected());
      }
      double falseNegativeRate = comparer.getFalseNegativeRate(StatisticalTests.TTEST);
      System.out.println("F_1-score: " + comparer.getFScore(StatisticalTests.TTEST) + " False negative: " + falseNegativeRate);
      return falseNegativeRate;
   }
}
