package idc.gc.guards;

import il.ac.idc.jdt.Point;

import java.util.HashSet;
import java.util.Set;

public class FilteredGreedy implements Algorithm {

	public Set<Point> guard(GuardGraph graph) {

		Set<Guard> result = new HashSet<Guard>();
		while (!graph.getTargets().isEmpty()) {
			result.addAll(Utils.filterMandatory(graph));
			if (graph.getTargets().isEmpty())
				break;

			int max = Integer.MIN_VALUE;
			Guard maxGuard = null;
			for (Guard guard : graph.getGuards()) {
				if (graph.getTargets(guard).size() > max) {
					maxGuard = guard;
					max = graph.getTargets(guard).size();
				}
			}
			if (maxGuard == null) {
				System.out.println("Few points cannot be guarded.");
				System.out.println(graph.getTargets());
				return null;
			}
			result.add(maxGuard);
			Set<Target> guardedTargets = graph.getTargets(maxGuard);
			graph.removeGuard(maxGuard);
			for (Target target : guardedTargets) {
				graph.removeTarget(target);
			}
		}
		return Utils.guardsToPoints(result);
	}
}
