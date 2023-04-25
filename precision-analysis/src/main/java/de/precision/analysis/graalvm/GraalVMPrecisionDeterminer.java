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

import org.apache.commons.math3.stat.inference.TTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.VMResult;
import de.dagere.peass.config.StatisticsConfig;
import de.dagere.peass.measurement.statistics.Relation;
import de.dagere.peass.measurement.statistics.StatisticUtil;
import de.dagere.peass.measurement.statistics.bimodal.CompareData;
import de.precision.analysis.repetitions.ExecutionData;
import de.precision.analysis.repetitions.PrecisionComparer;
import de.precision.analysis.repetitions.PrecisionConfigMixin;
import de.precision.analysis.repetitions.PrecisionWriter;
import de.precision.processing.repetitions.sampling.SamplingConfig;
import de.precision.processing.repetitions.sampling.SamplingExecutor;
import picocli.CommandLine;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

public class GraalVMPrecisionDeterminer implements Runnable {

   private static final Logger LOG = LogManager.getLogger(GraalVMPrecisionDeterminer.class);

   @Option(names = { "-folder", "--folder" }, description = "Folder, that contains *all* data folders for the analysis", required = true)
   private File folder;

   @Option(names = { "-endDate", "--endDate" }, description = "End date for the analysis", required = true)
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
      Date date;
      try {
         date = DateFormat.getInstance().parse(endDate);
         System.out.println("End date: " + date);

         ComparisonFinder finder = new ComparisonFinder(folder, date);

         System.out.println("Training comparisons: " + finder.getComparisonsTraining().size());
         System.out.println("Test comparisons: " + finder.getComparisonsTest().size());

         executeComparisons(finder);

      } catch (ParseException | IOException e1) {
         e1.printStackTrace();
      }
   }

   private void executeComparisons(ComparisonFinder finder) throws IOException, FileNotFoundException {
      for (Comparison comparison : finder.getComparisonsTraining().values()) {
         File folderPredecessor = new File(folder, "measurements/" + comparison.getIdOld());
         File folderCurrent = new File(folder, "measurements/" + comparison.getIdNew());

         System.out.println("Reading " + folderPredecessor + " " + folderCurrent);
         Kopemedata dataOld = GraalVMReadUtil.readData(folderPredecessor);
         Kopemedata dataNew = GraalVMReadUtil.readData(folderCurrent);

         CompareData data = new CompareData(dataOld.getFirstDatacollectorContent(), dataNew.getFirstDatacollectorContent());
         Relation expected = getRealRelation(data);
         LOG.info("Expected relation: {}", expected);

         executeOneComparison(comparison, dataOld, dataNew, expected);
         
      }
   }

   private void executeOneComparison(Comparison comparison, Kopemedata dataOld, Kopemedata dataNew, Relation expected) throws IOException {
      String fileName = (Relation.isUnequal(expected) ? "unequal_" : "equal_" )  + comparison.getIdNew() + ".csv";
      File resultFile = new File("results/" + fileName);
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile))){
         for (int vmCount : new int[] { 5, 10, 15, 20, 25, 30 }) {
            SamplingConfig samplingConfig = new SamplingConfig(vmCount, "GraalVMBenchmark");

            int maxRuns = getMaximumPossibleRuns(dataOld, dataNew);
            for (int iterations = 1; iterations < maxRuns; iterations++) {
               ExecutionData executionData = new ExecutionData(vmCount, 0, iterations, 1);
               
               final List<VMResult> fastShortened = StatisticUtil.shortenValues(dataOld.getFirstDatacollectorContent(), 0, iterations);
               final List<VMResult> slowShortened = StatisticUtil.shortenValues(dataNew.getFirstDatacollectorContent(), 0, iterations);
               
               CompareData shortenedData = new CompareData(fastShortened, slowShortened);
               
               StatisticsConfig config = new StatisticsConfig();

               PrecisionComparer comparer = new PrecisionComparer(config, precisionConfigMixin.getConfig());
               for (int i = 0; i < samplingConfig.getSamplingExecutions(); i++) {
                  SamplingExecutor samplingExecutor = new SamplingExecutor(samplingConfig, shortenedData, comparer);
                  samplingExecutor.executeComparisons(expected);
               }
               
               new PrecisionWriter(comparer, executionData).writeTestcase(writer, comparer.getOverallResults().getResults());
            }
         }
      }
   }

   private int getMaximumPossibleRuns(Kopemedata dataOld, Kopemedata dataNew) {
      int maxRuns = Integer.MAX_VALUE;
      for (VMResult result : dataOld.getFirstDatacollectorContent()) {
         maxRuns = Math.min(maxRuns, result.getFulldata().getValues().size());
      }
      for (VMResult result : dataNew.getFirstDatacollectorContent()) {
         maxRuns = Math.min(maxRuns, result.getFulldata().getValues().size());
      }
      return maxRuns;
   }

   private Relation getRealRelation(CompareData data) {
      final boolean tchange = new TTest().homoscedasticTTest(data.getPredecessor(), data.getCurrent(), 0.01);
      Relation expected;
      if (tchange) {
         if (data.getAvgPredecessor() > data.getAvgCurrent()) {
            expected = Relation.GREATER_THAN;
         } else {
            expected = Relation.LESS_THAN;
         }
      } else {
         expected = Relation.EQUAL;
      }
      return expected;
   }
}
