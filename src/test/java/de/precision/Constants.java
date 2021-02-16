package de.precision;

public class Constants {
	public static final int EXECUTIONS = 10000000;
	public static final boolean REDIRECT = true;
	public static final int REPETITIONS = 1;
   public static final int WORKLOADSIZE = System.getenv().containsKey("workloadsize") ? Integer.parseInt(System.getenv().get("workloadsize")) : 10;
   public static final int THROW_TEST_RATIO = 50;
}
