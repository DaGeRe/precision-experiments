package de.precision.file;

import java.io.IOException;

import de.dagere.kopeme.datacollection.DataCollectorList;
import de.dagere.kopeme.junit3.KoPeMeTestcase;
import de.precision.Constants;

public class OtherTest extends KoPeMeTestcase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected int getWarmupExecutions() {
		return 0;
	}

	@Override
	protected int getExecutionTimes() {
		return Constants.EXECUTIONS;
	}

	@Override
	protected int getRepetitions() {
		return 100;
	}

	@Override
	protected DataCollectorList getDataCollectors() {
		return DataCollectorList.ONLYTIME;
	}

	public void testAdd() throws IOException {
//	   byte[] data = new data[]
		final Stuff stuff = new Stuff(32);
		try {
			ReserveAndThrow.copyLarge((Stuff) null, stuff, "UTF16");
		} catch (final NullPointerException ex) {
		}
	}
}

class Stuff {
	byte[] data;

	public Stuff(final int size) {
		data = new byte[size];
	}
	
	public int read(final char[] myBuffer){
		return -1;
	}

	public void write(final char[] buffer) {
		data[0] = (byte) buffer[0];
	}
}

class ReserveAndThrow {
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	public static long copyLarge(final Stuff stuff, final Stuff stuff2, final String encoding) throws IOException {
		final char[] buffer = new char[DEFAULT_BUFFER_SIZE];
		stuff.read(buffer);
		return 0;
	}
}
