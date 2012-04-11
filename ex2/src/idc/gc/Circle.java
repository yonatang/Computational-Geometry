package idc.gc;

public class Circle implements Shape {

	public Circle(Point p, double r) {
		this.p = p;
		this.r = r;
	}

	public Circle() {
	}

	private Point p;
	private double r;

	public void setP(Point p) {
		this.p = p;
	}

	public Point getP() {
		return p;
	}

	public double getR() {
		return r;
	}

	public void setR(double r) {
		this.r = r;
	}

	@Override
	public String toString() {
		return "Circle [x=" + p.getX() + ", y=" + p.getY() + ", r=" + r + "]";
	}

	@Override
	public boolean contains(Point p) {
		return getP().distanceTo(p) < r;
	}

}
