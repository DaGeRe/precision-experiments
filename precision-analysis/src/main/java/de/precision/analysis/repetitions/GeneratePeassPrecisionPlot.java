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

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.peass.folders.PeassFolders;
import picocli.CommandLine;
import picocli.CommandLine.Option;

public class GeneratePeassPrecisionPlot implements Callable<Void> {

   private static final Logger LOG = LogManager.getLogger(GeneratePeassPrecisionPlot.class);
   

   @Option(names = { "-data", "--data" }, description = "Data-Folder for analysis", required = true)
   private File[] data;
   
   @Option(names = { "-slowVersionName", "--slowVersionName" }, description = "Version that is assumed to be slower", required = true)
   private String slowVersionName;
   
   @Option(names = { "-printPicks", "--printPicks" }, description = "Print the picked values summaries (for debugging)")
   private boolean printPicks;
   
   @Option(names = { "-threads", "--threads" }, description = "Count of threads for analysis")
   private int threads = 2;
   
   @Option(names = { "-iterationResolution", "--iterationResolution" }, description = "Resolution for iteration count analysis (by default: 50 steps for iteration count)")
   private int iterationResolution = 50;

   @Option(names = { "-vmResolution", "--vmResolution" }, description = "Resolution for VM count analysis (by default: 50 steps for VM count)")
   private int vmResolution = 20;
   
   @Option(names = { "-statisticalTests", "--statisticalTests" }, description = "Statistical tests that should be used (either ALL or ALL_NO_BIMODA)")
   private StatisticalTestList statisticalTestList = StatisticalTestList.ALL_NO_BIMODAL_NO_CONFIDENCE;

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

   private void createPoolTasks(final ExecutorService pool, final Map<String, BufferedWriter> testcaseWriters, final File file) throws IOException, JAXBException {
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
               reader.read(slowVersionName, versionFile, testclazzFile);
               
               Map<String, Testcases> testcasesV1 = reader.getTestcasesV1();
               Map<String, Testcases> testcasesV2 = reader.getTestcasesV2();
               int repetitions = reader.getRepetitions();
               int maxIterations = reader.getIterations();

               boolean removeOutliers = true;
               PrecisionConfig precisionConfig = new PrecisionConfig(false, removeOutliers, printPicks, threads, statisticalTestList.getTests(), iterationResolution, vmResolution);
               PrecisionPlotHandler handler = new PrecisionPlotHandler(testcasesV1, testcasesV2, pool, repetitions, precisionConfig, writingData);
               handler.handleAllParameters(100, maxIterations);
            }
         }
      }
   }
}
