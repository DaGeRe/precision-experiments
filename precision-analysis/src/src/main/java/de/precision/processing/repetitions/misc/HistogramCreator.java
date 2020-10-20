package de.precision.processing.repetitions.misc;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Fulldata.Value;

public class HistogramCreator {
   
   private static final int SIZE = 10;
   
   public static long[][] createCommonHistogram(final List<Result> beforeShortened, final List<Result> afterShortened) {
      final DescriptiveStatistics stat = new DescriptiveStatistics();
      final DescriptiveStatistics before = new DescriptiveStatistics();
      final DescriptiveStatistics after = new DescriptiveStatistics();

      fillStatistics(beforeShortened, stat, before);
      fillStatistics(afterShortened, stat, after);

      final long min = (long) stat.getMin();
      final long max = (long) stat.getPercentile(95);
      final long stepSize = 1 + ((max - min) / (SIZE - 1));

      final long histogramValues[][] = new long[2][SIZE];

      insertValues(before, min, stepSize, histogramValues[0]);
      insertValues(after, min, stepSize, histogramValues[1]);

      return histogramValues;
   }

   private static void fillStatistics(final List<Result> beforeShortened, final DescriptiveStatistics stat, final DescriptiveStatistics before) {
      for (final Result result : beforeShortened) {
         for (final Value value : result.getFulldata().getValue()) {
            final long val = value.getValue();
            stat.addValue(val);
            before.addValue(val);
         }
      }
   }
   
   private static void insertValues(final DescriptiveStatistics before, final long min, final long stepSize, final long[] histogramValues) {
      long currentMax = min + stepSize;
      int count = 1, index = 0;
      double[] sortedValues = before.getSortedValues();
      for (final double val : sortedValues) {
         if (val < currentMax) {
            count++;
         } else {
            histogramValues[index] = count;
            index++;
            count = 0;
            currentMax = min + stepSize * (index + 1);
            if (index >= histogramValues.length) {
               break;
            }
         }
      }
      for (int i = 0; i < histogramValues.length; i++) {
         if (histogramValues[i] == 0) {
            histogramValues[i] = 1;
         }
      }
      // System.out.println(Arrays.toString(histogramValues));
   }
}
