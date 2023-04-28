package de.precision.analysis.graalvm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.dagere.peass.measurement.statistics.Relation;

public class PrecisionFileManager {
   public Map<Integer, BufferedWriter> files = new HashMap<>();

   public BufferedWriter getFile(Integer id, Relation relation) throws IOException {
      if (!files.containsKey(id)) {
         String fileName = (Relation.isUnequal(relation) ? "unequal_" : "equal_") + id + ".csv";
         File resultFile = new File("results/" + fileName);
         BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(resultFile));
         files.put(id, bufferedWriter);
         return bufferedWriter;
      } else {
         return files.get(id);
      }
   }
   
   public void cleanup() throws IOException {
      for (BufferedWriter writer : files.values()) {
         writer.close();
      }
   }
}
