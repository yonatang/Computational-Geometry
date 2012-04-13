package idc.gc;

import java.util.Collection;
import java.util.Comparator;

public class Utils {

	public static <T> T smallest(Collection<T> set, Comparator<T> comperator) {
		T minimal = null;
		for (T t : set) {
			if (minimal == null)
				minimal = t;
			else {
				if (comperator.compare(minimal, t) > 0) {
					minimal = t;
				}
			}
		}
		return minimal;
	}

	public static <T> T largest(Collection<T> set, Comparator<T> comperator) {
		T maximal = null;
		for (T t : set) {
			if (maximal == null)
				maximal = t;
			else {
				if (comperator.compare(maximal, t) < 0) {
					maximal = t;
				}
			}
		}
		return maximal;
	}
}
