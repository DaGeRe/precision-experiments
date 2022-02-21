package de.precision.analysis.repetitions;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import picocli.CommandLine;
import picocli.CommandLine.Option;

/**
 * Takes a folder with sequence-executions and a precision-level as input. Tells, how many sequence-executions are needed in order to achieve the precision-level.
 * 
 * @author reichelt
 *
 */
public class GeneratePrecisionPlot implements Callable<Void> {
   
   public static final String MEAN = "MEAN";
   public static final String TTEST = "TTEST";
   public static final String TTEST2 = "TTEST2";
   public static final String CONFIDENCE = "CONFIDENCE";
   public static final String MANNWHITNEY = "MANNWHITNEY";

   final static String[] myTypes = new String[] { MEAN, TTEST, TTEST2, CONFIDENCE, MANNWHITNEY };

   private static final Logger LOG = LogManager.getLogger(GeneratePrecisionPlot.class);

   @Option(names = { "-only100k", "--only100k" }, description = "Only analyse 100.000 repetitions - for test comparison")
   private boolean only100k = false;
   
   @Option(names = { "-useConfidence", "--useConfidence" }, description = "Use confidence-interval-test (time-consuming, off by default)")
   private boolean useConfidence = false;
   
   @Option(names = { "-data", "--data" }, description = "Data-Folder for analysis", required = true)
   private String[] data;
   
   @Option(names = { "-printPicks", "--printPicks" }, description = "Print the picked values summaries (for debugging)")
   private boolean printPicks;
   
   public static void main(final String[] args) throws JAXBException, IOException, InterruptedException {
      // System.setOut(new PrintStream(new File("/dev/null")));
      Configurator.setLevel("de.peass.measurement.analysis.statistics.ConfidenceIntervalInterpretion", Level.INFO);

      final GeneratePrecisionPlot command = new GeneratePrecisionPlot();
      final CommandLine commandLine = new CommandLine(command);
      commandLine.execute(args);
   }

   private void createTasks(final String[] folders, final PrecisionConfig config, final String suffix) throws IOException, JAXBException, InterruptedException {
      for (final String inputFolderName : folders) {
         final File inputFolder = new File(inputFolderName);
         
         final File resultFolder = new File(inputFolder.getParentFile(), inputFolder.getName() + File.separator + suffix);
         new PrecisionPlotGenerationManager(resultFolder, config).handleFolder(inputFolder);
      }
   }


   @Override
   public Void call() throws Exception {
      createTasks(data, new PrecisionConfig(useConfidence, only100k, false, printPicks, myTypes), "results_noOutlierRemoval");
      createTasks(data, new PrecisionConfig(useConfidence, only100k, true, printPicks, myTypes), "results_outlierRemoval");

      SingleFileGenerator.createSingleFiles(data);
      return null;
   }
}
