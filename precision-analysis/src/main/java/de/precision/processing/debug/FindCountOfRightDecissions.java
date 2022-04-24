package de.precision.processing.debug;

import java.io.File;
import java.util.Arrays;
import java.util.List;



import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import org.apache.commons.math3.stat.inference.TestUtils;

import de.dagere.kopeme.datastorage.JSONDataLoader;
import de.dagere.kopeme.kopemedata.DatacollectorResult;
import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.VMResult;
import de.dagere.peass.measurement.dataloading.MultipleVMTestUtil;

public class FindCountOfRightDecissions {

   static interface StatisticMethod {
      boolean isDifferent(List<VMResult> list1, List<VMResult> list2);
   }

   static class TTest implements StatisticMethod {
      @Override
      public boolean isDifferent(final List<VMResult> list1, final List<VMResult> list2) {
         final SummaryStatistics statistics1 = MultipleVMTestUtil.getStatistic(list1);
         final SummaryStatistics statistics2 = MultipleVMTestUtil.getStatistic(list2);
         
         final boolean isDiff = TestUtils.tTest(statistics1, statistics2, 0.01);
         return isDiff;
      }
   }
   
   static class MyTest implements StatisticMethod {
      @Override
      public boolean isDifferent(final List<VMResult> list1, final List<VMResult> list2) {
         final SummaryStatistics statistics1 = MultipleVMTestUtil.getStatistic(list1);
         final SummaryStatistics statistics2 = MultipleVMTestUtil.getStatistic(list2);
         
         final DescriptiveStatistics st = new DescriptiveStatistics();
         final double[] values = new double[list1.size()];
         int i = 0;
         for (final VMResult r : list1) {
            values[i] = r.getValue();
            i++;
         }
         final double kurtosis = new Kurtosis().evaluate(values);
         final double skewness = new Skewness().evaluate(values);
         
         System.out.println(Arrays.toString(values));
         final int n = values.length;
         System.out.println(skewness + " " + kurtosis);
         final double factor = 3*Math.pow(n-1, 2) / ((n-2)*(n-3));
         final double bimodalityCoefficient = (Math.pow(skewness, 2)+1) / (kurtosis + factor);
         System.out.println("Coefficien: " + bimodalityCoefficient + " " + (bimodalityCoefficient > (5d/9)));
         
         final boolean isDiff = TestUtils.tTest(statistics1, statistics2, 0.01);
         return isDiff;
      }
   }

   public static void main(final String[] args)  {

      System.out.println("Files passed: " + args.length);

      for (int i = 0; i < args.length - 1; i++) {
         final File file1 = new File(args[i]);
         final File file2 = new File(args[i + 1]);

         final Kopemedata data = new JSONDataLoader(file1).getFullData();
         final Kopemedata data2 = new JSONDataLoader(file2).getFullData();

         final DatacollectorResult results = data.getMethods().get(0).getDatacollectorResults().get(0);
         final DatacollectorResult results2 = data2.getMethods().get(0).getDatacollectorResults().get(0);

         final int size = 30;
         
         final StatisticMethod method = new MyTest();

         int trueCount = 0;
         for (int firstIndex = 0; firstIndex < results.getResults().size() - size - 1; firstIndex++) {
            final List<VMResult> list1 = results.getResults().subList(firstIndex, firstIndex + size);
            final List<VMResult> list2 = results2.getResults().subList(firstIndex, firstIndex + size);
            final boolean isDiff = method.isDifferent(list1, list2);
            if (isDiff) {
               trueCount++;
            }
         }
//         System.out.println("True Count: " + trueCount);

         trueCount = 0;
         final int same1Count = getTrueCount(results, size, method);
//         System.out.println("True Count 2: " + same1Count);
         final int same2count = getTrueCount(results2, size, method);
//         System.out.println("True Count 3: " + same2count);
         System.out.println(trueCount+";"+same1Count+";"+same2count);
      }
   }

   private static int getTrueCount(final DatacollectorResult results, final int size, final StatisticMethod method) {
      int trueCount = 0;
      final int maxIndex = results.getResults().size() - size - 1;
      for (int firstIndex = 0; firstIndex < maxIndex; firstIndex++) {
         final List<VMResult> shortened = results.getResults().subList(firstIndex, firstIndex + size);
//         final SummaryStatistics statisticsFast = MultipleVMTestUtil.getStatistic(shortened);
         for (int secondIndex = firstIndex + 30; secondIndex < maxIndex; secondIndex++) {
            final List<VMResult> shortened2 = results.getResults().subList(secondIndex, secondIndex + size);
//            final SummaryStatistics statisticsSlow = MultipleVMTestUtil.getStatistic(shortened2);
//
//            final boolean isDiff = TestUtils.tTest(statisticsFast, statisticsSlow, 0.01);
            // System.out.println(tval + ";" + isDiff);
            final boolean isDiff = method.isDifferent(shortened, shortened2);
            if (isDiff) {
               trueCount++;
            }
         }
      }
      return trueCount;
   }
}
