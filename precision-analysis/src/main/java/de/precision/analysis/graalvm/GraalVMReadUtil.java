package de.precision.analysis.graalvm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedList;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import de.dagere.kopeme.kopemedata.DatacollectorResult;
import de.dagere.kopeme.kopemedata.Fulldata;
import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.MeasuredValue;
import de.dagere.kopeme.kopemedata.TestMethod;
import de.dagere.kopeme.kopemedata.VMResult;

public class GraalVMReadUtil {
   public static Kopemedata readData(File vmFolder) throws IOException, FileNotFoundException {
      Kopemedata data = new Kopemedata("unkownClazz");
      data.getMethods().add(new TestMethod("unkownMethod"));
      data.getMethods().get(0).getDatacollectorResults().add(new DatacollectorResult("time"));
      for (File versionDataFile : vmFolder.listFiles((FilenameFilter) new WildcardFileFilter("*-raw.csv"))) {
         VMResult vmResult = new VMResult();
         data.getFirstDatacollectorContent().add(vmResult);
         vmResult.setFulldata(new Fulldata());
         vmResult.setCommit(vmFolder.getName());
         LinkedList<MeasuredValue> values = new LinkedList<>();
         
         vmResult.getFulldata().setValues(values);
         try (BufferedReader reader = new BufferedReader(new FileReader(versionDataFile))){
            String line;
            while ((line = reader.readLine()) != null) {
               String[] parts = line.split(",");
               if (!"index".equals(parts[0])) {
                  long startTime = Long.parseLong(parts[0]);
                  long duration = Long.parseLong(parts[1]);
                  MeasuredValue measuredValue = new MeasuredValue();
                  measuredValue.setStartTime(startTime);
                  measuredValue.setValue(duration);
                  values.add(measuredValue);
               }
            }
         }
      }
      return data;
   }
}
