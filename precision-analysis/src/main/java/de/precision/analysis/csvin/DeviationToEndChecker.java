package de.precision.analysis.csvin;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import de.precision.analysis.csvin.FindRepeatableState.MeasurementEntry;

final class DeviationToEndChecker extends SteadyStateChecker {
   
   public DeviationToEndChecker(int finalIndex) {
      super(finalIndex);
   }

   @Override
   void check(int index, MeasurementEntry value, SummaryStatistics lastThree) {
      if (!success) {
         final double relativeDeviation = Math.abs(lastThree.getMean() - steadyStateStatistics.getMean()) / steadyStateStatistics.getMean();
         if (relativeDeviation < DIFF) {
            success = true;
            finishIndex = index;
            finishValue = value.getMean();
         }
      }
   }
   
   @Override
   String getGermanName() {
      // TODO Auto-generated method stub
      return "Abweichungsabbruch";
   }
}