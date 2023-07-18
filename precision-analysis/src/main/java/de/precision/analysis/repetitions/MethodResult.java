package de.precision.analysis.repetitions;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Saves a value for each statistic method that is examined
 * 
 * @author reichelt
 *
 */
public class MethodResult {
	
	private final Map<StatisticalTests, Map<StatisticalTestResult, Integer>> results = new LinkedHashMap<>();

	public MethodResult(StatisticalTests[] types) {
		for (final StatisticalTests type : types){
			getResults().put(type, new HashMap<>());
		}
		for (final Map<StatisticalTestResult, Integer> entry : getResults().values()) {
			entry.put(StatisticalTestResult.TRUEPOSITIVE, 0);
			entry.put(StatisticalTestResult.FALSENEGATIVE, 0);
			entry.put(StatisticalTestResult.TRUENEGATIVE, 0);
			entry.put(StatisticalTestResult.SELECTED, 0);
			entry.put(StatisticalTestResult.WRONGGREATER, 0);
			entry.put(StatisticalTestResult.FALSENEGATIVE_ABOVE_1_PERCENT, 0);
		}
	}

	@Override
	public String toString() {
		String result = "";
		for (final Map<StatisticalTestResult, Integer> value : getResults().values()) {
			result += value + ";";
		}
		return result;
	}

	public void increment(final StatisticalTests method, StatisticalTestResult type) {
		final Map<StatisticalTestResult, Integer> methodMap = getResults().get(method);
		final int increment = methodMap.get(type).intValue() + 1;
		methodMap.put(type, increment);
	}

   public Map<StatisticalTests, Map<StatisticalTestResult, Integer>> getResults() {
      return results;
   }
}