package de.precision.analysis.IterationEvolution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class IterationAnalyzer {
   
   private final VMExecution[] results;
   private final File resultFile;
   private final long iterations;
   private final boolean removeOutliers;
   
   public IterationAnalyzer(VMExecution[] results, File resultFile, long iterations, boolean removeOutliers) {
      this.results = results;
      this.resultFile = resultFile;
      this.iterations = iterations;
      this.removeOutliers = removeOutliers;
   }

   public void analyze() {
      try (PrintWriter writer = new PrintWriter(resultFile)) {
         final int stepsize = (int) (iterations/100);
         for (int maxIndex = stepsize; maxIndex <= iterations; maxIndex += stepsize) {
            IterationStatisticsReader reader = new IterationStatisticsReader(maxIndex, results);
            if (removeOutliers) {
               reader.removeOutliers();
            }
            SummaryStatistics iterationStatistics = reader.getIterationStatistics();
            writer.println(maxIndex + " " + iterationStatistics.getMean() + " " + iterationStatistics.getStandardDeviation() / iterationStatistics.getMean());
         }
         writer.flush();
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
   }
   
   
}
