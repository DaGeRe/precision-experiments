package de.precision.analysis.graalvm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.peass.measurement.statistics.Relation;

public class MetadiffReader {
   
   private static final Logger LOG = LogManager.getLogger(MetadiffReader.class);

   private final File metadiffFolder;
   private final MetadataFileReader metadataReader;

   public MetadiffReader(File metadiffFolder) {
      this.metadiffFolder = metadiffFolder;
      if (!metadiffFolder.isDirectory()) {
         throw new RuntimeException("A directory containing the *_metadiff.csv and *_metadata.csv files is expected!");
      }

      metadataReader = new MetadataFileReader(metadiffFolder);
   }

   public Map<String, Comparison> getComparisons() {
      Map<String, Comparison> comparisons = new LinkedHashMap<>();

      for (File metadiffFile : metadiffFolder.listFiles((FilenameFilter) new WildcardFileFilter("*_metadiff.csv"))) {
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

               String comparisonId = runOld + "_" + runNew;

               File folderOld = metadataReader.getFileById(runOld);
               File folderNew = metadataReader.getFileById(runNew);
               Date dateOld = metadataReader.getFileDates().get(folderOld);
               Date dateNew = metadataReader.getFileDates().get(folderNew);

               if (folderOld != null && folderNew != null && 
                     folderOld.exists() && folderNew.exists()) {
                  Comparison comparison = new Comparison(folderOld, folderNew, dateOld, dateNew);
                  comparisons.put(comparisonId, comparison);

                  if (pValue < 0.01) {
                     if (effectSize > 0) {
                        comparison.setRelation(Relation.LESS_THAN);
                     } else {
                        comparison.setRelation(Relation.GREATER_THAN);
                     }
                  } else {
                     comparison.setRelation(Relation.EQUAL);
                  }
               } else {
                  
                  if (comparisonId.startsWith("6-") && folderOld != null && folderNew != null) {
                     LOG.trace("Did not find " + comparisonId);
                     LOG.trace(folderOld.getAbsolutePath() + " " + folderOld.exists());
                     LOG.trace(folderNew.getAbsolutePath() + " " + folderNew.exists());
                  }
               }

            }

         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      return comparisons;
   }

   public void setRelations(ComparisonFinder finder) {
      for (File metadiffFile : metadiffFolder.listFiles((FilenameFilter) new WildcardFileFilter("*_metadiff.csv"))) {
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

               LOG.info("Checking {} Null: {}", comparisonId, current != null);

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

      cleanup(finder.getComparisonsTraining());
      cleanup(finder.getComparisonsTest());
   }

   private void cleanup(Map<String, Comparison> map) {
      Set<String> toDelete = new HashSet<>();
      for (Map.Entry<String, Comparison> check : map.entrySet()) {
         if (check.getValue().getRelation() == null) {
            toDelete.add(check.getKey());
         }
      }
      for (String deletable : toDelete) {
         map.remove(deletable);
      }
   }
}
