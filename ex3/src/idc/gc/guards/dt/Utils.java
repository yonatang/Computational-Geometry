package idc.gc.guards.dt;

import idc.gc.guards.Timer;
import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.Point;
import il.ac.idc.jdt.extra.los.Section;
import il.ac.idc.jdt.extra.los.Visibility;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Utils {

	public static void calcVisibilities(DelaunayTriangulation dt, GuardGraph graph) {
		Visibility v = new Visibility();
		Timer timer = new Timer();
		System.out.println("About to do it for " + graph.getTargets().size() * graph.getGuards().size() + " elements");
		long calc = 0;
		for (Target target : graph.getTargets()) {
			for (Guard guard : graph.getGuards()) {
				timer.start();
				Point targetPoint = target.getPoint();
				Point guardPoint = guard.getPoint();
				Section section = v.computeSection(dt, targetPoint, guardPoint);
				if (v.isVisible(section, targetPoint.getZ(), guardPoint.getZ())) {
					graph.addGuardToTarget(guard, target);
				}
				calc += (long) timer.duration();
			}
		}
		System.out.println(String.format("Average time per couple is %.2f ms", ((double) calc / (double) (graph
				.getTargets().size() * graph.getGuards().size()))));
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
		Set<Guard> guardsToRemove = new HashSet<Guard>();
		for (Guard guard : graph.getGuards()) {
			if (graph.getTargets(guard).isEmpty()) {
				guardsToRemove.add(guard);
			}
		}
		for (Guard g : guardsToRemove) {
			graph.removeGuard(g);
		}
		return mandatoryGuards;
	}

	public static Set<Point> guardsToPoints(Set<Guard> guards) {
		Set<Point> points = new HashSet<Point>();
		for (Guard g : guards) {
			points.add(g.getPoint());
		}
		return points;
	}

	public static Set<Point> targetsToPoint(Set<Target> targets) {
		Set<Point> points = new HashSet<Point>();
		for (Target t : targets) {
			points.add(t.getPoint());
		}
		return points;
	}

	public static Guard maxTargetGuard(GuardGraph graph) {
		return maxTargetGuard(graph.getGuards(), graph);
	}

	public static Guard maxTargetGuard(Set<Guard> guards, GuardGraph graph) {
		int max = Integer.MIN_VALUE;
		Guard maxGuard = null;
		for (Guard guard : guards) {
			if (graph.getTargets(guard).size() > max) {
				maxGuard = guard;
				max = graph.getTargets(guard).size();
			}
		}
		return maxGuard;
	}

	public static <T> T pickOne(Collection<T> col) {
		if (col.isEmpty())
			throw new IllegalArgumentException("Collection must be non empty");
		Random r = new Random();
		int target = r.nextInt(col.size()) + 1;
		Iterator<T> it = col.iterator();
		int i = 0;
		T one = null;
		while (i < target) {
			one = it.next();
			i++;
		}
		return one;
	}

	public static <T> T pickOne(List<T> col) {
		if (col.isEmpty())
			throw new IllegalArgumentException("Collection must be non empty");
		Random r = new Random();
		int target = r.nextInt(col.size());
		return col.get(target);
	}

	public static Guard minTargetGuard(GuardGraph graph) {
		int min = Integer.MAX_VALUE;
		Guard minGuard = null;
		for (Guard guard : graph.getGuards()) {
			if (graph.getTargets(guard).size() < min) {
				minGuard = guard;
				min = graph.getTargets(guard).size();
			}
		}
		return minGuard;
	}

	public static boolean verifyCoverage(GuardGraph graph) {
		return verifyCoverage(graph, false);
	}

	public static boolean verifyCoverage(GuardGraph graph, boolean output) {
		boolean ok = true;

		for (Target target : graph.getTargets()) {
			if (output)
				System.out.println("Point " + target + " is guarded by " + graph.getGuards(target));
			if (graph.getGuards(target).isEmpty())
				ok = false;
		}
		if (output) {
			System.out.println(graph.getTargets().size() + " points are guarded");
		}
		return ok;
	}
}
