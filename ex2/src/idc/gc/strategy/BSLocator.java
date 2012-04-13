package idc.gc.strategy;

import idc.gc.dt.Point;

import java.util.Iterator;

public abstract class BSLocator implements Iterator<Point> {

	private final int n;

	public BSLocator(int n) {
		this.n = n;
	}

	protected int getN() {
		return n;
	}
}
