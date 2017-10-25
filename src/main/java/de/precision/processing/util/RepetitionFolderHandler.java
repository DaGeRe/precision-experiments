package de.precision.processing.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Kopemedata.Testcases;

public abstract class RepetitionFolderHandler {

	private static final Logger LOG = LogManager.getLogger(RepetitionFolderHandler.class);

	@FunctionalInterface
	public static interface Creator {
		public RepetitionFolderHandler createHandler(File repetitionFolder);
	}

	protected final int repetitions;
	private final File repetitionFolder;

	public RepetitionFolderHandler(final File sequenceFolder) {
		this.repetitionFolder = sequenceFolder;
		repetitions = Integer.parseInt(sequenceFolder.getName().substring("sequence_".length()));
	}

	public File getFolder() {
		return repetitionFolder;
	}

	public void handleVersion() throws JAXBException, IOException {
		final Map<String, Testcases> testcases = new HashMap<>();
		LOG.debug("Loading: " + repetitions + " " + repetitionFolder);

		for (final File dataFile : FileUtils.listFiles(repetitionFolder, new WildcardFileFilter("*.xml"), TrueFileFilter.INSTANCE)) {
			final Kopemedata data = new XMLDataLoader(dataFile).getFullData();
			final Testcases testclazz = data.getTestcases();
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

	protected abstract void processTestcases(Testcases versionFast, Testcases versionSlow);

}
