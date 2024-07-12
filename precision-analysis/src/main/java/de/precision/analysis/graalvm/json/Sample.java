package de.precision.analysis.graalvm.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sample {
   @JsonProperty("platform_installation")
   private int platformInstallation;
   
   @JsonProperty("platform_installation_time")
   private String platformInstallationTime;
   
   private String commit;
   
   private String[] measurements;

   public int getPlatformInstallation() {
      return platformInstallation;
   }

   public void setPlatformInstallation(int platformInstallation) {
      this.platformInstallation = platformInstallation;
   }

   public String getPlatformInstallationTime() {
      return platformInstallationTime;
   }

   public void setPlatformInstallationTime(String platformInstallationTime) {
      this.platformInstallationTime = platformInstallationTime;
   }

   public String getCommit() {
      return commit;
   }

   public void setCommit(String commit) {
      this.commit = commit;
   }

   public String[] getMeasurements() {
      return measurements;
   }

   public void setMeasurements(String[] measurements) {
      this.measurements = measurements;
   }
   
   
}