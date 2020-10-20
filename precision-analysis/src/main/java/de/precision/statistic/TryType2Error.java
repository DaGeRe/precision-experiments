package de.precision.statistic;

import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.inference.TTest;

public class TryType2Error {
   public static void main(String[] args) {
      final int tries = 100000;
      
      final double type2error = getError(tries, 31189, 32007, 50);
      System.out.println(type2error);
      
      final double type2error2 = getError(tries, 31189, 32007, 100);
      System.out.println(type2error2);

//      for (int numberOfMeasurements = 10; numberOfMeasurements <= 100; numberOfMeasurements += 10) {
//         final double type2error = getError(tries, 100, 101, numberOfMeasurements);
//
//         final double type1error = getError(tries, 100, 100, numberOfMeasurements);
//
//         System.out.println(numberOfMeasurements + ": " + type2error + " " + type1error);
//
//         final double guessType2Error = getGuessedType2Error(numberOfMeasurements);
//         System.out.println("Beide Fehler: " + type2error + " " + guessType2Error);
//         System.out.println();
//         //
//         // System.out.println(numberOfMeasurements + " " + nd.cumulativeProbability(distributionValue) / type2error);
//      }
   }

   public static double getGuessedType2Error(int numberOfMeasurements) {
      TDistribution t = new TDistribution(numberOfMeasurements * 2 - 2);
      final double criticalTValue = t.inverseCumulativeProbability(0.995);
//         System.out.println(criticalTValue);
      double distributionValue = (criticalTValue - Math.sqrt(((double) numberOfMeasurements) / 8)) / (1.03);
      //
      System.out.println(distributionValue);
      NormalDistribution nd = new NormalDistribution();
      final double guessType2Error = nd.cumulativeProbability(distributionValue);
      return guessType2Error;
   }

   private static double getError(final int tries, final int mean1, final int mean2, final int numberOfMeasurements) {
      Random r = new Random();
      int countType2Errors = 0;
      
      final double degreesOfFreedom = (numberOfMeasurements * 2) - 2;
      // // pass a null rng to avoid unneeded overhead as we will not sample from this distribution
      final TDistribution distribution = new TDistribution(null, degreesOfFreedom);
      final double tCrit = distribution.inverseCumulativeProbability(0.995);
      
      final double standardDeviation = 800;
      
      DescriptiveStatistics maxWrong = new DescriptiveStatistics();
      for (int j = 0; j < tries; j++) {
         double[] val1 = new double[numberOfMeasurements];
         double[] val2 = new double[numberOfMeasurements];
         for (int i = 0; i < numberOfMeasurements; i++) {
            
            val1[i] = r.nextGaussian() * standardDeviation + mean1;
            val2[i] = r.nextGaussian() * standardDeviation + mean2;
         }
         final TTest tTest = new TTest();
         final double tValue = tTest.homoscedasticT(val1, val2);

         final boolean decision = (Math.abs(tValue) >= tCrit);
         // tTest.pairedTTest(val1, val2, 0.01);
         final boolean decision2 = new TTest().homoscedasticTTest(val1, val2, 0.01);
         if (decision != decision2) {
            // System.out.println(tValue);
            maxWrong.addValue(tValue);
            // final double degreesOfFreedom = (numberOfMeasurements*2) - 2;
            // // pass a null rng to avoid unneeded overhead as we will not sample from this distribution
            // final TDistribution distribution = new TDistribution(null, degreesOfFreedom);
            // System.out.println(distribution.inverseCumulativeProbability(0.995));
         }
         if (!decision) {
            countType2Errors++;
            // System.out.println(tValue);
         }
      }
      final double type2error = ((double) countType2Errors) / tries;
      return type2error;
   }
}
