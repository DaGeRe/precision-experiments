package de.precision.analysis.csvin;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import de.precision.analysis.csvin.FindRepeatableState.MeasurementEntry;

final class CoVChecker extends SteadyStateChecker {
   
   public CoVChecker(int finalIndex) {
      super(finalIndex);
   }
   
   @Override
   void check(int index, MeasurementEntry value, SummaryStatistics lastThree) {
      if (!success) {
         if (lastThree.getStandardDeviation() < DIFF) {
//            System.out.println("CoV Success: " + index);
            success = true;
            finishIndex = index;
            finishValue = value.getMean();
         }
      }
   }
}