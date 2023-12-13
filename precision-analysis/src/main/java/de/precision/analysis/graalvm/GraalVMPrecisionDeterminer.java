package de.precision.analysis.graalvm;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;

import de.dagere.peass.utils.Constants;
import de.precision.analysis.graalvm.resultingData.SimpleModel;
import de.precision.analysis.repetitions.PrecisionConfigMixin;
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

      try {
         SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
         Date date = sdf.parse(endDate);
         System.out.println("End date: " + date);

         // ComparisonFinder finder = first == null ? new ComparisonFinder(folder, date) : new ComparisonFinder(folder, DateFormat.getInstance().parse(first), date);
         MetadiffReader reader = new MetadiffReader(folder);

         ComparisonCollection comparisons = reader.getComparisons();

         for (Map.Entry<String, Map<String, Comparison>> benchmarkData : comparisons.getComparisons().entrySet()) {
            LOG.info("Reading benchmark {}", benchmarkData.getKey());
            Map<String, Comparison> thisBenchmarkComparisons = benchmarkData.getValue();
            ComparisonFinder finder = new ComparisonFinder(thisBenchmarkComparisons, null, date, folder);

            if (finder.getStartDate() != null) {
               createModel(true, date, finder, benchmarkData.getKey());
               createModel(false, date, finder, benchmarkData.getKey());
            }
         }

      } catch (ParseException | IOException | InterruptedException e1) {
         e1.printStackTrace();
      }
   }

   private void createModel(boolean cleaned, Date date, ComparisonFinder finder, String benchmarkKey)
         throws ParseException, InterruptedException, IOException, StreamWriteException, DatabindException {
      SimpleModel model = new SimpleModel();
      model.setLast(endDate);

      System.out.println("Start date: " + finder.getStartDate().toString());
      model.setFirst(finder.getStartDate().toString());

      System.out.println("Training comparisons: " + finder.getComparisonsTraining().size());
      System.out.println("Test comparisons: " + finder.getComparisonsTest().size());

      File resultsFolder = new File("results");
      resultsFolder.mkdirs();

      PrecisionFileManager manager = new PrecisionFileManager();

      ExecutorService pool = Executors.newFixedThreadPool(precisionConfigMixin.getThreads());

      final PlottableHistogramWriter histogramWriter = new PlottableHistogramWriter(new File("plottableGraphs/" + benchmarkKey));

      // for (int vmCount : new int[] { 5, 10, 20, 30 }) {
      for (double type2error : new double[] { 0.01, 0.1, 0.2, 0.5, 0.75, 0.9 }) {
         final GraalVMPrecisionThread precisionThread = new GraalVMPrecisionThread(cleaned, model, precisionConfigMixin.getConfig(), finder, manager, type2error, histogramWriter);
         pool.submit(() -> {
            try {
               precisionThread.getConfigurationAndTest();
            } catch (Throwable t) {
               t.printStackTrace();
            }
         });
      }
      // }

      LOG.info("Waiting for thread completion...");
      pool.shutdown();
      pool.awaitTermination(100, TimeUnit.HOURS);
      LOG.info("Finished");

      manager.cleanup();

      File resultFile = cleaned ? new File("model_" + benchmarkKey + "_cleaned.json") : new File("model_" + benchmarkKey + ".json");
      Constants.OBJECTMAPPER.writeValue(resultFile, model);
   }

}
