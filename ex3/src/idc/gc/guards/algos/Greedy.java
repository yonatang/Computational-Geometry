package idc.gc.guards.algos;

import idc.gc.guards.dt.Guard;
import idc.gc.guards.dt.GuardGraph;
import idc.gc.guards.dt.Target;
import idc.gc.guards.dt.Utils;
import il.ac.idc.jdt.Point;

import java.util.HashSet;
import java.util.Set;

public class Greedy implements Algorithm {

	public Set<Point> guard(GuardGraph graph) {

		Set<Point> result = new HashSet<Point>();
		while (!graph.getTargets().isEmpty()) {
			Guard maxGuard = Utils.maxTargetGuard(graph);
			if (maxGuard == null) {
				System.out.println("Few points cannot be guarded.");
				System.out.println(graph.getTargets());
				return null;
			}
			result.add(maxGuard.getPoint());
			Set<Target> guardedTargets = graph.getTargets(maxGuard);
			graph.removeGuard(maxGuard);
			for (Target target : guardedTargets) {
				graph.removeTarget(target);
			}
		}
		return result;
	}
}
