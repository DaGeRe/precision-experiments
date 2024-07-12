package de.precision.analysis.graalvm.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Pair(
      @JsonProperty("old_sample") Sample oldSample,
      @JsonProperty("new_sample") Sample newSample,
      @JsonProperty("compare_results") CompareResults compareResults,
      Prediction prediction
  ) {}