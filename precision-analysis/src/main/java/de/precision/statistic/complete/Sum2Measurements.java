package de.precision.statistic.complete;

import java.util.Random;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Sum2Measurements {
   
   public static void main(String[] args) {
      getExpectedMeanDiffValue(100000, 30, 101, 100);
      System.out.println();
      getExpectedMeanDiffValueDistribution(100000, 30, 101, 100);
   }
   
   private static void getExpectedMeanDiffValue(int tries, int numberOfMeasurements, final int mean1, final int mean2) {
      Random r = new Random();
      DescriptiveStatistics meanDifferenceStatistic = new DescriptiveStatistics();
      for (int j = 0; j < tries; j++) {
         double val1 = r.nextGaussian() * 2 + mean1;
         double val2 = r.nextGaussian() * 2 + mean2;
         meanDifferenceStatistic.addValue(val1 - val2);
      }
      System.out.println("Means: " + meanDifferenceStatistic.getMean() + " " + meanDifferenceStatistic.getStandardDeviation());
      System.out.println("Expected: " + 1 + " " + 2*Math.sqrt(2));
//      System.out.println("Diffs: " + differenceStatistic.getMean() + " " + differenceStatistic.getStandardDeviation());
   }
   
   private static void getExpectedMeanDiffValueDistribution(int tries, int numberOfMeasurements, final int mean1, final int mean2) {
      Random r = new Random();
      DescriptiveStatistics meanDifferenceStatistic = new DescriptiveStatistics();
      for (int j = 0; j < tries; j++) {
         double diffSample = r.nextGaussian() * (2*Math.sqrt(2)) + (mean1 - mean2);
         meanDifferenceStatistic.addValue(diffSample);
      }
      System.out.println("Means: " + meanDifferenceStatistic.getMean() + " " + meanDifferenceStatistic.getStandardDeviation());
      System.out.println("Expected: " + 1 + " " + 2*Math.sqrt(2));
//      System.out.println("Diffs: " + differenceStatistic.getMean() + " " + differenceStatistic.getStandardDeviation());
   }
}
