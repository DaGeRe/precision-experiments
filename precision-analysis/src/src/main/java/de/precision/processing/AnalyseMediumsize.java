package de.precision.processing;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.inference.TTest;

import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.peass.measurement.analysis.MultipleVMTestUtil;
import de.precision.processing.util.RepetitionFolderHandler;
import de.precision.processing.util.PrecisionFolderUtil;

public class AnalyseMediumsize extends RepetitionFolderHandler {
	public final static File RESULTFOLDER = new File("results/mediumsize/");

	static {
		RepetitionFolderHandler.clearResultFolder(RESULTFOLDER);
	}

	public AnalyseMediumsize(File repetitionFolder) {
		super(repetitionFolder);
	}

	public static void main(String[] args) throws JAXBException, IOException {
		final File folder = new File(args[0]);

		PrecisionFolderUtil.processFolder(folder, (repetition_folder) -> new AnalyseMediumsize(repetition_folder));
	}

	@Override
	protected void processTestcases(Testcases versionFast, Testcases versionSlow) {
		final SummaryStatistics statisticsFast = MultipleVMTestUtil.getStatistic(versionFast.getTestcase().get(0).getDatacollector().get(0).getResult());
		final SummaryStatistics statisticsSlow = MultipleVMTestUtil.getStatistic(versionSlow.getTestcase().get(0).getDatacollector().get(0).getResult());
		
		final double tval = new TTest().homoscedasticT(statisticsFast, statisticsSlow);
		
//		statisticsSlow.getMean() - statisticsFast.getMean();
		
		final double avgMean = (statisticsSlow.getMean() + statisticsFast.getMean() ) / 2;
		final double avgDeviation = (statisticsSlow.getStandardDeviation() + statisticsFast.getStandardDeviation() ) / 2;
		
		System.out.println(repetitions + ";" + avgMean + ";" + avgDeviation + ";" + avgDeviation / avgMean + ";" + tval);
		
//		System.out.println(repetitions + ";" + statisticsSlow.getStandardDeviation() / statisticsSlow.getMean());
//		System.out.println(repetitions + ";" + statisticsFast.getStandardDeviation() / statisticsFast.getMean());
	}
}
