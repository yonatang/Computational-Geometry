package idc.gc.strategy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import idc.gc.Utils;
import idc.gc.dt.Point;

public class BSLocator1 extends BSLocator {

	private List<Point> locatons;
	private Iterator<Point> iter;

	public BSLocator1(int n) {
		super(n);
		locatons = new ArrayList<Point>();
		int[] div = Utils.bestDividion(n);
		for (int i = 0; i < div.length; i++) {
			double xStep = StrategyData.FIELD_SIZE / (div.length +1);
			double yStep = StrategyData.FIELD_SIZE / (div[i] +1);

			for (int j = 0; j < div[i]; j++) {
				locatons.add(new Point(xStep  + i * xStep, yStep  + j * yStep));
			}
		}
		iter = locatons.iterator();

	}

	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}

	@Override
	public Point next() {
		return iter.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
