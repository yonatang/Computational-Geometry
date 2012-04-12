package idc.gc.dt;


public class Squere implements Shape {

	private Point topLeft;

	private double edge;

	public Squere(Point topLeft, double edge) {
		if (topLeft == null)
			throw new NullPointerException("Point must not be null");
		this.topLeft = topLeft;
		this.edge = edge;
	}

	public Point getTopLeft() {
		return topLeft;
	}

	public void setTopLeft(Point topLeft) {
		this.topLeft = topLeft;
	}

	public double getEdge() {
		return edge;
	}

	public void setEdge(double edge) {
		this.edge = edge;
	}

	@Override
	public String toString() {
		return "Squere [topLeft=" + topLeft + ", edge=" + edge + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(edge);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((topLeft == null) ? 0 : topLeft.hashCode());
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
		Squere other = (Squere) obj;
		if (Double.doubleToLongBits(edge) != Double.doubleToLongBits(other.edge))
			return false;
		if (topLeft == null) {
			if (other.topLeft != null)
				return false;
		} else if (!topLeft.equals(other.topLeft))
			return false;
		return true;
	}

	@Override
	public boolean contains(Point p) {
		return (p.getX() > topLeft.getX()) && (p.getY() > topLeft.getY()) && (p.getX() < topLeft.getX() + edge)
				&& (p.getY() < topLeft.getY() + edge);
	}

	public Circle boundingCircle() {
		Point center = new Point(topLeft.getX() + edge / 2, topLeft.getY() + edge / 2);
		return new Circle(center, center.distanceTo(topLeft) + 0.1);
	}

}
