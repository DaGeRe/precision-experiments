package de.precision.processing.compilations;

import java.io.File;
import java.io.IOException;

public class ReadCompilations {

   public static void main(String[] args) throws IOException {
      File compilationsFolder = new File(args[0]);

      final CompilationReader reader = new CompilationReader(compilationsFolder);
      final File[] files = reader.readCompilations();

      System.out.println(reader.getKeys().size());
      System.out.println(reader.getKeys());

      File outputFile = new File(compilationsFolder.getParentFile(), "compilations.csv");

      final CSVWriter writer = new CSVWriter(outputFile);
      writer.printHeader(reader);
      writer.printValues(reader, files);
   }

}
