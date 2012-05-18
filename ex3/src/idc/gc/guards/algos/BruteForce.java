package idc.gc.guards.algos;

import idc.gc.guards.dt.Guard;
import idc.gc.guards.dt.GuardGraph;
import idc.gc.guards.dt.Target;
import idc.gc.guards.dt.Utils;
import il.ac.idc.jdt.Point;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BruteForce implements Algorithm {

	public boolean test(GuardGraph graph, Set<Guard> guards, int target) {
		Set<Target> targets = new HashSet<Target>();
		for (Guard g : guards) {
			targets.addAll(graph.getTargets(g));
		}
		return targets.size() == target;
	}

	@Override
	public Set<Point> guard(GuardGraph graph) {

		Set<Guard> best = null;

		List<Guard> guards = new ArrayList<Guard>(graph.getGuards());

		BigInteger bi = BigInteger.valueOf(2).pow(guards.size());

		int totalPoints = graph.getTargets().size();

		System.out.println("Going to run over " + bi);
		for (BigInteger i = BigInteger.ONE; i.compareTo(bi) < 0; i = i.add(BigInteger.ONE)) {
			Set<Guard> canidate = new HashSet<Guard>();
			for (int j = 0; j < i.bitLength(); j++) {
				if (i.testBit(j)) {
					canidate.add(guards.get(j));
				}
			}
			if (test(graph, canidate, totalPoints)) {
				if (best == null || canidate.size() < best.size()) {
					best = canidate;
					System.out.println("Found canidate " + best);
				}
			}
		}
		return Utils.guardsToPoints(best);
	}
}
