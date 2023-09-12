package de.precision.analysis.graalvm;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class TestMetadataFileReader {

   
   
   @Test
   public void testBasicReading() throws ParseException {
      File folder = new File("src/test/resources/graalvm-example-data");
      Map<File, Date> fileDates = new MetadataFileReader(folder).getFileDates();

      // The files here are the pure data created by the phoenix extraction - they don't need to exist for test execution
      Date exampleDate1 = fileDates.get(new File("src/test/resources/graalvm-example-data/5/34/5/101/27/24/34197/71036/"));
      Date date1 = MetadataFileReader.METADATA_TIME_FORMAT.parse("2021-12-29T17:19:37+00:00");
      Assert.assertEquals(date1, exampleDate1);
      
      Date exampleDate2 = fileDates.get(new File("src/test/resources/graalvm-example-data/6/34/7/136/16/18/34991/71789"));
      Date date2 = MetadataFileReader.METADATA_TIME_FORMAT.parse("2022-01-19T04:20:54+00:00");
      Assert.assertEquals(exampleDate2, date2);
   }
}
