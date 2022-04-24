package de.precision.processing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.TestMethod;
import de.dagere.kopeme.kopemedata.VMResult;
import de.dagere.kopeme.datastorage.JSONDataLoader;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.peass.analysis.measurement.statistics.MeanCoVData;

/**
 * Generates csv-files and gnuplot-commands for printing an graph of the values and the coefficient of variation of all result files in one folder
 * 
 * @author reichelt
 *
 */
public final class GenerateCoVPlot {

	private GenerateCoVPlot() {

	}

	private static final Logger LOG = LogManager.getLogger(GenerateCoVPlot.class);

	static final int AVG_COUNT = 50;

	public static void main(final String[] args) throws IOException {

		GenerateCoVPlots.printConfig();

		final Map<String, Kopemedata> testcases = new HashMap<>();
		final File folder = new File(args[0]);
		for (final File dataFile : FileUtils.listFiles(folder, new WildcardFileFilter("*.xml"), TrueFileFilter.INSTANCE)) {
			LOG.debug("Loading: {}", dataFile);
			final Kopemedata data = new JSONDataLoader(dataFile).getFullData();
			// final String packageName = testclazz.getClazz().substring(0, testclazz.getClazz().lastIndexOf('.'));
			final Kopemedata otherPackageTestcase = testcases.get(data.getClazz().replace('2', '1'));
			if (otherPackageTestcase == null) {
				testcases.put(data.getClazz().replace('2', '1'), data);
			} else {
				if (data.getClazz().contains("1")) {
					processTestcases(data, otherPackageTestcase);
				} else {
					processTestcases(otherPackageTestcase, data);
				}
			}
		}
	}

	private static void processTestcases(final Kopemedata testclazz, final Kopemedata otherPackageTestcase) throws IOException {
		handleTestcase(testclazz.getClazz(), testclazz.getMethods().get(0));
		handleTestcase(otherPackageTestcase.getClazz(), otherPackageTestcase.getMethods().get(0));
	}

	public static void handleTestcase(final String clazzname, final TestMethod testcase) throws IOException {
		final MeanCoVData data = new MeanCoVData(testcase, AVG_COUNT);
		data.printTestcaseData(ProcessConstants.RESULTFOLDER_COV);

		printDeviations(clazzname, testcase, testcase.getDatacollectorResults().get(0).getResults());

		data.printAverages(ProcessConstants.RESULTFOLDER_COV, clazzname);
	}

	static void printDeviations(final String clazzname, final TestMethod testcase, final List<VMResult> results) throws IOException {
		final File summaryFile = new File(ProcessConstants.RESULTFOLDER_COV, "deviations_" + clazzname + "_" + testcase.getMethod() + "_all.csv");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(summaryFile))) {

			for (final VMResult result : results) {
				final DescriptiveStatistics statistics = new DescriptiveStatistics();
				result.getFulldata().getValues().forEach(value -> statistics.addValue(value.getValue()));
				writer.write(statistics.getMean() + ";" + statistics.getVariance() + "\n");
			}
			writer.flush();

			System.out.println("set title 'Means and Variation for " + clazzname + "." + testcase.getMethod() + "'");
			System.out.println("plot '" + summaryFile.getName() + "' u 1:2 title 'Variations'");
			System.out.println();
		}
	}
}
