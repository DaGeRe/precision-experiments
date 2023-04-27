package de.precision.analysis.graalvm.resultingData;

public class Counts {
   private int equal;
   private int unequal;

   public Counts() {
   }

   public Counts(int equal, int unequal) {
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