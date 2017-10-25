package de.precision.processing.debug;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.peran.measurement.analysis.statistics.MeanCoVData;
import de.precision.processing.GenerateCoVPlot;

public class PlotOne {
	public static void main(String[] args) throws JAXBException, IOException {
		final File folder = new File(args[0]);
		final Kopemedata data = new XMLDataLoader(folder).getFullData();
		final Testcases testclazz = data.getTestcases();
		
		final MeanCoVData data2 = new MeanCoVData(testclazz.getTestcase().get(0), 50);
		data2.printTestcaseData(GenerateCoVPlot.RESULTFOLDER);
	}
}
