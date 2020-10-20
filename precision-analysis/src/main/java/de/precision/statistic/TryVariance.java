package de.precision.statistic;

import java.util.Random;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.inference.TTest;

public class TryVariance {
   public static void main(String[] args) {
      final int tries = 100000;
      
      getExpectedTValue(tries, 2, 100, 101);

//      Random r = new Random();
//      for (int numberOfMeasurements = 10; numberOfMeasurements <= 100; numberOfMeasurements += 10) {
//         DescriptiveStatistics stat = new DescriptiveStatistics();
//         for (int j = 0; j < tries; j++) {
//            double sample = r.nextGaussian() * (2 * Math.sqrt(2 * numberOfMeasurements)) + numberOfMeasurements;
//            stat.addValue(sample);
//         }
//         System.out.println("Expected Sum Diff " + numberOfMeasurements + " " +
//               Math.abs((stat.getMean())) + " " +
//               stat.getStandardDeviation());
//         getExpectedTValue(tries, numberOfMeasurements, 100, 101);
//      }
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
      System.out.println("Expected T-Value " + numberOfMeasurements + " " +
            Math.abs((stat.getMean())) + " " +
            stat.getStandardDeviation());
      System.out.println();
   }
}
