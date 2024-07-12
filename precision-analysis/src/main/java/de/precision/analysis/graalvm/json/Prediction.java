package de.precision.analysis.graalvm.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Prediction {
   @JsonProperty("p_value")
   private double pValue;

   @JsonProperty("effect_size")
   private double effectSize;

   private boolean regression;

   @JsonProperty("used_runs_for_old_sample")
   private int usedRunsForOldSample;

   @JsonProperty("used_runs_for_new_sample")
   private int usedRunsForNewSample;

   @JsonProperty("used_iterations_for_old_sample")
   private int usedIterationsForOldSample;

   @JsonProperty("used_iterations_for_new_sample")
   private int usedIterationsForNewSample;

   public double getpValue() {
      return pValue;
   }

   public void setpValue(double pValue) {
      this.pValue = pValue;
   }

   public double getEffectSize() {
      return effectSize;
   }

   public void setEffectSize(double effectSize) {
      this.effectSize = effectSize;
   }

   public boolean isRegression() {
      return regression;
   }

   public void setRegression(boolean regression) {
      this.regression = regression;
   }

   public int getUsedRunsForOldSample() {
      return usedRunsForOldSample;
   }

   public void setUsedRunsForOldSample(int usedRunsForOldSample) {
      this.usedRunsForOldSample = usedRunsForOldSample;
   }

   public int getUsedRunsForNewSample() {
      return usedRunsForNewSample;
   }

   public void setUsedRunsForNewSample(int usedRunsForNewSample) {
      this.usedRunsForNewSample = usedRunsForNewSample;
   }

   public int getUsedIterationsForOldSample() {
      return usedIterationsForOldSample;
   }

   public void setUsedIterationsForOldSample(int usedIterationsForOldSample) {
      this.usedIterationsForOldSample = usedIterationsForOldSample;
   }

   public int getUsedIterationsForNewSample() {
      return usedIterationsForNewSample;
   }

   public void setUsedIterationsForNewSample(int usedIterationsForNewSample) {
      this.usedIterationsForNewSample = usedIterationsForNewSample;
   }

}