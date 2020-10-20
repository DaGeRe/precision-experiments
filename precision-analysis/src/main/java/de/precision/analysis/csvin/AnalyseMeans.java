package de.precision.analysis.csvin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

public class AnalyseMeans {
   public static void main(String[] args) throws NumberFormatException, IOException {
      File meanFile = new File("results/cov/steady_state.csv");
      File executionsFolder = new File("/home/reichelt/daten3/diss/repos/precision-experiment-clean/cov/cov_true_de.precision.add.GenericAddTest_NoGC_3");
      
      String line;

      try (BufferedReader reader = new BufferedReader(new FileReader(meanFile))){
         File slowFolder = new File("results/slow");
         File fastFolder = new File("results/fast");
         slowFolder.mkdirs();
         fastFolder.mkdirs();

         int index = 1;
         while ((line = reader.readLine()) != null) {
            double duration = Double.parseDouble(line.split(" ")[0]);
            // System.out.println(duration);
            File logFile = new File(executionsFolder, "execution_"+index+".txt");
            if (duration > 12700) {
               Files.copy(logFile.toPath(), new File(slowFolder, logFile.getName()).toPath());
               System.out.println(index);
            } else {
               Files.copy(logFile.toPath(), new File(fastFolder, logFile.getName()).toPath());
            }
            index++;
         }
      }
   }
}
