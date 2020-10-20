package de.precision.processing.compilations.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.precision.processing.compilations.CompilationSignature;

public class MergeCSVs {
   public static void main(String[] args) throws IOException {
      List<String> methods = getMethods(args);

      for (String inputCSVname : args) {
         File inputCSV = new File(inputCSVname);
         CSVReader reader = new CSVReader(inputCSV);
         System.out.println("Reading finished");
         List<CompilationSignature> signatures = readSignatures(reader);
         System.out.println("Signature Reading finished");
         addMissingMethods(methods, reader, signatures);
         
         System.out.println("Adding finished");
         PrintStream printer = printHeader(methods, inputCSV);
         printData(methods, signatures, printer);
         System.out.println("Printing finished");
      }
   }

   private static void printData(List<String> methods, List<CompilationSignature> signatures, PrintStream printer) {
      for (CompilationSignature signature : signatures) {
         printer.print("Unknown ");
         for (String compiledMethod : methods) {
            printer.print(signature.getLevels().get(compiledMethod) + " ");
         }
         printer.println();
      }
   }

   private static PrintStream printHeader(List<String> methods, File inputCSV) throws FileNotFoundException {
      PrintStream printer = new PrintStream(new File(inputCSV.getParentFile(), inputCSV.getName() + "_merged"));

      printer.print("File ");
      for (String compiledMethod : methods) {
         printer.print(compiledMethod + " ");
      }
      printer.println();
      return printer;
   }

   private static void addMissingMethods(List<String> methods, CSVReader reader, List<CompilationSignature> signatures) {
      for (String method : methods) {
         boolean contains = Arrays.stream(reader.headers).anyMatch(method::equals);
         if (!contains) {
            for (CompilationSignature signature : signatures) {
               signature.addCompilationLevel(method, 0);
            }
         }
      }
   }

   private static List<CompilationSignature> readSignatures(CSVReader reader) {
      List<CompilationSignature> signatures = new ArrayList<>();
      for (int line = 0; line < reader.values[0].length; line++) {
         CompilationSignature signature = new CompilationSignature();
         signatures.add(signature);
         for (int i = 0; i < reader.headers.length; i++) {
            final int level = (int) reader.values[i][line];
            signature.addCompilationLevel(reader.headers[i], level);
         }
      }
      return signatures;
   }

   private static List<String> getMethods(String[] args) throws IOException {
      List<String> methods = new ArrayList<>();
      for (String inputCSVname : args) {
         File inputCSV = new File(inputCSVname);
         CSVReader reader = new CSVReader(inputCSV);
         for (String method : reader.headers) {
            if (!methods.contains(method) && !method.equals("File")) {
               methods.add(method);
            }

         }
      }
      return methods;
   }
}
