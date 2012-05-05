package idc.gc.guards;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.IOParsers;
import il.ac.idc.jdt.Point;
import il.ac.idc.jdt.extra.los.Visibility;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Guards {

	private Map<Guard, Set<Target>> guardToTarget = new HashMap<Guard, Set<Target>>();
	private Map<Target, Set<Guard>> targetToGuards = new HashMap<Target, Set<Guard>>();

	public void run() throws IOException {
		DelaunayTriangulation dt = new DelaunayTriangulation(IOParsers.readPoints("./samples/t13000.tsin"));
		Visibility v = new Visibility();
		List<Point> targets = IOParsers.readPoints("./samples/c1.tsin");
		List<Point> guards = IOParsers.readPoints("./samples/g1.tsin");

		Greedy algo = new Greedy();
		Set<Point> result;
		GuardGraph graph = new GuardGraph(guards, targets);
		Utils.calcVisibilities(dt, graph);
		if (!Utils.verifyCoverage(dt, guards, targets, false)) {
			System.out.println("Guards cannot cover targets");
		}
		result = algo.guard(graph);

		if (true) {
			for (Point p : result) {
				System.out.println(p);
			}
			System.out.println(result.size());
			System.out.println();
			System.out.println("Verification: " + Utils.verifyCoverage(dt, result, targets, false));
			Utils.verifyCoverage(dt, result, targets, true);
			return;
		}

//		for (Point target : targets) {
//			targetToGuards.put(target, new HashSet<Point>());
//		}
//		for (Point guard : guards) {
//			guardToTarget.put(guard, new HashSet<Point>());
//		}
//
//		for (Point guard : guards) {
//			for (Point target : targets) {
//				if (v.isVisible(v.computeSection(dt, guard, target), guard.getZ(), target.getZ())) {
//					guardToTarget.get(guard).add(target);
//					targetToGuards.get(target).add(guard);
//				}
//			}
//		}
//
//		result = new HashSet<Point>();
//		while (true) {
//			if (targets.isEmpty()) {
//				System.out.println("Done!");
//				break;
//			}
//			boolean mod = false;
//			Set<Point> newTargets = new HashSet<Point>();
//			for (Point target : targets) {
//				Set<Point> potentialGuards = targetToGuards.get(target);
//				if (potentialGuards.size() == 0) {
//					System.out.println("No set will guard the thing, beause target " + target + " is invisible for all guards.");
//					System.exit(0);
//				}
//				if (potentialGuards.size() == 1) {
//					mod = true;
//					result.add(potentialGuards.iterator().next());
//					newTargets.add(target);
//				}
//			}
//
//			for (Point target : newTargets) {
//				targets.remove(target);
//				for (Point guard : guardToTarget.keySet()) {
//					if (guardToTarget.get(guard).contains(target)) {
//						guardToTarget.get(guard).remove(target);
//					}
//				}
//			}
//
//			if (!mod)
//				break;
//
//		}
//		for (Point p : result) {
//			System.out.println(p);
//		}
//		System.out.println(result.size());

	}

	public static void main(String[] args) throws IOException {
		Guards g = new Guards();
		g.run();
	}
}
