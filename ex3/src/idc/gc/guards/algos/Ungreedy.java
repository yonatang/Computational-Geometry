package idc.gc.guards.algos;

import idc.gc.guards.dt.Guard;
import idc.gc.guards.dt.GuardGraph;
import idc.gc.guards.dt.Target;
import idc.gc.guards.dt.Utils;
import il.ac.idc.jdt.Point;

import java.util.HashSet;
import java.util.Set;

public class Ungreedy implements Algorithm {

	@Override
	public Set<Point> guard(final GuardGraph graph) {
		Set<Guard> result = new HashSet<Guard>();
		while (true) {
			result.addAll(Utils.filterMandatory(graph));
			if (graph.getTargets().isEmpty())
				break;

			Guard minGuard = Utils.minTargetGuard(graph);

			Set<Target> targets = graph.getTargets(minGuard);
			Target target = Utils.pickOne(targets);
			Set<Guard> guards = graph.getGuards(target);

			Guard maxGuard = Utils.maxTargetGuard(guards, graph);

			result.add(maxGuard);
			Set<Target> guardedTargets = graph.getTargets(maxGuard);
			graph.removeGuard(maxGuard);
			for (Target toRemoveTarget : guardedTargets) {
				graph.removeTarget(toRemoveTarget);
			}
		}

		return Utils.guardsToPoints(result);
	}
}
