package de.precision.analysis.graalvm.resultingData;

public class TrainingMetadata {
   final double pValue;
   final int runsOld, runsNew;
   
   public TrainingMetadata(double pValue, int runsOld, int runsNew) {
      this.pValue = pValue;
      this.runsOld = runsOld;
      this.runsNew = runsNew;
   }

   public double getpValue() {
      return pValue;
   }

   public int getRunsOld() {
      return runsOld;
   }

   public int getRunsNew() {
      return runsNew;
   }
}