package de.precision.processing.repetitions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
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
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Result;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Result.Fulldata.Value;
import de.peran.analysis.helper.MinimalExecutionDeterminer;
import de.peran.measurement.analysis.AnalyseFullData;
import de.peran.measurement.analysis.statistics.ANOVATest;
import de.peran.measurement.analysis.statistics.ConfidenceIntervalInterpretion;
import de.peran.measurement.analysis.statistics.Relation;
import de.precision.processing.util.RepetitionFolderHandler;
import de.precision.processing.util.Util;

/**
 * Takes a folder with sequence-executions and a precision-level as input.
 * Tells, how many sequence-executions are needed in order to achieve the
 * precision-level.
 * 
 * @author reichelt
 *
 */
public class GeneratePrecisionPlot extends RepetitionFolderHandler {

	public final static File RESULTFOLDER = new File("results/precision/");

	static {
		if (!RESULTFOLDER.exists()) {
			RESULTFOLDER.mkdirs();
		}
	}

	private static final Logger LOG = LogManager.getLogger(AnalyseFullData.class);

	public static void main(final String[] args) throws JAXBException, IOException {
		// System.setOut(new PrintStream(new File("/dev/null")));
		Configurator.setLevel("de.peran.measurement.analysis.statistics.ANOVATest", Level.INFO);
		Configurator.setLevel("de.peran.measurement.analysis.statistics.ConfidenceIntervalInterpretion", Level.INFO);

		precisionRecallWriter = new BufferedWriter(new FileWriter(new File(RESULTFOLDER, "precision.csv")));
		precisionRecallWriter.write("#repetitions ; vms ; warmup ; overhead ; duration ;");
		for (final String method : new MethodResult().results.keySet()) {
			precisionRecallWriter.write(method + ";");
		}
		precisionRecallWriter.write("\n");

		precisionRecallWriter.flush();
		final File folder = new File(args[0]);

		Util.processFolder(folder, (repetitionFolder) -> new GeneratePrecisionPlot(repetitionFolder));

		precisionRecallWriter.flush();
		System.out.println(
				"'precision.csv' u 1:6 w lines title 'Precision ANOVA', 'precision.csv' u 1:7 w lines title 'Recall ANOVA', 'precision.csv' u 1:8 w lines title 'Wrong ANOVA', ");
		System.out.println(
				"'precision.csv' u 1:9 w lines title 'Precision Confidence', 'precision.csv' u 1:9 title 'Recall Confidence','precision.csv' u 1:10 title 'Wrong Confidence',");
		System.out.println(
				"'precision.csv' u 1:12 w lines title 'Precision Mann', 'precision.csv' u 1:13 w lines title 'Recall Mann','precision.csv' u 1:14 w lines title 'Wrong Mann', ");
		System.out.println(
				"'precision.csv' u 1:15 w lines title 'Precision Welch','precision.csv' u 1:16 w lines title 'Recall Welch', 'precision.csv' u 1:17 w lines title 'Wrong Welch'");

		System.out.println(
				"plot 'precision.csv' u 1:6 w lines title 'Precision ANOVA', 'precision.csv' u 1:9 w lines title 'Precision Confidence', 'precision.csv' u 1:12 w lines title 'Precision Mann', 'precision.csv' u 1:15 w lines title 'Precision Welch'");
	}

	public GeneratePrecisionPlot(final File sequenceFolder) {
		super(sequenceFolder);
	}

	final static Random random = new Random();

	int overallcount;

	private final MethodResult truePositives = new MethodResult();
	private final MethodResult falseNegative = new MethodResult();
	private final MethodResult selected = new MethodResult();
	private final MethodResult wrongGreater = new MethodResult();

	private static BufferedWriter precisionRecallWriter;

	static class MethodResult {
		Map<String, Integer> results = new LinkedHashMap<>();

		public MethodResult() {
			// results.put("ANOVA", 0);
			results.put("MEAN", 0);
			results.put("CONFIDENCE", 0);
			// results.put("MANN", 0);
			results.put("TTEST", 0);
			results.put("CHI", 0);
			results.put("COMBINE", 0);
			// results.put("WANOVA", 0);
		}

		@Override
		public String toString() {
			String result = "";
			for (final Integer value : results.values()) {
				result += value + ";";
			}
			return result;
		}

		public void increment(final String key) {
			final int increment = results.get(key).intValue() + 1;
			results.put(key, increment);
		}
	}

	private int vms = 20, warmup = 1000, executions = 1000;

	private long overhead = 0;
	private long duration = 0;

	private DecimalFormat df = new DecimalFormat("##.##");

