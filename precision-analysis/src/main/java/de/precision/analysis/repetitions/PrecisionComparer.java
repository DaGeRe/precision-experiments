package de.precision.analysis.repetitions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.math3.stat.inference.TTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.peass.config.StatisticsConfig;
import de.dagere.peass.measurement.statistics.Relation;
import de.dagere.peass.measurement.statistics.bimodal.CompareData;

public class PrecisionComparer {

   private static final Logger LOG = LogManager.getLogger(PrecisionComparer.class);

   private final PrecisionConfig precisionConfig;
   private final MethodResult overallResults;
   private final Map<String, MethodResult> testcaseResults = new HashMap<>();
   private final StatisticsConfig statisticsConfig;

   public PrecisionComparer(final StatisticsConfig statisticsConfig, PrecisionConfig precisionConfig) {
      this.statisticsConfig = statisticsConfig;
      this.precisionConfig = precisionConfig;
      overallResults = new MethodResult(precisionConfig.getTypes());

   }

   public void executeComparisons(final CompareData data, final Relation expectedRelation, final String testcaseName) {
      final Map<StatisticalTests, Relation> relations = new LinkedHashMap<>();

      boolean ttest = TestExecutors.getTTestRelation(relations, data, statisticsConfig);
      if (precisionConfig.isPrintPicks()) {
         LOG.debug(data.getAvgPredecessor() + " " + data.getAvgCurrent() + " " +
               data.getPredecessorStat().getVariance() + " " + data.getCurrentStat().getVariance()
               + " " + (ttest ? 1 : 0)
               + " " + new TTest().homoscedasticT(data.getPredecessor(), data.getCurrent())
               + " " + data.getPredecessor().length + " " + data.getCurrent().length);
      }

      if (Arrays.asList(precisionConfig.getTypes()).contains(StatisticalTests.TTEST2)) {
         TestExecutors.getTTestRelationBimodal(relations, data, statisticsConfig);
      }

      if (Arrays.asList(precisionConfig.getTypes()).contains(StatisticalTests.CONFIDENCE)) {
         TestExecutors.getConfidenceRelation(data, relations);
      }

      if (Arrays.asList(precisionConfig.getTypes()).contains(StatisticalTests.MEAN)) {
         TestExecutors.getMeanRelation(relations, data);
      }
      
      if (Arrays.asList(precisionConfig.getTypes()).contains(StatisticalTests.MANNWHITNEY)) {
         TestExecutors.getMannWhitneyRelation(relations, data, statisticsConfig);
      }
      
      // TestExecutors.getGTestRelation(beforeShortened, afterShortened, relations, data);

      double relativeDifference = (data.getAvgCurrent() - data.getAvgPredecessor() ) / data.getAvgPredecessor();
      manageResults(expectedRelation, testcaseName, relations, relativeDifference > 0.01, relativeDifference > 0.05);
   }

   private void manageResults(final Relation expectedRelation, final String testcaseName, final Map<StatisticalTests, Relation> relations, boolean isAbove1Percent, boolean isAbove5Percent) {
      MethodResult myMethodResult = testcaseResults.get(testcaseName);
      if (myMethodResult == null) {
         myMethodResult = new MethodResult(precisionConfig.getTypes());
         testcaseResults.put(testcaseName, myMethodResult);
      }

      for (final Map.Entry<StatisticalTests, Relation> relationByMethod : relations.entrySet()) {
         // System.out.println("Relation: " + relation.getValue() + "
         // Expected: " + );
         final StatisticalTests testName = relationByMethod.getKey();
         final Relation testRelation = relationByMethod.getValue();
         calculateOverallResult(expectedRelation, myMethodResult, testName, testRelation, isAbove1Percent, isAbove5Percent);
      }
   }

