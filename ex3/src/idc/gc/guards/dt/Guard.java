package idc.gc.guards.dt;

import il.ac.idc.jdt.Point;

public class Guard {
	private Point point;
	private Integer nick;

	public Point getPoint() {
		return point;
	}

	public Guard(Point guard) {
		this.point = guard;
	}

	public Guard(Point guard, int nick) {
		this.point = guard;
		this.nick = nick;
	}

	@Override
	public String toString() {
		if (nick != null) {
			return "Guard [nick=" + nick + "]";
		}
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
		Guard other = (Guard) obj;
		if (point == null) {
			if (other.point != null)
				return false;
		} else if (!point.equals(other.point))
			return false;
		return true;
	}

}
