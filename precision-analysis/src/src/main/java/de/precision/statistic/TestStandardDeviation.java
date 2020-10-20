package de.precision.statistic;

import java.util.Random;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class TestStandardDeviation {
   public static void main(String[] args) {
      Random r = new Random();

      for (int numberOfMeasurements = 2; numberOfMeasurements <= 100; numberOfMeasurements += 1) {
         trie(r, numberOfMeasurements);
      }
   }

   private static void trie(Random r, int numberOfMeasurements) {
      DescriptiveStatistics stat = new DescriptiveStatistics();
      for (int tries = 0; tries < 500000; tries++) {
         double sum = 0.0;
         for (int i = 0; i < numberOfMeasurements; i++) {
            double sample = (r.nextGaussian() * 2 * Math.sqrt(2)) + 1;
            // System.out.println(sample);
            sum += sample;
         }
         double averageMeanDiff = sum / numberOfMeasurements;
         stat.addValue(Math.sqrt(numberOfMeasurements/2) * averageMeanDiff / 2);

         // double averageMeanDiff = sum / numberOfMeasurements;
         // stat.addValue(Math.sqrt(numberOfMeasurements / 2) * averageMeanDiff / 2);
      }
      System.out.println(numberOfMeasurements + " Means: " + stat.getMean() + " " + stat.getStandardDeviation());
      System.out.println("Expected: " + 1 + " " + Math.sqrt(numberOfMeasurements/2) * Math.sqrt(2 * numberOfMeasurements) * 2 / (2 * numberOfMeasurements));
      System.out.println();
   }
}
