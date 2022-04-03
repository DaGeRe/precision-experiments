package de.precision.analysis.heatmap;

import org.junit.Assert;
import org.junit.jupiter.api.Test;


public class TestMinimalFeasibleConfigurationDeterminer {
   
   @Test
   public void testBasic() {
      PrecisionData data = new PrecisionData();
      createPrecisionData(data, 100);
      
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
      
      addFeasible40And50VMData(100, data);
      
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
      
      addFeasible40And50VMData(100, data);
      
      Configuration config = new MinimalFeasibleConfigurationDeterminer(99.0).getMinimalFeasibleConfiguration(data);
      
      Assert.assertEquals(20, config.getVMs());
      Assert.assertEquals(20, config.getIterations());
      Assert.assertEquals(100, config.getRepetitions());
   }

   @Test
   public void testDifferentRepetitionCount_EqualData() {
      PrecisionData data = new PrecisionData();
      createPrecisionData(data, 100);
      createPrecisionData(data, 1000);
      
      Configuration config = new MinimalFeasibleConfigurationDeterminer(99.0).getMinimalFeasibleConfiguration(data);
      
      Assert.assertEquals(30, config.getVMs());
      Assert.assertEquals(20, config.getIterations());
      Assert.assertEquals(1000, config.getRepetitions());
   }
   
   @Test
   public void testDifferentRepetitionCount_WorseDataWithBiggerCount() {
      PrecisionData data = new PrecisionData();
      createPrecisionData(data, 100);
      createPrecisionData(data, 1000);
      
      data.addData(1000, 30, 20, 98.1);
      
      Configuration config = new MinimalFeasibleConfigurationDeterminer(99.0).getMinimalFeasibleConfiguration(data);
      
      Assert.assertEquals(30, config.getVMs());
      Assert.assertEquals(20, config.getIterations());
      Assert.assertEquals(100, config.getRepetitions());
   }
   
   private void createPrecisionData(final PrecisionData data, final int repetitions) {
      
      data.addData(repetitions, 10, 10, 95.0);
      data.addData(repetitions, 10, 20, 95.0);
      data.addData(repetitions, 10, 30, 95.0);
      data.addData(repetitions, 20, 10, 95.0);
      data.addData(repetitions, 20, 20, 95.0);
      data.addData(repetitions, 20, 30, 95.0);
      data.addData(repetitions, 30, 10, 95.0);
      data.addData(repetitions, 30, 20, 99.5);
      data.addData(repetitions, 30, 30, 99.4);
      
      addFeasible40And50VMData(repetitions, data);
   }
   
   private void addFeasible40And50VMData(final int repetitions, final PrecisionData data) {
      data.addData(repetitions, 40, 10, 99.1);
      data.addData(repetitions, 40, 20, 99.2);
      data.addData(repetitions, 40, 30, 99.2);
      
      data.addData(repetitions, 50, 10, 99.3);
      data.addData(repetitions, 50, 20, 99.3);
      data.addData(repetitions, 50, 30, 99.3);
   }
}
