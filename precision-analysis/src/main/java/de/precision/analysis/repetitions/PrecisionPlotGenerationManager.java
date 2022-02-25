package de.precision.analysis.repetitions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import de.precision.processing.util.PrecisionFolderUtil;
import de.precision.processing.util.RepetitionFolderHandler.CreatorParallel;

public class PrecisionPlotGenerationManager {

   private final BufferedWriter precisionRecallWriter;
   private final Map<String, BufferedWriter> testcaseWriters = new HashMap<>();

   private final File resultFolder;
   private final PrecisionConfig config;

   public PrecisionPlotGenerationManager(final File resultFolder, final PrecisionConfig config) throws IOException {
      resultFolder.mkdir();
      this.resultFolder = resultFolder;
      this.config = config;
      precisionRecallWriter = new BufferedWriter(new FileWriter(new File(resultFolder, "precision.csv")));
   }

   void handleFolder(final File inputFolder) throws IOException, JAXBException, InterruptedException {
      PrecisionWriter.writeHeader(precisionRecallWriter, config.getTypes());

      boolean hasPrecisionChild = false;
      for (File child : inputFolder.listFiles()) {
         if (child.getName().startsWith("precision_")) {
            hasPrecisionChild = true;
         }
      }

      startProcessing(inputFolder, hasPrecisionChild);
   }

   private void startProcessing(final File inputFolder, final boolean hasPrecisionChild) throws JAXBException, IOException, InterruptedException {
      final WritingData writingData = new WritingData(resultFolder, precisionRecallWriter, testcaseWriters);
      if (inputFolder.getName().contains("Test") || hasPrecisionChild) {
         PrecisionFolderUtil.processFolderParallel(inputFolder, creatorFunction(writingData), config.getThreads());
      } else {
         for (File testFolder : inputFolder.listFiles()) {
            PrecisionFolderUtil.processFolderParallel(testFolder, creatorFunction(writingData), config.getThreads());
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
}
