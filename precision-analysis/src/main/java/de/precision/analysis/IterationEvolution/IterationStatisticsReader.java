package de.precision.analysis.IterationEvolution;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class IterationStatisticsReader {

   private SummaryStatistics overall = new SummaryStatistics();
   private final List<SummaryStatistics> iterationStatistics = new LinkedList<>();

   public IterationStatisticsReader(int maxIndex, VMExecution[] results) {
      for (int j = 0; j < results.length; j++) {
         SummaryStatistics statistic = new SummaryStatistics();
         for (int usedIndex = 0; usedIndex < maxIndex; usedIndex++) {
            statistic.addValue(results[j].getValues()[usedIndex]);
         }
         iterationStatistics.add(statistic);
         overall.addValue(statistic.getMean());
      }
   }

   public void removeOutliers() {
      for (Iterator<SummaryStatistics> iterator = iterationStatistics.iterator(); iterator.hasNext();) {
         SummaryStatistics current = iterator.next();
         double zscore = (current.getMean() - overall.getMean()) / overall.getStandardDeviation();
         if (zscore > 3) {
            iterator.remove();
         }
      }

      overall.clear();
      for (SummaryStatistics statistics : iterationStatistics) {
         overall.addValue(statistics.getMean());
      }
   }

   public SummaryStatistics getIterationStatistics() {
      return overall;
   }
}
