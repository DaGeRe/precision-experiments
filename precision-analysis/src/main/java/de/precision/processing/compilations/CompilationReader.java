package de.precision.processing.compilations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CompilationReader {

   private File compilationFolder;
   private final Map<CompilationSignature, Integer> signatures = new HashMap<>();
   private final Map<File, CompilationSignature> fileSignatures = new HashMap<>();
   private final List<String> keys = new LinkedList<>();

   public CompilationReader(File compilationFolder) {
      this.compilationFolder = compilationFolder;
   }

   public File[] readCompilations() throws FileNotFoundException, IOException {
      int index = 0;

      final File[] files = compilationFolder.listFiles();
      sortLogFiles(files);
      for (File compilationLog : files) {
         CompilationSignature signature = getSignature(compilationLog);
         if (!signatures.containsKey(signature)) {
            signatures.put(signature, index++);
         }
      }
      return files;
   }

   private void sortLogFiles(final File[] files) {
      Arrays.sort(files, new Comparator<File>() {

         @Override
         public int compare(File o1, File o2) {
            final String firstName = o1.getName();
            int repetitionsFirst = Integer.parseInt(firstName.substring(0, firstName.indexOf("_")));
            final String secondName = o2.getName();
            int repetitionsSecond = Integer.parseInt(secondName.substring(0, secondName.indexOf("_")));
            int indexFirst = getIndex(firstName);
            int indexSecond = getIndex(secondName);
            return (repetitionsFirst - repetitionsSecond) * 1000 + (indexFirst - indexSecond);
         }

         private int getIndex(final String firstName) {
            final int upperBound;
            final String beforeLastUnderscore = firstName.substring(0, firstName.lastIndexOf("_") - 1);
            if (firstName.contains("_2.txt") && beforeLastUnderscore.contains("_")) {
               upperBound = firstName.lastIndexOf('_');
            } else {
               upperBound = firstName.indexOf('.');
            }
            return Integer.parseInt(firstName.substring(firstName.indexOf("_") + 1, upperBound));
         }
      });
   }

   private CompilationSignature getSignature(File compilationLog) throws IOException, FileNotFoundException {
      CompilationSignature signature = new CompilationSignature();
      String line;
      try (BufferedReader reader = new BufferedReader(new FileReader(compilationLog))) {
         while ((line = reader.readLine()) != null) {
            // if (line.contains("de.")) {
            if (!line.startsWith("[GC") && !line.startsWith("[Full GC")) {
               final String shortenedLine = line.trim().replaceAll(" +", " ");
               System.out.println(shortenedLine);
               readMethodLine(signature, shortenedLine);
            }
            // }
         }
      }
      fileSignatures.put(compilationLog, signature);
      return signature;
   }

   private void readMethodLine(CompilationSignature signature, final String shortenedLine) {
      if (!shortenedLine.endsWith("made not entrant")) {
         String[] splitted = shortenedLine.split(" ");
         int level;
         String method;
         if (splitted[2].equals("%") || splitted[2].equals("!") || splitted[2].equals("s") || splitted[2].equals("s!")) {
            if (splitted[3].equals("!")) {
               level = Integer.parseInt(splitted[4]);
               method = splitted[5];
            } else {
               level = Integer.parseInt(splitted[3]);
               method = splitted[4];
            }
         } else {
            level = Integer.parseInt(splitted[2]);
            method = splitted[3];
         }
         String shortMethod = method.substring(method.lastIndexOf('.') + 1);
         if (!keys.contains(shortMethod)) {
            keys.add(shortMethod);
         }
         signature.addCompilationLevel(shortMethod, level);
      }
   }

   public Map<CompilationSignature, Integer> getSignatures() {
      return signatures;
   }

   public List<String> getKeys() {
      return keys;
   }

   public CompilationSignature getFileSignature(File file) {
      return fileSignatures.get(file);
   }
}
