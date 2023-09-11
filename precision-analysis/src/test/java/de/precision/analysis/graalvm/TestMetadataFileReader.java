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
      Date exampleDate1 = fileDates.get(new File("/home/reichelt/nvme/workspaces/dissworkspace/phoenix/extracted/6/34/14/291/16/18/29449/65590"));
      Date date1 = MetadataFileReader.METADATA_TIME_FORMAT.parse("2021-09-29T00:48:42+00:00");
      Assert.assertEquals(date1, exampleDate1);
      
      Date exampleDate2 = fileDates.get(new File("/home/reichelt/nvme/workspaces/dissworkspace/phoenix/extracted/5/34/5/101/27/24/34197/71036"));
      Date date2 = MetadataFileReader.METADATA_TIME_FORMAT.parse("2021-12-29T17:19:37+00:00");
      Assert.assertEquals(exampleDate2, date2);
   }
}
