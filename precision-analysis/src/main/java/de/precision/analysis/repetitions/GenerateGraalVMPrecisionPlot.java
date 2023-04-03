package de.precision.analysis.repetitions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import de.dagere.kopeme.kopemedata.DatacollectorResult;
import de.dagere.kopeme.kopemedata.Fulldata;
import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.MeasuredValue;
import de.dagere.kopeme.kopemedata.TestMethod;
import de.dagere.kopeme.kopemedata.VMResult;
import picocli.CommandLine;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

public class GenerateGraalVMPrecisionPlot implements Callable<Void> {

   @Option(names = { "-v1folder", "--v1folder" }, description = "Data-Folder for analysis", required = true)
   private File v1folder;
   
   @Option(names = { "-v2folder", "--v2folder" }, description = "Data-Folder for analysis", required = true)
   private File v2folder;

   @Mixin
   private PrecisionConfigMixin precisionConfigMixin;

   public static void main(String[] args) {
      GenerateGraalVMPrecisionPlot plot = new GenerateGraalVMPrecisionPlot();
      CommandLine cli = new CommandLine(plot);
      cli.execute(args);
   }

   @Override
   public Void call() throws Exception {
      ExecutorService pool = Executors.newFixedThreadPool(2);

      File resultFolder = new File(v1folder.getParentFile(), "results");
      resultFolder.mkdir();

      BufferedWriter precisionRecallWriter = new BufferedWriter(new FileWriter(new File(resultFolder, "precision.csv")));
      PrecisionWriter.writeHeader(precisionRecallWriter, StatisticalTestList.ALL.getTests());
      WritingData writingData = new WritingData(resultFolder, precisionRecallWriter, new HashMap<>());

      Map<String, Kopemedata> testcasesV1 = new HashMap<>();
      Map<String, Kopemedata> testcasesV2 = new HashMap<>();

      Kopemedata dataV1 = readData(v1folder);
      testcasesV1.put(dataV1.getClazz(), dataV1);
      
      Kopemedata dataV2 = readData(v2folder);
      testcasesV2.put(dataV2.getClazz(), dataV2);
      
      System.err.println("Data:" + dataV2.getFirstDatacollectorContent().size());

      PrecisionPlotHandler handler = new PrecisionPlotHandler(testcasesV1, testcasesV2, pool, 1, precisionConfigMixin.getConfig(), writingData);
      handler.handleAllParameters(30, 8, false);
      pool.shutdown();
      return null;
   }

   private Kopemedata readData(File vmFolder) throws IOException, FileNotFoundException {
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
