package idc.gc;

import java.util.Set;

public interface Strategy {

	public Set<Circle> execute(Set<Point> points, int n);
}
