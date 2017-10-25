package de.precision.processing;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Result;
import de.peran.analysis.helper.MinimalExecutionDeterminer;
import de.peran.measurement.analysis.AnalyseFullData;

public class GenerateDeviationPlot {

	private static final Logger LOG = LogManager.getLogger(AnalyseFullData.class);

	public final static File RESULTFOLDER = new File("results");
	final static int AVG_COUNT = 400;

	public static void main(final String[] args) throws JAXBException, IOException {
		if (!RESULTFOLDER.exists()) {
			RESULTFOLDER.mkdir();
		}

		System.out.println("set datafile separator ';'");
		System.out.println("set y2range [0:5]");
		System.out.println("set y2tics");

		final Map<String, Testcases> testcases = new HashMap<>();
		final File folder = new File(args[0]);
		for (final File dataFile : FileUtils.listFiles(folder, new WildcardFileFilter("*.xml"), TrueFileFilter.INSTANCE)) {
			final Kopemedata data = new XMLDataLoader(dataFile).getFullData();
			final Testcases testclazz = data.getTestcases();
			final String packageName = testclazz.getClazz().substring(0, testclazz.getClazz().lastIndexOf('.'));
			final Testcases otherPackageTestcase = testcases.get(packageName);
			if (otherPackageTestcase == null) {
				testcases.put(packageName, testclazz);
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
		printDeviations(testclazz.getClazz(), testclazz.getTestcase().get(0), testclazz.getTestcase().get(0).getDatacollector().get(0).getResult());
		printDeviations(otherPackageTestcase.getClazz(), otherPackageTestcase.getTestcase().get(0), otherPackageTestcase.getTestcase().get(0).getDatacollector().get(0).getResult());
	}



	private static void printDeviations(final String clazzname, final TestcaseType testcase, final List<Result> results) throws IOException {
		for (int warmup = 10000; warmup < 55000; warmup += 5000) {
			final File summaryFile = new File(RESULTFOLDER, "deviations_" + clazzname + "_" + testcase.getName() + "_" + warmup + ".csv");

			final List<Result> shortenedResults = MinimalExecutionDeterminer.shortenValues(results, warmup, results.get(0).getFulldata().getValue().size());
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(summaryFile))) {

				for (final Result result : shortenedResults) {
					final DescriptiveStatistics statistics = new DescriptiveStatistics();
					result.getFulldata().getValue().forEach(value -> statistics.addValue(Double.parseDouble(value.getValue())));
					writer.write(statistics.getMean() + ";" + statistics.getVariance() + "\n");
				}
				// for (int i = 0; i < allMeans.size(); i++) {
				// writer.write(allMeans.get(i).getMean() + ";" + allCoVs.get(i).getMean() + "\n");
				// }
				writer.flush();

				System.out.println("set title 'Means and Variation for " + clazzname + "." + testcase.getName() + "'");
				System.out.println("plot '" + summaryFile.getName() + "' u 1:2 title 'Variations'");
				System.out.println();
			}
		}
	}

}
