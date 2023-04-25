package de.precision.analysis.repetitions;

public enum StatisticalTestList {
   ALL(new StatisticalTests[] { StatisticalTests.MEAN, StatisticalTests.TTEST, StatisticalTests.TTEST2, StatisticalTests.CONFIDENCE, StatisticalTests.MANNWHITNEY }), 
      ALL_NO_BIMODAL(new StatisticalTests[] { StatisticalTests.MEAN, StatisticalTests.TTEST, StatisticalTests.CONFIDENCE, StatisticalTests.MANNWHITNEY }),
      ALL_NO_BIMODAL_NO_CONFIDENCE(new StatisticalTests[] { StatisticalTests.MEAN, StatisticalTests.TTEST, StatisticalTests.MANNWHITNEY });

   private StatisticalTests[] tests;

   StatisticalTestList(StatisticalTests[] tests) {
      this.tests = tests;
   }

   public StatisticalTests[] getTests() {
      return tests;
   }
}
