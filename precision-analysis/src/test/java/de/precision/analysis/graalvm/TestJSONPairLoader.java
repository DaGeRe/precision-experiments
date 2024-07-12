package de.precision.analysis.graalvm;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.dagere.peass.measurement.statistics.Relation;
import de.precision.analysis.graalvm.json.GraalVMJSONData;
import de.precision.analysis.graalvm.loading.JSONPairLoader;

public class TestJSONPairLoader {
   
   @Test
   public void testExampleFileLoading() throws StreamReadException, DatabindException, IOException {
      File exampleFile = new File("src/test/resources/json-example/exampleJSONFile.json");
      GraalVMJSONData data = new ObjectMapper().readValue(exampleFile, GraalVMJSONData.class);
      
      JSONPairLoader loader = new JSONPairLoader();
      loader.loadDiffPair(data.pairs()[0]);
      
      Assert.assertEquals(3, loader.getDataNew().getFirstDatacollectorContent().size());
      Assert.assertEquals(3, loader.getDataOld().getFirstDatacollectorContent().size());
      Assert.assertEquals(Relation.LESS_THAN, loader.getExpected());
   }
}
