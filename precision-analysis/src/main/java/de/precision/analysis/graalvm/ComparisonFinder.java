package de.precision.analysis.graalvm;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;
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

   private final Map<Integer, Comparison> comparisonsTraining = new TreeMap<>();
   private final Map<Integer, Comparison> comparisonsTest = new TreeMap<>();

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

      Map<File, Date> fileDates = new MetadataFileReader(folder).getFileDates();

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

      generateComparisons(trainingFiles, comparisonsTraining);
      generateComparisons(testFiles, comparisonsTest);
   }

   private void generateComparisons(TreeMap<Integer, File> files, Map<Integer, Comparison> comparisonMap) {
      File predecessor = null;
      int i = 0;
      
      LOG.info("Files: " + files.size());
      
      for (File current : files.values()) {
         if (predecessor != null) {
            int id = i++;
            Comparison comparison = new Comparison(predecessor, current, null, null);
            if (comparisonMap.containsKey(id)) {
               throw new RuntimeException("Id was created twice: " + id);
            }
            comparisonMap.put(id, comparison);
            LOG.info("Comparison " + predecessor.getName() + " " + current.getName());
         }
         predecessor = current;
      }
      LOG.info("Comparisons: " + comparisonMap.size());
   }

   public Map<Integer, Comparison> getComparisonsTraining() {
      return comparisonsTraining;
   }

   public Map<Integer, Comparison> getComparisonsTest() {
      return comparisonsTest;
   }

   public Date getStartDate() {
      return startDate;
   }
}
