package de.precision.analysis.repetitions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;

import de.precision.processing.ProcessConstants;

public class PrecisionWriter {

   private static final DecimalFormat df = new DecimalFormat("00.00", DecimalFormatSymbols.getInstance(Locale.US));

   private final PrecisionComparer comparer;
   private final ExecutionData executionData;

   public PrecisionWriter(final PrecisionComparer comparer, final ExecutionData executionData) {
      this.comparer = comparer;
      this.executionData = executionData;
   }

   public static void writeHeader(final BufferedWriter writer, final String[] types) throws IOException {
      writer.write("repetitions" + ProcessConstants.DATAFILE_SEPARATOR +
            "vms" + ProcessConstants.DATAFILE_SEPARATOR +
            "executions" + ProcessConstants.DATAFILE_SEPARATOR +
            "warmup" + ProcessConstants.DATAFILE_SEPARATOR +
            "overhead" + ProcessConstants.DATAFILE_SEPARATOR +
            "duration" + ProcessConstants.DATAFILE_SEPARATOR);
      for (final String method : new MethodResult(types).getResults().keySet()) {
         writer.write(method + ProcessConstants.DATAFILE_SEPARATOR + ProcessConstants.DATAFILE_SEPARATOR + ProcessConstants.DATAFILE_SEPARATOR);
      }
      writer.write("\n");
      writer.flush();
   }

   public void writeTestcase(final BufferedWriter testcaseWriter, final Map<String, Map<String, Integer>> statisticMethodResults) throws IOException {
      synchronized (testcaseWriter) {
         testcaseWriter.write(executionData.getRepetitions() + ProcessConstants.DATAFILE_SEPARATOR +
               executionData.getVms() + ProcessConstants.DATAFILE_SEPARATOR +
               executionData.getExecutions() + ProcessConstants.DATAFILE_SEPARATOR +
               executionData.getWarmup() + ProcessConstants.DATAFILE_SEPARATOR +
               executionData.getOverhead() + ProcessConstants.DATAFILE_SEPARATOR +
               executionData.getDuration() + ProcessConstants.DATAFILE_SEPARATOR);
         for (String statisticalTest : StatisticalTestList.ALL.getTests()) {
            Map<String, Integer> methodResult = statisticMethodResults.get(statisticalTest);
            writeData(statisticalTest, methodResult, testcaseWriter);
         }
         testcaseWriter.write("\n");
         testcaseWriter.flush();
      }
   }

   private void writeData(String staticalMethod, final Map<String, Integer> methodResult, final BufferedWriter writer) throws IOException {

      if (methodResult != null) {
         final int selected = methodResult.get(MethodResult.SELECTED);
         final int wronggreater = methodResult.get(MethodResult.WRONGGREATER);
         final double precision = comparer.getPrecision(staticalMethod);
         final double recall = comparer.getRecall(staticalMethod);
         final double fscore = comparer.getFScore(staticalMethod);
         final double wrongGreaterSelectionRate = (selected > 0) ? ((double) wronggreater) / selected : 0;
         writer.write(df.format(precision) + ProcessConstants.DATAFILE_SEPARATOR +
               df.format(recall) + ProcessConstants.DATAFILE_SEPARATOR +
               df.format(fscore) + ProcessConstants.DATAFILE_SEPARATOR +
               df.format(wrongGreaterSelectionRate) + ProcessConstants.DATAFILE_SEPARATOR);
      } else {
         writer.write(0.0 + ProcessConstants.DATAFILE_SEPARATOR +
               0.0 + ProcessConstants.DATAFILE_SEPARATOR +
               0.0 + ProcessConstants.DATAFILE_SEPARATOR +
               0.0 + ProcessConstants.DATAFILE_SEPARATOR);
      }

   }
}
