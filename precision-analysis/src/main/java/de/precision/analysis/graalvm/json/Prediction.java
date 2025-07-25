package de.precision.analysis.graalvm.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Prediction(
      @JsonProperty("used_runs_for_old_sample") int usedRunsForOldSample,
      @JsonProperty("used_runs_for_new_sample") int usedRunsForNewSample,
      @JsonProperty("used_iterations_for_old_sample") int usedIterationsForOldSample,
      @JsonProperty("used_iterations_for_new_sample") int usedIterationsForNewSample
  ) {}