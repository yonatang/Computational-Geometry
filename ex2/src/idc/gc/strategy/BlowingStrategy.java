package idc.gc.strategy;

import idc.gc.Benchmarker;
import idc.gc.Utils;
import idc.gc.dt.Circle;
import idc.gc.dt.Point;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class BlowingStrategy implements Strategy {

	private final Random RND = new Random();

	private boolean silent = false;

	private boolean randomLocation = false;

	@Override
	public Set<Circle> execute(Set<Point> points, int n) {
		if (!silent)
			System.out.println("Executing on " + points.size() + " points with " + n + " circles");

		Set<Circle> circles = new HashSet<Circle>();
		Benchmarker b = new Benchmarker();

		if (!randomLocation) {
			int[] div = Utils.bestDividion(n);
			for (int i = 0; i < div.length; i++) {
				double xStep = StrategyData.FIELD_SIZE / div.length;
				double yStep = StrategyData.FIELD_SIZE / div[i];

				for (int j = 0; j < div[i]; j++) {
					Circle c = new Circle(new Point(xStep / 2 + i * xStep, yStep / 2 + j * yStep), 5);
					circles.add(c);
				}
			}
		} else {
			for (int i = 0; i < n; i++) {
				Circle c = new Circle(new Point(RND.nextDouble() * StrategyData.FIELD_SIZE, RND.nextDouble()
						* StrategyData.FIELD_SIZE), 1);
				circles.add(c);
			}
		}
		while (true) {
			Set<Point> maxGroup = b.maxGroup(points, circles);
			if (maxGroup.isEmpty())
				break;
			Point rep = maxGroup.iterator().next();
			Set<Circle> workingSet = b.findExcludingShapes(rep, circles);
			boolean improved = false;
			while (true) {
				Circle c = Utils.smallest(workingSet, Circle.RADUIS_COMP);
				System.out.println("Working on " + c);
				// while (true) {
				Circle backup = c.deepClone();
				int score = b.score(points, circles);
				if (c.getR() > 100) {
					workingSet.remove(c);
					break;
				}
				c.setR(c.getR() + 1);
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
