package de.precision.analysis.graalvm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.kopemedata.DatacollectorResult;
import de.dagere.kopeme.kopemedata.Fulldata;
import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.MeasuredValue;
import de.dagere.kopeme.kopemedata.TestMethod;
import de.dagere.kopeme.kopemedata.VMResult;

public class GraalVMReadUtil {

   private static final Logger LOG = LogManager.getLogger(GraalVMReadUtil.class);

   public static Kopemedata readData(File vmFolder, boolean clean) {
      Kopemedata data = new Kopemedata("unkownClazz");

      FilenameFilter filter = new FilenameFilter() {
         @Override
         public boolean accept(File dir, String name) {
            LOG.trace("Testing {} clean: {}", name, clean);
            if (clean) {
               return name.endsWith("_cleaned.csv");
            } else {
               return name.endsWith(".csv") && !name.endsWith("_cleaned.csv");
            }
         }
      };

      for (File versionDataFile : vmFolder.listFiles(filter)) {
         String commitName = vmFolder.getName();
         readVersionDataFile(data, versionDataFile, commitName);
      }
      return data;
   }

   public static void readVersionDataFile(Kopemedata data, File versionDataFile, String commitName) {
      try (BufferedReader reader = new BufferedReader(new FileReader(versionDataFile))) {
         String line;

         SummaryStatistics statistics = new SummaryStatistics();

         String headline = reader.readLine();

         int columnIndex = getColumnIndex(headline, "iteration_time_ns");

         line = reader.readLine();

         String[] firstParts = line.split(",");
         String benchmarkName = firstParts[1];
         
         VMResult vmResult = createNewVMResult(data, benchmarkName, commitName);
         List<MeasuredValue> values = vmResult.getFulldata().getValues();
         int lineIndex = 0;
         readPartData(statistics, values, firstParts, lineIndex++, columnIndex);

         while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (!"index".equals(parts[0])) {
               readPartData(statistics, values, parts, lineIndex++, columnIndex);
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

   static int getColumnIndex(String headline, String columnName) {
      String[] headlineParts = headline.split(",");
      int columnIndex = -1;
      for (int i = 0; i < headlineParts.length; i++) {
         if (headlineParts[i].contains(columnName)) {
            columnIndex = i;
            break;
         }
      }
      return columnIndex;
   }

   private static void readPartData(SummaryStatistics statistics, List<MeasuredValue> values, String[] parts, int lineIndex, int columnIndex) {
      long startTime = lineIndex;
      long duration = Long.parseLong(parts[columnIndex]);
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
