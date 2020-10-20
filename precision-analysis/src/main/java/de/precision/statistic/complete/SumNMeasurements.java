package de.precision.statistic.complete;

import java.util.Random;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class SumNMeasurements {

   public static void main(String[] args) {
      
      for (int numberOfMeasuements = 2; numberOfMeasuements <= 10; numberOfMeasuements += 1) {
         getExpectedMeanDiffValueDistribution(100000, numberOfMeasuements, 101, 100);
      }
      
//      getExpectedMeanDiffValueDistribution(100000, 2, 101, 100);
//      for (int numberOfMeasuements = 5; numberOfMeasuements < 50; numberOfMeasuements += 5) {
//         getExpectedMeanDiffValueDistribution(100000, numberOfMeasuements, 101, 100);
//      }

      // getExpectedMeanDiffValue(100000, 2, 101, 100);
      // for (int numberOfMeasuements = 5; numberOfMeasuements < 50; numberOfMeasuements+=5) {
      // getExpectedMeanDiffValue(100000, numberOfMeasuements, 101, 100);
      // }
   }

   private static void getExpectedMeanDiffValue(int tries, int numberOfMeasurements, final int mean1, final int mean2) {
      Random r = new Random();
      DescriptiveStatistics meanDifferenceStatistic = new DescriptiveStatistics();
      for (int j = 0; j < tries; j++) {
         double sum = 0;
         for (int i = 0; i < numberOfMeasurements; i++) {
            double val1 = r.nextGaussian() * 2 + mean1;
            double val2 = r.nextGaussian() * 2 + mean2;
            sum += (val1 - val2);
         }

         meanDifferenceStatistic.addValue(sum);
      }
      System.out.println("Means: " + meanDifferenceStatistic.getMean() + " " + meanDifferenceStatistic.getStandardDeviation());
      System.out.println("Expected: " + numberOfMeasurements + " " + (2 * Math.sqrt(2 * numberOfMeasurements)));
      System.out.println();
      // System.out.println("Diffs: " + differenceStatistic.getMean() + " " + differenceStatistic.getStandardDeviation());
   }

   private static void getExpectedMeanDiffValueDistribution(int tries, int numberOfMeasurements, final int mean1, final int mean2) {
      Random r = new Random();
      DescriptiveStatistics meanDifferenceStatistic = new DescriptiveStatistics();
      for (int j = 0; j < tries; j++) {
         double sum = 0;
         for (int i = 0; i < numberOfMeasurements; i++) {
            double val1 = r.nextGaussian() * (2 * Math.sqrt(2)) + (mean1 - mean2);
            sum += (val1);
         }

         meanDifferenceStatistic.addValue(sum);
      }
      System.out.println("Means: " + meanDifferenceStatistic.getMean() + " " + meanDifferenceStatistic.getStandardDeviation());
      System.out.println("Expected: " + numberOfMeasurements + " " + (2 * Math.sqrt(2 * numberOfMeasurements)));
      System.out.println();
      // System.out.println("Diffs: " + differenceStatistic.getMean() + " " + differenceStatistic.getStandardDeviation());
   }
}
