package idc.gc.guards;

import il.ac.idc.jdt.Point;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GuardGraph {
	private Map<Guard, Set<Target>> guardToTarget = new HashMap<Guard, Set<Target>>();
	private Map<Target, Set<Guard>> targetToGuards = new HashMap<Target, Set<Guard>>();


	public GuardGraph(Collection<Point> guards, Collection<Point> targets) {
		for (Point guard : guards) {
			guardToTarget.put(new Guard(guard), new HashSet<Target>());
		}
		for (Point target : targets) {
			targetToGuards.put(new Target(target), new HashSet<Guard>());
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

}
