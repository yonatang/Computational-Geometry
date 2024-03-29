package idc.gc.dt;

import java.util.Comparator;

public class Circle implements Shape {

	public static final Comparator<Circle> RADUIS_COMP = new Comparator<Circle>() {

		@Override
		public int compare(Circle o1, Circle o2) {
			if (o1 == null && o2 == null)
				return 0;
			if (o1 == null)
				return -1;
			if (o2 == null)
				return 1;
			return (int) Math.signum(o1.r - o2.r);
		}
	};

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

	public Circle deepClone() {
		return new Circle(getP().deepClone(), getR());
	}

	// @Override
	// public int hashCode() {
	// final int prime = 31;
	// int result = 1;
	// result = prime * result + ((p == null) ? 0 : p.hashCode());
	// long temp;
	// temp = Double.doubleToLongBits(r);
	// result = prime * result + (int) (temp ^ (temp >>> 32));
	// return result;
	// }
	//
	// @Override
	// public boolean equals(Object obj) {
	// if (this == obj)
	// return true;
	// if (obj == null)
	// return false;
	// if (getClass() != obj.getClass())
	// return false;
	// Circle other = (Circle) obj;
	// if (p == null) {
	// if (other.p != null)
	// return false;
	// } else if (!p.equals(other.p))
	// return false;
	// if (Double.doubleToLongBits(r) != Double.doubleToLongBits(other.r))
	// return false;
	// return true;
	// }

}
