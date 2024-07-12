package de.precision.analysis.graalvm.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Pair(
      @JsonProperty("old_sample") Sample oldSample,
      @JsonProperty("new_sample") Sample newSample,
      @JsonProperty("compare_results") CompareResults compareResults,
      Prediction prediction
  ) {

   public String getName() {
      return oldSample.commit() + "-" + newSample.commit();
   }

   public String getVersionIdNew() {
      return newSample.commit();
   }
   
   public Pair newPairWithPrediction(Prediction prediction) {
      if (this.prediction != null) {
         throw new RuntimeException("Don't overwrite prediction twice!");
      }
      return new Pair(oldSample, newSample, compareResults, prediction);
   }
}