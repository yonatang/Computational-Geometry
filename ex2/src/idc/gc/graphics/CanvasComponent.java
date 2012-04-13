package idc.gc.graphics;

import idc.gc.Benchmarker;
import idc.gc.dt.Circle;
import idc.gc.dt.Point;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class CanvasComponent extends JComponent {

	private Set<Point> points;
	private Set<Circle> circles;
	private Set<Point> largestSet;
	private Set<Point> smallestSet;
	private Map<Point, Integer> pointSetSizes = new HashMap<Point, Integer>();
	private Map<Rectangle, Integer> rectsMap = new HashMap<Rectangle, Integer>();
	private Bus bus;

	public CanvasComponent(Bus bus, Set<Point> points, Set<Circle> circles) {
		super();
		this.bus = bus;
		this.points = points;
		this.circles = circles;
		Benchmarker b = new Benchmarker();
		Collection<Set<Point>> dividerResult = b.divider(points, circles);

		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for (Set<Point> part : dividerResult) {
			for (Point p : part) {
				pointSetSizes.put(p, part.size());
			}
			if (part.size() < min) {
				smallestSet = part;
				min = part.size();
			}
			if (part.size() > max) {
				largestSet = part;
				max = part.size();
			}
		}
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				boolean found = false;
				for (Rectangle r : rectsMap.keySet()) {
					if (r.contains(e.getPoint())) {
						CanvasComponent.this.bus.showSize(rectsMap.get(r));
						found = true;
						break;
					}
				}
				if (!found) {
					CanvasComponent.this.bus.showSize(-1);
				}
			}
		});
	}

	private int lastXSize = 0;
	private int lastYSize = 0;

	public void paint(Graphics g) {
		super.paint(g);
		g.draw3DRect(0, 0, getWidth() - 1, getHeight() - 1, true);
		boolean rescale = false;

		if (getHeight() != lastXSize || getWidth() != lastYSize) {
			rescale = true;
			lastXSize = getHeight();
			lastYSize = getWidth();
		}
		double xScale = (getWidth() - 10.0) / 100.0;
		double yScale = (getHeight() - 10.0) / 100.0;

		g.setColor(Color.RED);
		if (rescale)
			rectsMap.clear();

		for (Point p : points) {
			int x = (int) (p.getX() * xScale) + 5;
			int y = (int) (p.getY() * yScale) + 5;
			g.drawRect(x, y, 1, 1);
			if (rescale)
				rectsMap.put(new Rectangle(x - 5, y - 5, 10, 10), pointSetSizes.get(p));
			int setSize = pointSetSizes.get(p);

			if (setSize == smallestSet.size()) {
				g.setColor(Color.RED);
				g.drawRect(x, y, 2, 2);
				g.drawRect(x - 1, y - 1, 3, 3);
				g.setColor(Color.RED);
			}
			if (setSize == largestSet.size()) {
				g.setColor(Color.BLUE);
				g.drawRect(x, y, 2, 2);
				g.setColor(Color.RED);
			}
		}
		g.setColor(Color.BLACK);
		for (Circle c : circles) {
			int rX = (int) (c.getR() * xScale);
			int rY = (int) (c.getR() * yScale);
			int x = (int) (c.getP().getX() * xScale) + 5;
			int y = (int) (c.getP().getY() * yScale) + 5;
			g.drawOval(x - rX, y - rY, rX * 2, rY * 2);
		}
	}
}
