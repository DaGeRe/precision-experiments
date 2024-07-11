package de.precision.analysis.graalvm;

import java.io.File;

import picocli.CommandLine;
import picocli.CommandLine.Option;

public class GraalVMJSONPrecisionDeterminer implements Runnable {
   
   @Option(names = { "-inputJSON", "--inputJSON" }, description = "Input JSON file from GraalVM", required = true)
   private File inputJSON;
   
   public static void main(String[] args) {
      GraalVMJSONPrecisionDeterminer plot = new GraalVMJSONPrecisionDeterminer();
      CommandLine cli = new CommandLine(plot);
      cli.execute(args);
   }

   @Override
   public void run() {
      System.out.println("Reading " + inputJSON);
      
   }
}
