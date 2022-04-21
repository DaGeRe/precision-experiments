package de.precision.analysis.repetitions;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import jakarta.xml.bind.JAXBException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import picocli.CommandLine;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/**
 * Takes a folder with sequence-executions and a precision-level as input. Tells, how many sequence-executions are needed in order to achieve the precision-level.
 * 
 * @author reichelt
 *
 */
public class GeneratePrecisionPlot implements Callable<Void> {

   private static final Logger LOG = LogManager.getLogger(GeneratePrecisionPlot.class);

   @Option(names = { "-data", "--data" }, description = "Data-Folder for analysis", required = true)
   private String[] data;

   @Mixin
   private PrecisionConfigMixin precisionConfigMixin;

   public static void main(final String[] args) throws JAXBException, IOException, InterruptedException {
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
      PrecisionConfig precisionConfig = precisionConfigMixin.getConfig();

      String resultFolderName;
      if (precisionConfigMixin.isOutlierRemoval()) {
         resultFolderName = "results_outlierRemoval";
      } else {
         resultFolderName = "results_noOutlierRemoval";
      }

      createTasks(data, precisionConfig, resultFolderName);

      for (String datafileName : data) {
         File inputFolder = new File(datafileName);
         File resultsFolder = new File(inputFolder, resultFolderName);
         SingleFileGenerator.getSingleRepetitionFiles(inputFolder, resultsFolder);
      }
      
      return null;
   }
}
