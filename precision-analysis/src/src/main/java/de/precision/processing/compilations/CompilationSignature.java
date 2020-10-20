package de.precision.processing.compilations;

import java.util.Map;
import java.util.TreeMap;

public class CompilationSignature {
   TreeMap<String, Integer> levels = new TreeMap<>();

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof CompilationSignature) {
         CompilationSignature other = (CompilationSignature) obj;
         if (other.levels.size() == levels.size()) {
            for (Map.Entry<String, Integer> methodLevel : levels.entrySet()) {
               final Integer otherLevel = other.levels.get(methodLevel.getKey());
               if (otherLevel == null || !(otherLevel == methodLevel.getValue())) {
                  return false;
               }
            }
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
   
   public void addCompilationLevel(String method, int level) {
      levels.put(method, level);
   }
   
   public TreeMap<String, Integer> getLevels() {
      return levels;
   }

   @Override
   public int hashCode() {
      int hash = 0;
      for (Integer value : levels.values()) {
         hash = hash * 10 + value;
      }
      return hash;
   }
}