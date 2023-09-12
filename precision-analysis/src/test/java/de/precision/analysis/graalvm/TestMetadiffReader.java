package de.precision.analysis.graalvm;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.dagere.peass.measurement.statistics.Relation;

public class TestMetadiffReader {

   @Test
   public void testDefaultReading() {
      File fileToRead = new File("src/test/resources/graalvm-example-data/2022-01_metadiff.csv");
      MetadiffReader reader = new MetadiffReader(fileToRead);

      Comparison equalExample = new Comparison(new File("/dev/null/6/43/5/113/26/23/34966/71728"), new File("/dev/null/6/43/5/113/26/23/35058/71848"), null, null);
      Comparison unequalExample = new Comparison(new File("/dev/null/6/34/7/129/26/23/35058/71848"),new File("/dev/null/6/43/7/130/26/23/35232/72041"),  null, null);
      Comparison notFoundExample = new Comparison(new File("/dev/null/1"), new File("/dev/null/2"), null, null);

      ComparisonFinder finder = Mockito.mock(ComparisonFinder.class);

      Map<String, Comparison> comparisons = new HashMap<>();
      comparisons.put("6-43-113-71728_6-43-113-71848", equalExample);
      comparisons.put("6-34-129-71848_6-34-129-72041", unequalExample);
      comparisons.put("3", notFoundExample);

      Mockito.when(finder.getComparisonsTraining()).thenReturn(comparisons);

      reader.setRelations(finder);

      Assert.assertEquals(Relation.EQUAL, equalExample.getRelation());
      Assert.assertEquals(Relation.LESS_THAN, unequalExample.getRelation());
      Assert.assertNull(notFoundExample.getRelation());
   }
}
