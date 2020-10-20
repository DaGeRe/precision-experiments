package de.precision.analysis.repetitions;

import java.io.File;
import java.io.IOException;

public class SingleFileGenerator {
   public static void createSingleFiles(final String[] args) {
      for (final String inputFolderName : args) {
         final File inputFolder = new File(inputFolderName);
         File RESULTFOLDER = new File(inputFolder.getParentFile(), inputFolder.getName() + File.separator + "results_noOutlierRemoval");
         getSingleRepetitionFiles(inputFolder, RESULTFOLDER);

         File RESULTFOLDER_outlier = new File(inputFolder.getParentFile(), inputFolder.getName() + File.separator + "results_outlierRemoval");
         getSingleRepetitionFiles(inputFolder, RESULTFOLDER_outlier);
      }
   }

   private static void getSingleRepetitionFiles(final File inputFolder, File RESULTFOLDER) {
      for (File child : inputFolder.listFiles()) {
         final String name = child.getName();
         if (name.startsWith("precision_") && !name.endsWith(".tar")) {
            System.out.println(name);
            int count = Integer.valueOf(name.substring(name.indexOf('_') + 1));
            ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", "cat precision.csv | grep \"^" + count + " \" > " + count + ".csv");
            pb.directory(RESULTFOLDER);
            try {
               pb.start().waitFor();
            } catch (InterruptedException | IOException e) {
               e.printStackTrace();
            }
         }
      }
   }

}
