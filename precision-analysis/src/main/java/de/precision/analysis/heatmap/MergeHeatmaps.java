package de.precision.analysis.heatmap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Since heatmaps are
 * 
 * @author DaGeRe
 *
 */
public class MergeHeatmaps {

   private static final Logger LOG = LogManager.getLogger(MergeHeatmaps.class);

   public static void main(final String[] args) throws FileNotFoundException, IOException {
      List<WorkloadHeatmap> heatmaps = new LinkedList<WorkloadHeatmap>();
      for (String arg : args) {
         File heatmapFile = new File(arg);
         LOG.debug("Reading: {}", heatmapFile.getAbsolutePath());
         heatmaps.add(readHeatmap(heatmapFile));
      }
      WorkloadHeatmap merged = mergeHeatmaps(heatmaps);
      writeHeatmap(merged);
   }

   private static void writeHeatmap(final WorkloadHeatmap merged) throws IOException {
      File dest = new File("result.csv");
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(dest))) {
         for (Map.Entry<Integer, Map<Integer, Double>> vmMap : merged.getOneHeatmap().entrySet()) {
            for (Map.Entry<Integer, Double> iterationEntry : vmMap.getValue().entrySet()) {
               writer.write(vmMap.getKey() + " " + iterationEntry.getKey() + " " + iterationEntry.getValue() + "\n");
            }
            writer.write("\n");
         }
      }

   }

   private static WorkloadHeatmap readHeatmap(final File heatmapFile) throws FileNotFoundException, IOException {
      final WorkloadHeatmap heatmap = new WorkloadHeatmap();
      try (BufferedReader reader = new BufferedReader(new FileReader(heatmapFile))) {
         String line;
         while ((line = reader.readLine()) != null) {
            if (line.length() > 3) {
               String[] parts = line.split(" ");
               int VMs = Integer.parseInt(parts[0]);
               int iterations = Integer.parseInt(parts[1]);
               double value = Double.parseDouble(parts[2]);
               heatmap.add(VMs, iterations, value);
            }
         }
      }
      return heatmap;
   }

   public static WorkloadHeatmap mergeHeatmaps(final List<WorkloadHeatmap> heatmaps) {
      WorkloadHeatmap merged = new WorkloadHeatmap();

      WorkloadHeatmap reference = heatmaps.get(0);

      for (Integer VMcount : reference.getOneHeatmap().keySet()) {
         LinkedHashMap<Integer, Double> iterationMap = new LinkedHashMap<Integer, Double>();
         merged.getOneHeatmap().put(VMcount, iterationMap);
         for (Integer iterationCount : reference.getOneHeatmap().get(VMcount).keySet()) {
            double value = 0.0;
            for (WorkloadHeatmap heatmap : heatmaps) {
               value += heatmap.getOneHeatmap().get(VMcount).get(iterationCount);
            }
            value /= heatmaps.size();
            iterationMap.put(iterationCount, value);
         }
      }

      return merged;
   }
}
