package de.precision.analysis.repetitions;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import de.dagere.peass.config.StatisticsConfig;
import de.dagere.peass.measurement.statistics.Relation;
import de.dagere.peass.measurement.statistics.bimodal.CompareData;
import de.dagere.precision.analysis.repetitions.bimodal.BimodalTestUtil;

public class TryTests {
   
   @Test
   public void tryMannWhitneyLess() {
      final HashMap<StatisticalTests, Relation> relations = new HashMap<>();
      final CompareData data = new CompareData(BimodalTestUtil.buildValues(10, 10), BimodalTestUtil.buildValues(12, 12));
      TestExecutors.getMannWhitneyRelation(relations, data, new StatisticsConfig());
      
      Assert.assertEquals(Relation.LESS_THAN, relations.get(StatisticalTests.MANNWHITNEY));
   }
   
   @Test
   public void tryMannWhitneyEqual() {
      final HashMap<StatisticalTests, Relation> relations = new HashMap<>();
      final CompareData data = new CompareData(BimodalTestUtil.buildValues(10, 10), BimodalTestUtil.buildValues(10, 10));
      TestExecutors.getMannWhitneyRelation(relations, data, new StatisticsConfig());
      
      Assert.assertEquals(Relation.EQUAL, relations.get(StatisticalTests.MANNWHITNEY));
   }
}
