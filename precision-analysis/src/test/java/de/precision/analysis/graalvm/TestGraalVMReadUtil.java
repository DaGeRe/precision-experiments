package de.precision.analysis.graalvm;

import java.io.File;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import de.dagere.kopeme.kopemedata.Kopemedata;


public class TestGraalVMReadUtil {
   
   private static final File EXAMPLE_FOLDER = new File("src/test/resources/graalvm-example-data/6/34/14/291/16/18/29449/65590");
   
   @Test
   public void testCleanReading() {
      Kopemedata data = GraalVMReadUtil.readData(EXAMPLE_FOLDER, true);
      Assert.assertEquals(10, data.getFirstDatacollectorContent().size());
   }
   
   @Test
   public void testUncleanReading() {
      Kopemedata data = GraalVMReadUtil.readData(EXAMPLE_FOLDER, false);
      Assert.assertEquals(10, data.getFirstDatacollectorContent().size());
   }
}
