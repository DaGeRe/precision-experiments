package de.precision.analysis.heatmap;

import java.io.File;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Option;

public class GetMinimalFeasibleConfiguration implements Callable<Void> {

   @Option(names = { "-data", "--data" }, description = "Data-Folder for analysis", required = true)
   private File[] data;

   private final String[] testcases = new String[] { "AddTest", "RAMTest", "SysoutTest" };

   public static void main(final String[] args) {
      GetMinimalFeasibleConfiguration plot = new GetMinimalFeasibleConfiguration();
      CommandLine cli = new CommandLine(plot);
      cli.execute(args);
   }

   @Override
   public Void call() throws Exception {
      for (File dataFile : data) {
         for (String testcase : testcases) {
            File testcaseFolder = new File(dataFile, testcase);
            if (!testcaseFolder.exists()) {
               throw new RuntimeException("Folder " + testcaseFolder.getAbsolutePath() + " needs to exist for analysis");
            } else {
               File precisionFile = new File(testcaseFolder, "results_noOutlierRemoval/precision.csv");
               
               PrecisionData data = PrecisionDataReader.readHeatmap(precisionFile, 8);
            }
         }
      }
      return null;
   }
}
