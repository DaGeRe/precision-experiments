package de.precision.mediumsize;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class WorkloadUtil {
	private static final int size = 100;

	private static final Random r = new Random(0);

	public static final List<Runnable> workloads = new LinkedList<>();

	static {
		final Runnable memoryRunnable = new Runnable() {
			@Override
			public void run() {
				final int[][] reservedMemory = new int[size][];
				for (int i = 0; i < size; i++) {
					reservedMemory[i] = new int[size];
					for (int j = 0; j < size; j++) {
						reservedMemory[i][j] = r.nextInt(1000000);
					}
				}
				System.out.println(reservedMemory[r.nextInt(size)][r.nextInt(size)]);
			}
		};
		workloads.add(memoryRunnable);

		final Runnable sysoutWorkload = new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < size; i++) {
					System.out.println(r.nextInt());
				}
			}
		};
		workloads.add(sysoutWorkload);

		final Runnable calculationRunnable = new Runnable() {
			@Override
			public void run() {
				int value = 0;
				for (int j = 0; j < size; j++) {
					value += r.nextInt(1000000);
				}
				System.out.println(value);
			}
		};
		workloads.add(calculationRunnable);

		final Runnable fileWorkload = new Runnable() {
			@Override
			public void run() {
				try {
					final File testFile = File.createTempFile("precision", ".dat");
					final FileOutputStream stream = new FileOutputStream(testFile);
					try (final FileWriter writer = new FileWriter(stream.getFD())){
						for (int j = 0; j < size; j++) {
							writer.write("Number: " + r.nextInt());
						}
					}
					stream.flush();
					stream.getFD().sync();
					stream.close();
					testFile.deleteOnExit();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		};
		workloads.add(fileWorkload);

	}
}
