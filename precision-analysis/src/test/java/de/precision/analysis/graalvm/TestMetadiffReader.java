package de.precision.analysis.graalvm;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import de.dagere.peass.measurement.statistics.Relation;

public class TestMetadiffReader {

   private static final File DATA_FOLDER = new File("src/test/resources/graalvm-example-data");

   Comparison equalExample,unequalExample, notFoundExample;
   
   @TempDir
   File tempDir;

   @BeforeEach
   public void initComparisons() {
      File folder1 = new File(tempDir, "6/43/5/113/26/23/34966/71728");
      File folder2 = new File(tempDir, "6/43/5/113/26/23/35058/71848");
      folder1.mkdirs();
      folder2.mkdirs();
      equalExample = new Comparison("71728-71848", folder1, folder2, null, null, 43, 15, 15);
      File folder3 = new File(tempDir, "6/34/7/129/26/23/35058/71848");
      File folder4 = new File(tempDir, "6/43/7/130/26/23/35232/72041");
      folder3.mkdirs();
      folder4.mkdirs();
      unequalExample = new Comparison("71848-72041", folder3, folder4, null, null, 43, 15, 15);
      File folder5 = new File(tempDir, "1");
      File folder6 = new File(tempDir, "2");
      folder5.mkdirs();
      folder6.mkdirs();
      notFoundExample = new Comparison("1-2", folder5, folder6, null, null, 43, 15, 15);
   }
   
   @Test
   public void testDefaultReading() {
      File fileToRead = new File(DATA_FOLDER, "2022-01_metadiff.csv");

      MetadiffReader reader = new MetadiffReader(fileToRead.getParentFile());

      Map<String, Comparison> comparisons = new HashMap<>();
      comparisons.put("6-43-113-71728_6-43-113-71848", equalExample);
      comparisons.put("6-34-129-71848_6-34-129-72041", unequalExample);
      comparisons.put("3", notFoundExample);
      
      ComparisonFinder finder = Mockito.mock(ComparisonFinder.class);

      Mockito.when(finder.getComparisonsTraining()).thenReturn(comparisons);

      reader.setRelations(finder);

      Assert.assertEquals(Relation.EQUAL, equalExample.getRelation());
      Assert.assertEquals(Relation.GREATER_THAN, unequalExample.getRelation());
      Assert.assertNull(notFoundExample.getRelation());
   }

   
}
