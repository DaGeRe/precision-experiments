package de.precision.analysis.heatmap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Since heatmaps are created for one test case, this provides the option to merge them
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
         WorkloadHeatmap heatmap = WorkloadHeatmapReader.readHeatmap(heatmapFile);
         heatmaps.add(heatmap);
      }
      WorkloadHeatmap merged = mergeHeatmaps(heatmaps);
      writeHeatmap(merged);
   }

   private static void writeHeatmap(final WorkloadHeatmap merged) throws IOException {
      File dest = new File("result.csv");
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(dest))) {
         for (Map.Entry<Integer, SortedMap<Integer, Double>> vmMap : merged.getOneHeatmap().entrySet()) {
            for (Map.Entry<Integer, Double> iterationEntry : vmMap.getValue().entrySet()) {
               writer.write(vmMap.getKey() + " " + iterationEntry.getKey() + " " + iterationEntry.getValue() + "\n");
            }
            writer.write("\n");
         }
      }

   }

   

   public static WorkloadHeatmap mergeHeatmaps(final List<WorkloadHeatmap> heatmaps) {
      WorkloadHeatmap merged = new WorkloadHeatmap();

      WorkloadHeatmap reference = heatmaps.get(0);

      for (Integer VMcount : reference.getOneHeatmap().keySet()) {
         SortedMap<Integer, Double> iterationMap = new TreeMap<Integer, Double>();
         merged.getOneHeatmap().put(VMcount, iterationMap);
         for (Integer iterationCount : reference.getOneHeatmap().get(VMcount).keySet()) {
            double value = 0.0;
            for (WorkloadHeatmap heatmap : heatmaps) {
               Map<Integer, Double> vmValue = findValue(VMcount, heatmap);
               if (vmValue != null) {
                  value += vmValue.get(iterationCount);
               }

            }
            value /= heatmaps.size();
            iterationMap.put(iterationCount, value);
         }
      }

      WorkloadHeatmap resolutionFilled = fillResolutions(merged);

      return resolutionFilled;
   }

   private static WorkloadHeatmap fillResolutions(final WorkloadHeatmap merged) {
      WorkloadHeatmap resolutionFilled = new WorkloadHeatmap();
      
      Iterator<Integer> iterator = merged.getOneHeatmap().keySet().iterator();
     
      int first = iterator.next();
      resolutionFilled.getOneHeatmap().put(first, merged.getOneHeatmap().get(first));
      int second = iterator.next();
      resolutionFilled.getOneHeatmap().put(second, merged.getOneHeatmap().get(second));
      int difference = second - first;

      int last = second;
      while (iterator.hasNext()) {
         int key = iterator.next();
         
         if (key - last > difference) {
            SortedMap<Integer, Double> entry = merged.getOneHeatmap().get(key);
            for (int i = last; i < key; i+= difference) {
               resolutionFilled.getOneHeatmap().put(i, entry);
            }
         }
         resolutionFilled.getOneHeatmap().put(key, merged.getOneHeatmap().get(key));
         
         last = key;
      }
      return resolutionFilled;
   }

   private static Map<Integer, Double> findValue(final Integer VMcount, final WorkloadHeatmap heatmap) {
      Map<Integer, Double> vmValue = heatmap.getOneHeatmap().get(VMcount);
      int count = VMcount - 1;
      while (vmValue == null && count > 0) {
         vmValue = heatmap.getOneHeatmap().get(count);
         count--;
      }
      return vmValue;
   }
}
