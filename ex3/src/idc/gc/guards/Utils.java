package idc.gc.guards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.Point;
import il.ac.idc.jdt.extra.los.Section;
import il.ac.idc.jdt.extra.los.Visibility;

public class Utils {

	public static void calcVisibilities(DelaunayTriangulation dt, GuardGraph graph) {
		Visibility v = new Visibility();
		for (Target target : graph.getTargets()) {
			for (Guard guard : graph.getGuards()) {
				Point targetPoint = target.getPoint();
				Point guardPoint = guard.getPoint();
				Section section = v.computeSection(dt, targetPoint, guardPoint);
				if (v.isVisible(section, targetPoint.getZ(), guardPoint.getZ())) {
					graph.addGuardToTarget(guard, target);
				}
			}
		}
	}

	public static Set<Guard> filterMandatory(GuardGraph graph) {
		Set<Guard> mandatoryGuards = new HashSet<Guard>();
		while (true) {
			if (graph.getTargets().isEmpty())
				break;

			Set<Guard> guardsToAdd = new HashSet<Guard>();
			for (Target target : graph.getTargets()) {
				if (graph.getGuards(target).size() == 1) {
					guardsToAdd.add(graph.getGuards(target).iterator().next());
				}
			}
			if (guardsToAdd.isEmpty()) {
				break;
			} else {
				for (Guard guard : guardsToAdd) {
					mandatoryGuards.add(guard);
					Set<Target> guardedTargets = graph.getTargets(guard);
					graph.removeGuard(guard);
					for (Target target : guardedTargets) {
						graph.removeTarget(target);
					}

				}
			}
		}
		System.out.println("Found " + mandatoryGuards.size() + " mandatory guards");
		return mandatoryGuards;
	}

	public static boolean verifyCoverage(DelaunayTriangulation dt, Collection<Point> guards, Collection<Point> targets) {
		return verifyCoverage(dt, guards, targets, false);
	}

	public static boolean verifyCoverage(DelaunayTriangulation dt, Collection<Point> guards, Collection<Point> targets, boolean output) {
		GuardGraph graph = new GuardGraph(guards, targets);
		calcVisibilities(dt, graph);
		boolean ok = true;

		System.out.println();
		for (Target target : graph.getTargets()) {
			if (output)
				System.out.println("Point " + target + " is guarded by " + graph.getGuards(target));
			if (graph.getGuards(target).size() < 0)
				ok = false;
		}
		if (output) {
			System.out.println(graph.getTargets().size() + " points are guarded");
		}
		return ok;
	}
}
