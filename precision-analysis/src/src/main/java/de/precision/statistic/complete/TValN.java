package de.precision.statistic.complete;

import java.util.Random;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class TValN {
   public static void main(String[] args) {
      // getExpectedTValueN(1000, 2, 101, 100);
      for (int n = 2; n < 100; n += 1) {
         getExpectedTValueN(100000, n, 101, 100);
      }
      // getExpectedTValueN(100000, 30, 101, 100);
   }

   private static void getExpectedTValueN(int tries, int n, final int mean1, final int mean2) {
      Random r = new Random();
      DescriptiveStatistics tStatistic = new DescriptiveStatistics();
      DescriptiveStatistics sStatistic = new DescriptiveStatistics();
      DescriptiveStatistics meanDiffStatistic = new DescriptiveStatistics();
      for (int j = 0; j < tries; j++) {
         double[] val1 = new double[n];
         double[] val2 = new double[n];
         for (int i = 0; i < n; i++) {
            val1[i] = r.nextGaussian() * 2 + mean1;
            val2[i] = r.nextGaussian() * 2 + mean2;
         }
         DescriptiveStatistics stat1 = new DescriptiveStatistics(val1);
         DescriptiveStatistics stat2 = new DescriptiveStatistics(val2);

         double factor = ((double) n) / (n - 1);
         double pooledVariance = (stat1.getVariance() + stat2.getVariance()) * factor / 2;
         double s = Math.sqrt(pooledVariance);
         double meanDiff = stat1.getMean() - stat2.getMean();
         double tValue2 = Math.sqrt(((double) n) / 2) * meanDiff / s;


         tStatistic.addValue(tValue2);
         sStatistic.addValue(pooledVariance);
         meanDiffStatistic.addValue(meanDiff);

         // System.out.println(tValue2);
      }

//      System.out.println(n + " " + String.format("%.03f", tStatistic.getMean()) + " " +
//            String.format("%.03f", meanDiffStatistic.getMean()) + " " +
//            String.format("%.03f", sStatistic.getMean()));
      
      System.out.println(n + " " + String.format("%.03f", tStatistic.getMean()) + " " +
            String.format("%.03f", tStatistic.getStandardDeviation()));
   }
}
