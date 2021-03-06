package de.precision.processing.debug;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import org.apache.commons.math3.stat.inference.TestUtils;

import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.peass.measurement.analysis.MultipleVMTestUtil;

public class FindCountOfRightDecissions {

   static interface StatisticMethod {
      boolean isDifferent(List<Result> list1, List<Result> list2);
   }

   static class TTest implements StatisticMethod {
      @Override
      public boolean isDifferent(final List<Result> list1, final List<Result> list2) {
         final SummaryStatistics statistics1 = MultipleVMTestUtil.getStatistic(list1);
         final SummaryStatistics statistics2 = MultipleVMTestUtil.getStatistic(list2);
         
         final boolean isDiff = TestUtils.tTest(statistics1, statistics2, 0.01);
         return isDiff;
      }
   }
   
   static class MyTest implements StatisticMethod {
      @Override
      public boolean isDifferent(final List<Result> list1, final List<Result> list2) {
         final SummaryStatistics statistics1 = MultipleVMTestUtil.getStatistic(list1);
         final SummaryStatistics statistics2 = MultipleVMTestUtil.getStatistic(list2);
         
         final DescriptiveStatistics st = new DescriptiveStatistics();
         final double[] values = new double[list1.size()];
         int i = 0;
         for (final Result r : list1) {
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

   public static void main(final String[] args) throws JAXBException {

      System.out.println("Files passed: " + args.length);

      for (int i = 0; i < args.length - 1; i++) {
         final File file1 = new File(args[i]);
         final File file2 = new File(args[i + 1]);

         final Kopemedata data = new XMLDataLoader(file1).getFullData();
         final Kopemedata data2 = new XMLDataLoader(file2).getFullData();

         final Datacollector results = data.getTestcases().getTestcase().get(0).getDatacollector().get(0);
         final Datacollector results2 = data2.getTestcases().getTestcase().get(0).getDatacollector().get(0);

         final int size = 30;
         
         final StatisticMethod method = new MyTest();

         int trueCount = 0;
         for (int firstIndex = 0; firstIndex < results.getResult().size() - size - 1; firstIndex++) {
            final List<Result> list1 = results.getResult().subList(firstIndex, firstIndex + size);
            final List<Result> list2 = results2.getResult().subList(firstIndex, firstIndex + size);
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

   private static int getTrueCount(final Datacollector results, final int size, final StatisticMethod method) {
      int trueCount = 0;
      final int maxIndex = results.getResult().size() - size - 1;
      for (int firstIndex = 0; firstIndex < maxIndex; firstIndex++) {
         final List<Result> shortened = results.getResult().subList(firstIndex, firstIndex + size);
//         final SummaryStatistics statisticsFast = MultipleVMTestUtil.getStatistic(shortened);
         for (int secondIndex = firstIndex + 30; secondIndex < maxIndex; secondIndex++) {
            final List<Result> shortened2 = results.getResult().subList(secondIndex, secondIndex + size);
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
