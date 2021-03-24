package de.precision.analysis.heatmap;

import java.util.LinkedHashMap;
import java.util.Map;

public class WorkloadHeatmap {
   private final Map<Integer, Map<Integer, Double>> oneHeatmap = new LinkedHashMap<Integer, Map<Integer,Double>>();
   
   public Map<Integer, Map<Integer, Double>> getOneHeatmap() {
      return oneHeatmap;
   }
   
   public void add(final int VMs, final int iterations, final double value) {
      Map<Integer, Double> vmMap = oneHeatmap.get(VMs);
      if (vmMap == null) {
         vmMap = new LinkedHashMap<Integer, Double>();
         oneHeatmap.put(VMs, vmMap);
      }
      vmMap.put(iterations, value);
   }
}