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
	
	public static final String WRONGGREATER = "WRONGGREATER";
	public static final String SELECTED = "SELECTED";
	public static final String FALSENEGATIVE = "FALSENEGATIVE";
	public static final String TRUEPOSITIVE = "TRUEPOSITIVE";

	private final Map<String, Map<String, Integer>> results = new LinkedHashMap<>();

	public MethodResult(String[] types) {
		for (final String type : types){
			getResults().put(type, new HashMap<>());
		}
//		results.put("MEAN", new HashMap<>());
//		results.put("TTEST", new HashMap<>());
		for (final Map<String, Integer> entry : getResults().values()) {
			entry.put(TRUEPOSITIVE, 0);
			entry.put(FALSENEGATIVE, 0);
			entry.put(SELECTED, 0);
			entry.put(WRONGGREATER, 0);
		}
	}

	@Override
	public String toString() {
		String result = "";
		for (final Map<String, Integer> value : getResults().values()) {
			result += value + ";";
		}
		return result;
	}

	public void increment(final String method, String type) {
		final Map<String, Integer> methodMap = getResults().get(method);
		final int increment = methodMap.get(type).intValue() + 1;
		methodMap.put(type, increment);
	}

   public Map<String, Map<String, Integer>> getResults() {
      return results;
   }
}