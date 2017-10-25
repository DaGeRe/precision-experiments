package de.precision.workloads;

import java.util.Random;

public class ReserveRAM {
	
	private final static Random random = new Random();
	
	int [][]ram;
	
	public ReserveRAM(final int size){
		ram = new int[size][];
	}
	
	public void reserveRAM(){
		for (int i = 0; i < ram.length; i++){
			ram[i] = new int[20+random.nextInt(5)];
			for (int j = 0; j < ram[i].length; j++){
				ram[i][j] = random.nextInt();
			}
		}
	}

	public int [][] getInts() {
		return ram;
	}
}
