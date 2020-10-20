package de.precision.processing.compilations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

class CSVWriter {
   final PrintStream printer;

   public CSVWriter(File outputFile) throws FileNotFoundException {
      printer = new PrintStream(outputFile);
   }

   public void printValues(final CompilationReader reader, final File[] files) {
      for (File compilationLog : files) {
         CompilationSignature signature = reader.getFileSignature(compilationLog);
         printer.print(compilationLog.getName() + " ");
         for (String key : reader.getKeys()) {
            final Integer tier = signature.levels.get(key) != null ? signature.levels.get(key) : 0;
            printer.print(tier + " ");
         }
         printer.println();
      }
   }

   public void printHeader(final CompilationReader reader) {
      printer.print("File ");
      for (String compiledMethod : reader.getKeys()) {
         printer.print(compiledMethod + " ");
      }
      printer.println();
   }
}