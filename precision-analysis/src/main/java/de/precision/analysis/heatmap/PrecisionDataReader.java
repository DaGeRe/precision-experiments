package de.precision.analysis.heatmap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class PrecisionDataReader {

   public static PrecisionData readHeatmap(final File heatmapFile, final int columnIndex) throws FileNotFoundException, IOException {

      final PrecisionData heatmap = new PrecisionData();
      try (BufferedReader reader = new BufferedReader(new FileReader(heatmapFile))) {
         String line;
         while ((line = reader.readLine()) != null) {
            if (!line.startsWith("repetitions ") && line.length() > 3) {
               handleLine(heatmap, line, columnIndex);
            }
         }
      }
      return heatmap;
   }

   private static void handleLine(final PrecisionData heatmap, final String line, final int columnIndex) {
      String[] parts = line.split(" ");
      int repetitions = Integer.parseInt(parts[0]);
      int VMs = Integer.parseInt(parts[1]);
      int iterations = Integer.parseInt(parts[2]);
      double value = Double.parseDouble(parts[columnIndex]);

      heatmap.addData(repetitions, VMs, iterations, value);
   }
}
