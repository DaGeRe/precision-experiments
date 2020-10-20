package de.precision.analysis.csvin;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import de.precision.analysis.csvin.FindRepeatableState.MeasurementEntry;

abstract class SteadyStateChecker {

   static final double DIFF = 0.01;
   
   protected boolean success;
   protected SummaryStatistics steadyStateStatistics;
   protected int finishIndex;
   protected double finishValue = Double.MAX_VALUE;
   private final int finalIndex;
   
   private SummaryStatistics statisticSelectedIndex = new SummaryStatistics();
   private SummaryStatistics statisticSelectedValue = new SummaryStatistics();

   public SteadyStateChecker(int finalIndex) {
      this.finalIndex = finalIndex;
   }

   public void setSteadyStateStatistics(SummaryStatistics steadyStateStatistics) {
      this.steadyStateStatistics = steadyStateStatistics;
   }
   
   abstract void check(int index, MeasurementEntry value, SummaryStatistics lastThree);
   
   void finish() {
      statisticSelectedIndex.addValue(finishIndex);
      if (finishValue != Double.MAX_VALUE) {
         statisticSelectedValue.addValue(finishValue);
      }
      
      finishIndex = finalIndex;
      success = false;
   }
   
   public static SteadyStateChecker getEndChecker() {
      return new EndChecker(2000);
   }
   
   public static SteadyStateChecker getCoVChecker() {
      SteadyStateChecker checkerCoV = new CoVChecker(2000);
      return checkerCoV;
   }
   
   public static SteadyStateChecker getDeviationChecker() {
      SteadyStateChecker checkerDeviation = new DeviationToEndChecker(2000);
      return checkerDeviation;
   }

   public SummaryStatistics getIndexStatistics() {
      return statisticSelectedIndex;
   }

   public SummaryStatistics getValueStatistics() {
      return statisticSelectedValue;
   }
}
