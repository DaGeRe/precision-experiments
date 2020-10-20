package de.precision.processing;

import org.apache.commons.math3.stat.regression.SimpleRegression;

public class DurbinWatson {
	public static SimpleRegression getRegression(double[] values) {
		final SimpleRegression regression = new SimpleRegression();
		for (int i = 0; i < values.length; i++) {
			regression.addData(i, values[i]);
		}
		return regression;
	}

	public static double getDurbinWatson(SimpleRegression regression, double[] values, int lag) {

		double sum1 = 0.0;
		double sum2 = 0.0;
		for (int i = lag; i < values.length; i++) {
			final double residuum1 = values[i] - (regression.getIntercept() + regression.getSlope() * i);
			final double residuum2 = values[i - 1] - (regression.getIntercept() + regression.getSlope() * (i - 1));

			sum1 += Math.pow(residuum1 - residuum2, 2);
			sum2 += Math.pow(residuum1, 2);
		}

		return sum1 / sum2;
	}

	public static double getDurbinWatson(double[] values, int lag) {
		final SimpleRegression regression = getRegression(values);

		double sum1 = 0.0;
		double sum2 = 0.0;
		for (int i = lag; i < values.length; i++) {
			final double residuum1 = values[i] - (regression.getIntercept() + regression.getSlope() * i);
			final double residuum2 = values[i - 1] - (regression.getIntercept() + regression.getSlope() * (i - 1));

			sum1 += Math.pow(residuum1 - residuum2, 2);
			sum2 += Math.pow(residuum1, 2);
		}

		return sum1 / sum2;
	}

	public static void main(String[] args) {
		final double[] values = new double[] { 1.0, 3.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0 };

		System.out.println(getDurbinWatson(values, 1));

		final double[] values2 = new double[] { 5.0, 2.0, 8.0, 2.0, 4.0, 6.0, 12.0, 2.0, 1.0, 10.0 };

		System.out.println(getDurbinWatson(values2, 1));

		final double[] values3 = new double[] { 6.0, 1.0, 7.0, 2.0, 8.0, 4.0, 9.0, 5.0, 10.0, 6.0 };

		System.out.println(getDurbinWatson(values3, 1));

		final double[] values4 = new double[] { 1.0, 3.0, 5.0, 4.0, 7.0, 6.0, 8.0, 8.0, 9.0, 10.0 };

		System.out.println(getDurbinWatson(values4, 1));

		final double[] vals200 = new double[] { 1413,
				1413, 614, 707, 774, 574, 527, 624, 589, 468, 1298, 379, 618, 508, 369, 333, 513, 320, 258, 193, 172, 228, 117, 122, 178, 128, 151, 128,
				129, 153, 101, 126, 136, 103, 115, 131, 119, 185, 141, 122, 97, 137, 194, 129, 122, 140, 147, 140, 255, 141, 173, 138, 118, 141, 110, 101,
				134, 195, 181, 94, 137, 148, 110, 100, 154, 119, 130, 143, 139, 188, 124, 93, 116, 126, 112, 146, 109, 121, 107, 119, 97, 96, 129, 107, 143, 155,
				115, 84, 84, 89, 90, 85, 87, 106, 89, 84, 93, 123, 131, 104, 90, 107, 115, 129, 152, 126, 157, 89, 114, 140, 112, 125, 149, 93, 122, 130, 154, 165,
				151, 88, 107, 105, 183, 127, 132, 126, 96, 99, 128, 139, 75, 81, 71, 73, 126, 138, 132, 94, 66, 89,
				113, 77, 87, 80, 76, 77, 75, 84, 78, 110, 98, 119, 122, 77, 66, 63, 77, 65, 105, 99, 124,
				199, 114, 78, 83, 96, 76, 106,
				87, 94, 100, 71, 76, 92, 97, 54,
				114, 87, 105, 78, 106, 71, 70, 78, 93, 101, 103,
				104, 102, 60, 86, 75, 80, 59,
				84, 54, 117, 78, 68, 66
		};
		final SimpleRegression regression = getRegression(vals200);
		System.out.println(regression.getIntercept() + " " + regression.getSlope());

		System.out.println(DurbinWatson.getDurbinWatson(vals200, 1));
		// vals.ad(new Double[]{)
	}
}
