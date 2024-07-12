package de.precision.analysis.graalvm.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GraalVMJSONData(
      @JsonProperty("machine_type") int machineType,
      int configuration,
      @JsonProperty("benchmark_workload") int benchmarkWorkload,
      @JsonProperty("platform_installation_type") int platformInstallationType,
      Pair[] pairs
  ) {}
