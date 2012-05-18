package idc.gc.guards.algos;

import idc.gc.guards.dt.Guard;
import idc.gc.guards.dt.GuardGraph;
import idc.gc.guards.dt.Target;
import idc.gc.guards.dt.Utils;
import il.ac.idc.jdt.Point;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CHGuard implements Algorithm {

	@Override
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

			int threshold = max - (int) ((float) max * 0.1);

			double maxCH = Double.NEGATIVE_INFINITY;
			maxGuard = null;

			for (Guard g : graph.getGuards()) {
				if (graph.getTargets(g).size() >= threshold) {
					List<Point> ch = CH.findHull(Utils.targetsToPoint(graph.getTargets(g)));
					double canSize = getPolySize(ch);
					if (canSize > maxCH) {
						maxGuard = g;
						maxCH = canSize;
					}
				}
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

	private double getPolySize(List<Point> points) {
		double size = 0;

		for (int i = 0; i < points.size() - 2; i++) {
			Point p1 = points.get(i);
			Point p2 = points.get(i + 1);
			size += (p1.getX() * p2.getY() - p1.getY() * p2.getX());
		}
		size = size / 2;
		return size;
	}
}
