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
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.peass.measurement.dataloading.MultipleVMTestUtil;
import de.dagere.peass.measurement.statistics.Relation;
import de.dagere.peass.measurement.statistics.StatisticUtil;
import de.precision.analysis.repetitions.MethodResult;
import de.precision.processing.util.PrecisionFolderUtil;
import de.precision.processing.util.RepetitionFolderHandler;

/**
 * Takes a folder with sequence-executions and a precision-level as input. Tells, how many sequence-executions are needed in order to achieve the precision-level.
 * 
 * @author reichelt
 *
 */
public class GeneratePrecisionPlotEarlyStop extends RepetitionFolderHandler {

	public final static File RESULTFOLDER = new File("results/precision/");

	private static final String[] myTypes = new String[] { "MEAN", "TTEST" };

	static {
		if (!RESULTFOLDER.exists()) {
			RESULTFOLDER.mkdirs();
		}
	}

	private static final Logger LOG = LogManager.getLogger(GeneratePrecisionPlotEarlyStop.class);

	public static void main(final String[] args) throws JAXBException, IOException {
		// System.setOut(new PrintStream(new File("/dev/null")));
		Configurator.setLevel("de.peran.measurement.analysis.statistics.ConfidenceIntervalInterpretion", Level.INFO);

		earlyStopWriter = new BufferedWriter(new FileWriter(new File(RESULTFOLDER, "earlystop.csv")));
		precisionRecallWriter = new BufferedWriter(new FileWriter(new File(RESULTFOLDER, "precision.csv")));
		writeHeader(precisionRecallWriter);
		final File folder = new File(args[0]);

		PrecisionFolderUtil.processFolder(folder, (repetitionFolder) -> new GeneratePrecisionPlotEarlyStop(repetitionFolder));

		precisionRecallWriter.flush();
		System.out.println("'precision.csv' u 1:6 w lines title 'Precision Mean', 'precision.csv' u 1:7 w lines title 'Recall Mean', 'precision.csv' u 1:8 w lines title 'Wrong Mean', ");
		// System.out.println("'precision.csv' u 1:9 w lines title 'Precision Confidence', 'precision.csv' u 1:9 title 'Recall Confidence','precision.csv' u 1:10 title 'Wrong Confidence',");
		System.out.println("'precision.csv' u 1:12 w lines title 'Precision TTest', 'precision.csv' u 1:13 w lines title 'Recall TTest','precision.csv' u 1:14 w lines title 'Wrong TTest', ");
		// System.out.println("'precision.csv' u 1:15 w lines title 'Precision GTest','precision.csv' u 1:16 w lines title 'Recall GTest', 'precision.csv' u 1:17 w lines title 'Wrong GTest'");

		System.out.println(
				"plot 'precision.csv' u 1:6 w lines title 'Precision Mean', 'precision.csv' u 1:9 w lines title 'Precision Confidence', 'precision.csv' u 1:12 w lines title 'Precision TTest', 'precision.csv' u 1:15 w lines title 'Precision GTest'");
	}

	private static void writeHeader(final BufferedWriter writer) throws IOException {
		writer.write("#repetitions ; vms ; warmup ; overhead ; duration ;");
		for (final String method : new MethodResult(myTypes).getResults().keySet()) {
			writer.write(method + ";" + ";" + ";");
		}
		writer.write("\n");
		writer.flush();
	}

	public GeneratePrecisionPlotEarlyStop(final File sequenceFolder) {
		super(sequenceFolder);
	}

	private final static Random RANDOM = new Random();

	private final MethodResult overallResults = new MethodResult(myTypes);
	private final Map<String, MethodResult> testcaseResults = new HashMap<>();

	private static BufferedWriter precisionRecallWriter, earlyStopWriter;
	private static final Map<String, BufferedWriter> testcaseWriters = new HashMap<>();

	private int vms = 20;

	private int warmup = 1000;

	private int executions = 1000;

	private long overhead = 0;
	private long duration = 0;

