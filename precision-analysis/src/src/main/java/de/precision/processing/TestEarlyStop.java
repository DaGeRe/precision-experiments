package de.precision.processing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.Result;

public class TestEarlyStop {
	private static final Logger LOG = LogManager.getLogger(GenerateCoVPlot.class);

	public static void main(String[] args) throws JAXBException {
		final Map<String, Testcases> testcases = new HashMap<>();
		final File folder = new File(args[0]);
//		for (final File dataFile : FileUtils.listFiles(folder, new WildcardFileFilter("*.xml"), TrueFileFilter.INSTANCE)) {
//			LOG.debug("Loading: {}", dataFile);
//			if (dataFile.getName().contains("add") && !dataFile.getAbsolutePath().contains("RAM") && !dataFile.getAbsolutePath().contains("Big")) {
//
//				final Kopemedata data = new XMLDataLoader(dataFile).getFullData();
//				final Testcases testclazz = data.getTestcases();
//				// final String packageName = testclazz.getClazz().substring(0, testclazz.getClazz().lastIndexOf('.'));
//				final Testcases otherPackageTestcase = testcases.get(testclazz.getClazz().replace('2', '1'));
//				processTestcases(testclazz, null);
//				if (otherPackageTestcase == null) {
//					testcases.put(testclazz.getClazz().replace('2', '1'), testclazz);
//				} else {
//
//					if (testclazz.getClazz().contains("1")) {
//						processTestcases(testclazz, otherPackageTestcase);
//					} else {
//						processTestcases(otherPackageTestcase, testclazz);
//					}
//				}
//			}
//		}
	}

	private static void processTestcases(Testcases testclazz, Testcases otherPackageTestcase) {
		System.out.println(testclazz.getClazz());
		final List<Result> results = testclazz.getTestcase().get(0).getDatacollector().iterator().next().getResult();

		final int diff = 500;

		final int index = 0;
		final Result result = results.get(0);
		final File test = new File(testclazz.getClazz() + "_" + index + ".csv");
		try (final BufferedWriter writer = new BufferedWriter(new FileWriter(test))){
			System.out.println("Size: " + result.getFulldata().getValue().size());
			final double[] values = new double[result.getFulldata().getValue().size()];
			for (int i = 0; i < values.length; i++) {
				values[i] = result.getFulldata().getValue().get(i).getValue();
			}
			final SimpleRegression regression = DurbinWatson.getRegression(values);
			for (int startindex = 0; startindex < values.length - diff; startindex += 100) {
				double maxcorr = 0;
				int maxlag = 0;
				final double[] autocorrelateTest = new double[diff];
				System.arraycopy(values, startindex, autocorrelateTest, 0, diff);

				final SimpleRegression regression2 = DurbinWatson.getRegression(autocorrelateTest);
				for (int lag = 1; lag < diff / 2; lag++) {
					final double autocorrelation = DurbinWatson.getDurbinWatson(regression, autocorrelateTest, lag);
					final double corrValue = Math.abs(2 - autocorrelation);
					if (corrValue > maxcorr) {
						maxcorr = corrValue;
						maxlag = lag;
					}
				}
				final DescriptiveStatistics stat = new DescriptiveStatistics(autocorrelateTest);
				System.out.println(regression.getIntercept() + " " + regression.getSlope() + " " + stat.getMean() + " " + maxcorr);
				writer.write(NumberFormat.getInstance().format(stat.getMean()) + ";" +
						maxlag + ";" +
						NumberFormat.getInstance().format(regression.getSlope()) + ";"
						+ NumberFormat.getInstance().format(maxcorr) + ";" +
						NumberFormat.getInstance().format(regression2.getSlope()) + ";"
						+ "\n");
				writer.flush();
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}
}
