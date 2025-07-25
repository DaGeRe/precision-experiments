package de.precision.analysis.graalvm.resultingData;

public class ComparisonCounts {
   private int equal;
   private int unequal;

   public ComparisonCounts() {
   }

   public ComparisonCounts(int equal, int unequal) {
      this.equal = equal;
      this.unequal = unequal;
   }

   public int getEqual() {
      return equal;
   }

   public void setEqual(int equal) {
      this.equal = equal;
   }

   public int getUnequal() {
      return unequal;
   }

   public void setUnequal(int unequal) {
      this.unequal = unequal;
   }
}