package de.precision.analysis.csvin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FindRepeatableState {

   private static final DecimalFormat df = new DecimalFormat("#,###.00", DecimalFormatSymbols.getInstance(Locale.GERMAN));

   private static final Logger LOG = LogManager.getLogger(FindRepeatableState.class);

   static class MeasurementEntry {
      final double mean, deviation;

      public MeasurementEntry(double mean, double deviation) {
         this.mean = mean;
         this.deviation = deviation;
      }

      public double getMean() {
         return mean;
      }
   }

   public static void main(String[] args) throws FileNotFoundException, IOException {
      if (args.length == 0) {
         System.err.println("Please pass folders that contain aggregated CoV result data.\n"
               + "If you got the dataset, normale $DATASET_LOCATION/CoV/AddTest/aggregated");
      }

      for (String arg : args) {
         File folder = new File(arg);
         File baselinetestFile = new File(folder, "BaselineTest/aggregated");
         File addTestFile = new File(folder, "AddTest/aggregated");
         File ramTestFile = new File(folder, "RAMTest/aggregated");
         File sysoutTestFile = new File(folder, "SysoutTest/aggregated");

         // If these folders all exist, we can safely assume that the parent folder was passed to the program
         if (baselinetestFile.exists() && addTestFile.exists() && ramTestFile.exists() && sysoutTestFile.exists()) {
            handleResultFolder(baselinetestFile);
            handleResultFolder(addTestFile);
            handleResultFolder(ramTestFile);
            handleResultFolder(sysoutTestFile);
         } else {
            handleResultFolder(folder);
         }
      }
   }

   private static void handleResultFolder(File folder) throws IOException, FileNotFoundException {
      final List<SteadyStateChecker> checkers = getCheckerList();

      LOG.trace("Analyzing " + folder.getAbsolutePath());
      for (File measurementCSV : folder.listFiles((FileFilter) new WildcardFileFilter("result_*csv"))) {
         LOG.trace(measurementCSV.getAbsolutePath());
         List<MeasurementEntry> entries = readFile(measurementCSV);

         if (entries.size() > 0) {
            runChecking(checkers, entries);
         }
      }

      printResultTable(folder, checkers);
   }

   private static void runChecking(List<SteadyStateChecker> checkers, List<MeasurementEntry> entries) {
      SummaryStatistics endStatistics = getSteadyStateStatistics(entries);

      for (SteadyStateChecker checker : checkers) {
         checker.setSteadyStateStatistics(endStatistics);
      }

      findCheckersIndexes(entries, checkers);
      finishCheckers(checkers);
   }

   private static void printResultTable(File folder, List<SteadyStateChecker> checkers) {
      for (SteadyStateChecker checker : checkers) {
         System.out.print(folder.getParentFile().getName() + " & " + checker.getGermanName() + " & ");
         final double meanValue = checker.getValueStatistics().getMean();
         String formatedDuration = !Double.isNaN(meanValue) ? df.format(meanValue) : "NaN";
         int abortIndexValue = (int) checker.getIndexStatistics().getMean() * 5000;
         String abortIndex;
         if (abortIndexValue != 9990000) {
            abortIndex = NumberFormat.getInstance(Locale.GERMAN).format(abortIndexValue);
         } else {
            abortIndex = "-";
         }

         System.out.print(abortIndex + " & " + formatedDuration);
         System.out.println("\\\\");
         // System.out.println(checker.getIndexStatistics().getStandardDeviation() + " " + checker.getValueStatistics().getStandardDeviation());
      }
      System.out.println("\\hline");
   }

   private static List<SteadyStateChecker> getCheckerList() {
      SteadyStateChecker checkerDeviation = SteadyStateChecker.getDeviationChecker();
      SteadyStateChecker checkerCoV = SteadyStateChecker.getCoVChecker();
      SteadyStateChecker checkerEnd = SteadyStateChecker.getEndChecker();

      List<SteadyStateChecker> checkers = new LinkedList<>();

      checkers.add(checkerCoV);
      checkers.add(checkerDeviation);
      checkers.add(checkerEnd);
      return checkers;
   }

   private static void findCheckersIndexes(List<MeasurementEntry> entries, List<SteadyStateChecker> checkers) {
      int index = 0;
      final int STATE_SIZE = 10;
      double[] lastThree = new double[STATE_SIZE];
      for (MeasurementEntry entry : entries) {
         lastThree[index % STATE_SIZE] = entry.mean;
         SummaryStatistics stat = currentStatistics(lastThree);

         for (SteadyStateChecker checker : checkers) {
            checker.check(index, entry, stat);
         }
         index++;
      }
   }

   private static void finishCheckers(List<SteadyStateChecker> checkers) {
      for (SteadyStateChecker checker : checkers) {
         checker.finish();
      }
   }

   private static SummaryStatistics currentStatistics(double[] lastThree) {
      SummaryStatistics stat = new SummaryStatistics();
      for (int i = 0; i < 3; i++) {
         stat.addValue(lastThree[i]);
      }
      return stat;
   }

   private static SummaryStatistics getSteadyStateStatistics(List<MeasurementEntry> entries) {
      SummaryStatistics endStatistics = new SummaryStatistics();
      for (MeasurementEntry entry : entries.subList(entries.size() - 10, entries.size())) {
         endStatistics.addValue(entry.mean);
      }
      return endStatistics;
   }

   private static List<MeasurementEntry> readFile(File measurementCSV) throws IOException, FileNotFoundException {
      List<MeasurementEntry> entries = new LinkedList<>();
      String line;
      try (BufferedReader reader = new BufferedReader(new FileReader(measurementCSV))) {
         while ((line = reader.readLine()) != null) {
            String[] values = line.split(" ");
            double mean = Double.parseDouble(values[0]);
            double deviation = Double.parseDouble(values[1]);
            MeasurementEntry entry = new MeasurementEntry(mean, deviation);
            entries.add(entry);
         }
      }
      return entries;
   }
}
