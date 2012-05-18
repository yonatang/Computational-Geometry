package idc.gc.guards.algos;

import idc.gc.guards.dt.GuardGraph;
import il.ac.idc.jdt.Point;

import java.util.Set;

public interface Algorithm {

	Set<Point> guard(GuardGraph graph);
}
