package idc.gc.guards;

import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.IOParsers;
import il.ac.idc.jdt.Point;
import il.ac.idc.jdt.extra.los.Visibility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Guards {

	private static final Random RND = new Random();

	private GuardGraph getGraph(DelaunayTriangulation dt, List<Point> targets, List<Point> guards) {
		// GuardGraph graph = new GuardGraph(guards, targets);
		// Utils.calcVisibilities(dt, graph);

		// guards = Arrays.asList(new Point(1, 1), new Point(2, 2), new Point(3,
		// 3));
		// targets = Arrays.asList(new Point(1, 1), new Point(2, 2), new
		// Point(3, 3), new Point(4, 4), new Point(5, 5), new Point(6, 6));
		// GuardGraph graph = new GuardGraph(guards, targets);
		// graph.addGuardToTarget(new Guard(new Point(1,1)), new Target(new
		// Point(1,1)));
		// graph.addGuardToTarget(new Guard(new Point(1,1)), new Target(new
		// Point(2,2)));
		// graph.addGuardToTarget(new Guard(new Point(1,1)), new Target(new
		// Point(3,3)));
		//
		// graph.addGuardToTarget(new Guard(new Point(3,3)), new Target(new
		// Point(4,4)));
		// graph.addGuardToTarget(new Guard(new Point(3,3)), new Target(new
		// Point(5,5)));
		// graph.addGuardToTarget(new Guard(new Point(3,3)), new Target(new
		// Point(6,6)));
		//
		// graph.addGuardToTarget(new Guard(new Point(2,2)), new Target(new
		// Point(2,2)));
		// graph.addGuardToTarget(new Guard(new Point(2,2)), new Target(new
		// Point(3,3)));
		// graph.addGuardToTarget(new Guard(new Point(2,2)), new Target(new
		// Point(4,4)));
		// graph.addGuardToTarget(new Guard(new Point(2,2)), new Target(new
		// Point(5,5)));

		int nTargets = RND.nextInt(70)+1;
		int nGuards = RND.nextInt(250)+1;
		guards = new ArrayList<Point>();
		targets = new ArrayList<Point>();
		for (int i = 0; i < nGuards; i++) {
			guards.add(new Point(RND.nextDouble() * 10000, RND.nextDouble() * 10000));
		}
		for (int i = 0; i < nTargets; i++) {
			targets.add(new Point(RND.nextDouble() * 10000, RND.nextDouble() * 10000));
		}
		GuardGraph graph = new GuardGraph(guards, targets);

		for (Target t : graph.getTargets()) {
			graph.addGuardToTarget(new Guard(Utils.pickOne(guards)), t);
			int nConn = (int) (RND.nextGaussian() * 30);
			for (int i = 0; i < nConn; i++) {
				graph.addGuardToTarget(new Guard(Utils.pickOne(guards)), t);
			}
		}

		return graph;
	}

	private Map<Guard, Set<Target>> guardToTarget = new HashMap<Guard, Set<Target>>();
	private Map<Target, Set<Guard>> targetToGuards = new HashMap<Target, Set<Guard>>();

	public void run() throws IOException {
		DelaunayTriangulation dt = new DelaunayTriangulation(IOParsers.readPoints("./samples/t13000.tsin"));
		List<Point> targets = IOParsers.readPoints("./samples/c1.tsin");
		List<Point> guards = IOParsers.readPoints("./samples/g1.tsin");

		// Algorithm algo = new FilteredGreedy();
		Algorithm[] algos = new Algorithm[] { new Greedy(), new Ungreedy(), new FilteredGreedy() };

		// Algorithm algo = new Ungreedy();

		// double maxX = dt.getBoundingBox().maxX();
		// double minX = dt.getBoundingBox().minX();
		// double maxY = dt.getBoundingBox().maxY();
		// double minY = dt.getBoundingBox().minY();
		// Random r = new Random();
		// for (int i = 0; i < 50; i++) {
		// guards.add(new Point(r.nextDouble() * (maxX - minX) + minX,
		// r.nextDouble() * (maxY - minY) + minY, 30));
		// }

		GuardGraph originalGraph = getGraph(dt, targets, guards);

		for (Algorithm algo : algos) {
			Set<Point> result;
			System.out.println("Using " + algo.getClass().getSimpleName());
			System.out.println("------------------------------------------");
			GuardGraph graph = originalGraph.deepCopy();
			System.out.println(graph);

			if (!Utils.verifyCoverage(dt, guards, targets, false)) {
				System.out.println("Guards cannot cover targets");
			}
			result = algo.guard(graph);

			for (Point p : result) {
				System.out.println(p);
			}
			System.out.println(result.size());
			System.out.println();
			// System.out.println("Verification: " + Utils.verifyCoverage(dt,
			// result, targets, false));
			//
			// // Utils.verifyCoverage(dt, new HashSet<Point>(), targets,true);
			// System.out.print("Verify local minimality: ");
			// boolean ok = true;
			// for (Point p : result) {
			// Set<Point> result2 = new HashSet<Point>(result);
			// result2.remove(p);
			// if (Utils.verifyCoverage(dt, result2, targets)) {
			// System.out.println("Guard " + p + " is redundent");
			// ok = false;
			// }
			// }
			// System.out.println(ok);
			// System.out.println();
		}

		// System.out.println();
		// System.out.println("Added the following points:");
		// for (Point p : guards) {
		// System.out.println(p);
		// }

		return;

		// for (Point target : targets) {
		// targetToGuards.put(target, new HashSet<Point>());
		// }
		// for (Point guard : guards) {
		// guardToTarget.put(guard, new HashSet<Point>());
		// }
		//
		// for (Point guard : guards) {
		// for (Point target : targets) {
		// if (v.isVisible(v.computeSection(dt, guard, target), guard.getZ(),
		// target.getZ())) {
		// guardToTarget.get(guard).add(target);
		// targetToGuards.get(target).add(guard);
		// }
		// }
		// }
		//
		// result = new HashSet<Point>();
		// while (true) {
		// if (targets.isEmpty()) {
		// System.out.println("Done!");
		// break;
		// }
		// boolean mod = false;
		// Set<Point> newTargets = new HashSet<Point>();
		// for (Point target : targets) {
		// Set<Point> potentialGuards = targetToGuards.get(target);
		// if (potentialGuards.size() == 0) {
		// System.out.println("No set will guard the thing, beause target " +
		// target + " is invisible for all guards.");
		// System.exit(0);
		// }
		// if (potentialGuards.size() == 1) {
		// mod = true;
		// result.add(potentialGuards.iterator().next());
		// newTargets.add(target);
		// }
		// }
		//
		// for (Point target : newTargets) {
		// targets.remove(target);
		// for (Point guard : guardToTarget.keySet()) {
		// if (guardToTarget.get(guard).contains(target)) {
		// guardToTarget.get(guard).remove(target);
		// }
		// }
		// }
		//
		// if (!mod)
		// break;
		//
		// }
		// for (Point p : result) {
		// System.out.println(p);
		// }
		// System.out.println(result.size());

	}

	public static void main(String[] args) throws IOException {
		Guards g = new Guards();
		g.run();
	}
}
