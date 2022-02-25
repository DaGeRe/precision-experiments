package de.precision.analysis.repetitions;

public enum StatisticalTestList {
   ALL(new String[] { StatisticalTests.MEAN, StatisticalTests.TTEST, StatisticalTests.TTEST2, StatisticalTests.CONFIDENCE, StatisticalTests.MANNWHITNEY }), 
      ALL_NO_BIMODAL(new String[] { StatisticalTests.MEAN, StatisticalTests.TTEST, StatisticalTests.CONFIDENCE, StatisticalTests.MANNWHITNEY });

   private String[] tests;

   StatisticalTestList(String[] tests) {
      this.tests = tests;
   }

   public String[] getTests() {
      return tests;
   }
}
