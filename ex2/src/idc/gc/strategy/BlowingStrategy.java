package idc.gc.strategy;

import idc.gc.Benchmarker;
import idc.gc.Utils;
import idc.gc.dt.Circle;
import idc.gc.dt.Point;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class BlowingStrategy implements Strategy {

	private final Random RND = new Random();

	private boolean silent = false;
	int[][] div = { { 1, 1 }, { 1, 2 }, { 1, 3 }, { 2, 2 }, { 2, 2 }, { 2, 3 }, { 2, 3 } };

	private boolean isPrime(int n) {
		for (int i = 2; i <= (int) Math.floor(Math.sqrt(n)); i++) {
			if (n % i == 0)
				return false;
		}
		return true;
	}

	private int[] bestDividion(int n) {
		if (n == 0)
			return new int[0];
		if (n == 1)
			return new int[] { 1 };
		if (isPrime(n)) {
			int[] d = bestDividion(n - 1);
			int min = Integer.MAX_VALUE;
			int idx = -1;
			for (int i = 0; i < d.length; i++) {
				if (d[i] < min) {
					min = d[i];
					idx = i;
				}
			}
			d[idx]++;
			return d;
		}
		int[] division = null;
		for (int i = 2; i <= (int) Math.floor(Math.sqrt(n)); i++) {
			if (n % i == 0) {
				if (division == null) {
					division = new int[] { i, (int) n / i };
				} else {
					if (Math.abs(division[0] - division[1]) > Math.abs(i - n / i)) {
						division = new int[] { i, (int) n / i };
					}
				}
			}
		}
		int[] d = new int[division[0]];
		for (int i = 0; i < division[0]; i++) {
			d[i] = division[1];
		}
		return d;
	}

	@Override
	public Set<Circle> execute(Set<Point> points, int n) {
		if (!silent)
			System.out.println("Executing on " + points.size() + " points with " + n + " circles");

		n = 10;
		System.out.println(isPrime(n));
		System.out.println(Arrays.toString(bestDividion(n)));
		Set<Circle> circles = new HashSet<Circle>();
		Benchmarker b = new Benchmarker();

		int[] div = bestDividion(n);
		for (int i = 0; i < div.length; i++) {
			double xStep = StrategyData.FIELD_SIZE / div.length;
			double yStep = StrategyData.FIELD_SIZE / div[i];

			for (int j = 0; j < div[i]; j++) {
				Circle c = new Circle(new Point(xStep / 2 + i * xStep, yStep / 2 + j * yStep), 5);
				circles.add(c);
			}
		}
		// for (int i = 0; i < n; i++) {
		// Circle c = new Circle(new Point(RND.nextDouble() *
		// StrategyData.FIELD_SIZE, RND.nextDouble()
		// * StrategyData.FIELD_SIZE), 5);
		// circles.add(c);
		// }
		while (true) {
			Set<Circle> workingSet = new HashSet<Circle>(circles);
			boolean improved = false;
			while (true) {
				Circle c = Utils.smallest(workingSet, Circle.RADUIS_COMP);
				System.out.println("Working on " + c);
				// while (true) {
				Circle backup = c.deepClone();
				int score = b.score(points, circles);
				if (c.getR() > 70) {
					workingSet.remove(c);
					break;
				}
				c.setR(c.getR() + 3);
				int newScore = b.score(points, circles);
				if (newScore > score) {
					c.setR(backup.getR());
					System.out.println("Removing c " + c);
					System.out.println(workingSet.contains(c));
					workingSet.remove(c);
					// break;
				} else if (newScore < score) {
					improved = true;
				}
				// }
				if (workingSet.isEmpty())
					break;
			}
			if (!improved)
				break;
		}

		return circles;
	}

	@Override
	public String getName() {
		return "Blowing Circles";
	}

}
