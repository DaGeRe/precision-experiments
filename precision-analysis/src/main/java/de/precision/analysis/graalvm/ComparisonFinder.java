package de.precision.analysis.graalvm;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ComparisonFinder {

   private static final Logger LOG = LogManager.getLogger(ComparisonFinder.class);

   private final Date startDate;
   private final Date endDate;

   private final Map<String, Comparison> comparisonsTraining = new TreeMap<>();
   private final Map<String, Comparison> comparisonsTest = new TreeMap<>();

   public ComparisonFinder(Map<String, Comparison> comparisons, Date startDate, Date endDate, File folder) {
//      this.startDate = startDate;
      this.endDate = endDate;

      for (Map.Entry<String, Comparison> comparison : comparisons.entrySet()) {
         System.out.println("Reading: " + comparison.getKey());
         Date date = comparison.getValue().getDateNew();
         if (date.before(endDate)) {
            comparisonsTraining.put(comparison.getKey(), comparison.getValue());
         } else {
            comparisonsTest.put(comparison.getKey(), comparison.getValue());
         }
      }
      if (startDate != null) {
         this.startDate = startDate;
      } else {
         Comparison comparison = comparisonsTraining.values().iterator().next();
         this.startDate = comparison.getDateOld();
         System.out.println("Setting to... " + this.startDate + " " + comparison.getName());
      }
   }

   public ComparisonFinder(File folder, Date endDate) {
      this(folder, new Date(Long.MIN_VALUE), endDate);
   }

   public ComparisonFinder(File folder, Date startDate, Date endDate) {
      this.startDate = startDate;
      this.endDate = endDate;

      detectCommitFolders(folder);
   }

   private void detectCommitFolders(File folder) {
      TreeMap<Integer, File> trainingFiles = new TreeMap<>();
      TreeMap<Integer, File> testFiles = new TreeMap<>();

      MetadataFileReader metadataFileReader = new MetadataFileReader(folder);
      Map<File, Date> fileDates = metadataFileReader.getFileDates();
      Map<File, String> fileIds = metadataFileReader.getFileIds();

      System.out.println(folder.getAbsolutePath());
      FilenameFilter onlyNumberFilter = (FilenameFilter) new RegexFileFilter("[0-9]+");
      for (File machineFile : folder.listFiles(onlyNumberFilter)) {
         for (File configurationIdFile : machineFile.listFiles(onlyNumberFilter)) {
            for (File suiteIdFile : configurationIdFile.listFiles(onlyNumberFilter)) {
               for (File benchmarkIdFile : suiteIdFile.listFiles(onlyNumberFilter)) {
                  for (File platformTypeFile : benchmarkIdFile.listFiles(onlyNumberFilter)) {
                     for (File repositoryFile : platformTypeFile.listFiles(onlyNumberFilter)) {
                        for (File platformInstallationIdFile : repositoryFile.listFiles(onlyNumberFilter)) {
                           for (File versionIdFile : platformInstallationIdFile.listFiles(onlyNumberFilter)) {
                              int commitName = Integer.parseInt(versionIdFile.getName());
                              Date date = fileDates.get(versionIdFile);
                              if (date == null) {
                                 LOG.warn("File {} has no date according to metadata", versionIdFile);
                                 trainingFiles.put(commitName, versionIdFile);
                              }

                              System.out.println("Date: " + date + " " + startDate);

                              if (date != null && date.after(startDate)) {
                                 if (date.before(endDate)) {
                                    trainingFiles.put(commitName, versionIdFile);
                                 } else {
                                    testFiles.put(commitName, versionIdFile);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      generateComparisons(trainingFiles, comparisonsTraining, fileIds);
      generateComparisons(testFiles, comparisonsTest, fileIds);
   }

   private void generateComparisons(TreeMap<Integer, File> files, Map<String, Comparison> comparisonMap, Map<File, String> fileIds) {
      File predecessor = null;
      // int i = 0;

      LOG.info("Files: " + files.size());

      for (File current : files.values()) {
         if (predecessor != null) {
            String runOld = fileIds.get(predecessor);
            String runNew = fileIds.get(current);
            Comparison comparison = new Comparison(predecessor, current, null, null);
            String comparisonId = runOld + "_" + runNew;
            if (comparisonMap.containsKey(comparisonId)) {
               throw new RuntimeException("Id was created twice: " + comparisonId);
            }
            comparisonMap.put(comparisonId, comparison);
            LOG.info("Comparison " + predecessor.getName() + " " + current.getName());
         }
         predecessor = current;
      }
      LOG.info("Comparisons: " + comparisonMap.size());
   }

   public Map<String, Comparison> getComparisonsTraining() {
      return comparisonsTraining;
   }

   public Map<String, Comparison> getComparisonsTest() {
      return comparisonsTest;
   }

   public Date getStartDate() {
      return startDate;
   }
}
