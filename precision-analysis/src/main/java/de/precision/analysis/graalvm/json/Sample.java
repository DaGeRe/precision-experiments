package de.precision.analysis.graalvm.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Sample(
      @JsonProperty("platform_installation") int platformInstallation,
      @JsonProperty("platform_installation_time") String platformInstallationTime,
      String commit,
      String[] measurements
  ) {}