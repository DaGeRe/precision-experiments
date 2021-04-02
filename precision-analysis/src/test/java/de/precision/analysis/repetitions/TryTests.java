package de.precision.analysis.repetitions;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import de.peass.config.StatisticsConfiguration;
import de.peass.measurement.analysis.Relation;
import de.precision.analysis.repetitions.bimodal.BimodalTestUtil;
import de.precision.analysis.repetitions.bimodal.CompareData;

public class TryTests {
   
   @Test
   public void tryMannWhitneyLess() {
      final HashMap<String, Relation> relations = new HashMap<>();
      final CompareData data = new CompareData(BimodalTestUtil.buildValues(10, 10), BimodalTestUtil.buildValues(12, 12));
      TestExecutors.getMannWhitneyRelation(relations, data, new StatisticsConfiguration());
      
      Assert.assertEquals(Relation.LESS_THAN, relations.get("MANNWHITNEY"));
   }
   
   @Test
   public void tryMannWhitneyEqual() {
      final HashMap<String, Relation> relations = new HashMap<>();
      final CompareData data = new CompareData(BimodalTestUtil.buildValues(10, 10), BimodalTestUtil.buildValues(10, 10));
      TestExecutors.getMannWhitneyRelation(relations, data, new StatisticsConfiguration());
      
      Assert.assertEquals(Relation.EQUAL, relations.get("MANNWHITNEY"));
   }
}