	private final DecimalFormat df = new DecimalFormat("00.00");

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

		warmup = 5000;
		executions = 15000;
		for (int myVms = 30; myVms < 31; myVms += 20) {
			vms = myVms;
			overhead = 0;
			duration = 0;
			stopdata.clear();
			super.handleVersion();
			precisionRecallWriter.write(repetitions + ";" + vms + ";" + warmup + ";" + overhead + ";" + duration + ";");
			for (final Map.Entry<String, Map<String, Integer>> methodResult : overallResults.getResults().entrySet()) {
				writeData(methodResult, precisionRecallWriter);
			}
			precisionRecallWriter.write("\n");
			precisionRecallWriter.flush();

			for (final Map.Entry<String, MethodResult> entry : testcaseResults.entrySet()) {
				try {
					BufferedWriter testcaseWriter = testcaseWriters.get(entry.getKey());
					if (testcaseWriter == null) {
						testcaseWriter = new BufferedWriter(new FileWriter(new File(RESULTFOLDER, entry.getKey() + ".csv")));
						testcaseWriters.put(entry.getKey(), testcaseWriter);
						writeHeader(testcaseWriter);
					}
					testcaseWriter.write(repetitions + ";" + vms + ";" + warmup + ";" + overhead + ";" + duration + ";");
					for (final Map.Entry<String, Map<String, Integer>> methodResult : entry.getValue().getResults().entrySet()) {
						writeData(methodResult, testcaseWriter);
					}
					testcaseWriter.write("\n");
					testcaseWriter.flush();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}

			for (final Map.Entry<String, EarlyStopData> data : stopdata.entrySet()) {
				earlyStopWriter.write(df.format(data.getValue().save.getMean()) + ";" + data.getValue().wrong + ";");
			}
			earlyStopWriter.write("\n");
			earlyStopWriter.flush();

			// LOG.info("Results: " + truePositives.toString());
		}
	}

