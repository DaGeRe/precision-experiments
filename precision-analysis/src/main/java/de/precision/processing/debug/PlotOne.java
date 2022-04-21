package de.precision.processing.debug;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

import jakarta.xml.bind.JAXBException;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.peass.analysis.measurement.statistics.MeanCoVData;

public class PlotOne {

	public static final File RESULTFOLDER = new File("results/one/");

	static {
		if (!RESULTFOLDER.exists()) {
			RESULTFOLDER.mkdirs();
		}
	}

	private static final NumberFormat FORMAT = NumberFormat.getInstance();

	public static void main(final String[] args) throws JAXBException, IOException {
		final File folder = new File(args[0]);
		final Kopemedata data = new XMLDataLoader(folder).getFullData();
		final Testcases testclazz = data.getTestcases();

		final TestcaseType testcase = testclazz.getTestcase().get(0);
		final MeanCoVData data2 = new MeanCoVData(testcase, 100);
		data2.printTestcaseData(RESULTFOLDER);
		final List<DescriptiveStatistics> means = data2.getAllMeans();

		final int avgCount = 100;

		final File csvFile = new File(RESULTFOLDER, "result_" + testcase.getName() + "_mean.csv");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
			DescriptiveStatistics statistics = new DescriptiveStatistics();
			for (final DescriptiveStatistics mean : means) {
				statistics.addValue(mean.getMean());
				if (statistics.getValues().length == avgCount) {
					final double cov = statistics.getVariance() / statistics.getMean();
					writer.write(FORMAT.format(statistics.getMean()) + ";" + FORMAT.format(cov) + "\n");
					statistics = new DescriptiveStatistics();
				}
			}
		}
	}

}
