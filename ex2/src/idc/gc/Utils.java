package idc.gc;

import java.util.Collection;
import java.util.Comparator;

public class Utils {

	public static boolean isPrime(int n) {
		for (int i = 2; i <= (int) Math.floor(Math.sqrt(n)); i++) {
			if (n % i == 0)
				return false;
		}
		return true;
	}

	public static int[] bestDividion(int n) {
		if (n == 0)
			return new int[0];
		if (n == 1)
			return new int[] { 1 };
		if (isPrime(n)) {
			int[] d = bestDividion(n - 1);
			int min = Integer.MAX_VALUE;
			int idx = -1;
			for (int i = 0; i < d.length; i++) {
				if (d[i] < min) {
					min = d[i];
					idx = i;
				}
			}
			d[idx]++;
			return d;
		}
		int[] division = null;
		for (int i = 2; i <= (int) Math.floor(Math.sqrt(n)); i++) {
			if (n % i == 0) {
				if (division == null) {
					division = new int[] { i, (int) n / i };
				} else {
					if (Math.abs(division[0] - division[1]) > Math.abs(i - n / i)) {
						division = new int[] { i, (int) n / i };
					}
				}
			}
		}
		int[] d = new int[division[0]];
		for (int i = 0; i < division[0]; i++) {
			d[i] = division[1];
		}
		return d;
	}

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
