package idc.gc.guards;

import il.ac.idc.jdt.Point;

import java.util.Set;

public interface Algorithm {

	Set<Point> guard(GuardGraph graph);
}
