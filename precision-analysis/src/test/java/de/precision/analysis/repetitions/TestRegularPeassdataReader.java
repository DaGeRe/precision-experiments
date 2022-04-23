package de.precision.analysis.repetitions;

import java.io.File;

import jakarta.xml.bind.JAXBException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableWithSize;
import org.junit.jupiter.api.Test;

import de.dagere.kopeme.kopemedata.Kopemedata;

public class TestRegularPeassdataReader {

   @Test
   public void testReading() throws JAXBException {
      
      File exampleData = new File("src/test/resources/measurementsFull_example");
      
      RegularPeassdataReader regularPeassdataReader = new RegularPeassdataReader();
      File testcaseFolder = new File(exampleData, "measurements/de.test.CalleeTest");
      File versionFolder = new File(testcaseFolder, "a23e385264c31def8dcda86c3cf64faa698c62d8");
      regularPeassdataReader.read("a23e385264c31def8dcda86c3cf64faa698c62d8", versionFolder, testcaseFolder);
      
      Kopemedata testcasesV1 = regularPeassdataReader.getTestcasesV1().get("de.test.CalleeTest#onlyCallMethod1");
      MatcherAssert.assertThat(testcasesV1.getMethods().get(0).getDatacollectorResults().get(0).getResults(), IsIterableWithSize.iterableWithSize(3));
      
      Kopemedata testcasesV1_onlyCallmethod2 = regularPeassdataReader.getTestcasesV1().get("de.test.CalleeTest#onlyCallMethod2");
      MatcherAssert.assertThat(testcasesV1_onlyCallmethod2.getMethods().get(0).getDatacollectorResults().get(0).getResults(), IsIterableWithSize.iterableWithSize(3));
      
      Kopemedata testcasesV2 = regularPeassdataReader.getTestcasesV2().get("de.test.CalleeTest#onlyCallMethod2");
      MatcherAssert.assertThat(testcasesV2.getMethods().get(0).getDatacollectorResults().get(0).getResults(), IsIterableWithSize.iterableWithSize(3));
      
      Kopemedata testcasesV2_onlyCallmethod2 = regularPeassdataReader.getTestcasesV2().get("de.test.CalleeTest#onlyCallMethod2");
      MatcherAssert.assertThat(testcasesV2_onlyCallmethod2.getMethods().get(0).getDatacollectorResults().get(0).getResults(), IsIterableWithSize.iterableWithSize(3));
   }
}
