package de.precision.analysis.graalvm.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Meta {
   @JsonProperty("machine_type")
   private int machineType;
   
   private int configuration;
   
   @JsonProperty("benchmark_workload")
   private int benchmarkWorkload;
   
   @JsonProperty("platform_installation_type")
   private int platformInstallationType;

   public int getMachineType() {
      return machineType;
   }

   public void setMachineType(int machineType) {
      this.machineType = machineType;
   }

   public int getConfiguration() {
      return configuration;
   }

   public void setConfiguration(int configuration) {
      this.configuration = configuration;
   }

   public int getBenchmarkWorkload() {
      return benchmarkWorkload;
   }

   public void setBenchmarkWorkload(int benchmarkWorkload) {
      this.benchmarkWorkload = benchmarkWorkload;
   }

   public int getPlatformInstallationType() {
      return platformInstallationType;
   }

   public void setPlatformInstallationType(int platformInstallationType) {
      this.platformInstallationType = platformInstallationType;
   }
   
   
}