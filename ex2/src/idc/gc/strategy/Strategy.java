package idc.gc.strategy;

import idc.gc.dt.Circle;
import idc.gc.dt.Point;

import java.util.Set;

public interface Strategy {

	public Set<Circle> execute(Set<Point> points, int n);
	
	public String getName();
}
