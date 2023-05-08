package de.precision.analysis.graalvm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import de.dagere.kopeme.kopemedata.DatacollectorResult;
import de.dagere.kopeme.kopemedata.Fulldata;
import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.MeasuredValue;
import de.dagere.kopeme.kopemedata.TestMethod;
import de.dagere.kopeme.kopemedata.VMResult;

public class GraalVMReadUtil {
   public static Kopemedata readData(File vmFolder) {
      Kopemedata data = new Kopemedata("unkownClazz");
      for (File versionDataFile : vmFolder.listFiles((FilenameFilter) new WildcardFileFilter("*-raw.csv"))) {
         try (BufferedReader reader = new BufferedReader(new FileReader(versionDataFile))){
            String line;
            
            SummaryStatistics statistics = new SummaryStatistics();
            
            line = reader.readLine();
            
            line = reader.readLine();
            
            String[] firstParts = line.split(",");
            String benchmarkName = firstParts[1];
            VMResult vmResult = createNewVMResult(data, benchmarkName, vmFolder.getName());
            List<MeasuredValue> values = vmResult.getFulldata().getValues();
            readPartData(statistics, values, firstParts);

            while ((line = reader.readLine()) != null) {
               String[] parts = line.split(",");
               if (!"index".equals(parts[0])) {
                  readPartData(statistics, values, parts);
               }
            }
            vmResult.setValue(statistics.getMean());
            vmResult.setDeviation(statistics.getStandardDeviation());
         } catch (FileNotFoundException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      return data;
   }

   private static void readPartData(SummaryStatistics statistics, List<MeasuredValue> values, String[] parts) {
      long startTime = Long.parseLong(parts[0]);
      long duration = Long.parseLong(parts[3]);
      MeasuredValue measuredValue = new MeasuredValue();
      measuredValue.setStartTime(startTime);
      measuredValue.setValue(duration);
      values.add(measuredValue);
      statistics.addValue(duration);
   }

   private static VMResult createNewVMResult(Kopemedata data, String benchmarkName, String commitName) {
      data.getMethods().add(new TestMethod(benchmarkName));
      data.getMethods().get(0).getDatacollectorResults().add(new DatacollectorResult("time"));
      
      VMResult vmResult = new VMResult();
      data.getFirstDatacollectorContent().add(vmResult);
      vmResult.setFulldata(new Fulldata());
      vmResult.setCommit(commitName);
      LinkedList<MeasuredValue> values = new LinkedList<>();
      
      vmResult.getFulldata().setValues(values);
      return vmResult;
   }
}
