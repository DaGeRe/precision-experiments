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

   @Option(names = { "-trainingStartDate", "--trainingStartDate" }, description = "Start date for the training")
   private String trainingStartDate;
   
   @Option(names = { "-trainingEndDate", "--trainingDate" }, description = "End date of the training (so training is between startDate and trainingDate)", required = true)
   private String trainingEndDate;

   @Option(names = { "-testStartDate", "--testStartDate" }, description = "End date for the training (subsequent data will be used for testing)")
   private String testStartDate;
   
   @Option(names = { "-testEndDate", "--testEndDate" }, description = "End date for the training (subsequent data will be used for testing)")
   private String testEndDate;

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
         
         
         Date trainingStartDateD = trainingStartDate != null ? sdf.parse(trainingStartDate) : new Date(2000, 01, 01);
         System.out.println("Training start date: " + trainingStartDateD);
         
         Date trainingEndDateD = sdf.parse(trainingEndDate);
         System.out.println("Training end date: " + trainingEndDateD);
         
         Date testStartDateD = testStartDate != null ? sdf.parse(testStartDate) : new Date(2000, 01, 01);
         System.out.println("Test start date: " + testStartDateD);
         
         Date testEndDateD = testEndDate != null ? sdf.parse(testEndDate) : new Date(3000, 01, 01);
         System.out.println("Test end date: " + testEndDateD);

         // ComparisonFinder finder = first == null ? new ComparisonFinder(folder, date) : new ComparisonFinder(folder, DateFormat.getInstance().parse(first), date);
         MetadiffReader reader = new MetadiffReader(folder);

         ComparisonCollection comparisons = reader.getComparisons();

         for (Map.Entry<String, Map<String, Comparison>> benchmarkData : comparisons.getComparisons().entrySet()) {
            LOG.info("Reading benchmark {}", benchmarkData.getKey());
            Map<String, Comparison> thisBenchmarkComparisons = benchmarkData.getValue();
            ComparisonFinder finder = new ComparisonFinder(thisBenchmarkComparisons, trainingStartDateD, trainingEndDateD, testStartDateD, testEndDateD, folder);

            if (finder.isComparisonFound()) {
               createModel(true, testEndDateD, finder, benchmarkData.getKey());
               createModel(false, testEndDateD, finder, benchmarkData.getKey());
            }
         }

      } catch (ParseException | IOException | InterruptedException e1) {
         e1.printStackTrace();
      }
   }

   private void createModel(boolean cleaned, Date date, ComparisonFinder finder, String benchmarkKey)
         throws ParseException, InterruptedException, IOException, StreamWriteException, DatabindException {
      SimpleModel model = new SimpleModel();
      model.setTrainingStartDate(trainingStartDate);
      model.setTrainingEndDate(trainingEndDate);
      model.setTestStartDate(testStartDate);
      model.setTestEndDate(testEndDate);

      System.out.println("Training comparisons: " + finder.getComparisonsTraining().size());
      System.out.println("Test comparisons: " + finder.getComparisonsTest().size());

      File resultsFolder = new File("results");
      resultsFolder.mkdirs();

      PrecisionFileManager manager = new PrecisionFileManager();

      ExecutorService pool = Executors.newFixedThreadPool(precisionConfigMixin.getThreads());

      final PlottableHistogramWriter histogramWriter = new PlottableHistogramWriter(new File("plottableGraphs/" + benchmarkKey));

      // for (int vmCount : new int[] { 5, 10, 20, 30 }) {
      for (double type2error : new double[] { 0.01, 0.1, 0.2, 0.5}) {
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
