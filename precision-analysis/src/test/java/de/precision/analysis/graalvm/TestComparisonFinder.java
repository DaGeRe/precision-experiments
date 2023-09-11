package de.precision.analysis.graalvm;

import java.io.File;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

public class TestComparisonFinder {
   
   @Test
   public void testFinding() throws ParseException {
      File folder = new File("src/test/resources/graalvm-example-data");
      ComparisonFinder finder = new ComparisonFinder(folder, MetadataFileReader.METADATA_TIME_FORMAT.parse("2021-12-29T17:19:37+00:00"));
      
      System.out.println(finder.getComparisonsTraining().keySet());
      System.out.println(finder.getComparisonsTest().keySet());
   }
}
