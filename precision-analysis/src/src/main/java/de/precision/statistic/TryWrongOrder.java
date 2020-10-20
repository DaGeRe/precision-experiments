package de.precision.statistic;

import java.util.Random;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class TryWrongOrder {

   public static void main(String[] args) {
       getWrongOrderOneMeasurement();
       getWrongOrderNMeasurements(30);
       getExpectedDeviation(30);

   }
   
   private static void getWrongOrderNMeasurements(int numberOfMeasurements) {
      Random r = new Random();
      int count = 0;
      final int tries = 100000;
      for (int i = 0; i < tries; i++) {
         double sum = 0.0;
         for (int j = 0; j < numberOfMeasurements; j++) {
            double mySample = r.nextGaussian() * (2 * Math.sqrt(2)) + 1;
            sum += mySample;
         }
         if (sum < 0) {
            count++;
         }
      }
      System.out.println(((double) count) / tries);
   }

   private static void getWrongOrderOneMeasurement() {
      Random r = new Random();
      int count = 0;
      final int tries = 100000;
      for (int i = 0; i < tries; i++) {
         double mySample = r.nextGaussian() * (2 * Math.sqrt(2)) + 1;
         if (mySample < 0) {
            count++;
         }
      }
      System.out.println(((double) count) / tries);
   }
   
   private static void getExpectedDeviation(int numberOfMeasurements) {
      Random r = new Random();
      final int tries = 100000;
      DescriptiveStatistics stat = new DescriptiveStatistics();
      for (int i = 0; i < tries; i++) {
         double mySample = r.nextGaussian() * 2 + 1;
         stat.addValue(mySample);// Falsch: Das muss die Summe sein
      }
      System.out.println(stat.getMean() + " " + stat.getStandardDeviation());
   }
}
