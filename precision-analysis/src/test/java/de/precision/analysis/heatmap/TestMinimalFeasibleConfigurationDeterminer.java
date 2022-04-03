package de.precision.analysis.heatmap;

import org.junit.Assert;
import org.junit.jupiter.api.Test;


public class TestMinimalFeasibleConfigurationDeterminer {
   
   @Test
   public void testBasic() {
      PrecisionData data = new PrecisionData();
      data.addData(100, 10, 10, 95.0);
      data.addData(100, 10, 20, 95.0);
      data.addData(100, 10, 30, 95.0);
      data.addData(100, 20, 10, 95.0);
      data.addData(100, 20, 20, 95.0);
      data.addData(100, 20, 30, 95.0);
      data.addData(100, 30, 10, 95.0);
      data.addData(100, 30, 20, 99.5);
      data.addData(100, 30, 30, 99.4);
      
      addFeasible40And50VMData(data);
      
      Configuration config = new MinimalFeasibleConfigurationDeterminer(99.0).getMinimalFeasibleConfiguration(data);
      Assert.assertEquals(30, config.getVMs());
      Assert.assertEquals(20, config.getIterations());
      Assert.assertEquals(100, config.getRepetitions());
   }
   
   @Test
   public void testHigherIterationHasLowerF1Score() {
      PrecisionData data = new PrecisionData();
      data.addData(100, 10, 10, 95.0);
      data.addData(100, 10, 20, 95.0);
      data.addData(100, 10, 30, 95.0);
      
      data.addData(100, 20, 10, 99.1);
      data.addData(100, 20, 20, 99.2);
      data.addData(100, 20, 30, 95.0);
      
      data.addData(100, 30, 10, 95.0);
      data.addData(100, 30, 20, 99.5);
      data.addData(100, 30, 30, 99.4);
      
      addFeasible40And50VMData(data);
      
      Configuration config = new MinimalFeasibleConfigurationDeterminer(99.0).getMinimalFeasibleConfiguration(data);
      Assert.assertEquals(30, config.getVMs());
      Assert.assertEquals(20, config.getIterations());
      Assert.assertEquals(100, config.getRepetitions());
   }
   
   @Test
   public void testHigherVMCountHasLowerF1Score() {
      PrecisionData data = new PrecisionData();
      data.addData(100, 10, 10, 95.0);
      data.addData(100, 10, 20, 95.0);
      data.addData(100, 10, 30, 95.0);
      
      data.addData(100, 20, 10, 99.1);
      data.addData(100, 20, 20, 99.2);
      data.addData(100, 20, 30, 99.3);
      
      data.addData(100, 30, 10, 95.0);
      data.addData(100, 30, 20, 99.5);
      data.addData(100, 30, 30, 99.4);
      
      addFeasible40And50VMData(data);
      
      Configuration config = new MinimalFeasibleConfigurationDeterminer(99.0).getMinimalFeasibleConfiguration(data);
      
      System.out.println(config);
      
      Assert.assertEquals(20, config.getVMs());
      Assert.assertEquals(20, config.getIterations());
      Assert.assertEquals(100, config.getRepetitions());
   }

   private void addFeasible40And50VMData(final PrecisionData data) {
      data.addData(100, 40, 10, 99.1);
      data.addData(100, 40, 20, 99.2);
      data.addData(100, 40, 30, 99.2);
      
      data.addData(100, 50, 10, 99.3);
      data.addData(100, 50, 20, 99.3);
      data.addData(100, 50, 30, 99.3);
   }
   
   @Test
   public void testDifferentRepetitionCount() {
      
   }
}
