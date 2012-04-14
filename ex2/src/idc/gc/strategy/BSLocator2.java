package idc.gc.strategy;

import idc.gc.dt.Board;
import idc.gc.dt.Point;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class BSLocator2 extends BSLocator {

	private List<Point> points;

	private Iterator<Point> iter;
	public BSLocator2(final int n) {
		super(n);
		if (n < 1 || n > 63)
			throw new IllegalArgumentException("n should be between 1 to 63");

		//Already experiment with those locations, to find out the're the best. Dirty, but works.
		if (n < 9) {
			switch (n) {
			case 1:
				points = Arrays.asList(new Point(50, 50));
				break;
			case 2:
				points = Arrays.asList(new Point(33, 50), new Point(66, 50));
				break;
			case 3:
				points = Arrays.asList(new Point(33, 33), new Point(66, 50), new Point(33, 66));
				break;
			case 4:
				points = Arrays.asList(new Point(33, 33), new Point(33, 66), new Point(66, 33), new Point(66, 66));
				break;
			case 5:
				points = Arrays.asList(new Point(33, 33), new Point(33, 66), new Point(66, 33), new Point(66, 66), new Point(50,50));
				break;
			case 6:
				points = Arrays.asList(new Point(33, 33), new Point(33, 66), new Point(66, 33), new Point(66, 66), new Point(50,33), new Point(50,66));
				break;
			case 7:
				points = Arrays.asList(new Point(33, 33), new Point(33, 66), new Point(66, 33), new Point(66, 66), new Point(50,33), new Point(50,66), new Point(50,50));
				break;
			case 8:
				points = Arrays.asList(new Point(33, 33), new Point(33, 66), new Point(66, 33), new Point(66, 66), new Point(50,33), new Point(50,66), new Point(66,50), new Point(33,50));
				break;
			}
			iter=points.iterator();
			return;
		}

		int rows = (int) Math.floor(Math.sqrt(n));

		//Construct a rows*rows board, and incrementally add points.
		Board board = new Board(rows);

		int remains = n - rows * rows;

		// remains <= rows*2
		for (int i = 0; i < remains; i++) {
			board.addToRow((i + (rows / 2)) % rows);
		}
		
		points=board.asSetOfPoints();
		iter=points.iterator();

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
