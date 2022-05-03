package de.precision.processing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import de.dagere.kopeme.datastorage.JSONDataLoader;
import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.VMResult;

public class GetSizeEvolution {

   private List<String> testNames = new LinkedList<>();
   private SizeDataManager dataManager = new SizeDataManager();

   public static void main(String[] args) throws IOException {
      System.out.println(args.length);
      
      for (String folderPath : args) {
         GetSizeEvolution covEvolution = new GetSizeEvolution();
         File folder = new File(folderPath);
         System.out.println("Loading: " + folder);
         covEvolution.getSizeStatistics(folder);
         
         File resultFolder = new File(folder.getParentFile(), folder.getName() + "_result");
         resultFolder.mkdir();
         covEvolution.printStatistics(resultFolder);
      }

   }

   private void printStatistics(File folder) throws IOException {
      printValues(folder);
      printGnuplotCommands();
   }

   private void printValues(File folder) throws IOException {
      printCoVEvolution(folder);
      printMeanEvolution(folder);
      printVMDeviationEvolution(folder);
      printVMDeviationEvolutionAbsolute(folder);
   }

   private void printVMDeviationEvolutionAbsolute(File folder) throws IOException {
      final File summaryFile = new File(folder, "vmdeviation_evolution_absolute.csv");
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(summaryFile))) {
         writeHeader(writer);
         for (Map.Entry<Integer, Map<String, StatisticDataEntity>> size : dataManager.getValues().entrySet()) {
            writer.write(size.getKey() + " ");
            for (String type : testNames) {
               SummaryStatistics stat = size.getValue().get(type).getMean();
               double value = stat != null ? stat.getStandardDeviation() : 0.0;
               writer.write(value + " ");
            }
            writer.write("\n");
         }
      }
   }

   private void writeHeader(BufferedWriter writer) throws IOException {
      writer.write("#");
      for (String type : testNames) {
         writer.write(type + " ");
      }
      writer.write("\n");
   }

   private void printVMDeviationEvolution(File folder) throws IOException {
      final File summaryFile = new File(folder, "vmdeviation_evolution.csv");
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(summaryFile))) {
         writeHeader(writer);
         for (Map.Entry<Integer, Map<String, StatisticDataEntity>> size : dataManager.getValues().entrySet()) {
            writer.write(size.getKey() + " ");
            for (String type : testNames) {
               SummaryStatistics stat = size.getValue().get(type).getMean();
               double value = stat != null ? stat.getStandardDeviation() / stat.getMean() : 0.0;
               writer.write(value + " ");
            }
            writer.write("\n");
         }
      }
   }

   private void printMeanEvolution(File folder) throws IOException {
      final File summaryFile = new File(folder, "mean_evolution.csv");
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(summaryFile))) {
         writeHeader(writer);
         for (Map.Entry<Integer, Map<String, StatisticDataEntity>> size : dataManager.getValues().entrySet()) {
            writer.write(size.getKey() + " ");
            for (String type : testNames) {
               SummaryStatistics stat = size.getValue().get(type).getMean();
               double value = stat != null ? stat.getMean() : 0.0;
               writer.write(value + " ");
               double min = stat != null ? stat.getMin() : 0.0;
               writer.write(min + " ");
               double max = stat != null ? stat.getMax() : 0.0;
               writer.write(max + " ");
            }
            writer.write("\n");
         }
      }
      printMeanSizeParted(folder);
   }

   private void printMeanSizeParted(File folder) throws IOException {
      for (int i = 0; i < StatisticDataEntity.meanSize; i++) {
         final File summaryFile = new File(folder, "mean_evolution_" + i + ".csv");
         try (BufferedWriter writer = new BufferedWriter(new FileWriter(summaryFile))) {
            writeHeader(writer);
            for (Map.Entry<Integer, Map<String, StatisticDataEntity>> size : dataManager.getValues().entrySet()) {
               writer.write(size.getKey() + " ");
               for (String type : testNames) {
                  final List<SummaryStatistics> otherMeans = size.getValue().get(type).getOtherMeans();
                  if (otherMeans.size() > i) {
                     SummaryStatistics stat = otherMeans.get(i);
                     double value = stat != null ? stat.getMean() : 0.0;
                     writer.write(value + " ");
                  }
               }
               writer.write("\n");
            }
         }
      }
   }

   private void printCoVEvolution(File folder) throws IOException {
      final File summaryFile = new File(folder, "cov_evolution.csv");
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(summaryFile))) {
         writeHeader(writer);
         for (Map.Entry<Integer, Map<String, StatisticDataEntity>> size : dataManager.getValues().entrySet()) {
            writer.write(size.getKey() + " ");
            for (String type : testNames) {
               SummaryStatistics stat = size.getValue().get(type).getDeviation();
               double value = stat != null ? stat.getMean() : 0.0;
               writer.write(value + " ");
            }
            writer.write("\n");
         }
      }
   }

   private void printGnuplotCommands() {
      System.out.println("set encoding iso_8859_1");
      System.out.println("set terminal wxt size 600,400");
      System.out.println("set xlabel 'Workloadgr{\\366}{\\337}e'");
      // System.out.println("set xlabel 'Workload Size'");
      System.out.println("set ylabel 'Iteration-Standardabweichung s'");
      System.out.print("plot ");

      for (int i = 0; i < testNames.size(); i++) {
         System.out.print("'cov_evolution.csv' u 1:" + (i + 2) + " w lines title '" + testNames.get(i) + "',");
      }
   }

   private void getSizeStatistics(File folder)  {
      testNames.add(folder.getName());
      for (File file : folder.listFiles()) {
         if (file.getName().startsWith("wl_") && !file.getName().endsWith(".tar")) {
            System.out.println("File: " + file.getName());
            String[] parts = file.getName().split("_");
            int size = getSize(parts);
            // int index = Integer.parseInt(parts[1]);
            if (parts.length == 3) {
               final VMResult result = readShortenedResult(file);
               dataManager.addMean(size, folder.getName(), result.getValue());
               double relativeDeviation = getRelativeDeviation(result, size);
               dataManager.addDeviation(size, folder.getName(), relativeDeviation);
            }
         }
      }
   }

   private VMResult readShortenedResult(File file)  {
      System.out.println(Arrays.toString(file.listFiles()));
      File measurementFile = file.listFiles((FileFilter) new WildcardFileFilter("*.xml"))[0];
      final Kopemedata loadData = JSONDataLoader.loadWarmedupData(measurementFile);

      final VMResult basicResult = loadData.getFirstResult();
//      final Result result = StatisticUtil.shortenResult(basicResult);
      return basicResult;
   }

   private double getRelativeDeviation(VMResult result, int size)  {
      double relativeDeviation = result.getDeviation() / result.getValue();
      System.out.println("Values: " + size + " " + +relativeDeviation + " " + result.getDeviation() + " " + result.getValue());
      return relativeDeviation;
   }

   private int getSize(String[] parts) {
      int size;
      if (parts.length == 3) {
         size = Integer.parseInt(parts[1]);
      } else {
         size = Integer.parseInt(parts[2]);
      }
      return size;
   }
}
