package de.precision.analysis.graalvm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import de.dagere.peass.measurement.statistics.Relation;

public class MetadiffReader {

   private final File fileToRead;

   public MetadiffReader(File fileToRead) {
      this.fileToRead = fileToRead;
   }

   public void setRelations(ComparisonFinder finder) {

      try (BufferedReader reader = new BufferedReader(new FileReader(fileToRead))) {
         String headline = reader.readLine();
         
         int idIndex = GraalVMReadUtil.getColumnIndex(headline, "id");
         int versionOldIndex = GraalVMReadUtil.getColumnIndex(headline, "version_old");
         int versionNewIndex = GraalVMReadUtil.getColumnIndex(headline, "version_new");
         int pValueIndex = GraalVMReadUtil.getColumnIndex(headline, "p_value");
         int effectSizeIndex = GraalVMReadUtil.getColumnIndex(headline, "size_effect");

         String line;
         while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");

            int id = Integer.parseInt(parts[idIndex]);
            int versionOld = Integer.parseInt(parts[versionOldIndex]);
            int versionNew = Integer.parseInt(parts[versionNewIndex]);
            double pValue = Double.parseDouble(parts[pValueIndex]);
            double effectSize = Double.parseDouble(parts[effectSizeIndex]);

            Map<Integer, Comparison> comparisonsTraining = finder.getComparisonsTraining();
            Comparison current = comparisonsTraining.get(id);

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
