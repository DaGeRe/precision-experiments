package de.precision.analysis.graalvm.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CompareResults(
      String column,
      @JsonProperty("p_value") double pValue,
      @JsonProperty("effect_size") double effectSize,
      boolean regression,
      String overview
  ) {}