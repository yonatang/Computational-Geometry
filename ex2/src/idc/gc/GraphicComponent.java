package idc.gc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Set;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class GraphicComponent extends JComponent {

	private Set<Point> points;
	private Set<Circle> circles;

	public GraphicComponent(Set<Point> points, Set<Circle> circles) {
		super();
		this.points = points;
		this.circles = circles;
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
