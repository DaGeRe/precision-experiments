package de.precision.analysis.repetitions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import de.precision.processing.ProcessConstants;
import de.precision.processing.util.RepetitionFolderHandler.CreatorParallel;
import de.precision.processing.util.PrecisionFolderUtil;

public class PrecisionPlotGenerationManager {

   private final BufferedWriter precisionRecallWriter;
   private final Map<String, BufferedWriter> testcaseWriters = new HashMap<>();

   private final File resultFolder;
   private final PrecisionConfig config;

   public PrecisionPlotGenerationManager(File resultFolder, PrecisionConfig config) throws IOException {
      resultFolder.mkdir();
      this.resultFolder = resultFolder;
      this.config = config;
      precisionRecallWriter = new BufferedWriter(new FileWriter(new File(resultFolder, "precision.csv")));
   }

   void handleFolder(final File inputFolder) throws IOException, JAXBException, InterruptedException {
      writeHeader(precisionRecallWriter);

      boolean hasPrecisionChild = false;
      for (File child : inputFolder.listFiles()) {
         if (child.getName().startsWith("precision_")) {
            hasPrecisionChild = true;
         }
      }

      startProcessing(inputFolder, hasPrecisionChild);
   }

   private void startProcessing(final File inputFolder, boolean hasPrecisionChild) throws JAXBException, IOException, InterruptedException {
      final WritingData writingData = new WritingData(resultFolder, precisionRecallWriter, testcaseWriters);
      if (inputFolder.getName().contains("Test") || hasPrecisionChild) {
         PrecisionFolderUtil.processFolderParallel(inputFolder, creatorFunction(writingData));
      } else {
         for (File testFolder : inputFolder.listFiles()) {
            PrecisionFolderUtil.processFolderParallel(testFolder, creatorFunction(writingData));
         }
      }
   }

   private CreatorParallel creatorFunction(final WritingData writingData) {
      return (repetitionFolder, pool) -> {
         if (!config.isOnly100k() || repetitionFolder.getName().endsWith("100000")) {
            return new PrecisionPlotGenerator(repetitionFolder, config, writingData, pool);
         } else {
            return null;
         }
      };
   }

   public static void writeHeader(BufferedWriter writer) throws IOException {
      writer.write("#repetitions vms executions warmup overheadInS duration ");
      for (final String method : new MethodResult(GeneratePrecisionPlot.myTypes).getResults().keySet()) {
         writer.write(method + ProcessConstants.DATAFILE_SEPARATOR + ProcessConstants.DATAFILE_SEPARATOR + ProcessConstants.DATAFILE_SEPARATOR);
      }
      writer.write("\n");
      writer.flush();
   }
}
