package de.precision.analysis.graalvm;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.precision.analysis.graalvm.json.GraalVMJSONData;
import de.precision.analysis.graalvm.json.Pair;
import picocli.CommandLine;
import picocli.CommandLine.Option;

public class GraalVMJSONPrecisionDeterminer implements Runnable {
   
   @Option(names = { "-inputJSON", "--inputJSON" }, description = "Input JSON file from GraalVM", required = true)
   private File inputJSONs[];
   
   public static void main(String[] args) {
      GraalVMJSONPrecisionDeterminer plot = new GraalVMJSONPrecisionDeterminer();
      CommandLine cli = new CommandLine(plot);
      cli.execute(args);
   }

   @Override
   public void run() {
      for (File inputJSON : inputJSONs) {
         System.out.println("Reading " + inputJSON);
         GraalVMJSONData data;
         try {
            data = new ObjectMapper().readValue(inputJSON, GraalVMJSONData.class);
            
            for (Pair pair : data.pairs()) {
               System.out.println(pair.compareResults().pValue());
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
}
