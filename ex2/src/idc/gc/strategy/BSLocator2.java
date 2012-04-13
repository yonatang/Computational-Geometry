package idc.gc.strategy;

import java.util.Arrays;
import java.util.List;

import idc.gc.dt.Point;

public class BSLocator2 extends BSLocator {

	private List<Point> points;
	public BSLocator2(int n) {
		super(n);
		if (n < 1 || n > 63)
			throw new IllegalArgumentException("n should be between 1 to 63");
		switch (n) {
		case 1:
			points=Arrays.asList(new Point(50,50));
			break;
		case 2:
			points=Arrays.asList(new Point(25,50),new Point(75,50));
			break;
		case 3:
			points=Arrays.asList(new Point(25,50),new Point(75,50));
			break;
		default:
			break;
		}
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
