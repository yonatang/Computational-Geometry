package idc.gc.guards;

import idc.gc.guards.algos.Algorithm;
import idc.gc.guards.algos.BruteForce;
import idc.gc.guards.algos.CHGuard;
import idc.gc.guards.algos.Greedy;
import idc.gc.guards.algos.Ungreedy;
import idc.gc.guards.dt.GuardGraph;
import idc.gc.guards.dt.Utils;
import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.IOParsers;
import il.ac.idc.jdt.Point;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Guards {

	public void run() throws IOException {
		Timer timer = new Timer();
		System.out.print("Loading triangulation... ");
		timer.start();
		DelaunayTriangulation dt = new DelaunayTriangulation(IOParsers.readPoints("./samples/t13000.tsin"));
		timer.stop();
		System.out.println("Done in " + timer.durationStr());
		List<Point> targets = IOParsers.readPoints("./samples/c1.tsin");
		List<Point> guards = IOParsers.readPoints("./samples/g1.tsin");

		System.out.println("Solving for " + guards.size() + " guards with " + targets.size() + " targets");
		System.out.print("Generating guarding graph... ");
		timer.start();
		GuardGraph originalGraph = new GuardGraph(guards, targets);
		timer.stop();
		System.out.println("Done in " + timer.durationStr());
		System.out.println("Calculating guarding visibility... ");
		timer.start();
		Utils.calcVisibilities(dt, originalGraph);
		timer.stop();
		System.out.println("Done in " + timer.durationStr());
		if (!Utils.verifyCoverage(originalGraph, false)) {
			System.out.println("Guards cannot cover targets");
			return;
		}
		List<Algorithm> algos = new ArrayList<Algorithm>(Arrays.asList(new Greedy(), new CHGuard(), new Ungreedy()));

		if (guards.size() < 23) {
			algos.add(new BruteForce());
		}

		for (Algorithm algo : algos) {
			Set<Point> result;
			System.out.println("Using " + algo.getClass().getSimpleName());
			System.out.println("------------------------------------------");
			GuardGraph graph = originalGraph.deepCopy();
			timer.start();
			result = algo.guard(graph);
			timer.stop();

			for (Point p : result) {
				System.out.println(p);
			}
			System.out.println(String.format("Took %s. Got a cover of %d", timer.durationStr(), result.size()));
			System.out.println();
		}
		return;
	}

	public static void main(String[] args) throws IOException {
		Guards g = new Guards();
		g.run();
	}
}
