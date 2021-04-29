package de.precision.processing;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.peass.measurement.analysis.statistics.MeanCoVData;

/**
 * Helps comparing equal executions on different servers, including temperature analysis.
 * 
 * @author reichelt
 *
 */
public final class CompareSameExecutions {
	private CompareSameExecutions() {

	}

	public static final File RESULTFOLDER = new File("results/same/");

	static {
		if (!RESULTFOLDER.exists()) {
			RESULTFOLDER.mkdirs();
		}
	}

	static final int AVG_COUNT = 200;

	public static void main(final String[] args) throws JAXBException, IOException {

		System.out.println("set datafile separator ';'");
		System.out.println("set y2range [0:5]");
		System.out.println("set y2tics");

		final String plotTemp = "plot ";

		final Map<String, List<Testcases>> testcases = new HashMap<>();
		final File folder = new File(args[0]);
//		for (final File dataFile : FileUtils.listFiles(folder, new WildcardFileFilter("*.xml"), TrueFileFilter.INSTANCE)) {
//			final Kopemedata data = new XMLDataLoader(dataFile).getFullData();
//			final File parent = dataFile.getParentFile().getParentFile();
//			List<Testcases> equalTestcases = testcases.get(parent.getName());
//			if (equalTestcases == null) {
//				equalTestcases = new ArrayList<>();
//				testcases.put(parent.getName(), equalTestcases);
//			}
//			equalTestcases.add(data.getTestcases());
//			final File potentialTemp = new File(parent, "temp.csv_10_avg.csv");
//			if (potentialTemp.exists()) {
//				final File destFile = new File(RESULTFOLDER, "temp_" + parent.getName() + ".csv");
//				// if (!destFile.exists()) {
//				FileUtils.copyFile(potentialTemp, destFile);
//				final BufferedReader reader = new BufferedReader(new FileReader(potentialTemp));
//				final String line = reader.readLine();
//				final long startTime = Integer.parseInt(line.split(";")[0]);
//				plotTemp += "'temp_" + parent.getName() + ".csv' u ($1-" + startTime + "):($2/4) w lines axes x2y2,";
//				reader.close();
//				// }
//			}
//		}

		System.out.println(plotTemp.substring(0, plotTemp.length() - 1));
		System.out.println();

		plotMeans(testcases);
	}

	private static void plotMeans(final Map<String, List<Testcases>> testcases) throws IOException {
		String plotMeans = "plot ";
		for (final Map.Entry<String, List<Testcases>> testcaseEntry : testcases.entrySet()) {
			for (int i = 0; i < testcaseEntry.getValue().size(); i++) {
				final MeanCoVData data = new MeanCoVData(testcaseEntry.getValue().get(0).getTestcase().get(0), 100);
				final File avgFile = data.printAverages(RESULTFOLDER, testcaseEntry.getKey() + "_" + i);
				plotMeans += "'" + avgFile.getName() + "' u ($0*100):1 w lines,";
			}
		}
		System.out.println(plotMeans.substring(0, plotMeans.length() - 1));
		System.out.println();
	}
}
