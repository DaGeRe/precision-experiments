package de.precision.processing.repetitions.misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.bind.JAXBException;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.inference.GTest;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Longs;

import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Fulldata;
import de.dagere.kopeme.generated.Result.Fulldata.Value;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.peass.measurement.dataloading.MultipleVMTestUtil;
import de.dagere.peass.measurement.statistics.ConfidenceIntervalInterpretion;
import de.dagere.peass.measurement.statistics.Relation;
import de.dagere.peass.measurement.statistics.bimodal.CompareData;
import de.precision.processing.util.PrecisionFolderUtil;
import de.precision.processing.util.RepetitionFolderHandler;

/**
 * Takes a folder with sequence-executions and a precision-level as input. Tells, how many sequence-executions are needed in order to achieve the precision-level.
 * 
 * @author reichelt
 *
 */
public class GenerateStopPlot extends RepetitionFolderHandler {

	public final static File RESULTFOLDER = new File("results/stop/");

	private final static Random RANDOM = new Random();

	private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("00.00");

	private static final String WRONGGREATER = "WRONGGREATER";
	private static final String SELECTED = "SELECTED";
	private static final String FALSENEGATIVE = "FALSEPOSITIVE";
	private static final String TRUEPOSITIVE = "TRUEPOSITIVE";

	private final MethodResult overallResults = new MethodResult();
	private final Map<String, MethodResult> testcaseResults = new HashMap<>();

	private static BufferedWriter precisionRecallWriter;
	private static BufferedWriter standarddeviationWriter;
	private static final Map<String, BufferedWriter> testcaseWriters = new HashMap<>();

	private int vms = 20, executions = 2000;

	private long overhead = 0;
	private long duration = 0;

	static {
		if (!RESULTFOLDER.exists()) {
			RESULTFOLDER.mkdirs();
		}
	}

	private static final Logger LOG = LogManager.getLogger(GenerateStopPlot.class);

	public static void main(final String[] args) throws JAXBException, IOException {
		// System.setOut(new PrintStream(new File("/dev/null")));
		Configurator.setLevel("de.peran.measurement.analysis.statistics.ConfidenceIntervalInterpretion", Level.INFO);

		precisionRecallWriter = new BufferedWriter(new FileWriter(new File(RESULTFOLDER, "precision.csv")));
		standarddeviationWriter = new BufferedWriter(new FileWriter(new File(RESULTFOLDER, "standarddeviation.csv")));
		standarddeviationWriter.write("#Repetitions;Standarddeviation;MinimalDeviationIndex;Mean");
		writeHeader(precisionRecallWriter);
		final File folder = new File(args[0]);

		PrecisionFolderUtil.processFolder(folder, (repetitionFolder) -> new GenerateStopPlot(repetitionFolder));

		precisionRecallWriter.flush();
		System.out.println("'precision.csv' u 1:6 w lines title 'Precision Mean', 'precision.csv' u 1:7 w lines title 'Recall Mean', 'precision.csv' u 1:8 w lines title 'Wrong Mean', ");
		System.out.println("'precision.csv' u 1:9 w lines title 'Precision Confidence', 'precision.csv' u 1:9 title 'Recall Confidence','precision.csv' u 1:10 title 'Wrong Confidence',");
		System.out.println("'precision.csv' u 1:12 w lines title 'Precision TTest', 'precision.csv' u 1:13 w lines title 'Recall TTest','precision.csv' u 1:14 w lines title 'Wrong TTest', ");
		System.out.println("'precision.csv' u 1:15 w lines title 'Precision GTest','precision.csv' u 1:16 w lines title 'Recall GTest', 'precision.csv' u 1:17 w lines title 'Wrong GTest'");

		System.out.println(
				"plot 'precision.csv' u 1:6 w lines title 'Precision Mean', 'precision.csv' u 1:9 w lines title 'Precision Confidence', 'precision.csv' u 1:12 w lines title 'Precision TTest', 'precision.csv' u 1:15 w lines title 'Precision GTest'");
	}

	private static void writeHeader(final BufferedWriter writer) throws IOException {
		writer.write("#repetitions ; vms ; warmup ; overhead ; ");
		for (final String method : new MethodResult().results.keySet()) {
			writer.write(method + ";" + ";" + ";");
		}
		writer.write("\n");
		writer.flush();
	}

	public GenerateStopPlot(final File sequenceFolder) {
		super(sequenceFolder);
	}

	/**
	 * Saves a value for each statistic method that is examined
	 * 
	 * @author reichelt
	 *
	 */
	static class MethodResult {

		final Map<String, Map<String, Integer>> results = new LinkedHashMap<>();

