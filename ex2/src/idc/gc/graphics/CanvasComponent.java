package idc.gc.graphics;

import idc.gc.Benchmarker;
import idc.gc.dt.Circle;
import idc.gc.dt.Point;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
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
	private int largestSetSize;
	private Map<Point, Set<Point>> setsMap = new HashMap<Point, Set<Point>>();
	private Map<Point, Integer> pointSetSizes = new HashMap<Point, Integer>();
	private Map<Rectangle, Integer> rectsMap = new HashMap<Rectangle, Integer>();
	private Map<Rectangle, Point> rectToPoint = new HashMap<Rectangle, Point>();
	private Map<Rectangle, Circle> rectToCircle = new HashMap<Rectangle, Circle>();
	private Bus bus;
	private Set<Point> choosenSet;
	private Circle choosenCircle;

	public CanvasComponent(Bus bus, Set<Point> points, Set<Circle> circles) {
		super();
		this.bus = bus;
		this.points = points;
		this.circles = circles;
		Benchmarker b = new Benchmarker();
		Collection<Set<Point>> dividerResult = b.divider(points, circles);

		int max = Integer.MIN_VALUE;
		for (Set<Point> part : dividerResult) {
			for (Point p : part) {
				pointSetSizes.put(p, part.size());
				setsMap.put(p, part);
			}
			if (part.size() > max) {
				largestSetSize = part.size();
				max = part.size();
			}
		}
		addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				double xScale = (getWidth() - 10.0) / 100.0;
				double yScale = (getHeight() - 10.0) / 100.0;
				boolean found = false;
				for (Rectangle r : rectsMap.keySet()) {
					if (r.contains(e.getPoint())) {
						CanvasComponent.this.bus.showSize(rectsMap.get(r));
						choosenSet = setsMap.get(rectToPoint.get(r));
						found = true;
						break;
					}
				}
				if (!found) {
					choosenSet = null;
					CanvasComponent.this.bus.showSize(-1);
				}
				double vX=(e.getPoint().getX()-5)/xScale;
				double vY=(e.getPoint().getY()-5)/yScale;
				CanvasComponent.this.bus.showCords(vX, vY);

				found = false;
				for (Rectangle r : rectToCircle.keySet()) {
					if (r.contains(e.getPoint())) {
						choosenCircle = rectToCircle.get(r);
						found = true;
						break;
					}
				}
				if (!found) {
					choosenCircle = null;
				}

				repaint();
			}
		});
	}

	private int lastXSize = 0;
	private int lastYSize = 0;

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d=(Graphics2D)g;
		Stroke defaultStroke=g2d.getStroke();
		Stroke boldStroke=new BasicStroke(2);
		Stroke extraBoldStroke=new BasicStroke(3);
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
		if (rescale) {
			rectsMap.clear();
			rectToCircle.clear();
		}

		for (Point p : points) {
			g2d.setStroke(defaultStroke);
			int x = (int) (p.getX() * xScale) + 5;
			int y = (int) (p.getY() * yScale) + 5;

			if (rescale) {
				Rectangle r = new Rectangle(x - 5, y - 5, 10, 10);
				rectsMap.put(r, pointSetSizes.get(p));
				rectToPoint.put(r, p);
			}
			
			if (choosenSet!=null&&choosenSet.contains(p)){
				g2d.setStroke(extraBoldStroke);
			}
			int setSize = pointSetSizes.get(p);

			if (setSize == largestSetSize) {
				g.setColor(Color.BLUE);
				g.drawRect(x, y, 1, 1);
				g.setColor(Color.RED);
			} else {
				g.drawRect(x, y, 1, 1);
			}
		}
		g.setColor(Color.BLACK);
		for (Circle c : circles) {
			g2d.setStroke(defaultStroke);
			int rX = (int) (c.getR() * xScale);
			int rY = (int) (c.getR() * yScale);
			int x = (int) (c.getP().getX() * xScale) + 5;
			int y = (int) (c.getP().getY() * yScale) + 5;
			if (choosenCircle != null && choosenCircle.getP().equals(c.getP())) {
				g2d.setStroke(boldStroke);
			}
			g.drawOval(x - rX, y - rY, rX * 2, rY * 2);
			int cX = x; //(int) (c.getP().getX() * xScale) + 5;
			int cY = y; //(int) (c.getP().getY() * yScale) + 5;
			
			g.drawLine(cX - 2, cY, cX + 2, cY);
			g.drawLine(cX, cY - 2, cX, cY + 2);
			
			rectToCircle.put(new Rectangle(cX - 2, cY - 2, 4,4), c);
		}
		// if (choosenSet != null) {
		// g.setColor(Color.BLACK);
		// for (Point p : choosenSet) {
		// int x = (int) (p.getX() * xScale) + 5;
		// int y = (int) (p.getY() * yScale) + 5;
		// g.drawRect(x - 2, y - 2, 5, 5);
		// }
		// }
	}
}
