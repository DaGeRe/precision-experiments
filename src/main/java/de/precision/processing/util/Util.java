package de.precision.processing.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import javax.xml.bind.JAXBException;

import de.precision.processing.util.RepetitionFolderHandler.Creator;

/**
 * Helper class for processing folders
 * 
 * @author reichelt
 *
 */
public final class Util {

	private Util() {

	}

	/**
	 * Processes all repetition-folders in the given folder in the order of their repetition count by using the Creator
	 * 
	 * @param folder Folder to look for repetition folders
	 * @param creator Creator for ReptitionFolderHandler
	 */
	public static void processFolder(final File folder, final Creator creator) throws JAXBException, IOException {
		final File[] repetitionFolders = folder.listFiles();
		Arrays.sort(repetitionFolders, new Comparator<File>() {

			@Override
			public int compare(final File o1, final File o2) {

				final boolean o1Matches = o1.getName().matches("repetition_[0-9]+");
				final boolean o2Matches = o2.getName().matches("repetition_[0-9]+");
				if (o1Matches) {
					if (o2Matches) {
						final int o1Num = Integer.parseInt(o1.getName().substring("repetition_".length()));
						final int o2Num = Integer.parseInt(o2.getName().substring("repetition_".length()));
						return o1Num - o2Num;
					} else {
						System.out.println("No match: " + o1.getName() + " " + o2.getName());
						return 0;
					}
				} else {
					if (o2Matches) {
						return -10000;
					} else {
						return 0;
					}

				}
			}
		});
		for (final File subfolder : repetitionFolders) {
			if (subfolder.getName().matches("repetition_[0-9]+")) {
				creator.createHandler(subfolder).handleVersion();
			}
		}
	}

}
