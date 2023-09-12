package de.precision.analysis.graalvm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import de.dagere.peass.measurement.statistics.Relation;

public class MetadiffReader {

   private final File metadiffFolder;

   public MetadiffReader(File metadiffFolder) {
      this.metadiffFolder = metadiffFolder;
   }

   public void setRelations(ComparisonFinder finder) {
      for (File metadiffFile : metadiffFolder.listFiles((FilenameFilter) new WildcardFileFilter("*_metadiff.csv") )) {
         try (BufferedReader reader = new BufferedReader(new FileReader(metadiffFile))) {
            String headline = reader.readLine();
            
            int runOldIndex = GraalVMReadUtil.getColumnIndex(headline, "run_id_old");
            int runNewIndex = GraalVMReadUtil.getColumnIndex(headline, "run_id_new");
            int pValueIndex = GraalVMReadUtil.getColumnIndex(headline, "p_value");
            int effectSizeIndex = GraalVMReadUtil.getColumnIndex(headline, "size_effect");

            String line;
            while ((line = reader.readLine()) != null) {
               String[] parts = line.split(",");

               String runOld = parts[runOldIndex];
               String runNew = parts[runNewIndex];
               double pValue = Double.parseDouble(parts[pValueIndex]);
               double effectSize = Double.parseDouble(parts[effectSizeIndex]);

               Map<String, Comparison> comparisonsTraining = finder.getComparisonsTraining();
               String comparisonId = runOld + "_" + runNew;
               Comparison current = comparisonsTraining.get(comparisonId);
               if (current == null) {
                  current = finder.getComparisonsTest().get(comparisonId);
               }
               
               System.out.println("Checking " + comparisonId);
               System.out.println(current != null);
               
               if (current != null) {
                  if (pValue < 0.01) {
                     if (effectSize > 0) {
                        current.setRelation(Relation.LESS_THAN);
                     } else {
                        current.setRelation(Relation.GREATER_THAN);
                     }
                  } else {
                     current.setRelation(Relation.EQUAL);
                  }
               }
            }

         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
}
