package idc.gc.strategy;

import idc.gc.dt.Point;

public class BSLocator2 extends BSLocator {

	public BSLocator2(int n) {
		super(n);
		if (n < 1 || n > 63)
			throw new IllegalArgumentException("n should be between 1 to 63");
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Point next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

}
