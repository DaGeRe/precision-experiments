package de.precision.analysis.graalvm;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ComparisonFinder {

   private static final Logger LOG = LogManager.getLogger(ComparisonFinder.class);

   private final Date startDate;

   private final Map<String, Comparison> comparisonsTraining = new TreeMap<>();
   private final Map<String, Comparison> comparisonsTest = new TreeMap<>();

   private boolean comparisonFound;

   public ComparisonFinder(Map<String, Comparison> comparisons, Date trainingStartDate, Date trainingEndDate, Date testStartDate, Date testEndDate, File folder) {

      for (Map.Entry<String, Comparison> comparison : comparisons.entrySet()) {
         LOG.debug("Reading: " + comparison.getKey());
         Date date = comparison.getValue().getDateNew();
         
         if (date.after(trainingStartDate) && date.before(trainingEndDate)) {
           comparisonsTraining.put(comparison.getKey(), comparison.getValue());
         }
         
         if (date.after(testStartDate) && date.before(testEndDate)) {
            comparisonsTest.put(comparison.getKey(), comparison.getValue());
          }
      }
      
      this.startDate = trainingStartDate;
      
      if (!comparisonsTraining.isEmpty()) {
         LOG.info("Found training comparison, and therefore executing the comparisons");
         comparisonFound = true;
      } else {
         comparisonFound = false;
      }
   }

   public Map<String, Comparison> getComparisonsTraining() {
      return comparisonsTraining;
   }

   public Map<String, Comparison> getComparisonsTest() {
      return comparisonsTest;
   }

   public boolean isComparisonFound() {
      return comparisonFound;
   }

   public Date getStartDate() {
      return startDate;
   }
}
