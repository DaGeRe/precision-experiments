package de.precision.analysis.graalvm.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompareResults {
   
   private String column;
   
   @JsonProperty("p_value")
   private double pValue;
   
   @JsonProperty("effect_size")
   private double effectSize;
   
   private boolean regression;
   
   private String overview;
   
   public String getColumn() {
      return column;
   }

   public void setColumn(String column) {
      this.column = column;
   }

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

   public String getOverview() {
      return overview;
   }

   public void setOverview(String overview) {
      this.overview = overview;
   }
   
   
}