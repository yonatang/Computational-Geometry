package idc.gc;

import idc.gc.dt.Circle;
import idc.gc.dt.Point;
import idc.gc.dt.Shape;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Benchmarker {

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
				if (cArr[i].contains(p)) {
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

	public Set<Point> minGroup(Set<Point> points, Set<? extends Shape> shapes) {
		Collection<Set<Point>> setOfPoints = divider(points, shapes);
		int min = Integer.MAX_VALUE;
		Set<Point> result = null;
		for (Set<Point> set : setOfPoints) {
			if (set.size() < min) {
				result = set;
				min = set.size();
			}
		}
		return result;
	}

	public Set<Point> maxGroup(Set<Point> points, Set<? extends Shape> shapes) {
		Collection<Set<Point>> setOfPoints = divider(points, shapes);
		int max = Integer.MIN_VALUE;
		Set<Point> result = null;
		for (Set<Point> set : setOfPoints) {
			if (set.size()> max) {
				result = set;
				max= set.size();
			}
		}
		return result;
	}

	public Collection<Set<Point>> divider(Set<Point> points, Set<? extends Shape> shapes) {
		Shape[] shapeArr = new Shape[shapes.size()];
		{
			int i = 0;
			for (Shape shape : shapes) {
				shapeArr[i] = shape;
				i++;
			}
		}
		HashMap<String, Set<Point>> results = new HashMap<String, Set<Point>>();
		for (Point p : points) {
			StringBuilder sb = new StringBuilder();
			sb.append(';');
			for (int i = 0; i < shapeArr.length; i++) {
				if (shapeArr[i].contains(p)) {
					sb.append(i).append(';');
				}
			}
			String shapeStr = sb.toString();
			if (results.containsKey(shapeStr)) {
				results.get(shapeStr).add(p);
			} else {
				Set<Point> part = new HashSet<Point>();
				part.add(p);
				results.put(shapeStr, part);
			}
		}
		return results.values();
	}
}
