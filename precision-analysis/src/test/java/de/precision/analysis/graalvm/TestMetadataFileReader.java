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
      File folder = new File("src/test/resources");
      Map<File, Date> fileDates = new MetadataFileReader(folder).getFileDates();

      Date exampleDate1 = fileDates.get(new File("/home/reichelt/workspaces/dissworkspace/repos/graalvm/2022-01/measurement/30/39/74/7743930"));
      Date date1 = MetadataFileReader.METADATA_TIME_FORMAT.parse("2022-01-13T02:31:26+00:00");
      Assert.assertEquals(exampleDate1, date1);
      
      Date exampleDate2 = fileDates.get(new File("/home/reichelt/workspaces/dissworkspace/repos/graalvm/2022-01/measurement/30/33/71/7713330"));
      Date date2 = MetadataFileReader.METADATA_TIME_FORMAT.parse("2021-12-29T17:19:37+00:00");
      Assert.assertEquals(exampleDate2, date2);
   }
}