		public MethodResult() {
			results.put("MEAN", new HashMap<>());
			results.put("CONFIDENCE", new HashMap<>());
			results.put("TTEST", new HashMap<>());
			results.put("GTEST", new HashMap<>());
			results.put("COMBINE", new HashMap<>());
			for (Map<String, Integer> entry : results.values()) {
				entry.put(TRUEPOSITIVE, 0);
				entry.put(FALSENEGATIVE, 0);
				entry.put(SELECTED, 0);
				entry.put(WRONGGREATER, 0);
			}
		}

		@Override
		public String toString() {
			String result = "";
			for (final Map<String, Integer> value : results.values()) {
				result += value + ";";
			}
			return result;
		}

		public void increment(final String method, final String type) {
			Map<String, Integer> methodMap = results.get(method);
			final int increment = methodMap.get(type).intValue() + 1;
			methodMap.put(type, increment);
		}
	}

	/**
	 * Calculates the precision of the given sequence-folder
	 * 
	 * @param sequencefolder
	 * @param repetitions
	 * @param precision
	 * @throws JAXBException XML Exceptions should not occur at any time - if this happens, they should be thrown to the top level and handled by the user
	 * @throws IOException
	 */
	@Override
	public void handleVersion() throws JAXBException, IOException {
		for (int myVms = 30; myVms < 40; myVms += 10) {
			vms = myVms;
			overhead = 0;
			duration = 0;
			super.handleVersion();
			precisionRecallWriter.write(repetitions + ";" + vms + ";" + overhead + ";" + duration + ";");
			for (final Map.Entry<String, Map<String, Integer>> methodResult : overallResults.results.entrySet()) {
				writeData(methodResult, precisionRecallWriter);
			}
			precisionRecallWriter.write("\n");
			precisionRecallWriter.flush();

			for (Map.Entry<String, MethodResult> entry : testcaseResults.entrySet()) {
				try {
					BufferedWriter testcaseWriter = testcaseWriters.get(entry.getKey());
					if (testcaseWriter == null) {
						testcaseWriter = new BufferedWriter(new FileWriter(new File(RESULTFOLDER, entry.getKey() + ".csv")));
						testcaseWriters.put(entry.getKey(), testcaseWriter);
						writeHeader(testcaseWriter);
					}
					testcaseWriter.write(repetitions + ";" + vms + ";" + 0 + ";" + overhead + ";" + duration + ";");
					for (final Map.Entry<String, Map<String, Integer>> methodResult : entry.getValue().results.entrySet()) {
						writeData(methodResult, testcaseWriter);
					}
					testcaseWriter.write("\n");
					testcaseWriter.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void writeData(final Map.Entry<String, Map<String, Integer>> methodResult, final BufferedWriter writer) throws IOException {
		int selected = methodResult.getValue().get(SELECTED);
		int truepositive = methodResult.getValue().get(TRUEPOSITIVE);
		int falsenegative = methodResult.getValue().get(FALSENEGATIVE);
		int wronggreater = methodResult.getValue().get(WRONGGREATER);
		final double precision = 100d * ((selected > 0) ? ((double) truepositive) / selected : 0);
		final double recall = 100d * (((double) truepositive) / (truepositive + falsenegative));
		final double wrongGreaterSelectionRate = (selected > 0) ? ((double) wronggreater) / selected : 0;
		writer.write(PERCENT_FORMAT.format(precision) + ";" + PERCENT_FORMAT.format(recall) + ";" + PERCENT_FORMAT.format(wrongGreaterSelectionRate) + ";");
	}

	@Override
	protected void processTestcases(final Testcases testclazz, final Testcases otherPackageTestcase) {
		final TestcaseType before = testclazz.getTestcase().get(0);
		final TestcaseType after = otherPackageTestcase.getTestcase().get(0);

		final long averageOverhead = DetermineAverageTime.getOverhead(after.getDatacollector().get(0).getResult(), before.getDatacollector().get(0).getResult());
		overhead += averageOverhead * vms;

		List<Result> beforeShortened = getShortenedValues(before);
		List<Result> afterShortened = getShortenedValues(after);

		// final int allExecutions = warmup + executions;
		// final int count =
		// before.getDatacollector().get(0).getResult().get(0).getFulldata().getValue().size();
		// final List<Result> beforeShortened = MinimalExecutionDeterminer.shortenValues(before.getDatacollector().get(0).getResult(), warmup, allExecutions);
		// final List<Result> afterShortened = MinimalExecutionDeterminer.shortenValues(after.getDatacollector().get(0).getResult(), warmup, allExecutions);

		LOG.debug("Duration: {}", duration);

		DescriptiveStatistics averageDuration = new DescriptiveStatistics();

		// TODO Konvergenzzeitpunkt bestimmen statt einfach 500 mal..
		for (int i = 0; i < 10; i++) {
			final List<Result> beforeSelected = selectPart(beforeShortened, vms);
			final List<Result> afterSelected = selectPart(afterShortened, vms);

			final double duration2 = DetermineAverageTime.getAverageDuration(beforeSelected, afterSelected);
			averageDuration.addValue(duration2);
			duration += (duration2 * executions);

			executeComparisons(beforeSelected, afterSelected, Relation.LESS_THAN, testclazz.getClazz());

			final List<Result> beforeSecond = selectPart(beforeShortened, vms);

			executeComparisons(beforeSelected, beforeSecond, Relation.EQUAL, testclazz.getClazz());
		}
		LOG.debug("Duration: {} Average duration: {}", duration, averageDuration.getMean());

	}

	private static final int COUNT = 1000;

	private List<Result> getShortenedValues(final TestcaseType before) {
		DescriptiveStatistics minStandardDeviationStatistics = getMinimalDeviation(before);
		double minStandardDeviation = minStandardDeviationStatistics.getStandardDeviation() / minStandardDeviationStatistics.getMean();
		List<Result> beforeShortened = new LinkedList<>();
		DescriptiveStatistics indizes = new DescriptiveStatistics();
		for (Result result : before.getDatacollector().get(0).getResult()) {
			List<Double> last100 = new LinkedList<>();
			int index = 0;
			for (Value v : result.getFulldata().getValue()) {
				last100.add(new Double(v.getValue()));
				index++;
				if (last100.size() > COUNT) {
					last100.remove(0);
					DescriptiveStatistics statistics = new DescriptiveStatistics(Doubles.toArray(last100));
					double relativeStandardDeviation = statistics.getStandardDeviation() / statistics.getMean();
					if (relativeStandardDeviation < minStandardDeviation * 1.5) {
						break;
					}
				}
			}
			DescriptiveStatistics statistics = new DescriptiveStatistics(Doubles.toArray(last100));
			Result r = new Result();
			r.setValue(statistics.getMean());
			r.setFulldata(new Fulldata());
			if (index > result.getFulldata().getValue().size() - executions) {
				index = result.getFulldata().getValue().size() - executions - 1;
			}
			indizes.addValue(index);
			for (int i = index; i < index + executions; i++) {
				Value value = result.getFulldata().getValue().get(i);
				r.getFulldata().getValue().add(value);
			}
			beforeShortened.add(r);
		}
		try {
			standarddeviationWriter.write(repetitions + ";" + minStandardDeviation + ";" + indizes.getMean() + ";" + minStandardDeviationStatistics.getMean() + "\n");
			standarddeviationWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return beforeShortened;
	}

	private DescriptiveStatistics getMinimalDeviation(final TestcaseType before) {
		DescriptiveStatistics minDeviationStatistics = null;
		for (Result result : before.getDatacollector().get(0).getResult()) {
			List<Double> last100 = new LinkedList<>();
			for (Value v : result.getFulldata().getValue()) {
				last100.add(new Double(v.getValue()));
				if (last100.size() > COUNT) {
					last100.remove(0);
					DescriptiveStatistics currentStatistcs = new DescriptiveStatistics(Doubles.toArray(last100));
					double relativeStandardDeviation = currentStatistcs.getStandardDeviation() / currentStatistcs.getMean();
					if (minDeviationStatistics == null || relativeStandardDeviation < minDeviationStatistics.getStandardDeviation() / minDeviationStatistics.getMean()) {
						minDeviationStatistics = currentStatistcs;
					}
				}
			}
		}
		return minDeviationStatistics;
	}

	private void executeComparisons(final List<Result> beforeShortened, final List<Result> afterShortened, final Relation expectedRelation, final String test) {
		final Map<String, Relation> relations = new LinkedHashMap<>();

		List<double[]> values = new LinkedList<>();
		values.add(new double[beforeShortened.size()]);
		values.add(new double[afterShortened.size()]);
		for (int i = 0; i < beforeShortened.size(); i++) {
			values.get(0)[i] = beforeShortened.get(i).getValue();
			values.get(1)[i] = afterShortened.get(i).getValue();
		}

		double avgBefore = MultipleVMTestUtil.getStatistic(beforeShortened).getMean();
		double avgAfter = MultipleVMTestUtil.getStatistic(afterShortened).getMean();

		if (avgBefore < avgAfter * 0.99) {
			relations.put("MEAN", Relation.LESS_THAN);
		} else {
			relations.put("MEAN", avgBefore < avgAfter ? Relation.EQUAL : Relation.GREATER_THAN);
		}

		boolean tchange = new TTest().homoscedasticTTest(values.get(0), values.get(1), 0.01);
		if (tchange) {
			relations.put("TTEST", avgBefore < avgAfter ? Relation.LESS_THAN : Relation.GREATER_THAN);
		} else {
			relations.put("TTEST", Relation.EQUAL);
		}

		long[][] histogramValues = createCommonHistogram(beforeShortened, afterShortened);

		double[] histExpected = Doubles.toArray(Longs.asList(histogramValues[0]));
		boolean gchange = new GTest().gTest(histExpected, histogramValues[1], 0.01);
		if (gchange) {
			relations.put("GTEST", avgBefore < avgAfter ? Relation.LESS_THAN : Relation.GREATER_THAN);
		} else {
			relations.put("GTEST", Relation.EQUAL);
		}

		relations.put("COMBINE", tchange || gchange ? Relation.LESS_THAN : Relation.EQUAL);

		CompareData data = new CompareData(afterShortened, afterShortened);
		final Relation confidence = ConfidenceIntervalInterpretion.compare(data) == Relation.EQUAL
				? Relation.EQUAL : Relation.LESS_THAN;
		relations.put("CONFIDENCE", confidence);
		LOG.trace("Confidence: " + confidence);

		MethodResult myMethodResult = testcaseResults.get(test);
		if (myMethodResult == null) {
			myMethodResult = new MethodResult();
			testcaseResults.put(test, myMethodResult);
		}

		for (final Map.Entry<String, Relation> relationByMethod : relations.entrySet()) {
			// System.out.println("Relation: " + relation.getValue() + "
			// Expected: " + );
			if (relationByMethod.getValue() == Relation.LESS_THAN) {
				overallResults.increment(relationByMethod.getKey(), SELECTED);
				myMethodResult.increment(relationByMethod.getKey(), SELECTED);
				// selected.increment(relationByMethod.getKey());
				if (Relation.LESS_THAN == expectedRelation) {
					overallResults.increment(relationByMethod.getKey(), TRUEPOSITIVE);
					myMethodResult.increment(relationByMethod.getKey(), TRUEPOSITIVE);
					// truePositives.increment(relationByMethod.getKey());
				} else {
				}
			} else {
				if (Relation.LESS_THAN == expectedRelation) {
					overallResults.increment(relationByMethod.getKey(), FALSENEGATIVE);
					myMethodResult.increment(relationByMethod.getKey(), FALSENEGATIVE);
					// falseNegative.increment(relationByMethod.getKey());
				}
			}
			if (relationByMethod.getValue() == Relation.GREATER_THAN) {
				overallResults.increment(relationByMethod.getKey(), WRONGGREATER);
				myMethodResult.increment(relationByMethod.getKey(), WRONGGREATER);
				// wrongGreater.increment(relationByMethod.getKey());
			}
		}
	}

	private static final int SIZE = 10;

	private static long[][] createCommonHistogram(final List<Result> beforeShortened, final List<Result> afterShortened) {
		DescriptiveStatistics stat = new DescriptiveStatistics();
		DescriptiveStatistics before = new DescriptiveStatistics();
		DescriptiveStatistics after = new DescriptiveStatistics();

		for (Result result : beforeShortened) {
			for (Value value : result.getFulldata().getValue()) {
				long val = value.getValue();
				stat.addValue(val);
				before.addValue(val);
			}
		}
		for (Result result : afterShortened) {
			for (Value value : result.getFulldata().getValue()) {
				long val = value.getValue();
				stat.addValue(val);
				after.addValue(val);
			}
		}

		final long min = (long) stat.getMin();
		final long max = (long) stat.getPercentile(95);
		long stepSize = 1 + ((max - min) / (SIZE - 1));

		final long histogramValues[][] = new long[2][SIZE];

		insertValues(before, min, stepSize, histogramValues[0]);
		insertValues(after, min, stepSize, histogramValues[1]);

		return histogramValues;
	}

	private static void insertValues(final DescriptiveStatistics before, final long min, final long stepSize, final long[] histogramValues) {
		long currentMax = min + stepSize;
		int count = 1, index = 0;
		for (double val : before.getSortedValues()) {
			if (val < currentMax) {
				count++;
			} else {
				histogramValues[index] = count;
				index++;
				count = 0;
				currentMax = min + stepSize * (index + 1);
				if (index >= histogramValues.length) {
					break;
				}
			}
		}
		for (int i = 0; i < histogramValues.length; i++) {
			if (histogramValues[i] == 0) {
				histogramValues[i] = 1;
			}
		}
		// System.out.println(Arrays.toString(histogramValues));
	}

	private static List<Result> selectPart(final List<Result> beforeShortened, final int vms) {
		final List<Result> beforeSelected = new LinkedList<>();
		for (int insertion = 0; insertion < vms; insertion++) {

			final Result randomlyPickedResult = beforeShortened.get(RANDOM.nextInt(beforeShortened.size()));
			beforeSelected.add(randomlyPickedResult);
		}
		return beforeSelected;
	}
}
