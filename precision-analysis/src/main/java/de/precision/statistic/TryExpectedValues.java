package de.precision.statistic;

import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.inference.TTest;

public class TryExpectedValues {
   public static void main(String[] args) {
      final int tries = 100000;

      getExpectedTValue(tries, 2, 100, 101);
      getExpectedTValue(tries, 5, 100, 101);
      for (int numberOfMeasurements = 10; numberOfMeasurements <= 200; numberOfMeasurements += 10) {
         getExpectedTValue(tries, numberOfMeasurements, 100, 101);
      }
   } 

   private static void getExpectedTValue(int tries, int numberOfMeasurements, final int mean1, final int mean2) {
      Random r = new Random();
      DescriptiveStatistics stat = new DescriptiveStatistics();
      for (int j = 0; j < tries; j++) {
         double[] val1 = new double[numberOfMeasurements];
         double[] val2 = new double[numberOfMeasurements];
         for (int i = 0; i < numberOfMeasurements; i++) {
            val1[i] = r.nextGaussian() * 2 + mean1;
            val2[i] = r.nextGaussian() * 2 + mean2;
         }
         final double tValue = new TTest().t(val1, val2);
         stat.addValue(tValue);
         // System.out.println(tValue);
      }
      double realTMean = Math.abs((stat.getMean()));
      double expectedTMean = Math.sqrt(((double) numberOfMeasurements) / 8);
      System.out.println("Expected T-Value " + numberOfMeasurements + " " +
            realTMean + " " + expectedTMean);
      System.out.println("Expected T-Value " + numberOfMeasurements + " " +
            Math.abs((stat.getMean())) + " " +
            stat.getStandardDeviation() + " " +
            getGuessedType2Error(numberOfMeasurements, expectedTMean, stat.getStandardDeviation()));
      System.out.println();

      // System.out.println(expectedTValue);
   }

   public static double getGuessedType2Error(int numberOfMeasurements, double mean, double standarddeviation) {
      TDistribution t = new TDistribution(numberOfMeasurements * 2 - 2);
      final double criticalTValue = t.inverseCumulativeProbability(0.995);

      double distributionValue = (criticalTValue - mean) / standarddeviation;

      System.out.println("TCrit: " + criticalTValue + " P(Z<" + distributionValue + ")");
      NormalDistribution nd = new NormalDistribution();
      final double guessType2Error = nd.cumulativeProbability(distributionValue);
      return guessType2Error;
   }

}
