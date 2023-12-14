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
            comparisonsTraining.put(comparison.getKey(), comparison.getValue());
          }
      }
      
      this.startDate = trainingStartDate;
      
      if (!comparisonsTraining.isEmpty()) {
         LOG.info("Found training comparison, and therefore executing the comparisons");
         comparisonFound = true;
      } else {
         comparisonFound = false;
      }
//      Iterator<Comparison> iterator = comparisonsTraining.values().iterator();
//      if (iterator.hasNext()) {
//         Comparison comparison = iterator.next();
//         this.startDate = comparison.getDateOld();
//         if (comparison.getDateOld().before(startDate)) {
//            this.startDate = startDate;
//         } else {
//            
//         }
//         LOG.info("Setting to... " + this.startDate + " " + comparison.getName());
//         comparisonFound = true;
//      } else {
//         comparisonFound = false;
//         LOG.error("No training comparison found");
//         this.startDate = startDate;
//      }
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
