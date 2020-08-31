package de.precision.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;

import de.dagere.kopeme.datacollection.DataCollectorList;
import de.dagere.kopeme.junit3.KoPeMeTestcase;
import de.precision.Constants;

public class TestFileKoPeMe3 extends KoPeMeTestcase {

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
		final ByteArrayOutputStream baout = new ByteArrayOutputStream();
		try {
			MyCopy.copyLarge((Reader) null, baout, "UTF16");
		} catch (final NullPointerException ex) {
		}
	}
}

class MyCopy {
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	public static long copyLarge(final Reader input, final OutputStream output, final String encoding) throws IOException {
		final OutputStreamWriter out = new OutputStreamWriter(output, encoding);
		final char[] buffer = new char[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			out.write(buffer, 0, n);
			count += n;
		}
		return count;
	}
}