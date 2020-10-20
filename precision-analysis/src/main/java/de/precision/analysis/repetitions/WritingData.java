package de.precision.analysis.repetitions;

import java.io.BufferedWriter;
import java.io.File;
import java.util.Map;

public class WritingData {
   private final File resultFolder;
   private final BufferedWriter precisionRecallWriter;
   private final Map<String, BufferedWriter> testcaseWriters;
   
   public WritingData(File resultFolder, BufferedWriter precisionRecallWriter, Map<String, BufferedWriter> testcaseWriters) {
      this.resultFolder = resultFolder;
      this.precisionRecallWriter = precisionRecallWriter;
      this.testcaseWriters = testcaseWriters;
   }

   public File getResultFolder() {
      return resultFolder;
   }

   public BufferedWriter getPrecisionRecallWriter() {
      return precisionRecallWriter;
   }

   public Map<String, BufferedWriter> getTestcaseWriters() {
      return testcaseWriters;
   }

}