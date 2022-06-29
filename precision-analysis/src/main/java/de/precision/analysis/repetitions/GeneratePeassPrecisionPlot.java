package de.precision.analysis.repetitions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.peass.folders.PeassFolders;
import picocli.CommandLine;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

public class GeneratePeassPrecisionPlot implements Callable<Void> {

   private static final Logger LOG = LogManager.getLogger(GeneratePeassPrecisionPlot.class);

   @Option(names = { "-data", "--data" }, description = "Data-Folder for analysis", required = true)
   private File[] data;

   @Option(names = { "-slowCommitName", "--slowCommitName" }, description = "Commit that is assumed to be slower", required = true)
   private String slowCommitName;

   @Mixin
   private PrecisionConfigMixin precisionConfigMixin;

   public static void main(final String[] args) {
      GeneratePeassPrecisionPlot plot = new GeneratePeassPrecisionPlot();
      CommandLine cli = new CommandLine(plot);
      cli.execute(args);
   }

   @Override
   public Void call() throws Exception {

      ExecutorService pool = Executors.newFixedThreadPool(2);

      Map<String, BufferedWriter> testcaseWriters = new HashMap<>();

      for (File file : data) {
         createPoolTasks(pool, testcaseWriters, file);
      }
      pool.shutdown();
      pool.awaitTermination(10000, TimeUnit.HOURS);
      return null;
   }

   private void createPoolTasks(final ExecutorService pool, final Map<String, BufferedWriter> testcaseWriters, final File file) throws IOException {
      LOG.info("Handling Peass-Folder {}", file);
      File measurementsFolder = new File(file, PeassFolders.MEASUREMENTS);
      for (File testclazzFile : measurementsFolder.listFiles()) {
         File resultFolder = new File(testclazzFile, "results");
         resultFolder.mkdir();
         BufferedWriter precisionRecallWriter = new BufferedWriter(new FileWriter(new File(resultFolder, "precision.csv")));
         PrecisionWriter.writeHeader(precisionRecallWriter, StatisticalTestList.ALL.getTests());
         WritingData writingData = new WritingData(resultFolder, precisionRecallWriter, testcaseWriters);
         for (File versionFile : testclazzFile.listFiles()) {
            if (!versionFile.getName().equals("results")) {
               RegularPeassdataReader reader = new RegularPeassdataReader();
               reader.read(slowCommitName, versionFile, testclazzFile);

               Map<String, Kopemedata> testcasesV1 = reader.getTestcasesV1();
               Map<String, Kopemedata> testcasesV2 = reader.getTestcasesV2();
               int repetitions = reader.getRepetitions();
               int maxIterations = reader.getIterations();
               int maxVMsMeasured = reader.getVMs();

               PrecisionConfig precisionConfig = precisionConfigMixin.getConfig();
               PrecisionPlotHandler handler = new PrecisionPlotHandler(testcasesV1, testcasesV2, pool, repetitions, precisionConfig, writingData);
               handler.handleAllParameters(maxVMsMeasured, maxIterations);
            }
         }
      }
   }
}
