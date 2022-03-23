package de.precision.analysis.repetitions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.Callable;

import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Fulldata;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.peass.config.StatisticsConfig;
import de.precision.processing.repetitions.sampling.SamplingConfig;
import de.precision.processing.repetitions.sampling.VMCombinationSampler;
import picocli.CommandLine;
import picocli.CommandLine.Option;

/**
 * Because it is faster to calculate from aggregated values, this reads CSVs directly (instead of full XML result files)
 * 
 * @author reichelt
 *
 */
public class AggregatedValueSampler implements Callable<Void> {

   @Option(names = { "-fast", "--fast" }, description = "Aggregated data file of fast version", required = true)
   private String fastFileName;

   @Option(names = { "-slow", "--slow" }, description = "Aggregated data file of slow version", required = true)
   private String slowFileName;

   @Option(names = { "-out", "---out" }, description = "Filename of output file")
   private String out;

   private BufferedWriter precisionRecallWriter;

   public static void main(final String[] args) throws FileNotFoundException, IOException {

      final AggregatedValueSampler command = new AggregatedValueSampler();
      final CommandLine commandLine = new CommandLine(command);
      commandLine.execute(args);

      // executeSampling(versionSlow, versionFast, testclazz, out, 1000);
   }

   @Override
   public Void call() throws Exception {
      final File fileFast = new File(fastFileName);
      final File fileSlow = new File(slowFileName);

      final TestcaseType versionFast = readFile(fileFast);
      final TestcaseType versionSlow = readFile(fileSlow);

      System.out.println("Reading finished");

      final Testcases testclazz = new Testcases();
      testclazz.setClazz("Test");

      final File resultFile = new File(out != null ? out : "result.csv");
      precisionRecallWriter = new BufferedWriter(new FileWriter(resultFile));

      PrintStream out = System.out;

      for (int vms = 50; vms <= 1000; vms += 50) {
         executeSampling(versionSlow, versionFast, testclazz, out, vms);
      }
      return null;
   }

   private void executeSampling(final TestcaseType versionSlow, final TestcaseType versionFast, final Testcases testclazz, final PrintStream out, final int vms) throws FileNotFoundException {
      System.setOut(new PrintStream(new File("results", "vals_" + vms + ".csv")));

      final SamplingConfig config = new SamplingConfig(vms, "Test");
      StatisticsConfig statisticsConfig = new StatisticsConfig();
      statisticsConfig.setOutlierFactor(StatisticsConfig.DEFAULT_OUTLIER_FACTOR);
      PrecisionConfig precisionConfig = new PrecisionConfig(false, true, false, 2, StatisticalTestList.ALL.getTests(), 50, 20, -1);
      PrecisionComparer comparer = new PrecisionComparer(statisticsConfig, precisionConfig);
      VMCombinationSampler sampler = new VMCombinationSampler(0, 1, comparer, config, statisticsConfig);

      sampler.sampleArtificialVMCombinations(versionFast, versionSlow);

      System.setOut(out);

      System.out.println(vms + " " + comparer.getPrecision(StatisticalTests.TTEST2)
            + " " + comparer.getRecall(StatisticalTests.TTEST2)
            + comparer.getTestcaseResults().get("Test").getResults().get(StatisticalTests.TTEST2));

      try {
         new PrecisionWriter(comparer, new ExecutionData(vms, 5, 5, 10)).writeTestcase(precisionRecallWriter, comparer.getOverallResults().getResults());
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private static TestcaseType readFile(final File file) throws IOException, FileNotFoundException {
      TestcaseType versionSlow = new TestcaseType();
      final Datacollector datacollector = new Datacollector();
      try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
         String line;
         while ((line = reader.readLine()) != null) {
            final double thisVMValue = Double.parseDouble(line);

            buildResult(datacollector, thisVMValue);
         }
      }

      versionSlow.getDatacollector().add(datacollector);
      return versionSlow;
   }

   private static void buildResult(final Datacollector datacollector, final double thisVMValue) {
      final Result result = new Result();
      result.setDate(0l);
      final Result.Fulldata.Value value = new Result.Fulldata.Value();
      value.setStart(0l);
      value.setValue((long) thisVMValue);

      final Fulldata fulldata = new Fulldata();
      fulldata.getValue().add(value);

      result.setFulldata(fulldata);
      result.setValue(thisVMValue);
      datacollector.getResult().add(result);
   }

}
