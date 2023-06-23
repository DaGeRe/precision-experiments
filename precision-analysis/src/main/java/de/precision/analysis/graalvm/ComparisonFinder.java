package de.precision.analysis.graalvm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class ComparisonFinder {

   private final Date startDate;

   private final Map<Integer, Comparison> comparisonsTraining = new TreeMap<>();
   private final Map<Integer, Comparison> comparisonsTest = new TreeMap<>();

   public ComparisonFinder(File folder, Date endDate) {
      this(folder, new Date(Long.MIN_VALUE), endDate);
   }

   public ComparisonFinder(File folder, Date startDate, Date endDate) {
      this.startDate = startDate;

      TreeMap<Integer, File> files = new TreeMap<>();
      
      System.out.println(folder.getAbsolutePath());
      FilenameFilter onlyNumberFilter = (FilenameFilter) new RegexFileFilter("[0-9]+");
      for (File machineFile : folder.listFiles(onlyNumberFilter)) {
         for (File XFile : machineFile.listFiles(onlyNumberFilter)) {
            for (File YFile : XFile.listFiles(onlyNumberFilter)) {
               for (File ZFile : YFile.listFiles(onlyNumberFilter)) {
                  for (File AFile : ZFile.listFiles(onlyNumberFilter)) {
                     for (File BFile : AFile.listFiles(onlyNumberFilter)) {
                        for (File commitFile : BFile.listFiles(onlyNumberFilter)) {
                           int commitName = Integer.parseInt(commitFile.getName());
                           files.put(commitName, commitFile);
                        }
                     }
                  }
               }
            }
         }
      }
      
      File predecessor = null;
      int i = 0;
      for (File current : files.values()) {
         if (predecessor != null) {
            comparisonsTraining.put(i++, new Comparison(predecessor, current, null, null));
            System.out.println("Comparison " + predecessor.getName() + " " + current.getName());
         }
         predecessor = current;
      }
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
