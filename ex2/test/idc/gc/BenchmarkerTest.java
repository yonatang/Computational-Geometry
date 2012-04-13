package idc.gc;

import idc.gc.dt.Circle;
import idc.gc.dt.Point;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BenchmarkerTest {

	private Benchmarker b = new Benchmarker();
	private Set<Circle> circles;
	private Set<Point> points;

	@Before
	public void s() {
		circles = new HashSet<Circle>();
		points = new HashSet<Point>();
		Circle c1 = new Circle(new Point(3, 5), 2);
		Circle c2 = new Circle(new Point(6, 5), 2);
		circles.add(c1);
		circles.add(c2);
	}

	@Test
	public void shouldCalcPointOutOfCircles() {
		Point p1 = new Point(2, 2);
		points.add(p1);
		Assert.assertEquals(1, b.score(points, circles));
	}
	
	@Test
	public void shouldCalcTwoPointOutOfCircles() {
		points.add(new Point(2, 2));
		points.add(new Point(2, 1));
		Assert.assertEquals(2, b.score(points, circles));
	}
	
	@Test
	public void shouldCalcPointInCircles() {
		points.add(new Point(2, 5));
		points.add(new Point(2, 1));
		Assert.assertEquals(1, b.score(points, circles));
	}
	
	@Test
	public void shouldCalcPointInCirclesIntersect() {
		points.add(new Point(2, 5));
		points.add(new Point(4, 5));
		points.add(new Point(4.1, 5));
		points.add(new Point(5.1, 5));
		Assert.assertEquals(2, b.score(points, circles));
	}
	
}
