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

   public ComparisonFinder(Map<String, Comparison> comparisons, Date startDate, Date endDate, File folder) {

      for (Map.Entry<String, Comparison> comparison : comparisons.entrySet()) {
         LOG.debug("Reading: " + comparison.getKey());
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
         Iterator<Comparison> iterator = comparisonsTraining.values().iterator();
         if (iterator.hasNext()) {
            Comparison comparison = iterator.next();
            this.startDate = comparison.getDateOld();
            LOG.info("Setting to... " + this.startDate + " " + comparison.getName());
         } else {
            this.startDate = null;
            LOG.error("No training comparison found");
         }
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
