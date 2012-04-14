package idc.gc.dt;

import idc.gc.strategy.StrategyData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {
	private final double min;
	private final double max;
	private Point[][] board;

	public Board(int n) {
		board = new Point[n][n];
		double step = StrategyData.FIELD_SIZE / (n + 1);
		min = step;
		max = StrategyData.FIELD_SIZE - step;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				board[i][j] = new Point(step + i * step, step + j * step);
			}
		}
	}

	public void addToRow(int rowIdx) {
		Point[] row = board[rowIdx];
		Point[] newRow = Arrays.copyOf(row, row.length + 1);
		newRow[row.length] = row[0].deepClone();
		double first = row[0].getY();
		double step = (max - min) / (newRow.length - 1);
		for (int i = 0; i < newRow.length; i++) {
			newRow[i].setY(first + i * step);
		}
		board[rowIdx] = newRow;
	}

	public List<Point> asSetOfPoints() {
		List<Point> points = new ArrayList<Point>();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] != null)
					points.add(board[i][j]);
			}
		}
		return points;
	}

}
