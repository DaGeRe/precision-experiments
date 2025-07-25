package de.precision.analysis.heatmap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.precision.analysis.graalvm.ComparisonFinder;
import picocli.CommandLine;
import picocli.CommandLine.Option;

public class GetMinimalFeasibleConfigurationSampling implements Callable<Void> {

   private static final Logger LOG = LogManager.getLogger(GetMinimalFeasibleConfigurationSampling.class);

   @Option(names = { "-data", "--data" }, description = "Data-Folder for analysis", required = true)
   private File[] data;

   public static void main(final String[] args) {
      GetMinimalFeasibleConfigurationSampling plot = new GetMinimalFeasibleConfigurationSampling();
      CommandLine cli = new CommandLine(plot);
      cli.execute(args);
   }

   @Override
   public Void call() throws Exception {
      File f1ScoreComparison = new File(data[0].getParentFile(), "f1-score-comparison");
      f1ScoreComparison.mkdirs();
      
      for (File dataFile : data) {
         FileWriter f1ScoreWriter = new FileWriter(new File(f1ScoreComparison, dataFile.getName() + ".csv"));
         try (BufferedWriter writer = new BufferedWriter(f1ScoreWriter)){
            LOG.info("Analyzing: {}", dataFile);
            analyzeFile(dataFile, writer);
            writer.flush();
         }
      }

      return null;
   }

   private void analyzeFile(File dataFile, BufferedWriter writer) throws FileNotFoundException, IOException {
      for (int depth : new int[] { 2, 4, 6, 8, 10 }) {
         for (int changePercentage : new int[] { 1, 2, 3, 4, 5 }) {
            File file = new File(dataFile, "project_" + depth + "_" + (300 + 300 * changePercentage / 100) + "_peass_results/de.dagere.peass.MainTest_testMe.csv");
            if (file.exists()) {
//                     LOG.info("Reading " + file.getAbsolutePath());
               
               PrecisionData data = PrecisionDataReader.readHeatmap(file, 25);
               MinimalFeasibleConfigurationDeterminer determiner = new MinimalFeasibleConfigurationDeterminer(98.0);
               Map<Integer, Configuration> configuration = determiner.getMinimalFeasibleConfiguration(data);
//                     LOG.info("Suitable configurations: " + configuration.size());
//                     System.out.println(configuration);
               
               Collection<Configuration> configurations = configuration.values();
//                     f1ScoreComparison.wr
               writer.write(depth + " " + changePercentage + " " + 
                     (configuration.size() > 0 && configurations.iterator().next().getVMs() < 100 ? configurations.iterator().next().getVMs() : 100) + "\n");
               
//                     Map<Integer, Configuration> config = determiner.getMinimalFeasibleConfiguration(data);
//                     return config;
            } else {
               LOG.error("Missing: " + file.getAbsolutePath());
            }
         }
      }
   }
}