	/**
	 * Calculates the precision of the given sequence-folder
	 * 
	 * @param sequencefolder
	 * @param repetitions
	 * @param precision
	 * @throws JAXBException
	 *             XML Exceptions should not occur at any time - if this
	 *             happens, they should be thrown to the top level and handled
	 *             by the user
	 * @throws IOException
	 */
	@Override
	public void handleVersion() throws JAXBException, IOException {

		warmup = 2000;
		executions = 2000;
		for (int myVms = 2; myVms < 50; myVms += 2) {
			vms = myVms;
			overhead = 0;
			duration = 0;
			super.handleVersion();
			precisionRecallWriter.write(repetitions + ";" + vms + ";" + warmup + ";" + overhead + ";" + duration + ";");
			for (final String method : truePositives.results.keySet()) {
				final double precision = (selected.results.get(method) > 0) ? ((double) truePositives.results.get(method)) / selected.results.get(method) : 0;
				final double recall = ((double) truePositives.results.get(method)) / (truePositives.results.get(method) + falseNegative.results.get(method));
				final double wrongGreaterSelectionRate = (selected.results.get(method) > 0) ? ((double) wrongGreater.results.get(method)) / selected.results.get(method) : 0;
				precisionRecallWriter.write(df.format(precision) + ";" + df.format(recall) + ";" + df.format(wrongGreaterSelectionRate) + ";");
			}
			precisionRecallWriter.write("\n");
			precisionRecallWriter.flush();
			LOG.info("Results: " + truePositives.toString());
		}

		vms = 20;
		for (int myWarmup = 500; myWarmup < 5000; myWarmup += 500) {
			warmup = 1000;
			overhead = 0;
			duration = 0;
			super.handleVersion();
			precisionRecallWriter.write(repetitions + ";" + vms + ";" + warmup + ";" + overhead + ";" + duration + ";");
			for (final String method : truePositives.results.keySet()) {
				final double precision = (selected.results.get(method) > 0) ? ((double) truePositives.results.get(method)) / selected.results.get(method) : 0;
				final double recall = ((double) truePositives.results.get(method)) / (truePositives.results.get(method) + falseNegative.results.get(method));
				final double wrongGreaterSelectionRate = (selected.results.get(method) > 0) ? ((double) wrongGreater.results.get(method)) / selected.results.get(method) : 0;
				precisionRecallWriter.write(df.format(precision) + ";" + df.format(recall) + ";" + df.format(wrongGreaterSelectionRate) + ";");
			}
			precisionRecallWriter.write("\n");
			precisionRecallWriter.flush();
			LOG.info("Results: " + truePositives.toString());
		}

		warmup = 1000;
		for (int myExecutions = 100; myExecutions < 3000; myExecutions += 100) {
			executions = myExecutions;
			overhead = 0;
			duration = 0;
			super.handleVersion();
			precisionRecallWriter.write(repetitions + ";" + vms + ";" + warmup + ";" + overhead + ";" + duration + ";");
			for (final String method : truePositives.results.keySet()) {
				final double precision = (selected.results.get(method) > 0) ? ((double) truePositives.results.get(method)) / selected.results.get(method) : 0;
				final double recall = ((double) truePositives.results.get(method)) / (truePositives.results.get(method) + falseNegative.results.get(method));
				final double wrongGreaterSelectionRate = (selected.results.get(method) > 0) ? ((double) wrongGreater.results.get(method)) / selected.results.get(method) : 0;
				precisionRecallWriter.write(df.format(precision) + ";" + df.format(recall) + ";" + df.format(wrongGreaterSelectionRate) + ";");
			}
			precisionRecallWriter.write("\n");
			precisionRecallWriter.flush();
			LOG.info("Results: " + truePositives.toString());
		}
	}

