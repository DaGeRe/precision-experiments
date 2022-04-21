package de.precision.processing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Kopemedata.Testcases;
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

	public static void main(final String[] args) throws JAXBException, IOException {

		GenerateCoVPlots.printConfig();

		final Map<String, Testcases> testcases = new HashMap<>();
		final File folder = new File(args[0]);
		for (final File dataFile : FileUtils.listFiles(folder, new WildcardFileFilter("*.xml"), TrueFileFilter.INSTANCE)) {
			LOG.debug("Loading: {}", dataFile);
			final Kopemedata data = new XMLDataLoader(dataFile).getFullData();
			final Testcases testclazz = data.getTestcases();
			// final String packageName = testclazz.getClazz().substring(0, testclazz.getClazz().lastIndexOf('.'));
			final Testcases otherPackageTestcase = testcases.get(testclazz.getClazz().replace('2', '1'));
			if (otherPackageTestcase == null) {
				testcases.put(testclazz.getClazz().replace('2', '1'), testclazz);
			} else {
				if (testclazz.getClazz().contains("1")) {
					processTestcases(testclazz, otherPackageTestcase);
				} else {
					processTestcases(otherPackageTestcase, testclazz);
				}
			}
		}
	}

	private static void processTestcases(final Testcases testclazz, final Testcases otherPackageTestcase) throws IOException {
		handleTestcase(testclazz.getClazz(), testclazz.getTestcase().get(0));
		handleTestcase(otherPackageTestcase.getClazz(), otherPackageTestcase.getTestcase().get(0));
	}

	public static void handleTestcase(final String clazzname, final TestcaseType testcase) throws IOException {
		final MeanCoVData data = new MeanCoVData(testcase, AVG_COUNT);
		data.printTestcaseData(ProcessConstants.RESULTFOLDER_COV);

		printDeviations(clazzname, testcase, testcase.getDatacollector().get(0).getResult());

		data.printAverages(ProcessConstants.RESULTFOLDER_COV, clazzname);
	}

	static void printDeviations(final String clazzname, final TestcaseType testcase, final List<Result> results) throws IOException {
		final File summaryFile = new File(ProcessConstants.RESULTFOLDER_COV, "deviations_" + clazzname + "_" + testcase.getName() + "_all.csv");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(summaryFile))) {

			for (final Result result : results) {
				final DescriptiveStatistics statistics = new DescriptiveStatistics();
				result.getFulldata().getValue().forEach(value -> statistics.addValue(value.getValue()));
				writer.write(statistics.getMean() + ";" + statistics.getVariance() + "\n");
			}
			writer.flush();

			System.out.println("set title 'Means and Variation for " + clazzname + "." + testcase.getName() + "'");
			System.out.println("plot '" + summaryFile.getName() + "' u 1:2 title 'Variations'");
			System.out.println();
		}
	}
}
