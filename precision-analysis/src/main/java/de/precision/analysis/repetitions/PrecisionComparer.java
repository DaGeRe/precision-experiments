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
import de.precision.processing.repetitions.sampling.SamplingConfig;

public class PrecisionComparer {

   private static final Logger LOG = LogManager.getLogger(PrecisionComparer.class);

   private final PrecisionConfig precisionConfig;
   private final SamplingConfig config;
   private final MethodResult overallResults;
   private final Map<String, MethodResult> testcaseResults = new HashMap<>();
   private final StatisticsConfig statisticsConfig;

   public PrecisionComparer(final SamplingConfig config, final StatisticsConfig statisticsConfig, PrecisionConfig precisionConfig) {
      this.config = config;
      this.statisticsConfig = statisticsConfig;
      this.precisionConfig = precisionConfig;
      overallResults = new MethodResult(precisionConfig.getTypes());

   }

   public void executeComparisons(final CompareData data, final Relation expectedRelation, final String testcaseName) {
      final Map<String, Relation> relations = new LinkedHashMap<>();

      boolean ttest = TestExecutors.getTTestRelation(relations, data, statisticsConfig);
      if (precisionConfig.isPrintPicks()) {
         LOG.debug(data.getAvgBefore() + " " + data.getAvgAfter() + " " +
               data.getBeforeStat().getVariance() + " " + data.getAfterStat().getVariance()
               + " " + (ttest ? 1 : 0)
               + " " + new TTest().homoscedasticT(data.getBefore(), data.getAfter())
               + " " + data.getBefore().length + " " + data.getAfter().length);
      }

      if (Arrays.asList(precisionConfig.getTypes()).contains(StatisticalTests.TTEST2)) {
         TestExecutors.getTTestRelationBimodal(relations, data, statisticsConfig);
      }

      if (config.isUseConfidenceInterval()) {
         TestExecutors.getConfidenceRelation(data, relations);
      }

      TestExecutors.getMeanRelation(relations, data);
      TestExecutors.getMannWhitneyRelation(relations, data, statisticsConfig);
      // TestExecutors.getGTestRelation(beforeShortened, afterShortened, relations, data);

      manageResults(expectedRelation, testcaseName, relations);
   }

   private void manageResults(final Relation expectedRelation, final String testcaseName, final Map<String, Relation> relations) {
      MethodResult myMethodResult = testcaseResults.get(testcaseName);
      if (myMethodResult == null) {
         myMethodResult = new MethodResult(precisionConfig.getTypes());
         testcaseResults.put(testcaseName, myMethodResult);
      }

      for (final Map.Entry<String, Relation> relationByMethod : relations.entrySet()) {
         // System.out.println("Relation: " + relation.getValue() + "
         // Expected: " + );
         final String testName = relationByMethod.getKey();
         final Relation testRelation = relationByMethod.getValue();
         calculateOverallResult(expectedRelation, myMethodResult, testName, testRelation);
      }
   }

   private void calculateOverallResult(final Relation expectedRelation, final MethodResult myMethodResult, final String testName, final Relation testRelation) {
      if (testRelation == Relation.LESS_THAN) {
         overallResults.increment(testName, MethodResult.SELECTED);
         myMethodResult.increment(testName, MethodResult.SELECTED);
         if (Relation.LESS_THAN == expectedRelation) {
            overallResults.increment(testName, MethodResult.TRUEPOSITIVE);
            myMethodResult.increment(testName, MethodResult.TRUEPOSITIVE);
         } else {
            // System.out.println("False positive!");
         }
      } else {
         if (Relation.LESS_THAN == expectedRelation) {
            overallResults.increment(testName, MethodResult.FALSENEGATIVE);
            myMethodResult.increment(testName, MethodResult.FALSENEGATIVE);
         }
      }
      if (testRelation == Relation.GREATER_THAN) {
         overallResults.increment(testName, MethodResult.WRONGGREATER);
         myMethodResult.increment(testName, MethodResult.WRONGGREATER);
      }
   }

   public MethodResult getOverallResults() {
      return overallResults;
   }

   public double getPrecision(final String statisticMethod) {
      final Map<String, Integer> methodResults = overallResults.getResults().get(statisticMethod);
      final int selected = methodResults.get(MethodResult.SELECTED);
      final int truepositive = methodResults.get(MethodResult.TRUEPOSITIVE);
      final double precision = 100d * ((selected > 0) ? ((double) truepositive) / selected : 0);
      return precision;
   }

   public double getRecall(final String statisticMethod) {
      final Map<String, Integer> methodResults = overallResults.getResults().get(statisticMethod);
      final int truepositive = methodResults.get(MethodResult.TRUEPOSITIVE);
      final int falsenegative = methodResults.get(MethodResult.FALSENEGATIVE);
      final double recall = 100d * (((double) truepositive) / (truepositive + falsenegative));
      return recall;
   }

   public double getFScore(final String statisticMethod) {
      double precision = getPrecision(statisticMethod);
      double recall = getRecall(statisticMethod);
      final double precisionRecall = precision + recall;
      double fscore = precisionRecall > 0 ? 2 * (precision * recall) / precisionRecall : 0;
      return fscore;
   }

   public Map<String, MethodResult> getTestcaseResults() {
      return testcaseResults;
   }

}