	@Override
	protected void processTestcases(final Testcases testclazz, final Testcases otherPackageTestcase) {
		final TestcaseType before = testclazz.getTestcase().get(0);
		final TestcaseType after = otherPackageTestcase.getTestcase().get(0);

		final long averageOverhead = DetermineAverageTime.getOverhead(after.getDatacollector().get(0).getResult(), before.getDatacollector().get(0).getResult());
		overhead += averageOverhead * vms;

		final int allExecutions = warmup + executions;
		// final int count =
		// before.getDatacollector().get(0).getResult().get(0).getFulldata().getValue().size();
		final List<Result> beforeShortened = MinimalExecutionDeterminer.shortenValues(before.getDatacollector().get(0).getResult(), warmup, allExecutions);
		final List<Result> afterShortened = MinimalExecutionDeterminer.shortenValues(after.getDatacollector().get(0).getResult(), warmup, allExecutions);

		LOG.debug("Duration: {}", duration);

		DescriptiveStatistics averageDuration = new DescriptiveStatistics();

		for (int i = 0; i < 500; i++) {// TODO Konvergenzzeitpunkt bestimmen,
										// statt einfach 100 mal..
			final List<Result> beforeSelected = selectPart(beforeShortened, vms);
			final List<Result> afterSelected = selectPart(afterShortened, vms);

			final double duration2 = DetermineAverageTime.getAverageDuration(beforeSelected, afterSelected);
			averageDuration.addValue(duration2);
			duration += (duration2 * executions);

			executeComparisons(beforeSelected, afterSelected, Relation.LESS_THAN, testclazz.getClazz());

			final List<Result> beforeSecond = selectPart(beforeShortened, vms);

			executeComparisons(beforeSelected, beforeSecond, Relation.EQUAL, testclazz.getClazz());

			overallcount++;
		}
		LOG.debug("Duration: {} Average duration: {}", duration, averageDuration.getMean());

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

		double avgBefore = ANOVATest.getStatistic(beforeShortened).getMean();
		double avgAfter = ANOVATest.getStatistic(afterShortened).getMean();

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
			relations.put("CHI", avgBefore < avgAfter ? Relation.LESS_THAN : Relation.GREATER_THAN);
		} else {
			relations.put("CHI", Relation.EQUAL);
		}

		relations.put("COMBINE", tchange || gchange ? Relation.LESS_THAN : Relation.EQUAL);

		final Relation confidence = ConfidenceIntervalInterpretion.compare(afterShortened, afterShortened) == de.peran.measurement.analysis.statistics.Relation.EQUAL
				? Relation.EQUAL : Relation.LESS_THAN;
		relations.put("CONFIDENCE", confidence);
		LOG.trace("Confidence: " + confidence);

		for (final Map.Entry<String, Relation> relationByMethod : relations.entrySet()) {
			// System.out.println("Relation: " + relation.getValue() + "
			// Expected: " + );
			if (relationByMethod.getValue() == Relation.LESS_THAN) {
				selected.increment(relationByMethod.getKey());
				if (Relation.LESS_THAN == expectedRelation) {
					truePositives.increment(relationByMethod.getKey());
				} else {
				}
			} else {
				if (Relation.LESS_THAN == expectedRelation) {
					falseNegative.increment(relationByMethod.getKey());
				}
			}
			if (relationByMethod.getValue() == Relation.GREATER_THAN) {
				wrongGreater.increment(relationByMethod.getKey());
			}
		}
	}

	private static final int SIZE = 10;

	public static long[][] createCommonHistogram(final List<Result> beforeShortened, final List<Result> afterShortened) {
		DescriptiveStatistics stat = new DescriptiveStatistics();
		DescriptiveStatistics before = new DescriptiveStatistics();
		DescriptiveStatistics after = new DescriptiveStatistics();

		for (Result result : beforeShortened) {
			for (Value value : result.getFulldata().getValue()) {
				long val = Long.parseLong(value.getValue());
				stat.addValue(val);
				before.addValue(val);
			}
		}
		for (Result result : afterShortened) {
			for (Value value : result.getFulldata().getValue()) {
				long val = Long.parseLong(value.getValue());
				stat.addValue(val);
				after.addValue(val);
			}
		}

		final long min = (long) stat.getMin();
		final long max = (long) stat.getPercentile(95);
		long stepSize = (long) (1 + ((max - min) / (SIZE - 1)));

		final long histogramValues[][] = new long[2][SIZE];

		insertValues(before, min, stepSize, histogramValues[0]);
		insertValues(after, min, stepSize, histogramValues[1]);

		return histogramValues;
	}

	private static void insertValues(DescriptiveStatistics before, final long min, long stepSize, final long[] histogramValues) {
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

	private static DescriptiveStatistics getStatistics(final List<Result> beforeShortened, final List<Result> afterShortened) {
		DescriptiveStatistics stat = new DescriptiveStatistics();

		for (Result result : beforeShortened) {
			for (Value value : result.getFulldata().getValue()) {
				long val = Long.parseLong(value.getValue());
				stat.addValue(val);
			}
		}
		for (Result result : afterShortened) {
			for (Value value : result.getFulldata().getValue()) {
				long val = Long.parseLong(value.getValue());
				stat.addValue(val);
			}
		}
		return stat;
	}

	private static List<Result> selectPart(final List<Result> beforeShortened, final int vms) {
		final List<Result> beforeSelected = new LinkedList<>();
		for (int insertion = 0; insertion < vms; insertion++) {

			final Result randomlyPickedResult = beforeShortened.get(random.nextInt(beforeShortened.size()));
			beforeSelected.add(randomlyPickedResult);
		}
		return beforeSelected;
	}
}
