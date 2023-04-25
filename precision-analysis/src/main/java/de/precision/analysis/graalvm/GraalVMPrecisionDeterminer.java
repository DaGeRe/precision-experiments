package de.precision.analysis.graalvm;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.math3.stat.inference.TTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.peass.config.StatisticsConfig;
import de.dagere.peass.measurement.statistics.Relation;
import de.dagere.peass.measurement.statistics.bimodal.CompareData;
import de.precision.analysis.repetitions.PrecisionComparer;
import de.precision.analysis.repetitions.PrecisionConfigMixin;
import de.precision.analysis.repetitions.StatisticalTests;
import de.precision.analysis.repetitions.TestExecutors;
import de.precision.processing.repetitions.sampling.SamplingConfig;
import de.precision.processing.repetitions.sampling.SamplingExecutor;
import de.precision.processing.repetitions.sampling.VMCombinationSampler;
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

         for (Comparison comparison : finder.getComparisonsTraining().values()) {
            File folderPredecessor = new File(folder, "measurements/" + comparison.getIdOld());
            File folderCurrent = new File(folder, "measurements/" + comparison.getIdNew());

            System.out.println("Reading " + folderPredecessor + " " + folderCurrent);
            Kopemedata dataOld = GraalVMReadUtil.readData(folderPredecessor);
            Kopemedata dataNew = GraalVMReadUtil.readData(folderCurrent);

            CompareData data = new CompareData(dataOld.getFirstDatacollectorContent(), dataNew.getFirstDatacollectorContent());
            Relation expected = getRealRelation(data);

            LOG.info("Expected relation: {}", expected);

            for (int vmCount : new int[] { 5, 10, 15, 20, 25, 30 }) {
               SamplingConfig samplingConfig = new SamplingConfig(vmCount, "GraalVMBenchmark");

               for (double type2error : new double[] { 0.01 }) {
                  StatisticsConfig config = new StatisticsConfig();
                  config.setType2error(type2error);

                  PrecisionComparer comparer = new PrecisionComparer(config, precisionConfigMixin.getConfig());
                  SamplingExecutor samplingExecutor = new SamplingExecutor(samplingConfig, data, comparer);
                  for (int i = 0; i < samplingConfig.getSamplingExecutions(); i++) {
                     samplingExecutor.executeComparisons(expected);
                  }
                  
                  double precision = comparer.getPrecision(StatisticalTests.TTEST);
                  double fscore = comparer.getFScore(StatisticalTests.TTEST);
                  System.out.println("Precision: " + precision + " F-Score: " +fscore);
               }
            }
         }

      } catch (ParseException | IOException e1) {
         e1.printStackTrace();
      }
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
