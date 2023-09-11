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

      Comparison equalExample = new Comparison(new File("/dev/null/129/26/23/35232/72041"), new File("/dev/null/129/26/23/35232/72372"), null, null);
      Comparison unequalExample = new Comparison(new File("/dev/null/110/28/25/34818/67559"), new File("/dev/null/110/28/25/34818/71561"), null, null);
      Comparison notFoundExample = new Comparison(new File("/dev/null/1"), new File("/dev/null/2"), null, null);

      ComparisonFinder finder = Mockito.mock(ComparisonFinder.class);

      Map<Integer, Comparison> comparisons = new HashMap<>();
      comparisons.put(2285829, equalExample);
      comparisons.put(1896529, unequalExample);
      comparisons.put(3, notFoundExample);

      Mockito.when(finder.getComparisonsTraining()).thenReturn(comparisons);

      reader.setRelations(finder);

      Assert.assertEquals(Relation.EQUAL, equalExample.getRelation());
      Assert.assertEquals(Relation.LESS_THAN, unequalExample.getRelation());
      Assert.assertNull(notFoundExample.getRelation());
   }
}
