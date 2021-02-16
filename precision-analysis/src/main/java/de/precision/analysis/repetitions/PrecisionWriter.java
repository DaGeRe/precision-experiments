package de.precision.analysis.repetitions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.precision.processing.ProcessConstants;

public class PrecisionWriter {
   
   private static final DecimalFormat df = new DecimalFormat("00.00", DecimalFormatSymbols.getInstance(Locale.US));
   
   private final PrecisionComparer comparer;
   private final ExecutionData executionData;
   
   
   public PrecisionWriter(final PrecisionComparer comparer, final ExecutionData executionData) {
      this.comparer = comparer;
      this.executionData = executionData;
   }
   
   public static void writeHeader(final BufferedWriter writer) throws IOException{
      synchronized (writer) {
         writer.write("repetitions" + ProcessConstants.DATAFILE_SEPARATOR +
               "vms" + ProcessConstants.DATAFILE_SEPARATOR +
               "executions" + ProcessConstants.DATAFILE_SEPARATOR +
               "warmup" + ProcessConstants.DATAFILE_SEPARATOR +
               "overhead" + ProcessConstants.DATAFILE_SEPARATOR +
               "duration" + ProcessConstants.DATAFILE_SEPARATOR);
         writer.write("\n");
         writer.flush();
      }
   }
   
   public static void writeHeader(final BufferedWriter writer, final String[] types) throws IOException {
      writer.write("repetitions" + ProcessConstants.DATAFILE_SEPARATOR +
            "vms" + ProcessConstants.DATAFILE_SEPARATOR +
            "executions" + ProcessConstants.DATAFILE_SEPARATOR +
            "warmup" + ProcessConstants.DATAFILE_SEPARATOR +
            "overhead" + ProcessConstants.DATAFILE_SEPARATOR +
            "duration" + ProcessConstants.DATAFILE_SEPARATOR);
      for (final String method : new MethodResult(GeneratePrecisionPlot.myTypes).getResults().keySet()) {
         writer.write(method + ProcessConstants.DATAFILE_SEPARATOR + ProcessConstants.DATAFILE_SEPARATOR + ProcessConstants.DATAFILE_SEPARATOR);
      }
      writer.write("\n");
      writer.flush();
   }

   public void writeTestcase(final BufferedWriter testcaseWriter, final Set<Entry<String, Map<String, Integer>>> statisticMethodResults) throws IOException {
      synchronized (testcaseWriter) {
         testcaseWriter.write(executionData.getRepetitions() + ProcessConstants.DATAFILE_SEPARATOR +
               executionData.getVms() + ProcessConstants.DATAFILE_SEPARATOR +
               executionData.getExecutions() + ProcessConstants.DATAFILE_SEPARATOR +
               executionData.getWarmup() + ProcessConstants.DATAFILE_SEPARATOR +
               executionData.getOverhead() + ProcessConstants.DATAFILE_SEPARATOR +
               executionData.getDuration() + ProcessConstants.DATAFILE_SEPARATOR);
         for (final Map.Entry<String, Map<String, Integer>> methodResult : statisticMethodResults) {
            writeData(methodResult, testcaseWriter);
         }
         testcaseWriter.write("\n");
         testcaseWriter.flush();
      }
   }
   
   private void writeData(final Map.Entry<String, Map<String, Integer>> methodResult, final BufferedWriter writer) throws IOException {

      final int selected = methodResult.getValue().get(MethodResult.SELECTED);
      // final int truepositive = methodResult.getValue().get(MethodResult.TRUEPOSITIVE);
      // final int falsenegative = methodResult.getValue().get(MethodResult.FALSENEGATIVE);
      final int wronggreater = methodResult.getValue().get(MethodResult.WRONGGREATER);
      final double precision = comparer.getPrecision(methodResult.getKey());
      final double recall = comparer.getRecall(methodResult.getKey());
      final double fscore = comparer.getFScore(methodResult.getKey());
      final double wrongGreaterSelectionRate = (selected > 0) ? ((double) wronggreater) / selected : 0;
      writer.write(df.format(precision) + ProcessConstants.DATAFILE_SEPARATOR +
            df.format(recall) + ProcessConstants.DATAFILE_SEPARATOR +
            df.format(fscore) + ProcessConstants.DATAFILE_SEPARATOR +
            df.format(wrongGreaterSelectionRate) + ProcessConstants.DATAFILE_SEPARATOR);
   }
}
