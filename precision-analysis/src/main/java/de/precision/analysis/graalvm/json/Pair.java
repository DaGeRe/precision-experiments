package de.precision.analysis.graalvm.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pair {
   @JsonProperty("old_sample")
   private Sample oldSample;
   
   @JsonProperty("new_sample")
   private Sample newSample;
   
   @JsonProperty("compare_results")
   private CompareResults compareResults;
   
   private Prediction prediction;

   public Sample getOldSample() {
      return oldSample;
   }

   public void setOldSample(Sample oldSample) {
      this.oldSample = oldSample;
   }

   public Sample getNewSample() {
      return newSample;
   }

   public void setNewSample(Sample newSample) {
      this.newSample = newSample;
   }

   public CompareResults getCompareResults() {
      return compareResults;
   }

   public void setCompareResults(CompareResults compareResults) {
      this.compareResults = compareResults;
   }

   public Prediction getPrediction() {
      return prediction;
   }

   public void setPrediction(Prediction prediction) {
      this.prediction = prediction;
   }
   
   
}