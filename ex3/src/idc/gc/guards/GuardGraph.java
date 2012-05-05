package idc.gc.guards;

import il.ac.idc.jdt.Point;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class GuardGraph {
	private Map<Guard, Set<Target>> guardToTarget = new HashMap<Guard, Set<Target>>();
	private Map<Target, Set<Guard>> targetToGuards = new HashMap<Target, Set<Guard>>();

	public GuardGraph deepCopy() {
		GuardGraph g2 = new GuardGraph(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
		for (Entry<Guard, Set<Target>> ent : guardToTarget.entrySet()) {
			g2.guardToTarget.put(ent.getKey(), new HashSet<Target>());
			for (Target t : ent.getValue()) {
				g2.guardToTarget.get(ent.getKey()).add(t);
			}
		}

		for (Entry<Target, Set<Guard>> ent : targetToGuards.entrySet()) {
			g2.targetToGuards.put(ent.getKey(), new HashSet<Guard>());
			for (Guard t : ent.getValue()) {
				g2.targetToGuards.get(ent.getKey()).add(t);
			}
		}
		return g2;
	}

	public GuardGraph(Collection<Point> guards, Collection<Point> targets) {
		int count;
		count = 0;
		for (Point guard : guards) {
			guardToTarget.put(new Guard(guard, count++), new HashSet<Target>());
		}
		count = 0;
		for (Point target : targets) {
			targetToGuards.put(new Target(target, count++), new HashSet<Guard>());
		}

	}

	public Set<Target> getTargets() {
		return Collections.unmodifiableSet(targetToGuards.keySet());
	}

	public Set<Guard> getGuards() {
		return Collections.unmodifiableSet(guardToTarget.keySet());
	}

	public Set<Target> getTargets(Guard guard) {
		if (!guardToTarget.containsKey(guard))
			throw new IllegalArgumentException(guard + " not exists in graph");
		return Collections.unmodifiableSet(guardToTarget.get(guard));
	}

	public Set<Guard> getGuards(Target target) {
		if (!targetToGuards.containsKey(target))
			throw new IllegalArgumentException(target + " not exists in graph");
		return Collections.unmodifiableSet(targetToGuards.get(target));
	}

	public void removeTarget(Target target) {
		for (Guard guard : targetToGuards.get(target)) {
			guardToTarget.get(guard).remove(target);
		}
		targetToGuards.remove(target);
	}

	public void removeGuard(Guard guard) {
		for (Target target : guardToTarget.get(guard)) {
			targetToGuards.get(target).remove(guard);
		}
		guardToTarget.remove(guard);
	}

	public void addGuardToTarget(Guard guard, Target target) {
		if (!guardToTarget.containsKey(guard))
			throw new IllegalArgumentException("Guard " + guard + " not exists on the graph");
		if (!targetToGuards.containsKey(target))
			throw new IllegalArgumentException("Target " + target + " not exists on the graph");

		guardToTarget.get(guard).add(target);
		targetToGuards.get(target).add(guard);
	}

	@Override
	public String toString() {
		return "GuardGraph [\n\tguardToTarget=" + guardToTarget + ",\n\ttargetToGuards=" + targetToGuards + "]";
	}

}
