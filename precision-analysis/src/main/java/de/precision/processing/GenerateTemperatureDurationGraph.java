package de.precision.processing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import de.dagere.kopeme.datastorage.JSONDataLoader;
import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.MeasuredValue;
import de.dagere.kopeme.kopemedata.TestMethod;
import de.dagere.kopeme.kopemedata.VMResult;
import de.dagere.peass.measurement.statistics.StatisticUtil;

/**
 * Plots a graph of execution time and cpu temperature. Unfortunately, no correlation could be detected.
 * 
 * @author reichelt
 *
 */
public class GenerateTemperatureDurationGraph {
	public static final File RESULTFOLDER = new File("results/tempvalue/");

	static {
		if (!RESULTFOLDER.exists()) {
			RESULTFOLDER.mkdirs();
		}
	}

	public static void main(final String[] args) throws IOException {
		final File temperatureFile = new File(args[0]);

		for (int arg = 1; arg < args.length; arg++) {
			final File timeValueFile = new File(args[arg]);
			// final Map<Integer, Integer> temperatureMap = readTemperature(temperatureFile);
			//
			// final Testcases testcases = new XMLDataLoader(timeValueFile).getFullData().getTestcases();
			// final TestcaseType testcase = testcases.getTestcase().get(0);
			// final List<Result> results = testcase.getDatacollector().get(0).getResult();

			final GenerateTemperatureDurationGraph corelator = new GenerateTemperatureDurationGraph(timeValueFile, temperatureFile);
			corelator.analyse();

			// final File indexCorrelationFile = new File(RESULTFOLDER, "indexcorrelation_" + testcases.getClazz() + "_" + testcase.getName() + ".csv");

		}
	}

	private final File indexCorrelationFile;
	private final Map<Integer, Integer> temperatureMap;
	private final List<VMResult> results;

	public GenerateTemperatureDurationGraph(final File timeValueFile, final File temperatureFile) throws FileNotFoundException, IOException {
		temperatureMap = readTemperature(temperatureFile);

		final Kopemedata testcases = new JSONDataLoader(timeValueFile).getFullData();
		final TestMethod testcase = testcases.getMethods().get(0);
		results = StatisticUtil.shortenValues(testcase.getDatacollectorResults().get(0).getResults(), 10000, 20000);
		// results = testcase.getDatacollector().get(0).getResult();

		indexCorrelationFile = new File(RESULTFOLDER, "indexcorrelation_" + testcases.getClazz() + "_" + testcase.getMethod() + ".csv");
	}

	public void analyse() {
		try (final BufferedWriter indexWriter = new BufferedWriter(new FileWriter(indexCorrelationFile))) {
			for (int i = 0; i < results.size(); i++) {
				final DescriptiveStatistics overallMean = new DescriptiveStatistics();
				final DescriptiveStatistics meanTemp = new DescriptiveStatistics();

				final long unusedSecond = results.get(i).getFulldata().getValues().get(0).getStartTime() / 1000;
				cleanTemperature(unusedSecond);

				handleIndex(i, overallMean, meanTemp);

				System.out.println(meanTemp.getMean() + " " + temperatureMap.size());
				indexWriter.write(i + ";" + overallMean.getMean() + ";" + overallMean.getStandardDeviation() + ";" + meanTemp.getMean() + ";" + meanTemp.getStandardDeviation() + "\n");
				indexWriter.flush();
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void cleanTemperature(final long unusedSecond) {
		for (final Iterator<Map.Entry<Integer, Integer>> tempIterator = temperatureMap.entrySet().iterator(); tempIterator.hasNext();) {
			final Map.Entry<Integer, Integer> tempEntry = tempIterator.next();
			if (tempEntry.getKey() < unusedSecond) {
				tempIterator.remove();
			}
		}
	}

	private void handleIndex(final int i, final DescriptiveStatistics overallMean, final DescriptiveStatistics meanTemp)
			throws IOException {
		final File csvFile = new File(RESULTFOLDER, "tempValue_" + i + ".csv");
		try (final BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
			final DescriptiveStatistics statistics = new DescriptiveStatistics();
			final DescriptiveStatistics temp = new DescriptiveStatistics();
			for (final MeasuredValue value : results.get(i).getFulldata().getValues()) {
				handleValue(overallMean, meanTemp, writer, statistics, temp, value);
			}

			writer.flush();
			System.out.println("plot 'tempValue_" + i + ".csv', 'tempValue_" + i + ".csv' u 1:3");
		}
	}

	private void handleValue(final DescriptiveStatistics overallMean, final DescriptiveStatistics meanTemp, final BufferedWriter writer,
			final DescriptiveStatistics statistics, final DescriptiveStatistics temp, final MeasuredValue value) throws IOException {
		final double currentValue = value.getValue();
		statistics.addValue(currentValue);
		overallMean.addValue(currentValue);

		final int second = (int) (value.getStartTime() / 1000);
		// writer.write(second + ";" + value.getValue());
		final int foundTemp = findTemperature(temperatureMap, second);
		if (foundTemp != -1) {
			temp.addValue(foundTemp);
			meanTemp.addValue(foundTemp);
		}

		if (statistics.getValues().length > 10) {
			writer.write(second + ";" + statistics.getMean() + ";" + temp.getMean() + "\n");
		}
		// writer.write("\n");
	}

	private static int findTemperature(final Map<Integer, Integer> temperatureMap, final int second) {
		int tempBefore = 0;
		int foundTemp = -1;
		for (final Map.Entry<Integer, Integer> tempEntry : temperatureMap.entrySet()) {
			if (second > tempBefore && second < tempEntry.getKey()) {
				foundTemp = tempEntry.getValue();
				break;
			}

			tempBefore = tempEntry.getValue();
		}
		return foundTemp;
	}

	private static Map<Integer, Integer> readTemperature(final File temperatureFile) throws IOException, FileNotFoundException {
		final Map<Integer, Integer> temperatureMap;
		try (final BufferedReader temperatureReader = new BufferedReader(new FileReader(temperatureFile))) {
			temperatureMap = new LinkedHashMap<>();
			String line = null;
			while ((line = temperatureReader.readLine()) != null) {
				final String[] parts = line.split(";");
				final int time = Integer.parseInt(parts[0]);
				final int temperature = Integer.parseInt(parts[1]);
				temperatureMap.put(time, temperature);
			}
		}
		return temperatureMap;
	}
}
