package de.precision.workloads;

import java.util.Random;

/**
 * Reserves RAM and fills it for benchmarking
 * 
 * @author reichelt
 *
 */
public class ReserveRAM {

	private static final Random RANDOM = new Random();

	int[][] ram;

	public ReserveRAM(final int size) {
		ram = new int[size][];
	}

	public void reserveRAM() {
		for (int i = 0; i < ram.length; i++) {
			ram[i] = new int[3 + RANDOM.nextInt(3)];
			for (int j = 0; j < ram[i].length; j++) {
				ram[i][j] = RANDOM.nextInt();
			}
		}
	}

	public int[][] getInts() {
		return ram;
	}
}
