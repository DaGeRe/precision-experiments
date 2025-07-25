package de.precision.analysis.graalvm.resultingData;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TrainingMetadata {
   final double pValue;
   final int runsOld, runsNew;
   
   @JsonCreator
   public TrainingMetadata(@JsonProperty("pValue") double pValue, @JsonProperty("runsOld") int runsOld, @JsonProperty("runsNew") int runsNew) {
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