package de.precision.analysis.heatmap;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Saves the mapping from VMs to a map from iterations to the concrete F1 values
 * @author DaGeRe
 *
 */
public class WorkloadHeatmap {
   private final SortedMap<Integer, SortedMap<Integer, Double>> oneHeatmap = new TreeMap<Integer, SortedMap<Integer,Double>>();
   
   public SortedMap<Integer, SortedMap<Integer, Double>> getOneHeatmap() {
      return oneHeatmap;
   }
   
   public void add(final int VMs, final int iterations, final double value) {
      SortedMap<Integer, Double> vmMap = oneHeatmap.get(VMs);
      if (vmMap == null) {
         vmMap = new TreeMap<Integer, Double>();
         oneHeatmap.put(VMs, vmMap);
      }
      vmMap.put(iterations, value);
   }
}