package de.precision.complex;

import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;
import de.precision.Constants;

@PerformanceTestingClass(logFullData = true, overallTimeout = 0)
@RunWith(PerformanceTestRunnerJUnit.class)
public class ComplexTest1 {
	
	private static final Random random = new Random(0);
	private int localVar = 0;

	@PerformanceTest(warmup = 0, iterations = Constants.EXECUTIONS,  logFullData = true, timeout = 0, dataCollectors = "ONLYTIME")
	@Test
	public void complexStuff(){
		int val1 = addInts();
		long val2 = addLongs();
		doSomeSynchronizedStuff();
		System.out.println(val1 + " " + val2 + " " + localVar);
	}
	
	
	
	private int addInts(){
		int sum = 0;
		for (int i = 0; i < 11; i++){
			sum+=random.nextInt();
		}
		return sum;
	}
	
	private long addLongs(){
		long sum = 0;
		for (int i = 0; i < 10; i++){
			sum+=addInts();
		}
		return sum;
	}
	
	private void doSomeSynchronizedStuff(){
		for (int i = 0; i < 10; i++){
			synchronizedStuff();
		}
	}
	
	private synchronized void synchronizedStuff(){
		localVar+=random.nextInt(100);
	}
}
