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

	public <T extends Shape> Set<T> findIncludingShapes(Point point, Set<T> shapes) {
		Set<T> result = new HashSet<T>();
		for (T shape : shapes) {
			if (shape.contains(point)) {
				result.add(shape);
			}
		}
		return result;
	}

	public <T extends Shape> Set<T> findExcludingShapes(Point point, Set<T> shapes) {
		Set<T> result = new HashSet<T>();
		for (T shape : shapes) {
			if (!shape.contains(point)) {
				result.add(shape);
			}
		}
		return result;
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

		HashMap<Long, MutableInteger> results = new HashMap<Long, Benchmarker.MutableInteger>();
		for (Point p : points) {
			long l = 1;
			for (int i = 0; i < cArr.length; i++) {
				l *= 2;
				if (cArr[i].contains(p)) {
					l += 1;
				}
			}
			if (results.containsKey(l)) {
				results.get(l).inc();
			} else {
				results.put(l, new MutableInteger(1));
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
			if (set.size() > max) {
				result = set;
				max = set.size();
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
		HashMap<Long, Set<Point>> results = new HashMap<Long, Set<Point>>();
		for (Point p : points) {
			long l = 1;
			for (int i = 0; i < shapeArr.length; i++) {
				l *= 2;
				if (shapeArr[i].contains(p)) {
					l += 1;
				}
			}
			if (results.containsKey(l)) {
				results.get(l).add(p);
			} else {
				Set<Point> part = new HashSet<Point>();
				part.add(p);
				results.put(l, part);
			}
		}
		return results.values();
	}
}
