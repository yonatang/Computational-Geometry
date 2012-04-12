package idc.gc;

import idc.gc.dt.Circle;
import idc.gc.dt.Point;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Set;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class GraphicComponent extends JComponent {

	private Set<Point> points;
	private Set<Circle> circles;
	private Set<Point> largestSet;
	private Set<Point> smallestSet;

	public GraphicComponent(Set<Point> points, Set<Circle> circles) {
		super();
		this.points = points;
		this.circles = circles;
		Benchmarker b=new Benchmarker();
		Collection<Set<Point>> dividerResult=b.divider(points, circles);
		int min=Integer.MAX_VALUE;
		int max=Integer.MIN_VALUE;
		for (Set<Point> part:dividerResult){
			if (part.size()<min){
				smallestSet=part;
				min=part.size();
			}
			if (part.size()>max){
				largestSet=part;
				max=part.size();
			}
		}
		
	}

	public void paint(Graphics g) {
		Rectangle rect = g.getClipBounds();
		double xScale = (rect.getWidth() - 5.0) / 100.0;
		double yScale = (rect.getHeight() - 5.0) / 100.0;

		g.setColor(Color.RED);
		for (Point p : points) {
			int x = (int) (p.getX() * xScale) + 5;
			int y = (int) (p.getY() * yScale) + 5;
			g.drawRect(x, y, 1, 1);
			if (smallestSet.contains(p)){
				g.setColor(Color.RED);
				g.drawRect(x, y, 2, 2);
				g.drawRect(x-1, y-1, 3, 3);
				g.setColor(Color.RED);
			}
			if (largestSet.contains(p)){
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
