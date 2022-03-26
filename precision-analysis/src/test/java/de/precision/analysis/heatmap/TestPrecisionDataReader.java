package de.precision.analysis.heatmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class TestPrecisionDataReader {

   @Test
   public void readHeatmap() throws FileNotFoundException, IOException {
      File exampleFile = new File("src/test/resources/precision-example/precision.csv");

      PrecisionData meanHeatmap = PrecisionDataReader.readHeatmap(exampleFile, 8);

      WorkloadHeatmap heatmap1k = meanHeatmap.getPrecisionData().get(1000);
      
      // Attention: The heatmaps may contain certain values (especially the value when the resolution changes) twice; these should not differ by order of magnitute; the second one is read
      Assert.assertEquals(71.96, heatmap1k.getOneHeatmap().get(100).get(100), 0.01);
      
      Assert.assertEquals(75.91, heatmap1k.getOneHeatmap().get(150).get(100), 0.01);
      
      WorkloadHeatmap heatmap10k = meanHeatmap.getPrecisionData().get(10000);
      Assert.assertEquals(76.87, heatmap10k.getOneHeatmap().get(550).get(10), 0.01);
      
      
      PrecisionData ttestHeatmap = PrecisionDataReader.readHeatmap(exampleFile, 12);
      WorkloadHeatmap heatmapttest10k = ttestHeatmap.getPrecisionData().get(10000);
      Assert.assertEquals(13.63, heatmapttest10k.getOneHeatmap().get(150).get(20), 0.01);
      
   }
}
