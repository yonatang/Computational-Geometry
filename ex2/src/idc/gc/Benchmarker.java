package idc.gc;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Benchmarker {

	private static double dist(Point p1, Point p2) {
		double x = p1.getX() - p2.getX();
		double y = p1.getY() - p2.getY();
		return Math.sqrt(x * x + y * y);
	}

	private class MutableInteger {

		public MutableInteger(int value) {
			this.value = value;
		}

		private int value;

		public int getValue() {
			return value;
		}

		public int inc() {
			return value++;
		}

	}

	public int score(Set<Point> points, Set<Circle> circles) {
		Circle[] cArr = new Circle[circles.size()];
		{
			int i = 0;
			for (Circle c : circles) {
				cArr[i] = c;
				i++;
			}
		}

		HashMap<String, MutableInteger> results = new HashMap<String, Benchmarker.MutableInteger>();
		for (Point p : points) {
			StringBuilder sb = new StringBuilder();
			sb.append(';');
			for (int i = 0; i < cArr.length; i++) {
				double dist=dist(cArr[i].getP(), p);
				if (dist < cArr[i].getR()) {
					sb.append(i).append(';');
				}
			}
			String circleStr = sb.toString();
			if (results.containsKey(circleStr)) {
				results.get(circleStr).inc();
			} else {
				results.put(circleStr, new MutableInteger(1));
			}
		}
		int max = Integer.MIN_VALUE;
		for (MutableInteger mi : results.values()) {
			if (mi.getValue() > max) {
				max = mi.getValue();
			}
		}
		return max;
	}
	
	public Collection<Set<Point>> divider(Set<Point> points, Set<Circle> circles){
		Circle[] cArr = new Circle[circles.size()];
		{
			int i = 0;
			for (Circle c : circles) {
				cArr[i] = c;
				i++;
			}
		}
		HashMap<String, Set<Point>> results = new HashMap<String, Set<Point>>();
		for (Point p : points) {
			StringBuilder sb = new StringBuilder();
			sb.append(';');
			for (int i = 0; i < cArr.length; i++) {
				double dist=dist(cArr[i].getP(), p);
				if (dist < cArr[i].getR()) {
					sb.append(i).append(';');
				}
			}
			String circleStr = sb.toString();
			if (results.containsKey(circleStr)) {
				results.get(circleStr).add(p);
			} else {
				Set<Point> part=new HashSet<Point>();
				part.add(p);
				results.put(circleStr, part);
			}
		}
		return results.values();
	}
}
