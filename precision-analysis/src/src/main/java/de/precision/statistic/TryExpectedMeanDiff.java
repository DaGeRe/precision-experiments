package de.precision.statistic;

import java.util.Random;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class TryExpectedMeanDiff {
   public static void main(String[] args) {
      final int tries = 100000;
 
      getExpectedMeanDiffValue(tries, 3, 100, 101);
      getExpectedMeanDiffValue(tries, 5, 100, 101);
      for (int numberOfMeasurements = 10; numberOfMeasurements <= 50; numberOfMeasurements += 5) {
         getExpectedMeanDiffValue(tries, numberOfMeasurements, 100, 101);
      }
   }

   private static void getExpectedMeanDiffValue(int tries, int numberOfMeasurements, final int mean1, final int mean2) {
      Random r = new Random();
      DescriptiveStatistics stat = new DescriptiveStatistics();
      for (int j = 0; j < tries; j++) {
         double[] val1 = new double[numberOfMeasurements];
         double[] val2 = new double[numberOfMeasurements];
         for (int i = 0; i < numberOfMeasurements; i++) {
            val1[i] = r.nextGaussian() * 2 + mean1;
            val2[i] = r.nextGaussian() * 2 + mean2;
         }
         final double meanDiff = new DescriptiveStatistics(val1).getMean() - new DescriptiveStatistics(val2).getMean();
         stat.addValue(meanDiff);
         // System.out.println(tValue);
      }
      System.out.println(stat.getMean() + " " + stat.getStandardDeviation());
   }
}
