package de.precision.analysis.graalvm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.VMResult;

public class PlottableHistogramWriter {
   private final File trainingFolder;
   private final File testingFolder;

   public PlottableHistogramWriter(final File folder) {
      if (!folder.exists()) {
         folder.mkdirs();
      }
      trainingFolder = new File(folder, "training");
      testingFolder = new File(folder, "testing");
      trainingFolder.mkdir();
      testingFolder.mkdir();
   }
   
   public void plotTraining(final String comparisonName, final Kopemedata dataOld, final Kopemedata dataNew) {
      plot(comparisonName, dataOld, dataNew, trainingFolder);
   }
   
   public void plotTesting(final String comparisonName, final Kopemedata dataOld, final Kopemedata dataNew) {
      plot(comparisonName, dataOld, dataNew, testingFolder);
   }
   
   private void plot(final String comparisonName, final Kopemedata dataOld, final Kopemedata dataNew, File folder) {
      File goalFile = new File(folder, comparisonName + ".csv");
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(goalFile))){
         List<VMResult> collectorOld = dataOld.getFirstDatacollectorContent();
         List<VMResult> collectorNew = dataNew.getFirstDatacollectorContent();
         for (int i = 0; i < Math.max(collectorOld.size(), collectorNew.size()); i++) {
            double valueOld = collectorOld.size() > i ? collectorOld.get(i).getValue() : Double.NaN;
            double valueNew = collectorNew.size() > i ? collectorNew.get(i).getValue() : Double.NaN;
            writer.write(valueOld + ";" + valueNew + "\n");
         }
         writer.flush();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
