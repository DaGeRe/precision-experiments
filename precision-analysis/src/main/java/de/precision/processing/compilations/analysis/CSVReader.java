package de.precision.processing.compilations.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class CSVReader {
   String[] headers;
   double[][] values;

   public CSVReader(File file) throws IOException {
      try (BufferedReader csvReader = new BufferedReader(new FileReader(file))) {
         String line;
         line = csvReader.readLine();
         headers = line.split(" ");

         List<double[]> lines = readLines(csvReader);
         readValues(lines);
      }
   }

   private void readValues(List<double[]> lines) {
      values = new double[headers.length][lines.size()];
      for (int lineI = 0; lineI < lines.size(); lineI++) {
         for (int index = 0; index < headers.length; index++) {
            values[index][lineI] = lines.get(lineI)[index];
         }
      }
   }

   private List<double[]> readLines(BufferedReader csvReader) throws IOException {
      String line;
      List<double[]> lines = new ArrayList<>();
      while ((line = csvReader.readLine()) != null) {
         String[] elements = line.split(" ");
         if (elements.length != headers.length) {
            throw new RuntimeException("Wrong size in line: " + elements.length + " " + headers.length);
         }
         double[] values = new double[headers.length];
         for (int i = 0; i < headers.length; i++) {
            if (!elements[i].endsWith(".txt")) {
               values[i] = Double.parseDouble(elements[i]);
            } else {
               values[i] = 0;
            }
         }
         lines.add(values);
      }
      return lines;
   }
   
}