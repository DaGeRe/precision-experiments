package de.precision.analysis.heatmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Option;

public class GetMinimalFeasibleConfiguration implements Callable<Void> {

   public final static Map<String, Integer> statisticTestIndexes = new LinkedHashMap<>();

   static {
      statisticTestIndexes.put("Mean Comparison", 8);
      statisticTestIndexes.put("T-Test", 12);
      statisticTestIndexes.put("Confidence Interval Comparison", 20);
      statisticTestIndexes.put("Mann-Whitney Test", 24);
   }

   @Option(names = { "-data", "--data" }, description = "Data-Folder for analysis", required = true)
   private File[] data;

   private final String[] workloads = new String[] { "AddTest", "RAMTest", "SysoutTest" };

   public static void main(final String[] args) {
      GetMinimalFeasibleConfiguration plot = new GetMinimalFeasibleConfiguration();
      CommandLine cli = new CommandLine(plot);
      cli.execute(args);
   }

   @Override
   public Void call() throws Exception {

      for (File dataFile : data) {
         for (Map.Entry<String, Integer> statisticalTest : statisticTestIndexes.entrySet()) {
            for (String outlierRemovalString : new String[] { "noOutlierRemoval", "outlierRemoval" }) {
               Integer f1ScoreIndex = statisticalTest.getValue();
               Configuration overallConfig = null;
               for (String workload : workloads) {
                  File testcaseFolder = new File(dataFile, workload);
                  if (!testcaseFolder.exists()) {
                     throw new RuntimeException("Folder " + testcaseFolder.getAbsolutePath() + " needs to exist for analysis");
                  } else {
                     overallConfig = analyzePrecisionFile(outlierRemovalString, f1ScoreIndex, overallConfig, testcaseFolder);
                  }
               }

               printResult(statisticalTest, outlierRemovalString, overallConfig);
            }
         }
      }

      return null;
   }

   private void printResult(Map.Entry<String, Integer> statisticalTest, String outlierRemovalString, Configuration overallConfig) {
      System.out.print(statisticalTest.getKey() + " & ");

      if (outlierRemovalString.equals("outlierRemoval")) {
         System.out.print(" x & ");
      } else {
         System.out.print(" & ");
      }
      System.out.println(overallConfig.getRepetitions() + " & " + overallConfig.getVMs() + " & " + overallConfig.getIterations());
   }

   private Configuration analyzePrecisionFile(String outlierRemovalString, Integer f1ScoreIndex, Configuration overallConfig, File testcaseFolder)
         throws FileNotFoundException, IOException {
      File precisionFile = new File(testcaseFolder, "results_" + outlierRemovalString + "/precision.csv");

      PrecisionData data = PrecisionDataReader.readHeatmap(precisionFile, f1ScoreIndex);

      MinimalFeasibleConfigurationDeterminer determiner = new MinimalFeasibleConfigurationDeterminer(99.0);
      Configuration config = determiner.getMinimalFeasibleConfiguration(data);

      // System.out.println(config);

      if (overallConfig == null) {
         overallConfig = config;
      } else {
         int VMs = Math.max(overallConfig.getVMs(), config.getVMs());
         int iterations = Math.max(overallConfig.getIterations(), config.getIterations());
         overallConfig = new Configuration(overallConfig.getRepetitions(), VMs, iterations);
      }
      return overallConfig;
   }
}
