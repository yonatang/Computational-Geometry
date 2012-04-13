package idc.gc.strategy;

import idc.gc.Benchmarker;
import idc.gc.Utils;
import idc.gc.dt.Circle;
import idc.gc.dt.Point;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlowingStrategy implements Strategy {

	private boolean silent = false;

	private boolean logStep = false;

	private int stepSize = 2;

	@Override
	public Set<Circle> execute(Set<Point> points, int n) {
		if (!silent)
			System.out.println("Executing on " + points.size() + " points with " + n + " circles");

		Set<Circle> circles = new HashSet<Circle>();
		Benchmarker b = new Benchmarker();
		n = 11;
		BSLocator locator = new BSLocator1(n);
		while (locator.hasNext()) {
			Circle c = new Circle(locator.next(), 5);
			circles.add(c);
		}
		 circles.clear();
		 circles.add(new Circle(new Point(25,25),5));
		 circles.add(new Circle(new Point(25,75),5));
		 circles.add(new Circle(new Point(50,25),5));
		 circles.add(new Circle(new Point(50,75),5));
		 circles.add(new Circle(new Point(75,25),5));
		 circles.add(new Circle(new Point(75,75),5));
		 
		 circles.add(new Circle(new Point(25,50),5));
		 circles.add(new Circle(new Point(37.5,50),5));
		 circles.add(new Circle(new Point(50,50),5));
		 circles.add(new Circle(new Point(62.5,50),5));
		 circles.add(new Circle(new Point(75,50),5));

//		circles.clear();
//
//		 circles.add(new Circle(new Point(25,50),5));
//		 circles.add(new Circle(new Point(50,50),5));
//		 circles.add(new Circle(new Point(75,50),5));
//
//		 circles.add(new Circle(new Point(25,25),5));
//		 circles.add(new Circle(new Point(41.6,25),5));
//		 circles.add(new Circle(new Point(58.2,25),5));
//		 circles.add(new Circle(new Point(75,25),5));
//		 
//		 circles.add(new Circle(new Point(25,75),5));
//		 circles.add(new Circle(new Point(41.6,75),5));
//		 circles.add(new Circle(new Point(58.2,75),5));
//		 circles.add(new Circle(new Point(75,75),5));

//		 circles.add(new Circle(new Point(66,33),5));
//		 circles.add(new Circle(new Point(50,50),5));
//		 circles.add(new Circle(new Point(50,33),5));
//		 circles.add(new Circle(new Point(50,66),5));
//		 circles.add(new Circle(new Point(33,50),5));
//		 circles.add(new Circle(new Point(66,50),5));
		//
//		 circles.clear();
//		 circles.add(new Circle(new Point(33,33),5));
//		 circles.add(new Circle(new Point(66,66),5));
//		 circles.add(new Circle(new Point(33,66),5));
//		 circles.add(new Circle(new Point(66,33),5));
//		 circles.add(new Circle(new Point(50,33),5));
//		 circles.add(new Circle(new Point(50,66),5));
//		 circles.add(new Circle(new Point(50,50),5));

		final double maxRaduisSize = Math.sqrt(2) * StrategyData.FIELD_SIZE;
		while (true) {
			Set<Point> maxGroup = b.maxGroup(points, circles);
			if (maxGroup.isEmpty())
				break;
			Point rep = maxGroup.iterator().next();
			Set<Circle> workingSet = b.findExcludingShapes(rep, circles);
			boolean improved = false;
			Map<Circle, Integer> steps = new HashMap<Circle, Integer>();
			if (logStep) {
				for (Circle c : workingSet) {
					steps.put(c, stepSize);
				}
			}
			while (true) {
				if (workingSet.isEmpty())
					break;
				Circle c = Utils.smallest(workingSet, Circle.RADUIS_COMP);
				int score = b.score(points, circles);
				if (c.getR() > maxRaduisSize) {
					workingSet.remove(c);
					break;
				}
				int thisStepSize;
				if (logStep) {
					thisStepSize = steps.get(c);
				} else {
					thisStepSize = stepSize;
				}
				c.setR(c.getR() + thisStepSize);

				int newScore = b.score(points, circles);
				if (newScore > score) {
					if (logStep) {
						c.setR(c.getR() - thisStepSize);
						if (thisStepSize == 1) {
							workingSet.remove(c);
						} else {
							steps.put(c, Math.max(thisStepSize / 2, 1));
						}
					} else {
						c.setR(c.getR() - thisStepSize);
						workingSet.remove(c);
					}
				} else if (newScore < score) {
					improved = true;
				}
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

	public int getStepSize() {
		return stepSize;
	}

	public void setStepSize(int stepSize) {
		this.stepSize = stepSize;
	}

}