   private void calculateOverallResult(final Relation expectedRelation, final MethodResult myMethodResult, final StatisticalTests testName, final Relation testRelation, boolean isAbove1Percent, boolean isAbove5Percent) {
      if (Relation.isUnequal(testRelation)) {
         overallResults.increment(testName, StatisticalTestResult.SELECTED);
         myMethodResult.increment(testName, StatisticalTestResult.SELECTED);
         if (Relation.isUnequal(expectedRelation)) {
            overallResults.increment(testName, StatisticalTestResult.TRUEPOSITIVE);
            myMethodResult.increment(testName, StatisticalTestResult.TRUEPOSITIVE);
         } else {
            // System.out.println("False positive!");
         }
      } else {
         if (Relation.isUnequal(expectedRelation)) {
            overallResults.increment(testName, StatisticalTestResult.FALSENEGATIVE);
            myMethodResult.increment(testName, StatisticalTestResult.FALSENEGATIVE);
            if (isAbove1Percent) {
               overallResults.increment(testName, StatisticalTestResult.FALSENEGATIVE_ABOVE_1_PERCENT);
               myMethodResult.increment(testName, StatisticalTestResult.FALSENEGATIVE_ABOVE_1_PERCENT);
            }
            if (isAbove5Percent) {
               overallResults.increment(testName, StatisticalTestResult.FALSENEGATIVE_ABOVE_5_PERCENT);
               myMethodResult.increment(testName, StatisticalTestResult.FALSENEGATIVE_ABOVE_5_PERCENT);
            }
         } else {
            overallResults.increment(testName, StatisticalTestResult.TRUENEGATIVE);
            myMethodResult.increment(testName, StatisticalTestResult.TRUENEGATIVE);
         }
      }
      if (testRelation == Relation.GREATER_THAN) {
         if (expectedRelation != Relation.GREATER_THAN) {
            overallResults.increment(testName, StatisticalTestResult.WRONGGREATER);
            myMethodResult.increment(testName, StatisticalTestResult.WRONGGREATER);
         }
      }
   }

   public MethodResult getOverallResults() {
      return overallResults;
   }

   public double getPrecision(final StatisticalTests statisticMethod) {
      final Map<StatisticalTestResult, Integer> methodResults = overallResults.getResults().get(statisticMethod);
      final int selected = methodResults.get(StatisticalTestResult.SELECTED);
      final int truepositive = methodResults.get(StatisticalTestResult.TRUEPOSITIVE);
      final double precision = 100d * ((selected > 0) ? ((double) truepositive) / selected : 0);
      return precision;
   }

   public double getRecall(final StatisticalTests statisticMethod) {
      final Map<StatisticalTestResult, Integer> methodResults = overallResults.getResults().get(statisticMethod);
      final int truepositive = methodResults.get(StatisticalTestResult.TRUEPOSITIVE);
      final int falsenegative = methodResults.get(StatisticalTestResult.FALSENEGATIVE);
      final double recall = 100d * (((double) truepositive) / (truepositive + falsenegative));
      return recall;
   }

   public double getFalseNegativeRate(final StatisticalTests statisticMethod) {
      final Map<StatisticalTestResult, Integer> methodResults = overallResults.getResults().get(statisticMethod);
      final int truePositive = methodResults.get(StatisticalTestResult.TRUEPOSITIVE);
      final int falseNegative = methodResults.get(StatisticalTestResult.FALSENEGATIVE);
      final double falseNegativeRate = 100d * (((double) falseNegative) / (truePositive + falseNegative));
      return falseNegativeRate;
   }

   public double getTrueNegativeRate(final StatisticalTests statisticMethod) {
      final Map<StatisticalTestResult, Integer> methodResults = overallResults.getResults().get(statisticMethod);
      final int truenegative = methodResults.get(StatisticalTestResult.TRUENEGATIVE);
      final int falsenegative = methodResults.get(StatisticalTestResult.FALSENEGATIVE);
      final double trueNegativeRate = 100d * (((double) truenegative) / (truenegative + falsenegative));
      return trueNegativeRate;
   }

   public double getFScore(final StatisticalTests statisticMethod) {
      double precision = getPrecision(statisticMethod);
      double recall = getRecall(statisticMethod);
      final double precisionRecall = precision + recall;
      double fscore = precisionRecall > 0 ? 2 * (precision * recall) / precisionRecall : 0;
      return fscore;
   }

   public Map<String, MethodResult> getTestcaseResults() {
      return testcaseResults;
   }

   public StatisticsConfig getStatisticsConfig() {
      return statisticsConfig;
   }

   public double getFalseNegativeRateAbove1Percent(StatisticalTests statisticMethod) {
      final Map<StatisticalTestResult, Integer> methodResults = overallResults.getResults().get(statisticMethod);
      final int truePositive = methodResults.get(StatisticalTestResult.TRUEPOSITIVE);
      final int falsenegative = methodResults.get(StatisticalTestResult.FALSENEGATIVE_ABOVE_1_PERCENT);
      final double falseNegativeRate = 100d * (((double) falsenegative) / (truePositive + falsenegative));
      return falseNegativeRate;
   }
   
   public double getFalseNegativeRateAbove5Percent(StatisticalTests statisticMethod) {
      final Map<StatisticalTestResult, Integer> methodResults = overallResults.getResults().get(statisticMethod);
      final int truePositive = methodResults.get(StatisticalTestResult.TRUEPOSITIVE);
      final int falsenegative = methodResults.get(StatisticalTestResult.FALSENEGATIVE_ABOVE_5_PERCENT);
      final double falseNegativeRate = 100d * (((double) falsenegative) / (truePositive + falsenegative));
      return falseNegativeRate;
   }
}
