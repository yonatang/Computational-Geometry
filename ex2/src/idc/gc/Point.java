package idc.gc;

public class Point {

	private double x;
	private double y;

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}

	public double distanceTo(Point p) {
		double x = this.getX() - p.getX();
		double y = this.getY() - p.getY();
		return Math.sqrt(x * x + y * y);
	}

	public double cross(Point p) {
		return x * p.y - y * p.x;
	}

	public Point sub(Point p) {
		return new Point(x - p.x, y - p.y);
	}
	public Point leftUpEps(){
		return new Point(x-0.1,y-0.1);
	}

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point() {
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

}
