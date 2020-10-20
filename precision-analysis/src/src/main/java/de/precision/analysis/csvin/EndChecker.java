package de.precision.analysis.csvin;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import de.precision.analysis.csvin.FindRepeatableState.MeasurementEntry;

final class EndChecker extends SteadyStateChecker {

   public EndChecker(int finalIndex) {
      super(finalIndex);
   }

   @Override
   void check(int index, MeasurementEntry value, SummaryStatistics lastThree) {
      final double diffToEnd = Math.abs(lastThree.getMean() - steadyStateStatistics.getMean());
      // System.out.println(index + " " + diffToEnd / steadyStateStatistics.getMean());
      if (!(diffToEnd / steadyStateStatistics.getMean() < DIFF)) {
         finishIndex = index + 1;
         finishValue = steadyStateStatistics.getMean();
      }
   }
}