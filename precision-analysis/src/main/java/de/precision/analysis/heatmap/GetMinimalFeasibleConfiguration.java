package de.precision.analysis.heatmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Option;

public class GetMinimalFeasibleConfiguration implements Callable<Void> {

   public final static Map<String, Integer> statisticTestIndexes = new LinkedHashMap<>();
   public final static Map<String, Integer> statisticTestIndexesDe = new LinkedHashMap<>();

   static {
      statisticTestIndexes.put("Mean Comparison", 8);
      statisticTestIndexesDe.put("Mittelwertvergleich", 8);
      statisticTestIndexes.put("T-Test", 12);
      statisticTestIndexesDe.put("T-Test", 12);
      statisticTestIndexes.put("Confidence Interval Comparison", 20);
      statisticTestIndexesDe.put("Kofidenzintervallvergleich", 20);
      statisticTestIndexes.put("Mann-Whitney Test", 24);
      statisticTestIndexesDe.put("Mann-Whitney Test", 24);
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
         for (Map.Entry<String, Integer> statisticalTest : statisticTestIndexesDe.entrySet()) {
            for (String outlierRemovalString : new String[] { "noOutlierRemoval", "outlierRemoval" }) {
               Integer f1ScoreIndex = statisticalTest.getValue();
               Map<Integer, Configuration> overallConfigs = getRepetitionCandidates(dataFile, outlierRemovalString, f1ScoreIndex);

               Configuration minimal = detectBestRepetitionCandidate(overallConfigs);

               printResult(statisticalTest, outlierRemovalString, minimal);
            }
         }
      }

      return null;
   }

   private Map<Integer, Configuration> getRepetitionCandidates(File dataFile, String outlierRemovalString, Integer f1ScoreIndex) throws FileNotFoundException, IOException {
      Map<Integer, Configuration> overallConfigs = null;
      for (String workload : workloads) {
         // System.out.println("Workload: " + workload);
         File testcaseFolder = new File(dataFile, workload);
         if (!testcaseFolder.exists()) {
            throw new RuntimeException("Folder " + testcaseFolder.getAbsolutePath() + " needs to exist for analysis");
         } else {
            Map<Integer, Configuration> currentConfig = analyzePrecisionFile(outlierRemovalString, f1ScoreIndex, testcaseFolder);
            if (overallConfigs == null) {
               overallConfigs = currentConfig;
            } else {
               Set<Integer> containedKeys = currentConfig.keySet();
               containedKeys.retainAll(overallConfigs.keySet());
               for (Integer repetitions : containedKeys) {
                  Configuration old = overallConfigs.get(repetitions);
                  Configuration current = currentConfig.get(repetitions);
                  Configuration merged = mergeConfigurations(repetitions, old, current);
                  overallConfigs.put(repetitions, merged);
               }
            }
         }
      }
      return overallConfigs;
   }

   public static Configuration mergeConfigurations(Integer repetitions, Configuration old, Configuration current) {
      int newVMs = Math.max(old.getVMs(), current.getVMs());
      int newIterations = Math.max(old.getIterations(), current.getIterations());
      Configuration merged = new Configuration(repetitions, newVMs, newIterations);
      return merged;
   }

   private Configuration detectBestRepetitionCandidate(Map<Integer, Configuration> overallConfigs) {
      Configuration minimal;
      if (!overallConfigs.isEmpty()) {
         minimal = overallConfigs.values().iterator().next();
         for (Configuration config : overallConfigs.values()) {
            if (config.getIterations() * config.getRepetitions() * config.getVMs() < minimal.getIterations() * config.getRepetitions() * config.getVMs()) {
               minimal = config;
            }
         }
      } else {
         minimal = new Configuration(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
      }
      return minimal;
   }

   private void printResult(Map.Entry<String, Integer> statisticalTest, String outlierRemovalString, Configuration overallConfig) {
      System.out.print(statisticalTest.getKey() + " & ");

      if (outlierRemovalString.equals("outlierRemoval")) {
         System.out.print(" x & ");
      } else {
         System.out.print(" & ");
      }
      String vMs = overallConfig.getVMs() == Integer.MAX_VALUE ? "-" : NumberFormat.getInstance().format(overallConfig.getVMs());
      String iterations = overallConfig.getIterations() == Integer.MAX_VALUE ? "-" : NumberFormat.getInstance().format(overallConfig.getIterations());
      String repetitions = overallConfig.getRepetitions() == Integer.MAX_VALUE ? "-" : NumberFormat.getInstance().format(overallConfig.getRepetitions());
      System.out.println(repetitions + " & " + vMs + " & " + iterations + "\\\\");
   }

   private Map<Integer, Configuration> analyzePrecisionFile(String outlierRemovalString, Integer f1ScoreIndex, File testcaseFolder)
         throws FileNotFoundException, IOException {
      File precisionFile = new File(testcaseFolder, "results_" + outlierRemovalString + "/precision.csv");

      PrecisionData data = PrecisionDataReader.readHeatmap(precisionFile, f1ScoreIndex);

      MinimalFeasibleConfigurationDeterminer determiner = new MinimalFeasibleConfigurationDeterminer(99.0);
      Map<Integer, Configuration> config = determiner.getMinimalFeasibleConfiguration(data);
      return config;
   }
}
