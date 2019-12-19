package de.precision.processing.repetitions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.Result;
import de.peran.measurement.analysis.AnalyseFullData;
import de.precision.processing.util.RepetitionFolderHandler;
import de.precision.processing.util.Util;

/**
 * Generates the csv-file for a plot showing how the differences change with growing repetition count
 * 
 * @author reichelt
 *
 */
public class GenerateDiffPlot extends RepetitionFolderHandler {

	public GenerateDiffPlot(final File sequenceFolder) {
		super(sequenceFolder);
	}

	private static final Logger LOG = LogManager.getLogger(AnalyseFullData.class);

	public final static File RESULTFOLDER = new File("results/diff/");

	static {
		try {
			FileUtils.deleteDirectory(RESULTFOLDER);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		RESULTFOLDER.mkdir();
	}

	public static void main(final String[] args) throws JAXBException, IOException {
		final File folder = new File(args[0]);
		Util.processFolder(folder, (repetition_folder) -> new GenerateDiffPlot(repetition_folder));
	}

	@Override
	protected void processTestcases(final Testcases versionFast, final Testcases versionSlow) {
		final File testcaseFile = new File(RESULTFOLDER, versionFast.getClazz() + ".csv");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(testcaseFile, true))) {
			final Iterator<Result> slowIt = versionSlow.getTestcase().get(0).getDatacollector().get(0).getResult().iterator();
			for (final Iterator<Result> fastIt = versionFast.getTestcase().get(0).getDatacollector().get(0).getResult().iterator(); fastIt.hasNext();) {
				final Result fast = fastIt.next();
				final Result slow = slowIt.next();
				writer.write("V1 " + repetitions + ";" + fast.getValue() + "\n");
				writer.write("V2 " + repetitions + ";" + slow.getValue() + "\n");
				writer.flush();
			}
		} catch (

		final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