	private void writeData(final Map.Entry<String, Map<String, Integer>> methodResult, final BufferedWriter writer) throws IOException {
		final int selected = methodResult.getValue().get(MethodResult.SELECTED);
		final int truepositive = methodResult.getValue().get(MethodResult.TRUEPOSITIVE);
		final int falsenegative = methodResult.getValue().get(MethodResult.FALSENEGATIVE);
		final int wronggreater = methodResult.getValue().get(MethodResult.WRONGGREATER);
		final double precision = 100d * ((selected > 0) ? ((double) truepositive) / selected : 0);
		final double recall = 100d * (((double) truepositive) / (truepositive + falsenegative));
		final double wrongGreaterSelectionRate = (selected > 0) ? ((double) wronggreater) / selected : 0;
		writer.write(df.format(precision) + ";" + df.format(recall) + ";" + df.format(wrongGreaterSelectionRate) + ";");
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
		final List<Result> beforeShortened = StatisticUtil.shortenValues(before.getDatacollector().get(0).getResult(), warmup, allExecutions);
		final List<Result> afterShortened = StatisticUtil.shortenValues(after.getDatacollector().get(0).getResult(), warmup, allExecutions);

		LOG.debug("Duration: {} VMs: {}", duration, vms);

		final DescriptiveStatistics averageDuration = new DescriptiveStatistics();

		// TODO Konvergenzzeitpunkt bestimmen statt einfach 500 mal..
		for (int i = 0; i < 100; i++) {
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

	class EarlyStopData {
		int wrong = 0;
		SummaryStatistics save = new SummaryStatistics();
	}

	Map<String, EarlyStopData> stopdata = new HashMap<>();

	private void executeComparisons(final List<Result> beforeShortened, final List<Result> afterShortened, final Relation expectedRelation, final String test) {
		final Map<String, Relation> relations = new LinkedHashMap<>();

		final List<double[]> values = new LinkedList<>();
		values.add(new double[beforeShortened.size()]);
		values.add(new double[afterShortened.size()]);
		for (int i = 0; i < beforeShortened.size(); i++) {
			values.get(0)[i] = beforeShortened.get(i).getValue();
			values.get(1)[i] = afterShortened.get(i).getValue();
		}

		final double avgBefore = MultipleVMTestUtil.getStatistic(beforeShortened).getMean();
		final double avgAfter = MultipleVMTestUtil.getStatistic(afterShortened).getMean();

		if (avgBefore < avgAfter * 0.99) {
			relations.put("MEAN", Relation.LESS_THAN);
		} else {
			relations.put("MEAN", avgBefore < avgAfter ? Relation.EQUAL : Relation.GREATER_THAN);
		}

		final boolean tchange = new TTest().homoscedasticTTest(values.get(0), values.get(1), 0.01);

		getEarlyBreakData(test, values, tchange);

		// values.get(0).

		if (tchange) {
			relations.put("TTEST", avgBefore < avgAfter ? Relation.LESS_THAN : Relation.GREATER_THAN);
		} else {
			relations.put("TTEST", Relation.EQUAL);
		}

		MethodResult myMethodResult = testcaseResults.get(test);
		if (myMethodResult == null) {
			myMethodResult = new MethodResult(myTypes);
			testcaseResults.put(test, myMethodResult);
		}

		for (final Map.Entry<String, Relation> relationByMethod : relations.entrySet()) {
			if (relationByMethod.getValue() == Relation.LESS_THAN) {
				overallResults.increment(relationByMethod.getKey(), MethodResult.SELECTED);
				myMethodResult.increment(relationByMethod.getKey(), MethodResult.SELECTED);
				if (Relation.LESS_THAN == expectedRelation) {
					overallResults.increment(relationByMethod.getKey(), MethodResult.TRUEPOSITIVE);
					myMethodResult.increment(relationByMethod.getKey(), MethodResult.TRUEPOSITIVE);
				} else {
				}
			} else {
				if (Relation.LESS_THAN == expectedRelation) {
					overallResults.increment(relationByMethod.getKey(), MethodResult.FALSENEGATIVE);
					myMethodResult.increment(relationByMethod.getKey(), MethodResult.FALSENEGATIVE);
				}
			}
			if (relationByMethod.getValue() == Relation.GREATER_THAN) {
				overallResults.increment(relationByMethod.getKey(), MethodResult.WRONGGREATER);
				myMethodResult.increment(relationByMethod.getKey(), MethodResult.WRONGGREATER);
			}
		}
	}

	private void getEarlyBreakData(final String test, final List<double[]> values, final boolean tchange) {
		int earlyBreakCount = -1;
		for (int breakVMs = 10; breakVMs < values.get(0).length; breakVMs++) {
			final double tvalue = getTValueBreak(values, breakVMs);
			if (Math.abs(tvalue) > 10) {
				earlyBreakCount = breakVMs;
				break;
			}
			if (Math.abs(tvalue) < 0.05) {
				earlyBreakCount = breakVMs;
				break;
			}
		}

		if (earlyBreakCount != -1) {
			final double tvalue = getTValueBreak(values, earlyBreakCount);
			final boolean earlyBreakSuccess = tchange == (Math.abs(tvalue) > 2.7);
			EarlyStopData data = stopdata.get(test);
			if (data == null) {
				data = new EarlyStopData();
				stopdata.put(test, data);
			}
			data.save.addValue(values.get(0).length - earlyBreakCount);
			if (earlyBreakSuccess != true) {
				data.wrong++;
			}
		}
	}

	private double getTValueBreak(final List<double[]> values, final int breakVMs) {
		final double[] earlyBreak1 = new double[breakVMs];
		final double[] earlyBreak2 = new double[breakVMs];
		System.arraycopy(values.get(0), 0, earlyBreak1, 0, breakVMs);
		System.arraycopy(values.get(1), 0, earlyBreak2, 0, breakVMs);
		final double tvalue = new TTest().homoscedasticT(earlyBreak1, earlyBreak2);
		return tvalue;
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
