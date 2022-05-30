package de.precision.analysis.heatmap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class WorkloadHeatmapReader {
   public static WorkloadHeatmap readHeatmap(final File heatmapFile) throws FileNotFoundException, IOException {
      final WorkloadHeatmap heatmap = new WorkloadHeatmap();
      try (BufferedReader reader = new BufferedReader(new FileReader(heatmapFile))) {
         String line;
         while ((line = reader.readLine()) != null) {
            if (line.length() > 3) {
               if (!line.startsWith("#") && !line.startsWith("repetitions")) {
                  String[] parts = line.split(" ");
                  if (parts.length > 3) {
                     throw new RuntimeException("Expecting pure heatmap data! These contain only 3 columns (VMs, iterations, value e.g. F1-Score)");
                  }
                  int VMs = Integer.parseInt(parts[0]);
                  int iterations = Integer.parseInt(parts[1]);
                  double value = Double.parseDouble(parts[2]);
                  heatmap.add(VMs, iterations, value);
               }
            }
         }
      }
      return heatmap;
   }
}
