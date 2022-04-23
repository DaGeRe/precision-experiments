package de.precision.analysis.IterationEvolution;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Fulldata.Value;
import de.dagere.kopeme.kopemedata.MeasuredValue;
import de.dagere.kopeme.kopemedata.VMResult;

public class VMExecution {
   private final double[] values;
   private final double average;

   public VMExecution(VMResult r) {
      values = new double[r.getFulldata().getValues().size()];
      int i = 0;
      for (MeasuredValue value : r.getFulldata().getValues()) {
         values[i] = value.getValue();
         i++;
      }
      average = getMean(values);
   }

   public VMExecution(double[] values) {
      this.values = values;
      average = getMean(values);
   }

   private double getMean(double[] values) {
      SummaryStatistics stat = new SummaryStatistics();
      for (double d : values) {
         stat.addValue(d);
      }
      return stat.getMean();
   }

   public double[] getValues() {
      return values;
   }

   public double getAverage() {
      return average;
   }
}