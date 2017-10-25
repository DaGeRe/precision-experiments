package de.precision.workloads;

public class ThrowSomething {
	public int returnMe(int whatToDo){
		if (whatToDo == 1){
			return 1;
		}
		throw new RuntimeException("Give me one!");
	}
}
