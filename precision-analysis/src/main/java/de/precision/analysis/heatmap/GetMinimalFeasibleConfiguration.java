package de.precision.analysis.heatmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Option;

public class GetMinimalFeasibleConfiguration implements Callable<Void> {

   public final static Map<String, Integer> statisticTestIndexes = new LinkedHashMap<>();
   public final static Map<String, Integer> statisticTestIndexesDe = new LinkedHashMap<>();

   static {
//      statisticTestIndexes.put("Mean Comparison", 8);
//      statisticTestIndexesDe.put("Mittelwertvergleich", 8);
//      statisticTestIndexes.put("T-Test", 12);
//      statisticTestIndexesDe.put("T-Test", 12);
//      statisticTestIndexes.put("Confidence Interval Comparison", 20);
//      statisticTestIndexesDe.put("Kofidenzintervallvergleich", 20);
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
               Configuration overallConfig = null;
               for (String workload : workloads) {
//                  System.out.println("Workload: " + workload);
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
      String vMs = overallConfig.getVMs() == Integer.MAX_VALUE ? "-" : NumberFormat.getInstance().format(overallConfig.getVMs());
      String iterations = overallConfig.getIterations() == Integer.MAX_VALUE ? "-" : NumberFormat.getInstance().format(overallConfig.getIterations());
      String repetitions = overallConfig.getRepetitions() == Integer.MAX_VALUE ? "-" : NumberFormat.getInstance().format(overallConfig.getRepetitions());
      System.out.println(repetitions + " & " + vMs + " & " + iterations + "\\\\");
   }

   private Configuration analyzePrecisionFile(String outlierRemovalString, Integer f1ScoreIndex, Configuration overallConfig, File testcaseFolder)
         throws FileNotFoundException, IOException {
      File precisionFile = new File(testcaseFolder, "results_" + outlierRemovalString + "/precision.csv");

      PrecisionData data = PrecisionDataReader.readHeatmap(precisionFile, f1ScoreIndex);

      MinimalFeasibleConfigurationDeterminer determiner = new MinimalFeasibleConfigurationDeterminer(99.0);
      Configuration config = determiner.getMinimalFeasibleConfiguration(data);

//       System.out.println(config);

      if (config != null && config.getVMs() != Integer.MAX_VALUE) {
         if (overallConfig == null) {
            overallConfig = config;
         } else {
            int VMs = Math.max(overallConfig.getVMs(), config.getVMs());
            int iterations = Math.max(overallConfig.getIterations(), config.getIterations());
            overallConfig = new Configuration(overallConfig.getRepetitions(), VMs, iterations);
         }
      } else {
         overallConfig = new Configuration(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
      }

      return overallConfig;
   }
}
