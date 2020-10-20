package de.precision.statistic.complete;

import java.util.Random;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class TVal2 {
   public static void main(String[] args) {
      for (int i = 0; i < 5; i++) {
         getExpectedTValue2(1000000, 101, 100);
      }
   }
   
   private static void getExpectedTValue2(int tries, final int mean1, final int mean2) {
      Random r = new Random();
      DescriptiveStatistics tStatistic = new DescriptiveStatistics();
      DescriptiveStatistics sStatistic = new DescriptiveStatistics();
      DescriptiveStatistics meanDiffStatistic = new DescriptiveStatistics();
      for (int j = 0; j < tries; j++) {
         double[] val1 = new double[2];
         double[] val2 = new double[2];
         for (int i = 0; i < 2; i++) {
            val1[i] = r.nextGaussian() * 2 + mean1;
            val2[i] = r.nextGaussian() * 2 + mean2;
         }
         DescriptiveStatistics stat1 = new DescriptiveStatistics(val1);
         DescriptiveStatistics stat2 = new DescriptiveStatistics(val2);
         
         double pooledVariance = (stat1.getVariance() + stat2.getVariance()) / 2;
         double s = Math.sqrt(pooledVariance);
         
         double meanDiff = stat1.getMean() - stat2.getMean();
         double tValue2 = Math.sqrt(2 / 2) * meanDiff / s;
         
         tStatistic.addValue(tValue2);
         sStatistic.addValue(s);
         meanDiffStatistic.addValue(meanDiff);
      }
      System.out.println("T: " + tStatistic.getMean() + " " + tStatistic.getStandardDeviation());
      System.out.println("Mean: " + meanDiffStatistic.getMean() + " " + meanDiffStatistic.getStandardDeviation());
      System.out.println("S: " + sStatistic.getMean() + " " + sStatistic.getStandardDeviation());
      System.out.println();
//      System.out.println("Expected: " + 1 + " " + 2*Math.sqrt(2));
//      System.out.println("Diffs: " + differenceStatistic.getMean() + " " + differenceStatistic.getStandardDeviation());
   }
}
