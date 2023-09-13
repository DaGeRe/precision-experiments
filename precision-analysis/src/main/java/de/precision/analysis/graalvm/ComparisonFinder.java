package de.precision.analysis.graalvm;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ComparisonFinder {

   private static final Logger LOG = LogManager.getLogger(ComparisonFinder.class);

   private final Date startDate;

   private final Map<String, Comparison> comparisonsTraining = new TreeMap<>();
   private final Map<String, Comparison> comparisonsTest = new TreeMap<>();

   public ComparisonFinder(Map<String, Comparison> comparisons, Date startDate, Date endDate, File folder) {

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
         LOG.info("Setting to... " + this.startDate + " " + comparison.getName());
      }
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
