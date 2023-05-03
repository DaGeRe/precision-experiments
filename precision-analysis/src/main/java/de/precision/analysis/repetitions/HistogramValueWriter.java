package de.precision.analysis.repetitions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import de.dagere.kopeme.kopemedata.VMResult;

public class HistogramValueWriter {
   public static void writeValues(final List<VMResult> values, final File destination) {
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(destination))) {
         for (VMResult r : values) {
            writer.write(r.getValue() + "\n");
         }
         writer.flush();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
