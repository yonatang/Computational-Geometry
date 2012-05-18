package idc.gc.guards.algos;

import il.ac.idc.jdt.Point;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class CH {

	private static Point sub(Point p1, Point p2) {
		return new Point(p1.getX() - p2.getX(), p1.getY() - p2.getY());
	}

	private static double cross(Point p1, Point p2) {
		return p1.getX() * p2.getY() - p1.getY() * p2.getX();
	}

	// http://www.algorithmist.com/index.php/Monotone_Chain_Convex_Hull.java
	public static Point[] findHull(Point[] points) {
		int n = points.length;

		Point[] workingPoints = Arrays.copyOf(points, n);
		Arrays.sort(workingPoints, new Comparator<Point>() {

			@Override
			public int compare(Point o1, Point o2) {
				if (o1.getX() == o2.getX())
					return Double.compare(o1.getY(), o2.getY());
				else
					return Double.compare(o1.getX(), o2.getX());
			}
		});
		Point[] ans = new Point[2 * n]; // In between we may have a 2n points

		int k = 0;
		int start = 0; // start is the first insertion point

		for (int i = 0; i < n; i++) // Finding lower layer of hull
		{
			Point p = workingPoints[i];
			while (k - start >= 2 && cross(sub(p, ans[k - 1]), sub(p, ans[k - 2])) > 0)
				k--;
			ans[k++] = p;
		}

		k--; // drop off last point from lower layer
		start = k;

		for (int i = n - 1; i >= 0; i--) // Finding top layer from hull
		{
			Point p = workingPoints[i];
			while (k - start >= 2 && cross(sub(p, ans[k - 1]), sub(p, ans[k - 2])) > 0)
				k--;
			ans[k++] = p;
		}
		k--; // drop off last point from top layer

		return Arrays.copyOf(ans, k); // convex hull is of size k
	}

	public static List<Point> findHull(Collection<Point> points) {
		Point[] result = findHull(points.toArray(new Point[0]));
		return Arrays.asList(result);
	}

}
