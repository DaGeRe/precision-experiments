package de.precision.processing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.TestcaseType;
import de.peran.measurement.analysis.statistics.MeanCoVData;

public class AnalyseData {
	public static void main(final String[] args) throws JAXBException, IOException {
		if (!GenerateCoVPlot.RESULTFOLDER.exists()) {
			GenerateCoVPlot.RESULTFOLDER.mkdir();
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
		handleTestcase(testclazz.getClazz(), testclazz.getTestcase().get(0));
		handleTestcase(otherPackageTestcase.getClazz(), otherPackageTestcase.getTestcase().get(0));

		// compare(testclazz.getTestcase().get(0), otherPackageTestcase.getTestcase().get(0));
	}

	private static void handleTestcase(final String clazzname, final TestcaseType testcase) throws IOException {
		final MeanCoVData data = new MeanCoVData(testcase, 100);
		data.printTestcaseData(GenerateCoVPlot.RESULTFOLDER);

		final File summaryFile = new File(GenerateCoVPlot.RESULTFOLDER, "result_" + clazzname + "_" + testcase.getName() + "_all.csv");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(summaryFile))) {
			for (int i = 0; i < data.getAllMeans().size(); i++) {
				writer.write(data.getAllMeans().get(i).getMean() + ";" + data.getAllCoVs().get(i).getMean() + "\n");
			}
			writer.flush();

			System.out.println("set title 'Mean Mean and Mean Coefficient of Variation for " + clazzname + "." + testcase.getName() + "'");
			System.out.println("plot '" + summaryFile.getName() + "' u ($0*" + GenerateCoVPlot.AVG_COUNT + "):1 title 'Mean', '" + summaryFile.getName() + "' u ($0*" + GenerateCoVPlot.AVG_COUNT
					+ "):2 title 'CoV' axes x1y2");
			System.out.println();
		}
	}

}
