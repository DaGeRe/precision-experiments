package de.precision.analysis.repetitions;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.inference.GTest;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Longs;

import de.dagere.kopeme.generated.Result;
import de.dagere.peass.config.StatisticsConfig;
import de.dagere.peass.measurement.statistics.ConfidenceIntervalInterpretion;
import de.dagere.peass.measurement.statistics.Relation;
import de.dagere.peass.measurement.statistics.bimodal.BimodalityTester;
import de.dagere.peass.measurement.statistics.bimodal.CompareData;
import de.precision.processing.repetitions.misc.HistogramCreator;

public class TestExecutors {
   private static final Logger LOG = LogManager.getLogger(TestExecutors.class);

   public static void getMeanRelation(final Map<String, Relation> relations, final CompareData data) {
      final double minChange = 0.997;
      if (data.getAvgBefore() < data.getAvgAfter() * minChange) {
         relations.put(GeneratePrecisionPlot.MEAN, Relation.LESS_THAN);
      } else {
         if (data.getAvgAfter() * 0.99 > data.getAvgBefore()) {
            relations.put(GeneratePrecisionPlot.MEAN, Relation.GREATER_THAN);
         } else {
            relations.put(GeneratePrecisionPlot.MEAN, Relation.EQUAL);
         }
      }
   }

   public static boolean getTTestRelation(final Map<String, Relation> relations, final CompareData data, final StatisticsConfig config) {
      final boolean tchange = new TTest().homoscedasticTTest(data.getBefore(), data.getAfter(), config.getType1error());
      // final boolean tchange = new TTest().homoscedasticTTest(values.get(0), values.get(1), 0.01);
      if (tchange) {
         relations.put(GeneratePrecisionPlot.TTEST, data.getAvgBefore() < data.getAvgAfter() ? Relation.LESS_THAN : Relation.GREATER_THAN);
      } else {
         relations.put(GeneratePrecisionPlot.TTEST, Relation.EQUAL);
      }
      return tchange;
   }

   public static boolean getTTestRelationBimodal(final Map<String, Relation> relations, final CompareData data, final StatisticsConfig statisticsConfig) {
      final BimodalityTester tester = new BimodalityTester(data);
      final boolean tchange = tester.isTChange(statisticsConfig.getType1error());
      if (tchange) {
         final Relation relation = tester.getRelation();
         relations.put(GeneratePrecisionPlot.TTEST2, relation);
      } else {
         relations.put(GeneratePrecisionPlot.TTEST2, Relation.EQUAL);
      }
      return tchange;
   }

   public static void getGTestRelation(final List<Result> beforeShortened, final List<Result> afterShortened, final Map<String, Relation> relations, final CompareData data) {
      final long[][] histogramValues = HistogramCreator.createCommonHistogram(beforeShortened, afterShortened);
      final double[] histExpected = Doubles.toArray(Longs.asList(histogramValues[0]));
      final boolean gchange = new GTest().gTest(histExpected, histogramValues[1], 0.01);
      if (gchange) {
         relations.put("GTEST", data.getAvgBefore() < data.getAvgAfter() ? Relation.LESS_THAN : Relation.GREATER_THAN);
      } else {
         relations.put("GTEST", Relation.EQUAL);
      }
   }

   public static void getMannWhitneyRelation(final Map<String, Relation> relations, final CompareData data, final StatisticsConfig config) {
      final double statistic = new MannWhitneyUTest().mannWhitneyUTest(data.getBefore(), data.getAfter());
      LOG.trace(statistic);
      final boolean mannchange = statistic < config.getType1error(); // 2.33 - critical value for confidence level 0.99
      if (mannchange) {
         relations.put(GeneratePrecisionPlot.MANNWHITNEY, data.getAvgBefore() < data.getAvgAfter() ? Relation.LESS_THAN : Relation.GREATER_THAN);
      } else {
         relations.put(GeneratePrecisionPlot.MANNWHITNEY, Relation.EQUAL);
      }
   }

   public static void getConfidenceRelation(final CompareData cd, final Map<String, Relation> relations) {
      final Relation confidence = ConfidenceIntervalInterpretion.compare(cd, 90);
      relations.put(GeneratePrecisionPlot.CONFIDENCE, confidence);
      LOG.trace("Confidence: " + confidence);
   }
}
