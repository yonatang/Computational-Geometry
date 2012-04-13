package idc.gc.strategy;

import idc.gc.Benchmarker;
import idc.gc.CH;
import idc.gc.dt.Circle;
import idc.gc.dt.Point;
import idc.gc.dt.Squere;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DivideAndConquerStrategy implements Strategy {

	@Override
	public Set<Circle> execute(Set<Point> points, int n) {
		System.out.println("Executing on "+points.size()+" points with "+n+" circles");
		final Squere ZERO_SQUERE = new Squere(new Point(-0.1, -0.1), StrategyData.FIELD_SIZE + 0.1);
		Set<Squere> squeres = new HashSet<Squere>();

		squeres.add(ZERO_SQUERE);

		for (int i = 0; i < n; i++) {
			Set<Point> largestSet = findLargestIntersect(squeres, points);
			if (largestSet == null || largestSet.size() == 0)
				break;

			List<Point> ch = CH.findHull(largestSet);
			double sum = 0;
			for (int j = 0; j < ch.size() - 1; j++) {
				Point thisPoint = ch.get(j);
				Point nextPoint = ch.get(j + 1);
				sum += thisPoint.getX() * nextPoint.getY() - thisPoint.getY() - nextPoint.getX();
			}
			sum=Math.abs(sum*0.5);

			double edge = Math.min(Math.sqrt(sum),StrategyData.FIELD_SIZE/2);
			Point bestPoint = null;
			int min = Integer.MAX_VALUE;

			for (Point p : largestSet) {
				Squere s = new Squere(p.leftUpEps(), edge+0.2);
				if (squeres.contains(s))
					continue;
				squeres.add(s);
				Set<Point> canidate = findLargestIntersect(squeres, points);
				if (canidate.size() < min) {
					bestPoint = p;
					min = canidate.size();
				}
				squeres.remove(s);
			}
			if (bestPoint != null){
				Squere newSquere=new Squere(bestPoint.leftUpEps(), edge+0.2);
				squeres.add(newSquere);
			}
		}
		squeres.remove(ZERO_SQUERE);
		Set<Circle> result = new HashSet<Circle>();

		for (Squere s : squeres) {
			result.add(s.boundingCircle());
		}
		return result;
	}

	private Set<Point> findLargestIntersect(Set<Squere> squeres, Set<Point> points) {
		Benchmarker b = new Benchmarker();
		Collection<Set<Point>> setOfSets = b.divider(points, squeres);

		int max = Integer.MIN_VALUE;
		Set<Point> result = null;
		for (Set<Point> pointSet : setOfSets) {
			if (pointSet.size() > max) {
				result = pointSet;
				max = pointSet.size();
			}
		}
		return result;
	}
	
	@Override
	public String getName() {
		return "Divide and Conquer";
	}

}
