package idc.gc.guards;

import il.ac.idc.jdt.Point;

public class Target {

	private Point point;

	Point getPoint() {
		return point;
	}
	public Target(Point target) {
		this.point = target;
	}

	@Override
	public String toString() {
		return "Guard [x=" + point.getX() + ", y=" + point.getY() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((point == null) ? 0 : point.hashCode());
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
		Target other = (Target) obj;
		if (point == null) {
			if (other.point != null)
				return false;
		} else if (!point.equals(other.point))
			return false;
		return true;
	}

}
